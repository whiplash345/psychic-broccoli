package com.example.myapplication.model;

import android.util.Log;
import java.util.List;
import java.util.ArrayList;

public class TableauPile extends Pile {

    public boolean canAddCard(Card card) {
        if (cards.isEmpty()) {
            return "King".equals(card.getValue());  // Only King can go on empty tableau pile
        } else {
            Card topCard = peekTopCard();
            Log.d("TableauPile", "Top card: " + topCard.getValue() + " of " + topCard.getSuit());
            Log.d("TableauPile", "Dragged card: " + card.getValue() + " of " + card.getSuit());

            // Ensure the card is one rank lower and of a different color
            boolean isValidMove = !card.isSameColor(topCard) && card.isOneRankLower(topCard);
            Log.d("TableauPile", "Is valid move: " + isValidMove);
            return isValidMove;
        }
    }

    public void addCard(Card card, boolean shouldFlip) {
        super.addCard(card);
        // Only flip the card if we're adding it during gameplay
        if (shouldFlip && !card.isFaceUp()) {
            card.flip();
        }
    }
    public List<Card> getFaceUpCardsFrom(Card card) {
        List<Card> faceUpCards = new ArrayList<>();
        boolean found = false;

        for (Card c : cards) {
            if (c == card) {
                found = true; // Start adding cards once the touched card is found
            }
            if (found && c.isFaceUp()) {
                faceUpCards.add(c); // Add only face-up cards from the touched card onwards
            }
        }

        return faceUpCards;
    }
}