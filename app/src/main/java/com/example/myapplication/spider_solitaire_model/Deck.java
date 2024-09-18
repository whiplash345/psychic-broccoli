package com.example.myapplication.spider_solitaire_model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    public List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        // Add four of each card rank for the spades suit
        for (Card.Ranks rank : Card.Ranks.values()) {
            // Add four of each card rank
            for (int i = 0; i < 4; i++) {
                cards.add(new Card(Card.Suits.Spades, rank));
            }
        }
        Shuffle();
    }
    public void Shuffle() {
        Collections.shuffle(cards);
    }
    public Card DrawCard(){
        if (!cards.isEmpty()) {
            return cards.remove(cards.size() - 1);
        } else {
            return null;
        }
    }
    public boolean IsEmpty() {
        return cards.isEmpty();
    }

}
