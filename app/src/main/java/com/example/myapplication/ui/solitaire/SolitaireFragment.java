package com.example.myapplication.ui.solitaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
            initializeGameBoard(root);
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

        // Add the stock pile (column 0)
        addStockPile(solitaireBoard);

        // Add the waste pile (column 1)
        addWastePile(solitaireBoard);

        // Set how much to move the entire tableau pile (column) down
        int verticalOffset = 500; // Adjust this value as needed

        // Start rendering tableau piles from column 2 onwards
        for (int i = 0; i < tableauPiles.size(); i++) {
            TableauPile tableauPile = tableauPiles.get(i);

            // Create a FrameLayout for each tableau pile
            FrameLayout tableauLayout = new FrameLayout(getContext());
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.columnSpec = GridLayout.spec(i + 2); // Start tableau piles at column 2
            layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;

            // Add top margin to move the column downwards
            layoutParams.topMargin = verticalOffset;

            tableauLayout.setLayoutParams(layoutParams);

            // Add cards to FrameLayout, which will stack them
            for (int j = 0; j < tableauPile.getCards().size(); j++) {
                Card card = tableauPile.getCards().get(j);
                ImageView cardView = createCardView(card, isLarge);

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

    private void addStockPile(GridLayout solitaireBoard) {
        ImageView stockPileView = new ImageView(getContext());

        // Set the image for the stock pile
        stockPileView.setImageResource(stockPile.isEmpty() ? R.drawable.backgroundtransparent : R.drawable.cardsback);

        // Set the size and position for the stock pile view
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.columnSpec = GridLayout.spec(0);  // Column 0 for stock pile
        layoutParams.topMargin = 50;  // Adjust as needed
        layoutParams.leftMargin = 50; // Adjust as needed
        stockPileView.setLayoutParams(layoutParams);

        // Handle click events on the stock pile
        stockPileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawFromStock();
            }
        });

        solitaireBoard.addView(stockPileView);
    }

    private void drawFromStock() {
        if (!stockPile.isEmpty()) {
            // Draw the top card from the stock pile
            Card drawnCard = stockPile.pop();
            drawnCard.flip(); // Flip it face up

            // Add the card to the waste pile
            wastePile.push(drawnCard);

            // Update the board (re-render)
            renderBoard(solitaireBoard, solitaireViewModel.getIsLargeCard().getValue());

            // Save the state to ViewModel
            solitaireViewModel.setStockPile(stockPile);
            solitaireViewModel.setWastePile(wastePile);
        } else {
            // TODO: If stock is empty, optionally reset the stock pile from the waste pile
        }
    }

    private void addWastePile(GridLayout solitaireBoard) {
        ImageView wastePileView = new ImageView(getContext());

        if (!wastePile.isEmpty()) {
            Card topCard = wastePile.peek();
            wastePileView.setImageResource(getCardDrawableResource(topCard, solitaireViewModel.getIsLargeCard().getValue()));
        } else {
            wastePileView.setImageResource(R.drawable.backgroundtransparent); // Empty waste pile image
        }

        // Set the size and position for the waste pile view
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.columnSpec = GridLayout.spec(1);  // Column 1 for waste pile
        layoutParams.topMargin = 50;  // Adjust as needed
        layoutParams.leftMargin = 50; // Adjust as needed
        wastePileView.setLayoutParams(layoutParams);

        solitaireBoard.addView(wastePileView);
    }

    private ImageView createCardView(Card card, Boolean isLarge) {
        ImageView cardView = new ImageView(getContext());

        // Original dimensions of the card (approximately 147x229)
        int originalWidth = 147;
        int originalHeight = 229;

        // Calculate the 50% size
        int scaledWidth = (int) (originalWidth * 0.5);
        int scaledHeight = (int) (originalHeight * 0.5);

        // Set the fixed size of the ImageView
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(scaledWidth, scaledHeight);
        cardView.setLayoutParams(layoutParams);

        // Set scale type to ensure the card is resized properly
        cardView.setScaleType(ImageView.ScaleType.FIT_XY);

        // Set the card image (face-up or face-down)
        if (card.isFaceUp()) {
            int resId = getCardDrawableResource(card, isLarge);
            cardView.setImageResource(resId);
        } else {
            cardView.setImageResource(R.drawable.cardsback);
        }

        // Apply scale to the ImageView
        cardView.setScaleX(0.5f); // Scale 50% horizontally
        cardView.setScaleY(0.5f); // Scale 50% vertically

        // Set onClick listener for flipping cards
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.flip();
                cardView.setImageResource(card.isFaceUp() ? getCardDrawableResource(card, isLarge) : R.drawable.cardsback);
            }
        });

        return cardView;
    }

    private int getCardDrawableResource(Card card, Boolean isLarge) {
        String value = card.getValue();
        String suit = card.getSuit();
        String cardName;

        // Handle special names for aces, jacks, queens, kings
        if ("A".equals(value)) {
            cardName = "aaceof" + suit.toLowerCase();
        } else if ("J".equals(value)) {
            cardName = "ajackof" + suit.toLowerCase();
        } else if ("Q".equals(value)) {
            cardName = "aqueenof" + suit.toLowerCase();
        } else if ("K".equals(value)) {
            cardName = "akingof" + suit.toLowerCase();
        } else {
            // Prefix 'a' for numbered cards (2-10)
            cardName = "a" + value + "of" + suit.toLowerCase();
        }
        if (isLarge) {
            cardName = cardName + "large";
        }

        // Get the drawable resource ID based on the card name
        int resId = getResources().getIdentifier(cardName, "drawable", getContext().getPackageName());

        // Return either the valid resId or the back of the card
        return resId != 0 ? resId : R.drawable.cardsback;
    }

    private void updateCardSizes(boolean isLarge) {
        renderBoard(solitaireBoard, isLarge); // Re-render the board with updated card sizes
    }
}
