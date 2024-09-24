package com.example.myapplication.spider_solitaire_model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final ArrayList<Card> cards; // Changed from List<Card> to ArrayList<Card>

    public Deck() {
        cards = new ArrayList<>();
        // Add four of each card rank for the spades suit
        for (Card.Ranks rank : Card.Ranks.values()) {
            for (int i = 0; i < 8; i++) {
                cards.add(new Card(Card.Suits.Spades, rank));
            }
        }
        shuffle();
    }

    public ArrayList<Card> getCards() {
        return cards; // No need to cast anymore
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (!cards.isEmpty()) {
            return cards.remove(cards.size() - 1);
        } else {
            return null; // Or throw an exception
        }
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size(); // Optional: method to get the number of remaining cards
    }
}
