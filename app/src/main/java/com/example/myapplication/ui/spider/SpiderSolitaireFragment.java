package com.example.myapplication.ui.spider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.R;
import com.example.myapplication.spider_solitaire_model.Card;
import com.example.myapplication.spider_solitaire_model.CardAdapter;
import com.example.myapplication.spider_solitaire_model.Deck;

import java.util.ArrayList;
import java.util.Map;

public class SpiderSolitaireFragment extends Fragment {

    private LinearLayout mainScreen;
    private RelativeLayout gameBoard;
    private Button playButton;
    private Button resetButton;
    private CardAdapter cardAdapter;
    private ArrayList<ArrayList<Card>> boardPiles;
    private ArrayList<Card> mainDeckPile;
    private Map<String, Integer> cardImageMap;
    private ArrayList<ArrayList<Card>> completedDeckPiles;

    private SpiderSolitaireViewModel spiderSolitaireViewModel;
    private boolean isTtsEnabled = false;

    private int cardWidth;
    private int cardHeight;
    private int cardOffset;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spider, container, false);

        playButton = view.findViewById(R.id.playButton);
        resetButton = view.findViewById(R.id.resetButton);
        mainScreen = view.findViewById(R.id.mainScreen);
        gameBoard = view.findViewById(R.id.gameBoard);

        cardWidth = 200;
        cardHeight = 260;
        cardOffset = 220;

        // Initialize ViewModel and observe TTS state and game board state
        spiderSolitaireViewModel = new ViewModelProvider(requireActivity()).get(SpiderSolitaireViewModel.class);
        spiderSolitaireViewModel.getIsTtsEnabled().observe(getViewLifecycleOwner(), isEnabled -> isTtsEnabled = isEnabled);

        spiderSolitaireViewModel.getBoardPilesLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<ArrayList<Card>>>() {
            @Override
            public void onChanged(ArrayList<ArrayList<Card>> savedBoardPiles) {
                if (savedBoardPiles != null) {
                    boardPiles = savedBoardPiles;
                    displaySavedGame();
                }
            }
        });

        spiderSolitaireViewModel.getMainDeckLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Card>>() {
            @Override
            public void onChanged(ArrayList<Card> savedMainDeck) {
                if (savedMainDeck != null) {
                    mainDeckPile = savedMainDeck;
                }
            }
        });

        playButton.setOnClickListener(v -> {
            mainScreen.setVisibility(View.GONE);
            gameBoard.setVisibility(View.VISIBLE);
            if (boardPiles == null || boardPiles.isEmpty()) {
                startGame();
            }
            resetButton.setVisibility(View.VISIBLE);
        });

        resetButton.setOnClickListener(v -> resetGame());

        return view;
    }

    private void startGame() {
        Deck deck = new Deck();
        boardPiles = new ArrayList<>();
        completedDeckPiles = new ArrayList<>();
        mainDeckPile = new ArrayList<>(deck.getCards());

        for (int i = 0; i < 10; i++) {
            ArrayList<Card> pile = new ArrayList<>();
            int cardsInPile = (i < 4) ? 6 : 5;
            for (int j = 0; j < cardsInPile; j++) {
                Card drawnCard = deck.drawCard();
                if (drawnCard != null) {
                    pile.add(drawnCard);
                }
            }
            boardPiles.add(pile);
        }

        for (ArrayList<Card> pile : boardPiles) {
            if (!pile.isEmpty()) {
                for (int j = 0; j < pile.size() - 1; j++) {
                    pile.get(j).setFaceUp(false);
                }
                pile.get(pile.size() - 1).setFaceUp(true);
            }
        }

        for (int i = 0; i < 4; i++) {
            completedDeckPiles.add(new ArrayList<>());
        }

        cardAdapter = new CardAdapter(boardPiles, cardImageMap, cardWidth, cardHeight, cardOffset, null, mainDeckPile, 450, gameBoard, isTtsEnabled);
        cardAdapter.displayPiles(requireContext());

        // Save the game state in ViewModel
        spiderSolitaireViewModel.setBoardPiles(boardPiles);
        spiderSolitaireViewModel.setMainDeck(mainDeckPile);
    }

    private void displaySavedGame() {
        // Display saved game using cardAdapter
        cardAdapter = new CardAdapter(boardPiles, cardImageMap, cardWidth, cardHeight, cardOffset, null, mainDeckPile, 450, gameBoard, isTtsEnabled);
        cardAdapter.displayPiles(requireContext());
    }

    private void resetGame() {
        // Clear the ViewModel data
        spiderSolitaireViewModel.setBoardPiles(new ArrayList<>());
        spiderSolitaireViewModel.setMainDeck(new ArrayList<>());

        // Restart the game with a fresh setup
        startGame();
    }
}


//bleh blah
