package com.example.myapplication.spider_solitaire_model;

import android.widget.*;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;


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

                // Set card face-up or face-down based on its actual state
                if (card.isFaceUp()) {
                    cardView.setImageResource(Card.getCardImageResource(card));  // Show face-up card
                } else {
                    cardView.setImageResource(R.drawable.spiderback);  // Face down for other cards
                }

                // Set OnClickListener only for face-up cards
                if (card.isFaceUp()) {
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
        mainDeckView.setImageResource(R.drawable.spiderback);// Show the back of the main deck
        RelativeLayout.LayoutParams mainDeckParams = new RelativeLayout.LayoutParams(cardWidth, cardHeight);
        mainDeckParams.setMargins(2360, 150, 0, 0);

        // Add click listener for the main deck
        mainDeckView.setOnClickListener(v -> dealFromMainDeck());

        gameBoard.addView(mainDeckView, mainDeckParams);
    }


    private void handleCardClick(Card clickedCard, int pileIndex) {
        ArrayList<Card> sourcePile = boardPiles.get(pileIndex);

        // Get the index of the clicked card in the source pile
        int clickedCardIndex = sourcePile.indexOf(clickedCard);

        // If clicked card is face down or doesn't exist in the pile, exit early
        if (clickedCardIndex == -1 || !clickedCard.isFaceUp()) {
            return; // Invalid click
        }

        // Get all the face-up cards starting from the clicked card
        ArrayList<Card> cardsToMove = new ArrayList<>(sourcePile.subList(clickedCardIndex, sourcePile.size()));

        // Validate the sequence of cards in the stack
        if (!isSequential(cardsToMove)) {
            return; // The sequence is invalid, exit early
        }

        // Find a target pile where the cards can be moved
        for (int i = 0; i < boardPiles.size(); i++) {
            if (i != pileIndex) { // Skip the same pile
                ArrayList<Card> targetPile = boardPiles.get(i);
                Card topCardInTargetPile = targetPile.isEmpty() ? null : targetPile.get(targetPile.size() - 1);

                // Check if the top card in the target pile is one rank higher than the first card in cardsToMove
                if (topCardInTargetPile == null || cardsToMove.get(0).getRank().ordinal() == topCardInTargetPile.getRank().ordinal() - 1) {
                    // Valid move: Move the entire stack of cards
                    targetPile.addAll(cardsToMove);  // Add the cards to the target pile
                    sourcePile.subList(clickedCardIndex, sourcePile.size()).clear(); // Remove the cards from the source pile

                    // After moving the cards, flip the new last card in the source pile if it exists
                    if (!sourcePile.isEmpty()) {
                        Card newLastCard = sourcePile.get(sourcePile.size() - 1);
                        newLastCard.setFaceUp(true); // Flip the new last card face-up
                    }

                    // Refresh the display to reflect the changes
                    displayPiles(gameBoard.getContext());  // Re-draw piles with updated data
                    return;  // Exit after a successful move
                }
            }
        }
    }

    // Method to check if the cards form a valid sequential order
    private boolean isSequential(ArrayList<Card> cardsToMove) {
        for (int i = 0; i < cardsToMove.size() - 1; i++) {
            if (cardsToMove.get(i).getRank().ordinal() != cardsToMove.get(i + 1).getRank().ordinal() + 1) {
                return false; // Invalid sequence
            }
        }
        return true; // Valid sequence
    }


    private void dealFromMainDeck() {
        // Ensure there are enough cards in the main deck to deal
        if (mainDeckPile.size() < boardPiles.size()) return; // Ensure we can deal one card to each pile

        // Deal one card to each pile
        for (ArrayList<Card> pile : boardPiles) {
            if (!mainDeckPile.isEmpty()) {
                // Get the index of the last card
                int lastIndex = mainDeckPile.size() - 1;
                // Retrieve the last card
                Card newCard = mainDeckPile.get(lastIndex);
                // Remove the last card using its index
                mainDeckPile.remove(lastIndex);

                // Ensure the newly dealt card is face-up
                newCard.setFaceUp(true);
                pile.add(newCard); // Add it to the pile

                // Check the previous last card in the pile to set it face down if necessary
                if (pile.size() > 1) {
                    Card previousLastCard = pile.get(pile.size() - 2); // Get the previous last card
                    previousLastCard.setFaceUp(false); // Set the previous last card to face down
                }
            }
        }

        // Refresh the display to reflect the changes
        displayPiles(gameBoard.getContext()); // Update display using the context
    }
}