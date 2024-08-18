package com.example.myapplication.model;

import java.util.Stack;

public class Pile {
    protected Stack<Card> cards;

    public Pile() {
        cards = new Stack<>();
    }

    public void addCard(Card card) {
        cards.push(card);
    }

    public Card removeCard() {
        return cards.isEmpty() ? null : cards.pop();
    }

    public Card peekTopCard() {
        return cards.isEmpty() ? null : cards.peek();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public Stack<Card> getCards() {
        return cards;
    }
}