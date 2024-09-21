package com.example.myapplication.model;

public class TableauPile extends Pile {

    public boolean canAddCard(Card card) {
        if (cards.isEmpty()) {
            // If the tableau is empty, only a King can be placed.
            return "King".equals(card.getValue());
        } else {
            Card topCard = peekTopCard();
            return !card.isSameColor(topCard) && card.isOneRankLower(topCard);
        }
    }

    private boolean isAlternatingColor(Card card, Card topCard) {
        return (isRed(card) && isBlack(topCard)) || (isBlack(card) && isRed(topCard));
    }

    private boolean isRed(Card card) {
        return "Hearts".equals(card.getSuit()) || "Diamonds".equals(card.getSuit());
    }

    private boolean isBlack(Card card) {
        return "Clubs".equals(card.getSuit()) || "Spades".equals(card.getSuit());
    }

    private boolean isOneRankLower(Card card, Card topCard) {
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        int cardIndex = indexOf(card.getValue(), ranks);
        int topCardIndex = indexOf(topCard.getValue(), ranks);
        return cardIndex + 1 == topCardIndex;
    }

    private int indexOf(String value, String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) return i;
        }
        return -1;
    }
}