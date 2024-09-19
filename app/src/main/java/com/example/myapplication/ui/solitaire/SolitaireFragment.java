package com.example.myapplication.ui.solitaire;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import com.example.myapplication.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class SolitaireFragment extends Fragment {

    private static final int ORIGINAL_WIDTH = 147;  // Original card width
    private static final int ORIGINAL_HEIGHT = 229; // Original card height

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

    private int getScaledWidth() {
        return (int) (ORIGINAL_WIDTH * 1.15); // Adjust this value as needed
    }

    private int getScaledHeight() {
        return (int) (ORIGINAL_HEIGHT * 1.15); // Adjust this value as needed
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
        String[] values = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};
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

        // Add the stock pile (column 0, row 0)
        addStockPile(solitaireBoard, isLarge);

        // Add the waste pile (column 1, row 0)
        addWastePile(solitaireBoard, isLarge);

        // Access MainActivity to get the TextToSpeech instance
        MainActivity mainActivity = (MainActivity) getActivity();
        TextToSpeech tts = mainActivity.getTextToSpeech();

        // Observe the TTS enabled state
        boolean isTtsEnabled = solitaireViewModel.getIsTtsEnabled().getValue() != null && solitaireViewModel.getIsTtsEnabled().getValue();

        // Set the vertical overlap between cards. Reduce this value to increase overlap.
        int verticalOverlap = 50; // Adjust this value to control the overlap

        // Add the four foundation piles (columns 3-6, row 0)
        addFoundationPiles(solitaireBoard, isLarge);

        // Set vertical offset to move tableau piles below stock/waste/foundation piles
        int verticalOffset = getScaledHeight() - 200; // Adjust as needed

        // Get the total width of the screen to distribute columns evenly
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        // Calculate an equal width for each tableau pile (dividing by 7 columns)
        int tableauColumnWidth = screenWidth / 7;

        // Render tableau piles starting from column 0, but on the next row (row 1)
        for (int i = 0; i < tableauPiles.size(); i++) {
            TableauPile tableauPile = tableauPiles.get(i);

            // Create a FrameLayout for each tableau pile
            FrameLayout tableauLayout = new FrameLayout(getContext());
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();

            // Set each tableau pile in its respective column
            layoutParams.columnSpec = GridLayout.spec(i); // Place in column 0-6
            layoutParams.rowSpec = GridLayout.spec(1);    // Place in row 1

            // Set consistent width for each column
            layoutParams.width = tableauColumnWidth; // Each column has equal width
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;

            // Remove any left or right margins to avoid gaps
            layoutParams.leftMargin = 0;
            layoutParams.rightMargin = 0;

            // Add top margin to move the tableau piles down (from stock/waste/foundation piles)
            layoutParams.topMargin = verticalOffset;

            tableauLayout.setLayoutParams(layoutParams);

            // Add cards to FrameLayout, stacking them with overlapping
            for (int j = 0; j < tableauPile.getCards().size(); j++) {
                Card card = tableauPile.getCards().get(j);  // Get the current card

                // Create ImageView for the card
                ImageView cardView = createCardView(card, isLarge);

                // Set layout parameters for cardView to create overlap
                FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(
                        getScaledWidth(), getScaledHeight() // Ensure sizes are scaled
                );

                // Adjust overlapping margin for stacked cards
                cardParams.topMargin = j * 35; // Adjust overlap between cards
                cardView.setLayoutParams(cardParams);

                // Set OnClickListener for each card to speak its name (only if face-up)
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (card.isFaceUp() && isTtsEnabled) { // Only speak if the card is face-up and TTS is enabled
                            String cardName = card.getValue() + " of " + card.getSuit();
                            if (tts != null) {
                                tts.speak(cardName, TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                    }
                });

                // Add the card to the FrameLayout (the tableau pile)
                tableauLayout.addView(cardView);
            }

            // Add the FrameLayout (tableau pile) to the GridLayout
            solitaireBoard.addView(tableauLayout);
        }
    }

    private void addStockPile(GridLayout solitaireBoard, Boolean isLarge) {
        ImageView stockPileView = new ImageView(getContext());
        stockPileView.setImageResource(stockPile.isEmpty() ? R.drawable.backgroundtransparent : R.drawable.cardsback);

        int scaledWidth = getScaledWidth();
        int scaledHeight = getScaledHeight();

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = scaledWidth;
        layoutParams.height = scaledHeight;
        layoutParams.columnSpec = GridLayout.spec(0); // Place stock pile in column 0
        layoutParams.rowSpec = GridLayout.spec(0); // Place in row 0
        layoutParams.topMargin = 100; // Adjust top margin if necessary

        // Remove left margin to align it with the first tableau pile
        layoutParams.leftMargin = 0;

        stockPileView.setLayoutParams(layoutParams);
        stockPileView.setScaleType(ImageView.ScaleType.FIT_XY);

        // Handle click events on the stock pile
        stockPileView.setOnClickListener(view -> drawFromStock());

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

    private void addWastePile(GridLayout solitaireBoard, Boolean isLarge) {
        MainActivity mainActivity = (MainActivity) getActivity();
        TextToSpeech tts = mainActivity.getTextToSpeech();

        // Observe the TTS enabled state
        boolean isTtsEnabled = solitaireViewModel.getIsTtsEnabled().getValue() != null && solitaireViewModel.getIsTtsEnabled().getValue();

        ImageView wastePileView = new ImageView(getContext());

        if (!wastePile.isEmpty()) {
            // Get the top card of the waste pile
            Card topCard = wastePile.peek();

            // Set the image for the top card
            wastePileView.setImageResource(getCardDrawableResource(topCard, isLarge));

            // Set OnClickListener for TTS (only if the top card is face-up)
            wastePileView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (topCard.isFaceUp() && isTtsEnabled) { // Only speak if the card is face-up and TTS is enabled
                        String cardName = topCard.getValue() + " of " + topCard.getSuit();
                        if (tts != null) {
                            tts.speak(cardName, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    }
                }
            });

        } else {
            // If the waste pile is empty, show a transparent background
            wastePileView.setImageResource(R.drawable.backgroundtransparent);
        }

        // Set the dimensions and layout for the waste pile
        int scaledWidth = getScaledWidth();
        int scaledHeight = getScaledHeight();

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = scaledWidth;
        layoutParams.height = scaledHeight;
        layoutParams.columnSpec = GridLayout.spec(1); // Place waste pile in column 1
        layoutParams.rowSpec = GridLayout.spec(0);    // Place in row 0
        layoutParams.topMargin = 100; // Adjust top margin if necessary

        // Align with the stock pile
        layoutParams.leftMargin = 0;

        wastePileView.setLayoutParams(layoutParams);
        wastePileView.setScaleType(ImageView.ScaleType.FIT_XY);

        // Add the waste pile view to the solitaire board
        solitaireBoard.addView(wastePileView);
    }

    private void addFoundationPiles(GridLayout solitaireBoard, Boolean isLarge) {
        MainActivity mainActivity = (MainActivity) getActivity();
        TextToSpeech tts = mainActivity.getTextToSpeech();

        boolean isTtsEnabled = solitaireViewModel.getIsTtsEnabled().getValue() != null && solitaireViewModel.getIsTtsEnabled().getValue();

        for (int i = 0; i < foundationPiles.size(); i++) {
            FoundationPile foundationPile = foundationPiles.get(i);
            Card topCard = foundationPile.peekTopCard();

            // Create an ImageView for the top card of each foundation pile
            ImageView foundationPileView = new ImageView(getContext());

            // Check if there's a top card
            if (topCard != null) {
                // Set the image of the top card
                foundationPileView.setImageResource(getCardDrawableResource(topCard, isLarge));

                // Set OnClickListener for TTS without changing card's position
                foundationPileView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (topCard.isFaceUp() && isTtsEnabled) { // Only speak if the card is face-up and TTS is enabled
                            String cardName = topCard.getValue() + " of " + topCard.getSuit();
                            if (tts != null) {
                                tts.speak(cardName, TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                    }
                });
            } else {
                // If no card is present in the foundation pile, show a transparent background
                foundationPileView.setImageResource(R.drawable.backgroundtransparent);
            }

            // Set layout params for foundation piles
            int scaledWidth = getScaledWidth();
            int scaledHeight = getScaledHeight();

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = scaledWidth;
            layoutParams.height = scaledHeight;
            layoutParams.columnSpec = GridLayout.spec(3 + i); // Columns 3, 4, 5, 6
            layoutParams.rowSpec = GridLayout.spec(0);        // Row 0
            layoutParams.topMargin = 100; // Adjust top margin as necessary

            foundationPileView.setLayoutParams(layoutParams);
            foundationPileView.setScaleType(ImageView.ScaleType.FIT_XY);

            // Add the foundation pile view to the solitaire board
            solitaireBoard.addView(foundationPileView);
        }
    }

    private ImageView createCardView(Card card, Boolean isLarge) {
        ImageView cardView = new ImageView(getContext());

        int scaledWidth = getScaledWidth();
        int scaledHeight = getScaledHeight();

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
            // Display the back of the card when it's face down
            cardView.setImageResource(R.drawable.cardsback);
        }

        // Set onClick listener for flipping cards
        cardView.setOnClickListener(view -> {
            card.flip();
            cardView.setImageResource(card.isFaceUp() ? getCardDrawableResource(card, isLarge) : R.drawable.cardsback);
        });

        return cardView;
    }

    private int getCardDrawableResource(Card card, Boolean isLarge) {
        String value = card.getValue();
        String suit = card.getSuit();
        String cardName;

        // Handle special names for aces, jacks, queens, kings
        if ("Ace".equals(value)) {
            cardName = "aaceof" + suit.toLowerCase();
        } else if ("Jack".equals(value)) {
            cardName = "ajackof" + suit.toLowerCase();
        } else if ("Queen".equals(value)) {
            cardName = "aqueenof" + suit.toLowerCase();
        } else if ("King".equals(value)) {
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