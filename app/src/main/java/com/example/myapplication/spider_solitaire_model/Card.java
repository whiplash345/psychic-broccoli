package com.example.myapplication.spider_solitaire_model;

public class Card {
    public enum Suits{
        Spades;
    }

    public enum Ranks{
        Ace, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King;
    }

    private final Suits suit;
    private final Ranks rank;
    private boolean IsFaceUp;

    public Card(Suits suit, Ranks rank) {
        this.suit = suit;
        this.rank = rank;
        IsFaceUp = false;
    }

    public Suits getSuit() {
        return suit;
    }
    public Ranks getRank() {
        return rank;
    }
    public boolean isFaceUp() {
        return IsFaceUp;
    }
    public void setFaceUp(boolean faceUp) {
        IsFaceUp = faceUp;
    }

}
