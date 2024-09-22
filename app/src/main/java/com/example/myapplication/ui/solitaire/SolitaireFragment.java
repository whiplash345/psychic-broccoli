package com.example.myapplication.ui.solitaire;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.*;
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
import com.example.myapplication.model.Pile;
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

        // Access MainActivity and the TextToSpeech instance
        MainActivity mainActivity = (MainActivity) getActivity();
        TextToSpeech tts = mainActivity.getTextToSpeech();

        // Observe the TTS enabled state
        boolean isTtsEnabled = solitaireViewModel.getIsTtsEnabled().getValue() != null && solitaireViewModel.getIsTtsEnabled().getValue();

        // Set card overlap value and margins
        int verticalOverlap = 50; // Adjust for card overlap
        int verticalOffset = getScaledHeight() - 200; // Adjust positioning
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int tableauColumnWidth = screenWidth / 7; // Divide width into 7 columns

        // Render piles
        addStockPile(solitaireBoard, isLarge);
        addWastePile(solitaireBoard, isLarge);
        addFoundationPiles(solitaireBoard, isLarge);

        // Render tableau piles
        for (int i = 0; i < tableauPiles.size(); i++) {
            TableauPile tableauPile = tableauPiles.get(i);
            FrameLayout tableauLayout = createTableauLayout(i, tableauColumnWidth, verticalOffset);
            addCardsToTableau(tableauLayout, tableauPile, isLarge, isTtsEnabled, tts);

            // Add drag listeners for moving cards to tableau
            addTableauPileDragListeners(tableauLayout, tableauPile);

            solitaireBoard.addView(tableauLayout);
        }
    }

    private FrameLayout createTableauLayout(int columnIndex, int tableauColumnWidth, int verticalOffset) {
        FrameLayout tableauLayout = new FrameLayout(getContext());
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();

        layoutParams.columnSpec = GridLayout.spec(columnIndex); // Column 0-6
        layoutParams.rowSpec = GridLayout.spec(1); // Row 1
        layoutParams.width = tableauColumnWidth; // Equal width for each column
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.topMargin = verticalOffset;

        tableauLayout.setLayoutParams(layoutParams);
        return tableauLayout;
    }

    private void addCardsToTableau(FrameLayout tableauLayout, TableauPile tableauPile, Boolean isLarge, boolean isTtsEnabled, TextToSpeech tts) {
        for (int j = 0; j < tableauPile.getCards().size(); j++) {
            Card card = tableauPile.getCards().get(j);

            // Create ImageView for the card
            ImageView cardView = createCardView(card, isLarge);
            FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(getScaledWidth(), getScaledHeight());
            cardParams.topMargin = j * 35; // Adjust card overlap

            cardView.setLayoutParams(cardParams);

            // Set up card click for TTS (if enabled and card is face-up)
            if (card.isFaceUp() && isTtsEnabled) {
                cardView.setOnClickListener(v -> tts.speak(card.getValue() + " of " + card.getSuit(), TextToSpeech.QUEUE_FLUSH, null, null));
            }

            tableauLayout.addView(cardView);
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
            // Reset stock pile if stock is empty and waste is not empty
            if (!wastePile.isEmpty()) {
                resetStockFromWaste();
            }
        }
    }

    private void resetStockFromWaste() {
        while (!wastePile.isEmpty()) {
            stockPile.push(wastePile.pop());
        }
        solitaireViewModel.setStockPile(stockPile);
        renderBoard(solitaireBoard, solitaireViewModel.getIsLargeCard().getValue());
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

            // Set OnTouchListener to start drag event when card is touched
            wastePileView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        // Create a drag shadow and initiate drag event
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(wastePileView);
                        view.startDrag(null, shadowBuilder, wastePile.peek(), 0); // Pass the top card as localState
                        return true;
                    }
                    return false;
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

            // Add drag listeners for moving cards to foundation
            addFoundationPileDragListeners(foundationPileView, foundationPile);

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

    public boolean canMoveToTableauPile(Card card, TableauPile tableauPile) {
        // Check if the tableau pile can accept the card
        return tableauPile.canAddCard(card);
    }

    public boolean canMoveToFoundationPile(Card card, FoundationPile foundationPile) {
        // Check if the foundation pile can accept the card
        return foundationPile.canAddCard(card);
    }

    public boolean moveCardToTableau(TableauPile targetPile, Pile sourcePile) {
        // Check if source pile has any cards
        if (sourcePile.isEmpty()) {
            return false;
        }

        // Get the top card of the source pile
        Card cardToMove = sourcePile.peekTopCard();

        // Check if the card can be added to the tableau pile
        if (targetPile.canAddCard(cardToMove)) {
            // Remove the card from the source pile and add it to the target pile
            targetPile.addCard(sourcePile.removeCard());
            return true;
        }

        return false; // Move is invalid
    }

    public boolean moveCardToFoundation(FoundationPile targetPile, Pile sourcePile) {
        // Check if source pile has any cards
        if (sourcePile.isEmpty()) {
            return false;
        }

        // Get the top card of the source pile
        Card cardToMove = sourcePile.peekTopCard();

        // Check if the card can be added to the foundation pile
        if (canMoveToFoundationPile(cardToMove, targetPile)) {
            // Remove the card from the source pile and add it to the target pile
            targetPile.addCard(sourcePile.removeCard());
            return true;
        }

        return false; // Move is invalid
    }

    public boolean moveCardFromWaste(Pile wastePile, TableauPile tableauPile) {
        // Check if waste pile has any cards
        if (wastePile.isEmpty()) {
            return false;
        }

        // Get the top card of the waste pile
        Card cardToMove = wastePile.peekTopCard();

        // Check if the card can be moved to the tableau pile
        if (tableauPile.canAddCard(cardToMove)) {
            // Move the card from waste to tableau
            tableauPile.addCard(wastePile.removeCard());
            return true;
        }

        return false; // Move is invalid
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

        // Set OnTouchListener to start drag event when card is touched
        cardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Create drag shadow and initiate drag event
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(cardView);
                    view.startDrag(null, shadowBuilder, card, 0);
                    return true;
                }
                return false;
            }
        });

        return cardView;
    }

    private Pile getSourcePileForCard(Card card) {
        // Check the tableau piles
        for (TableauPile tableauPile : tableauPiles) {
            if (tableauPile.containsCard(card)) {
                return tableauPile;
            }
        }

        // Check the waste pile (now a Stack<Card>)
        if (wastePile.contains(card)) {
            // wastePile is not Pile type, so return null here
            // TODO: Revisit this part and see whether wastePile or getSourcePileForCard needs to be refactored to be a different type
            return null;
        }

        // If the card is not found, return null or handle the case accordingly
        return null;
    }

    private void addTableauPileDragListeners(FrameLayout tableauLayout, TableauPile tableauPile) {
        tableauLayout.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DROP:
                    Card draggedCard = (Card) event.getLocalState();

                    // Check if the move is valid
                    if (canMoveToTableauPile(draggedCard, tableauPile)) {
                        moveCardToTableau(tableauPile, getSourcePileForCard(draggedCard));
                        renderBoard(solitaireBoard, solitaireViewModel.getIsLargeCard().getValue());
                        return true;
                    }
                    return false;

                default:
                    return false;
            }
        });
    }

    private void addFoundationPileDragListeners(ImageView foundationPileView, FoundationPile foundationPile) {
        foundationPileView.setOnDragListener((v, event) -> {
            if (event.getAction() == DragEvent.ACTION_DROP) {
                Card draggedCard = (Card) event.getLocalState();
                if (canMoveToFoundationPile(draggedCard, foundationPile)) {
                    moveCardToFoundation(foundationPile, getSourcePileForCard(draggedCard));
                    renderBoard(solitaireBoard, solitaireViewModel.getIsLargeCard().getValue());
                    return true;
                }
            }
            return false;
        });
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