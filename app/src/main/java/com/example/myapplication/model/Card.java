package com.example.myapplication.model;

public class Card {
    private final String suit;
    private final String value;
    private boolean faceUp;

    public Card(String suit, String value) {
        this.suit = suit;
        this.value = value;
        this.faceUp = false;
    }

    public String getSuit() {
        return suit;
    }

    public String getValue() {
        return value;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public void flip() {
        faceUp = !faceUp;
    }

    public boolean isSameColor(Card other) {
        boolean isRed = this.suit.equals("Hearts") || this.suit.equals("Diamonds");
        boolean otherIsRed = other.suit.equals("Hearts") || other.suit.equals("Diamonds");
        return isRed == otherIsRed;
    }

    public boolean isOneRankLower(Card other) {
        return this.getRankValue() == other.getRankValue() - 1;
    }

    public boolean isOneRankHigher(Card other) {
        return this.getRankValue() == other.getRankValue() + 1;
    }

    private int getRankValue() {
        switch (this.value) {
            case "Ace":
                return 1;
            case "2":
                return 2;
            case "3":
                return 3;
            case "4":
                return 4;
            case "5":
                return 5;
            case "6":
                return 6;
            case "7":
                return 7;
            case "8":
                return 8;
            case "9":
                return 9;
            case "10":
                return 10;
            case "Jack":
                return 11;
            case "Queen":
                return 12;
            case "King":
                return 13;
            default:
                throw new IllegalArgumentException("Invalid card value: " + this.value);
        }
    }
}

//blah