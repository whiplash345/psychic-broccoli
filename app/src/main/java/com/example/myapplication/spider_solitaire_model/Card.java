package com.example.myapplication.spider_solitaire_model;

import android.util.Log;
import com.example.myapplication.R;

import java.util.HashMap;
import java.util.Map;

public class Card {
    public enum Suits{
        Spades;
    }

    public enum Ranks{
        aaceofspades, a2ofspades, a3ofspades, a4ofspades, a5ofspades, a6ofspades, a7ofspades
        , aeightofspades, a9ofspades, a10ofspades, ajackofspades, aqueenofspades, akingofspades;
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
        IsFaceUp = true;
    }

    public static int getCardImageResource(Card card) {
        if (cardImageMap == null) {
            Log.e("CardImage", "cardImageMap is null");
            return R.drawable.spiderback; // Return default image if map is null
        }

        String key = card.getRank().name().toLowerCase(); // The rank already includes the suit

        Log.d("CardImage", "Looking for card image with key: " + key);

        // Return the card image from the map, or a default image if the key is not found
        return cardImageMap.getOrDefault(key, R.drawable.spiderback);
    }

}
