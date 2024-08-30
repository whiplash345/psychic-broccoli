package com.example.myapplication.spider_solitaire_model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    public List<Card> cards;

    public Deck() {
        cards = new ArrayList<Card>();
        for (Card.Suits suit : Card.Suits.values()) {
            for (Card.Ranks rank : Card.Ranks.values()) {
                cards.add(new Card(suit, rank));
                cards.add(new Card(suit, rank)); // Spider Solitaire uses 2 decks
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
