package com.example.myapplication.model;

import android.util.Log;

public class TableauPile extends Pile {

    public boolean canAddCard(Card card) {
        Log.d("TableauPile", "canAddCard called for card: " + card.getValue());
        Log.d("TableauPile", "Tableau pile size: " + cards.size());

        if (cards.isEmpty()) {
            // If the tableau pile is empty, only a King can be placed
            return "King".equals(card.getValue());
        } else {
            // Standard rule: cards must alternate colors and be one rank lower
            Card topCard = peekTopCard();
            return !card.isSameColor(topCard) && card.isOneRankLower(topCard);
        }
    }

    public void addCard(Card card, boolean shouldFlip) {
        super.addCard(card);
        // Only flip the card if we're adding it during gameplay
        if (shouldFlip && !card.isFaceUp()) {
            card.flip();
        }
    }
}