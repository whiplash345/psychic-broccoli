package com.example.myapplication.ui.solitaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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

    private SolitaireViewModel solitaireViewModel;
    private GridLayout solitaireBoard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_solitaire, container, false);

        // Initialize the shared SolitaireViewModel
        solitaireViewModel = new ViewModelProvider(requireActivity()).get(SolitaireViewModel.class);

        // Initialize the game board view
        solitaireBoard = root.findViewById(R.id.solitaireBoard);

        // Observe the card size state from the ViewModel
        solitaireViewModel.getIsLargeCard().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLarge) {
                updateCardSizes(isLarge); // Update all cards based on the size
            }
        });

        // Check if game data exists in the ViewModel
        if (solitaireViewModel.getTableauPiles() == null) {
            // If there's no saved state, initialize a new game
            initializeGameBoard();
        } else {
            // Restore the game state from the ViewModel
            tableauPiles = solitaireViewModel.getTableauPiles();  // Get from ViewModel
            stockPile = solitaireViewModel.getStockPile();        // Get from ViewModel
            wastePile = solitaireViewModel.getWastePile();        // Get from ViewModel
            foundationPiles = solitaireViewModel.getFoundationPiles();  // Get from ViewModel
            renderBoard(solitaireBoard, solitaireViewModel.getIsLargeCard().getValue());
        }

        return root;
    }

    private void initializeGameBoard() {
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
                    card.flip();  // Ensure the last card in the pile is face up
                }
                tableauPiles.get(i).addCard(card);
            }
        }

        stockPile.addAll(deck);

        // Save game state to ViewModel
        solitaireViewModel.setTableauPiles(tableauPiles);
        solitaireViewModel.setStockPile(stockPile);
        solitaireViewModel.setWastePile(wastePile);
        solitaireViewModel.setFoundationPiles(foundationPiles);

        renderBoard(solitaireBoard, solitaireViewModel.getIsLargeCard().getValue());
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

    private void renderBoard(GridLayout solitaireBoard, Boolean isLarge) {
        // Clear the board before rendering
        solitaireBoard.removeAllViews();

        // Set number of columns in the GridLayout
        solitaireBoard.setColumnCount(7);

        // Add tableau piles to the board
        for (int i = 0; i < tableauPiles.size(); i++) {
            TableauPile tableauPile = tableauPiles.get(i);

            for (int j = 0; j < tableauPile.getCards().size(); j++) {
                Card card = tableauPile.getCards().get(j);
                ImageView cardView = createCardView(card, isLarge);

                solitaireBoard.addView(cardView);
            }
        }
    }

    private ImageView createCardView(Card card, Boolean isLarge) {
        ImageView cardView = new ImageView(getContext());

        if (card.isFaceUp()) {
            int resId = getCardDrawableResource(card, isLarge);
            cardView.setImageResource(resId);
        } else {
            // Display the back of the card when it's face down
            cardView.setImageResource(R.drawable.cardsback);
        }

        return cardView;
    }

    private int getCardDrawableResource(Card card, Boolean isLarge) {
        String cardName;

        // For Ace, Jack, Queen, King, prefix with "a"
        if (card.getValue().matches("\\d+")) { // For numeric values
            cardName = "a" + card.getValue() + "of" + card.getSuit().toLowerCase();
        } else { // For face cards like Ace, Jack, Queen, King
            cardName = "a" + card.getValue().toLowerCase() + "of" + card.getSuit().toLowerCase();
        }

        // If large card is needed, append "large"
        if (isLarge) {
            cardName = cardName + "large";
        }

        int resId = getResources().getIdentifier(cardName, "drawable", getContext().getPackageName());

        return resId != 0 ? resId : R.drawable.cardsback;
    }

    private void updateCardSizes(boolean isLarge) {
        renderBoard(solitaireBoard, isLarge); // Re-render the board with updated card sizes
    }
}