package com.example.myapplication.ui.spider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.spider_solitaire_model.Card;
import com.example.myapplication.spider_solitaire_model.CardAdapter;
import com.example.myapplication.spider_solitaire_model.Deck;

import java.util.ArrayList;


public class Game2Fragment extends Fragment {

    private Button playButton;
    private Deck deck;
    private ArrayList<ArrayList<Card>> boardPiles;

    // Store references to the pile layouts
    private LinearLayout[] pileLayouts = new LinearLayout[10];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spider, container, false);

        playButton = view.findViewById(R.id.play);

        // Correctly reference each pile layout
        pileLayouts[0] = view.findViewById(R.id.pile1);
        pileLayouts[1] = view.findViewById(R.id.pile2);
        pileLayouts[2] = view.findViewById(R.id.pile3);
        pileLayouts[3] = view.findViewById(R.id.pile4);
        pileLayouts[4] = view.findViewById(R.id.pile5);
        pileLayouts[5] = view.findViewById(R.id.pile6);
        pileLayouts[6] = view.findViewById(R.id.pile7);
        pileLayouts[7] = view.findViewById(R.id.pile8);
        pileLayouts[8] = view.findViewById(R.id.pile9);
        pileLayouts[9] = view.findViewById(R.id.pile10);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup layout = (ViewGroup) playButton.getParent();
                if (layout != null) {
                    layout.removeView(playButton); // Remove the Play button after the game starts
                }
                startNewGame();
            }
        });

        return view;
    }

    private void startNewGame() {
        deck = new Deck();  // Initialize and shuffle the deck
        boardPiles = new ArrayList<>();  // Create the piles

        dealCardsToBoard();
        displayBoard();
    }

    private void dealCardsToBoard() {
        for (int i = 0; i < 10; i++) {
            ArrayList<Card> pile = new ArrayList<>();
            int cardsInPile = (i < 4) ? 6 : 5;  // First 4 piles get 6 cards, the rest get 5

            for (int j = 0; j < cardsInPile; j++) {
                Card card = deck.DrawCard();
                if (card != null) {
                    pile.add(card);
                }
            }
            boardPiles.add(pile);
        }
    }

    private void displayBoard() {
        for (int i = 0; i < pileLayouts.length; i++) {
            pileLayouts[i].removeAllViews();
            ArrayList<Card> pile = boardPiles.get(i);

            for (Card card : pile) {
                ImageView cardView = new ImageView(getContext());

                // Set the card image (front or back depending on the card state)
                if (card.isFaceUp()) {
                    cardView.setImageResource(Card.getCardImageResource(card));
                } else {
                    cardView.setImageResource(R.drawable.spiderback);
                }

                // Set the desired width and height for the card
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        dpToPx(70),  // Width in dp
                        dpToPx(110)  // Height in dp
                );
                layoutParams.setMargins(0, -80, 0, 0);  // Adjust margins to overlap cards
                cardView.setLayoutParams(layoutParams);

                pileLayouts[i].addView(cardView);
            }
        }
    }

    // Helper function to convert dp to pixels
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}