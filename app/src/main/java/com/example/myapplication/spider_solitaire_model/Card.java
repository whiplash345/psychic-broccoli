package com.example.myapplication.spider_solitaire_model;

import com.example.myapplication.R;

import java.util.HashMap;
import java.util.Map;

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

    // Static map of drawable resources
    private static final Map<String, Integer> cardImageMap = new HashMap<>();

    //Luis, the card names changed. I had to do this to implement the large cards. I changed the face cards and aces names in your code
    //to match what I added. I changed some of your code below to fix it, but if this messes with any other code, I am not aware. Just an FYI.
    static {
        // Initialize the map with card images
        cardImageMap.put("a2ofspades", R.drawable.a2ofspades);
        cardImageMap.put("a3ofspades", R.drawable.a3ofspades);
        cardImageMap.put("a4ofspades", R.drawable.a4ofspades);
        cardImageMap.put("a5ofspades", R.drawable.a5ofspades);
        cardImageMap.put("a6ofspades", R.drawable.a6ofspades);
        cardImageMap.put("a7ofspades", R.drawable.a7ofspades);
        cardImageMap.put("a8ofspades", R.drawable.a8ofspades);
        cardImageMap.put("a9ofspades", R.drawable.a9ofspades);
        cardImageMap.put("a10ofspades", R.drawable.a10ofspades);
        cardImageMap.put("ajackofspades", R.drawable.ajackofspades);
        cardImageMap.put("aqueenofspades", R.drawable.aqueenofspades);
        cardImageMap.put("akingofspades", R.drawable.akingofspades);
        cardImageMap.put("aaceofspades", R.drawable.aaceofspades);
    }

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

    public static int getCardImageResource(Card card) {
        String key = card.getSuit().name().toLowerCase() + "_" + card.getRank().name().toLowerCase();
        return cardImageMap.getOrDefault(key, R.drawable.spiderback);  // Return card back if not found
    }

}
