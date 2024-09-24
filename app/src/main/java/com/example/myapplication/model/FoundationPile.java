package com.example.myapplication.model;

import android.util.Log;

public class FoundationPile extends Pile {

    public boolean canAddCard(Card card) {
        Log.d("FoundationPile", "Attempting to add card: " + card.getValue() + " of " + card.getSuit());

        // Foundation pile can only start with an Ace if empty
        if (cards.isEmpty()) {
            Log.d("FoundationPile", "Foundation pile is empty, checking for Ace.");
            boolean isAce = "Ace".equals(card.getValue());
            Log.d("FoundationPile", "Is Ace: " + isAce);
            return isAce;
        } else {
            Card topCard = peekTopCard();
            // Cards in the foundation must be of the same suit and in ascending order
            boolean isSameSuit = card.getSuit().equals(topCard.getSuit());
            boolean isHigherRank = card.isOneRankHigher(topCard);
            Log.d("FoundationPile", "Top card: " + topCard.getValue() + " of " + topCard.getSuit());
            Log.d("FoundationPile", "Same suit: " + isSameSuit + ", One rank higher: " + isHigherRank);
            return isSameSuit && isHigherRank;
        }
    }

    private int indexOf(String value, String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) return i;
        }
        return -1;
    }
}
