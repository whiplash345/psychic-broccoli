package com.example.myapplication.spider_solitaire_model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final ArrayList<Card> cards; // Using ArrayList for the collection of cards
    private int cardCounter; // Unique ID counter

    public Deck() {
        cards = new ArrayList<>();
        cardCounter = 0; // Initialize counter

        // Add eight of each card rank for the spades suit
        for (Card.Ranks rank : Card.Ranks.values()) {
            for (int i = 0; i < 8; i++) {
                cards.add(new Card(Card.Suits.Spades, rank, cardCounter++)); // Use counter for unique ID
            }
        }
        shuffle();
    }

    public ArrayList<Card> getCards() {
        return cards; // Return the list of cards
    }

    public void shuffle() {
        Collections.shuffle(cards); // Shuffle the deck
    }

    public Card drawCard() {
        if (!cards.isEmpty()) {
            return cards.remove(cards.size() - 1); // Draw the top card
        } else {
            return null; // Return null if the deck is empty
        }
    }

    public boolean isEmpty() {
        return cards.isEmpty(); // Check if the deck is empty
    }

    public int size() {
        return cards.size(); // Get the number of remaining cards
    }
}

