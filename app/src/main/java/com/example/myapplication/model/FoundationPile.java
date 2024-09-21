package com.example.myapplication.model;

public class FoundationPile extends Pile {

    public boolean canAddCard(Card card) {
        if (cards.isEmpty()) {
            // Only an Ace can be placed in an empty foundation pile.
            return "Ace".equals(card.getValue());
        } else {
            Card topCard = peekTopCard();
            return card.getSuit().equals(topCard.getSuit()) && card.isOneRankHigher(topCard);
        }
    }

    private boolean isOneRankHigher(Card card, Card topCard) {
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        int cardIndex = indexOf(card.getValue(), ranks);
        int topCardIndex = indexOf(topCard.getValue(), ranks);
        return cardIndex == topCardIndex + 1;
    }

    private int indexOf(String value, String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) return i;
        }
        return -1;
    }
}