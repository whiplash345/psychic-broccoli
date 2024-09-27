package com.example.myapplication.spider_solitaire_model;

import android.util.Log;
import com.example.myapplication.R;

import java.util.HashMap;
import java.util.Map;

public class Card {
    public enum Suits {
        Spades;
    }

    public enum Ranks {
        ACE("aaceofspades"),
        TWO("a2ofspades"),
        THREE("a3ofspades"),
        FOUR("a4ofspades"),
        FIVE("a5ofspades"),
        SIX("a6ofspades"),
        SEVEN("a7ofspades"),
        EIGHT("a8ofspades"),
        NINE("a9ofspades"),
        TEN("a10ofspades"),
        JACK("ajackofspades"),
        QUEEN("aqueenofspades"),
        KING("akingofspades");

        private final String identifier;

        Ranks(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }
    }

    private final Suits suit;
    private final Ranks rank;
    private boolean isFaceUp;
    private final int id;  // Unique identifier for each card instance

    // Static map of drawable resources
    private static final Map<String, Integer> cardImageMap = new HashMap<>();

    static {
        cardImageMap.put(Ranks.ACE.getIdentifier(), R.drawable.aaceofspades);
        cardImageMap.put(Ranks.TWO.getIdentifier(), R.drawable.a2ofspades);
        cardImageMap.put(Ranks.THREE.getIdentifier(), R.drawable.a3ofspades);
        cardImageMap.put(Ranks.FOUR.getIdentifier(), R.drawable.a4ofspades);
        cardImageMap.put(Ranks.FIVE.getIdentifier(), R.drawable.a5ofspades);
        cardImageMap.put(Ranks.SIX.getIdentifier(), R.drawable.a6ofspades);
        cardImageMap.put(Ranks.SEVEN.getIdentifier(), R.drawable.a7ofspades);
        cardImageMap.put(Ranks.EIGHT.getIdentifier(), R.drawable.a8ofspades);
        cardImageMap.put(Ranks.NINE.getIdentifier(), R.drawable.a9ofspades);
        cardImageMap.put(Ranks.TEN.getIdentifier(), R.drawable.a10ofspades);
        cardImageMap.put(Ranks.JACK.getIdentifier(), R.drawable.ajackofspades);
        cardImageMap.put(Ranks.QUEEN.getIdentifier(), R.drawable.aqueenofspades);
        cardImageMap.put(Ranks.KING.getIdentifier(), R.drawable.akingofspades);
    }

    public Card(Suits suit, Ranks rank, int id) {
        this.suit = suit;
        this.rank = rank;
        this.isFaceUp = false;
        this.id = id;  // Assign unique ID
    }

    public Suits getSuit() {
        return suit;
    }

    public Ranks getRank() {
        return rank;
    }

    public void toggleFaceUp() {
        isFaceUp = !isFaceUp;
    }

    public boolean isFaceUp() {
        return isFaceUp;
    }

    public int getId() {
        return id; // Getter for unique ID
    }

    public static Map<String, Integer> getCardImageMap() {
        return cardImageMap;
    }

    public void setFaceUp(boolean faceUp) {
        isFaceUp = faceUp;
    }

    public static int getCardImageResource(Card card) {
        if (cardImageMap == null) {
            Log.e("CardImage", "cardImageMap is null");
            return R.drawable.spiderback; // Return default image if map is null
        }

        String key = card.getRank().getIdentifier();
        Log.d("CardImage", "Looking for card image with key: " + key);

        // Return the card image from the map, or a default image if the key is not found
        return cardImageMap.getOrDefault(key, R.drawable.spiderback);
    }
}

