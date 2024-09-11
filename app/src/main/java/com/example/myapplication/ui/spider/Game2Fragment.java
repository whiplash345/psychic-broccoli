package com.example.myapplication.ui.spider;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.myapplication.R;
import com.example.myapplication.spider_solitaire_model.Card;
import com.example.myapplication.spider_solitaire_model.Deck;
import java.io.PrintStream;
import java.util.ArrayList;


public class Game2Fragment extends Fragment {

    Button PlayButton;
    Deck deck; // Your deck object
    ArrayList<ArrayList<Card>> boardPiles; // Each pile is a list of cards

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spider, container, false);
        PlayButton = view.findViewById(R.id.play);

        // Initialize the deck and board piles when PlayButton is clicked
        PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup layout = (ViewGroup) PlayButton.getParent();
                if (layout != null) {
                    layout.removeView(PlayButton);
                }

                startNewGame();
            }
        });

        return view;
    }

    private void startNewGame() {
        deck = new Deck(); // Initialize and shuffle the deck
        boardPiles = new ArrayList<>(); // Initialize the list of piles

        // Deal the cards to the 10 piles
        dealCardsToBoard();

        // Now you need to display the piles on the board (UI setup, e.g., RecyclerView or GridView)
        displayBoard();
    }

    private void dealCardsToBoard() {
        // Spider Solitaire has 10 piles: 4 piles with 6 cards and 6 piles with 5 cards
        for (int i = 0; i < 10; i++) {
            ArrayList<Card> pile = new ArrayList<>();
            int cardsInPile = (i < 4) ? 6 : 5; // First 4 piles get 6 cards, the rest get 5 cards

            // Draw cards from the deck and add them to the pile
            for (int j = 0; j < cardsInPile; j++) {
                Card card = deck.DrawCard();
                if (card != null) {
                    pile.add(card);
                }
            }
            boardPiles.add(pile); // Add the pile to the board
        }
    }

    private void displayBoard() {
        // This is where you would update your UI to display the piles and cards
        // You can use a RecyclerView or manually update the layout with ImageViews for cards
    }
}