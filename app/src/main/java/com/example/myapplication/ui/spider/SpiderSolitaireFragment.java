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
import com.example.myapplication.ui.spider.SpiderSolitaireViewModel;

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
    private ArrayList<ArrayList<Card>> completedDeckPiles;
    private Map<String, Integer> cardImageMap;

    private SpiderSolitaireViewModel spiderSolitaireViewModel; // ViewModel for TTS state
    private boolean isTtsEnabled = false; // Local variable to store TTS state

    private int cardWidth;
    private int cardHeight;
    private int cardOffset;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spider, container, false);

        spiderSolitaireViewModel = new ViewModelProvider(requireActivity()).get(SpiderSolitaireViewModel.class);

        spiderSolitaireViewModel.getIsLargeCard().observe(getViewLifecycleOwner(), isLarge -> {
            if (cardAdapter != null) {
                // Re-initialize the cardAdapter with the new card size
                cardAdapter = new CardAdapter(boardPiles, cardImageMap, cardWidth, cardHeight, cardOffset,
                        completedDeckImages, mainDeckPile, pileTopMargin, gameBoard, isLarge);
                cardAdapter.displayPiles(getContext());
            }
        });

        playButton = view.findViewById(R.id.playButton);
        resetButton = view.findViewById(R.id.resetButton);
        mainScreen = view.findViewById(R.id.mainScreen);
        gameBoard = view.findViewById(R.id.gameBoard);

        cardWidth = 200;
        cardHeight = 260;
        cardOffset = 220;

        // Initialize ViewModel and observe TTS state
        spiderSolitaireViewModel = new ViewModelProvider(requireActivity()).get(SpiderSolitaireViewModel.class);
        spiderSolitaireViewModel.getIsTtsEnabled().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isEnabled) {
                isTtsEnabled = isEnabled; // Update the local TTS state
            }
        });

        playButton.setOnClickListener(v -> {
            mainScreen.setVisibility(View.GONE);
            gameBoard.setVisibility(View.VISIBLE);
            startGame();
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

        boolean isLargeCard = spiderSolitaireViewModel.getIsLargeCard().getValue() != null && spiderSolitaireViewModel.getIsLargeCard().getValue();
        cardAdapter = new CardAdapter(boardPiles, cardImageMap, cardWidth, cardHeight, cardOffset,
                completedDeckImages, mainDeckPile, pileTopMargin, gameBoard, isLargeCard);
        cardAdapter.displayPiles(requireContext());

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

        int[] completedDeckImages = {
                R.drawable.backgroundtransparent,
                R.drawable.backgroundtransparent,
                R.drawable.backgroundtransparent,
                R.drawable.backgroundtransparent
        };

        int pileTopMargin = 450;

        // Initialize adapter and pass the TTS state
        cardAdapter = new CardAdapter(boardPiles, cardImageMap, cardWidth, cardHeight, cardOffset, completedDeckImages, mainDeckPile, pileTopMargin, gameBoard, isTtsEnabled);
        cardAdapter.displayPiles(requireContext());
    }

    private void resetGame() {
        boardPiles.clear();
        completedDeckPiles.clear();
        mainDeckPile.clear();

        startGame();
    }
}
