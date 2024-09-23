package com.example.myapplication.model;

import android.util.Log;

public class FoundationPile extends Pile {

    public boolean canAddCard(Card card) {
        Log.d("FoundationPile", "Checking if card can be added: " + card.getValue() + " of " + card.getSuit());

        if (cards.isEmpty()) {
            Log.d("FoundationPile", "Foundation pile is empty. Can add Ace only.");
            return "Ace".equals(card.getValue());
        } else {
            Card topCard = peekTopCard();
            boolean sameSuit = card.getSuit().equals(topCard.getSuit());
            boolean oneRankHigher = isOneRankHigher(card, topCard);

            Log.d("FoundationPile", "Top card: " + topCard.getValue() + " of " + topCard.getSuit());
            Log.d("FoundationPile", "Same suit: " + sameSuit + ", One rank higher: " + oneRankHigher);

            return sameSuit && oneRankHigher;
        }
    }


    private boolean isOneRankHigher(Card card, Card topCard) {
        String[] ranks = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};
        int cardIndex = indexOf(card.getValue(), ranks);
        int topCardIndex = indexOf(topCard.getValue(), ranks);

        // Log the rank comparison
        Log.d("FoundationPile", "Card rank index: " + cardIndex + ", Top card rank index: " + topCardIndex);

        return cardIndex == topCardIndex + 1;
    }

    private int indexOf(String value, String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) return i;
        }
        return -1;
    }
}
