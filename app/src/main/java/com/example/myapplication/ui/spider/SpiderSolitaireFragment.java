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
import com.example.myapplication.R;
import com.example.myapplication.spider_solitaire_model.Card;
import com.example.myapplication.spider_solitaire_model.CardAdapter;
import com.example.myapplication.spider_solitaire_model.Deck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpiderSolitaireFragment extends Fragment {

    private LinearLayout mainScreen;
    private RelativeLayout gameBoard;
    private Button playButton;
    private Button resetButton;  // Add a reference to the reset button
    private CardAdapter cardAdapter;
    private ArrayList<ArrayList<Card>> boardPiles;
    private ArrayList<Card> mainDeckPile;
    private ArrayList<ArrayList<Card>> completedDeckPiles;
    private Map<String, Integer> cardImageMap;

    // Define the card dimensions and offset for overlapping
    private int cardWidth;
    private int cardHeight;
    private int cardOffset;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spider, container, false);

        // Get references to the UI elements
        playButton = view.findViewById(R.id.playButton);
        resetButton = view.findViewById(R.id.resetButton);  // Reference the reset button
        mainScreen = view.findViewById(R.id.mainScreen);
        gameBoard = view.findViewById(R.id.gameBoard);

        // Initialize card size and overlap settings
        cardWidth = 200;
        cardHeight = 260;
        cardOffset = 220;

        // Set OnClickListener for the Play button
        playButton.setOnClickListener(v -> {
            mainScreen.setVisibility(View.GONE);
            gameBoard.setVisibility(View.VISIBLE);
            startGame();
            resetButton.setVisibility(View.VISIBLE); // Show reset button after starting the game
        });

        // Set OnClickListener for the Reset button
        resetButton.setOnClickListener(v -> resetGame());  // Call resetGame() when clicked

        return view;
    }

    private void startGame() {
        Deck deck = new Deck();  // Create a new deck of cards
        boardPiles = new ArrayList<>();
        completedDeckPiles = new ArrayList<>();
        mainDeckPile = new ArrayList<>(deck.getCards());  // Remaining cards for the main deck

        // Create piles from the deck
        for (int i = 0; i < 10; i++) {
            ArrayList<Card> pile = new ArrayList<>();
            int cardsInPile = (i < 4) ? 6 : 5;  // First 4 piles get 6 cards, rest get 5 cards
            for (int j = 0; j < cardsInPile; j++) {
                Card drawnCard = deck.drawCard();
                if (drawnCard != null) {
                    pile.add(drawnCard);
                }
            }
            boardPiles.add(pile);
        }

        // Set the last card of each pile face-up
        for (ArrayList<Card> pile : boardPiles) {
            if (!pile.isEmpty()) {
                for (int j = 0; j < pile.size() - 1; j++) {
                    pile.get(j).setFaceUp(false);  // Face down all but the last card
                }
                pile.get(pile.size() - 1).setFaceUp(true);  // Set the last card face-up
            }
        }

        // Initialize empty completed deck piles
        for (int i = 0; i < 4; i++) {
            completedDeckPiles.add(new ArrayList<>());  // Empty pile
        }

        // Completed deck images (transparent backgrounds for now)
        int[] completedDeckImages = {
                R.drawable.backgroundtransparent,
                R.drawable.backgroundtransparent,
                R.drawable.backgroundtransparent,
                R.drawable.backgroundtransparent
        };

        // Define the margin from the top of the screen for the piles
        int pileTopMargin = 450;

        // Initialize the adapter and display piles on the gameBoard
        cardAdapter = new CardAdapter(boardPiles, cardImageMap, cardWidth, cardHeight, cardOffset, completedDeckImages, mainDeckPile, pileTopMargin, gameBoard);
        cardAdapter.displayPiles(requireContext());
    }

    private void resetGame() {
        // Clear all piles and reinitialize
        boardPiles.clear();
        completedDeckPiles.clear();
        mainDeckPile.clear();

        // Restart the game with a fresh setup
        startGame();
    }
}
