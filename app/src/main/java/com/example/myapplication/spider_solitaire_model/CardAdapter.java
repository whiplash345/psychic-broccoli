package com.example.myapplication.spider_solitaire_model;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.example.myapplication.MainActivity;
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
    private RelativeLayout gameBoard;
    private TextToSpeech tts;
    private boolean isTtsEnabled; // TTS state
    private boolean isLargeCard; // Card size state

    // Constructor for CardAdapter
    public CardAdapter(ArrayList<ArrayList<Card>> boardPiles, Map<String, Integer> cardImageMap,
                       int cardWidth, int cardHeight, int cardOffset,
                       int[] completedDeckImages, ArrayList<Card> mainDeckPile,
                       int pileTopMargin, RelativeLayout gameBoard, boolean isTtsEnabled, boolean isLargeCard) {
        this.boardPiles = boardPiles;
        this.cardImageMap = cardImageMap;
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
        this.cardOffset = cardOffset;
        this.completedDeckImages = completedDeckImages;
        this.mainDeckPile = mainDeckPile;
        this.pileTopMargin = pileTopMargin;
        this.gameBoard = gameBoard;

        // Initialize TTS and TTS state
        MainActivity mainActivity = (MainActivity) gameBoard.getContext();
        this.tts = mainActivity.getTextToSpeech();
        this.isTtsEnabled = isTtsEnabled;
        this.isLargeCard = isLargeCard; // Card size state
    }

    public void displayPiles(Context context) {
        gameBoard.removeAllViews();  // Clear previous views

        // Display the 10 piles
        for (int i = 0; i < boardPiles.size(); i++) {
            final int pileIndex = i;  // Make a final copy of 'i'
            ArrayList<Card> pile = boardPiles.get(i);
            LinearLayout pileLayout = new LinearLayout(context);
            pileLayout.setOrientation(LinearLayout.VERTICAL);

            for (int j = 0; j < pile.size(); j++) {
                Card card = pile.get(j);
                ImageView cardView = new ImageView(context);

                // Set card face-up or face-down based on its actual state
                if (card.isFaceUp()) {
                    // Load either normal or large card image based on the isLargeCard flag
                    int cardImageResource = getCardImageResource(card, isLargeCard);
                    cardView.setImageResource(cardImageResource);  // Show face-up card
                } else {
                    cardView.setImageResource(R.drawable.spiderback);  // Face down for other cards
                }

                // Set OnClickListener only for face-up cards with TTS
                if (card.isFaceUp()) {
                    cardView.setOnClickListener(v -> {
                        handleCardClick(card, pileIndex); // Use final 'pileIndex' instead of 'i'

                        // TTS: Speak the card details when clicked
                        if (isTtsEnabled && tts != null) {
                            String cardName = card.getRank().name() + " of " + card.getSuit().name();
                            Log.d("SpiderSolitaire", "Speaking card: " + cardName);
                            tts.speak(cardName, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    });
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

        // Display completed deck piles and main deck (rest of the method remains the same)
    }

    // Helper method to get the card image resource, with support for large cards
    private int getCardImageResource(Card card, boolean isLarge) {
        String cardName = card.getRank().name().toLowerCase() + "_of_" + card.getSuit().name().toLowerCase();
        if (isLarge) {
            cardName += "_large"; // Use the large version if flag is true
        }
        return gameBoard.getContext().getResources().getIdentifier(cardName, "drawable", gameBoard.getContext().getPackageName());
    }

    private void handleCardClick(Card clickedCard, int pileIndex) {
        ArrayList<Card> sourcePile = boardPiles.get(pileIndex);

        int clickedCardIndex = sourcePile.indexOf(clickedCard);
        if (clickedCardIndex == -1 || !clickedCard.isFaceUp()) {
            return;
        }

        ArrayList<Card> cardsToMove = new ArrayList<>(sourcePile.subList(clickedCardIndex, sourcePile.size()));

        if (!isSequential(cardsToMove)) {
            return;
        }

        for (int i = 0; i < boardPiles.size(); i++) {
            if (i != pileIndex) {
                ArrayList<Card> targetPile = boardPiles.get(i);
                Card topCardInTargetPile = targetPile.isEmpty() ? null : targetPile.get(targetPile.size() - 1);

                if (topCardInTargetPile == null || cardsToMove.get(0).getRank().ordinal() == topCardInTargetPile.getRank().ordinal() - 1) {
                    targetPile.addAll(cardsToMove);
                    sourcePile.subList(clickedCardIndex, sourcePile.size()).clear();

                    if (!sourcePile.isEmpty()) {
                        Card newLastCard = sourcePile.get(sourcePile.size() - 1);
                        newLastCard.setFaceUp(true);
                    }

                    displayPiles(gameBoard.getContext());
                    return;
                }
            }
        }
    }

    private boolean isSequential(ArrayList<Card> cardsToMove) {
        for (int i = 0; i < cardsToMove.size() - 1; i++) {
            if (cardsToMove.get(i).getRank().ordinal() != cardsToMove.get(i + 1).getRank().ordinal() + 1) {
                return false;
            }
        }
        return true;
    }

    private void dealFromMainDeck() {
        if (mainDeckPile.size() < boardPiles.size()) return;

        for (ArrayList<Card> pile : boardPiles) {
            if (!mainDeckPile.isEmpty()) {
                Card newCard = mainDeckPile.remove(mainDeckPile.size() - 1);
                newCard.setFaceUp(true);
                pile.add(newCard);

                if (pile.size() > 1) {
                    Card previousLastCard = pile.get(pile.size() - 2);
                    previousLastCard.setFaceUp(false);
                }
            }
        }

        displayPiles(gameBoard.getContext());
    }
}