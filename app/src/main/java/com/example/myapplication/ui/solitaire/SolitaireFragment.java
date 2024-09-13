package com.example.myapplication.ui.solitaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import com.example.myapplication.R;
import com.example.myapplication.model.Card;
import com.example.myapplication.model.FoundationPile;
import com.example.myapplication.model.TableauPile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class SolitaireFragment extends Fragment {

    private List<TableauPile> tableauPiles;
    private Stack<Card> stockPile;
    private Stack<Card> wastePile;
    private List<FoundationPile> foundationPiles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_solitaire, container, false);

        // Example: Create an ImageView for a card and add it to the GridLayout
        GridLayout solitaireBoard = root.findViewById(R.id.solitaireBoard);

        // Create a sample card (e.g., Ace of Spades)
        Card sampleCard = new Card("Spades", "A");


        initializeGameBoard(root);
        return root;
    }

    private void initializeGameBoard(View root) {
        GridLayout solitaireBoard = root.findViewById(R.id.solitaireBoard);

        tableauPiles = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            tableauPiles.add(new TableauPile());
        }
        stockPile = new Stack<>();
        wastePile = new Stack<>();
        foundationPiles = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            foundationPiles.add(new FoundationPile());
        }

        List<Card> deck = createDeck();
        Collections.shuffle(deck);

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j <= i; j++) {
                Card card = deck.remove(0);
                if (j == i) {
                    card.flip();
                }
                tableauPiles.get(i).addCard(card);
            }
        }

        stockPile.addAll(deck);
        renderBoard(solitaireBoard);
    }

    private List<Card> createDeck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        List<Card> deck = new ArrayList<>();
        for (String suit : suits) {
            for (String value : values) {
                deck.add(new Card(suit, value));
            }
        }
        return deck;
    }

    private void renderBoard(GridLayout solitaireBoard) {
        // Clear previous card views
        solitaireBoard.removeAllViews();

        // Set how much to move the entire tableau pile (column) down
        int verticalOffset = 100; // Adjust this value as needed

        for (int i = 0; i < tableauPiles.size(); i++) {
            TableauPile tableauPile = tableauPiles.get(i);

            // Create a FrameLayout for each tableau pile
            FrameLayout tableauLayout = new FrameLayout(getContext());
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.columnSpec = GridLayout.spec(i);
            layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;

            // Add top margin to move the column downwards
            layoutParams.topMargin = verticalOffset;

            tableauLayout.setLayoutParams(layoutParams);

            // Add cards to FrameLayout, which will stack them
            for (int j = 0; j < tableauPile.getCards().size(); j++) {
                Card card = tableauPile.getCards().get(j);
                ImageView cardView = createCardView(card);

                // Stack cards with overlapping
                FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );

                // Adjust overlapping as needed
                cardParams.topMargin = j * 60;
                cardView.setLayoutParams(cardParams);

                tableauLayout.addView(cardView);
            }

            // Add the tableau pile to the GridLayout
            solitaireBoard.addView(tableauLayout);
        }
    }

    private ImageView createCardView(Card card) {
        ImageView cardView = new ImageView(getContext());

        if (card.isFaceUp()) {
            int resId = getCardDrawableResource(card);
            cardView.setImageResource(resId);
        } else {
            // Display the back of the card when it's face down
            cardView.setImageResource(R.drawable.cardsback);
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.flip();
                cardView.setImageResource(card.isFaceUp() ? getCardDrawableResource(card) : R.drawable.cardsback);
            }
        });

        return cardView;
    }

    private int getCardDrawableResource(Card card) {
        String value = card.getValue();
        String suit = card.getSuit();
        String cardName;

        // Handle special names for aces, jacks, queens, kings
        if ("A".equals(value)) {
            cardName = "aceof" + suit.toLowerCase();
        } else if ("J".equals(value)) {
            cardName = "jackof" + suit.toLowerCase();
        } else if ("Q".equals(value)) {
            cardName = "queenof" + suit.toLowerCase();
        } else if ("K".equals(value)) {
            cardName = "kingof" + suit.toLowerCase();
        } else {
            // Prefix 'a' for numbered cards (2-10)
            cardName = "a" + value + "of" + suit.toLowerCase();
        }

        // Get the drawable resource ID based on the card name
        int resId = getResources().getIdentifier(cardName, "drawable", getContext().getPackageName());

        // Return either the valid resId or the back of the card
        return resId != 0 ? resId : R.drawable.cardsback;
    }

    //Hi
}
