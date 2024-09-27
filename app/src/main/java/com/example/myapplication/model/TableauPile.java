package com.example.myapplication.model;

import android.util.Log;
import java.util.List;
import java.util.ArrayList;

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
    public List<Card> getFaceUpCardsFrom(Card card) {
        List<Card> faceUpCards = new ArrayList<>();

        // Find the index of the card in the tableau pile
        int cardIndex = cards.indexOf(card);

        // If the card is not found, return an empty list
        if (cardIndex == -1) {
            return faceUpCards;
        }

        // Loop through all cards from the given card onward
        for (int i = cardIndex; i < cards.size(); i++) {
            Card currentCard = cards.get(i);
            if (currentCard.isFaceUp()) {
                faceUpCards.add(currentCard);
            }
        }

        return faceUpCards;
    }
}