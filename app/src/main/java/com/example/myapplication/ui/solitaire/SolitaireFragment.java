package com.example.myapplication.ui.solitaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        // Clear the board before rendering
        solitaireBoard.removeAllViews();

        // Declare amount of tableau piles
        int columnCount = 7;

        // Set number of columns in the GridLayout
        solitaireBoard.setColumnCount(columnCount);

        // Add tableau piles to the board
        for (int i = 0; i < tableauPiles.size(); i++) {
            TableauPile tableauPile = tableauPiles.get(i);

            for (int j = 0; j < tableauPile.getCards().size(); j++) {
                Card card = tableauPile.getCards().get(j); // Access the card
                ImageView cardView = createCardView(card);

                // Set layout parameters
                //GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                //params.columnSpec = GridLayout.spec(i);
                //params.rowSpec = GridLayout.spec(j);
                //cardView.setLayoutParams(params);

                // Add the card view to the GridLayout
                solitaireBoard.addView(cardView);
            }
        }
    }

    private ImageView createCardView(Card card) {
        ImageView cardView = new ImageView(getContext());

        if (card == null) {
            // TODO: Display a placeholder for when there is no card
            //cardView.setImageResource();
        } else if (card.isFaceUp()) {
            int resId = getCardDrawableResource(card);
            cardView.setImageResource(resId);
        } else {
            // TODO: Display the back of the card for when isFaceUp is false
            // cardView.setImageResource();
        }

        return cardView;
    }

    private int getCardDrawableResource(Card card) {
        // TODO: Map card suit and value to drawable resource IDs
        String suit = card.getSuit().toLowerCase();
        String value = card.getValue().toLowerCase();

        String resourceName = "card_" + value + "_of_" + suit + ".png"; // TODO: double check whether ".png" needs to be appended

        // TODO: look into getIdentifier being discouraged and getPackageName possibly throwing an NPE
        int resId = getResources().getIdentifier(resourceName, "drawable", getContext().getPackageName());

        return resId;
    }
}
