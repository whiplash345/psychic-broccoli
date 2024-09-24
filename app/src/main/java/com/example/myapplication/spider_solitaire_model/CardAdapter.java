package com.example.myapplication.spider_solitaire_model;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Map;

public class CardAdapter {
    private ArrayList<ArrayList<Card>> boardPiles;
    private Map<String, Integer> cardImageMap;
    private int cardWidth;
    private int cardHeight;
    private int cardOffset;
    private int[] completedDeckImages;
    private ArrayList<Card> mainDeckPile;
    private int pileTopMargin;
    private RelativeLayout gameBoard; // Add gameBoard as a class member

    // Constructor for CardAdapter
    public CardAdapter(ArrayList<ArrayList<Card>> boardPiles, Map<String, Integer> cardImageMap,
                       int cardWidth, int cardHeight, int cardOffset,
                       int[] completedDeckImages, ArrayList<Card> mainDeckPile,
                       int pileTopMargin, RelativeLayout gameBoard) {
        this.boardPiles = boardPiles;
        this.cardImageMap = cardImageMap;
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
        this.cardOffset = cardOffset;
        this.completedDeckImages = completedDeckImages;
        this.mainDeckPile = mainDeckPile;
        this.pileTopMargin = pileTopMargin;
        this.gameBoard = gameBoard; // Initialize gameBoard
    }

    // Method to display piles on the given RelativeLayout
    public void displayPiles(Context context) {
        gameBoard.removeAllViews();  // Clear previous views

        // Display the 10 piles
        for (int i = 0; i < boardPiles.size(); i++) {
            ArrayList<Card> pile = boardPiles.get(i);
            LinearLayout pileLayout = new LinearLayout(context);
            pileLayout.setOrientation(LinearLayout.VERTICAL);

            for (int j = 0; j < pile.size(); j++) {
                Card card = pile.get(j);
                ImageView cardView = new ImageView(context);

                // Set card face-up or face-down
                if (j == pile.size() - 1) {
                    card.setFaceUp(true);
                    cardView.setImageResource(Card.getCardImageResource(card));
                } else {
                    cardView.setImageResource(R.drawable.spiderback);  // Face down for other cards
                }

                // Set OnClickListener for face-up cards
                if (j == pile.size() - 1) {
                    int finalI = i;
                    cardView.setOnClickListener(v -> handleCardClick(card, finalI)); // Pass the clicked card and its pile index
                }

                // Set card size and margin for overlapping effect
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(cardWidth, cardHeight);
                if (j > 0) {
                    params.topMargin = -cardOffset;  // Overlapping margin
                }
                cardView.setLayoutParams(params);
                pileLayout.addView(cardView);
            }

            // Set up the RelativeLayout positioning for the pile
            RelativeLayout.LayoutParams pileParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            pileParams.setMargins(i * cardWidth, pileTopMargin, 40, 0);  // Position each pile with a margin
            gameBoard.addView(pileLayout, pileParams);
        }

        // Display the 4 completed deck piles
        for (int i = 0; i < completedDeckImages.length; i++) {
            ImageView completedDeckView = new ImageView(context);
            completedDeckView.setImageResource(completedDeckImages[i]);
            RelativeLayout.LayoutParams completedDeckParams = new RelativeLayout.LayoutParams(cardWidth, cardHeight);
            completedDeckParams.setMargins(i * cardWidth, 150, 0, 0);  // Adjust the position of the completed deck piles
            gameBoard.addView(completedDeckView, completedDeckParams);
        }

        // Display the main deck
        ImageView mainDeckView = new ImageView(context);
        mainDeckView.setImageResource(R.drawable.spiderback);  // Show the back of the main deck
        RelativeLayout.LayoutParams mainDeckParams = new RelativeLayout.LayoutParams(cardWidth, cardHeight);
        mainDeckParams.setMargins(2360, 150, 0, 0);

        // Add click listener for the main deck
        mainDeckView.setOnClickListener(v -> dealFromMainDeck());

        gameBoard.addView(mainDeckView, mainDeckParams);
    }

    // Handle the logic when a face-up card is clicked
    private void handleCardClick(Card clickedCard, int pileIndex) {
        // Check if the clicked card can be moved to any other pile
        for (int i = 0; i < boardPiles.size(); i++) {
            if (i != pileIndex) { // Don't check the same pile
                Card topCard = boardPiles.get(i).isEmpty() ? null : boardPiles.get(i).get(boardPiles.get(i).size() - 1);
                if (topCard != null && clickedCard.getRank().ordinal() == topCard.getRank().ordinal() - 1) {
                    // Move the clicked card to the target pile
                    boardPiles.get(i).add(clickedCard); // Add to the target pile
                    boardPiles.get(pileIndex).remove(clickedCard); // Remove from the original pile

                    // Refresh the display to reflect the changes
                    displayPiles(gameBoard.getContext()); // Update display using the context
                    return;
                }
            }
        }
    }

    // Deal from the main deck and ensure already face-up cards stay face up
    private void dealFromMainDeck() {
        // Ensure there are cards in the main deck to deal
        if (mainDeckPile.size() < 10) return; // Assuming 10 piles

        // Deal one card to each pile
        for (ArrayList<Card> pile : boardPiles) {
            if (!mainDeckPile.isEmpty()) {
                Card newCard = mainDeckPile.remove(mainDeckPile.size() - 1); // Draw from the main deck
                pile.add(newCard); // Add it to the pile

                // Flip the card face up if it’s the last card in the pile
                if (pile.size() == 1) {
                    newCard.setFaceUp(true); // Flip the first card face up
                }
            }
        }

        // Refresh the display to reflect the changes
        displayPiles(gameBoard.getContext()); // Update display using the context
    }
}
