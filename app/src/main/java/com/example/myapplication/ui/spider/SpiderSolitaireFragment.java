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
    private RelativeLayout gameBoard; // Ensure this is a RelativeLayout
    private Button playButton;
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
        mainScreen = view.findViewById(R.id.mainScreen);
        gameBoard = view.findViewById(R.id.gameBoard);

        // Initialize the card image map with card images
        initializeCardImageMap();

        // Initialize card size and overlap settings
        cardWidth = 200;  // Adjust to fit the layout
        cardHeight = 260; // Adjust to fit the layout
        cardOffset = 240; // Overlapping, adjust as necessary

        // Set OnClickListener for the Play button
        playButton.setOnClickListener(v -> {
            // Hide main screen and show the game board
            mainScreen.setVisibility(View.GONE);
            gameBoard.setVisibility(View.VISIBLE);
            startGame();
        });

        return view;
    }

    private void initializeCardImageMap() {
        // Populate cardImageMap with card image resources
        // Example: cardImageMap.put("Ace of Spades", R.drawable.ace_of_spades);
        // Add all necessary card images here.
    }

    private void startGame() {
        Deck deck = new Deck(); // Create a new deck of cards
        boardPiles = new ArrayList<>();
        completedDeckPiles = new ArrayList<>();
        mainDeckPile = new ArrayList<>(deck.getCards()); // Remaining cards for the main deck

        // Create piles from the deck
        for (int i = 0; i < 10; i++) {  // 10 piles in Spider Solitaire
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

        // Initialize 4 empty completed deck piles
        for (int i = 0; i < 4; i++) {
            completedDeckPiles.add(new ArrayList<>()); // Empty pile
        }

        // Completed deck images
        int[] completedDeckImages = {
                R.drawable.backgroundtransparent,
                R.drawable.backgroundtransparent,
                R.drawable.backgroundtransparent,
                R.drawable.backgroundtransparent
        };

        // Define the margin from the top of the screen for the piles
        int pileTopMargin = 450;  // Example margin, adjust based on layout needs

        // Initialize the adapter with the piles, card image map, size, offset, and completed deck images
        cardAdapter = new CardAdapter(boardPiles, cardImageMap, cardWidth, cardHeight, cardOffset, completedDeckImages, mainDeckPile, pileTopMargin, gameBoard);

        // Display the card piles on the gameBoard
        cardAdapter.displayPiles(requireContext()); // Use requireContext() for a non-null context
    }
}