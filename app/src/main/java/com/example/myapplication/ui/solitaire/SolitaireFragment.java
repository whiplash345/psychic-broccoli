package com.example.myapplication.ui.solitaire;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;
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

        // Distribute cards to tableau piles
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j <= i; j++) {
                Card card = deck.remove(0);
                // Pass false because we're in initialization phase
                tableauPiles.get(i).addCard(card, false);
            }
            Card topCard = tableauPiles.get(i).peekTopCard();
            if (!topCard.isFaceUp()) {
                topCard.flip();
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
        // Clear the solitaire board to avoid duplicating views
        solitaireBoard.removeAllViews();

        // Access MainActivity and TextTo-Speech instance
        MainActivity mainActivity = (MainActivity) getActivity();
        TextToSpeech tts = mainActivity.getTextToSpeech();

        if (tts == null) {
            Log.e("SolitaireFragment", "TTS is null!");
        } else {
            Log.d("SolitaireFragment", "TTS retrieved successfully.");
        }

        boolean isTtsEnabled = solitaireViewModel.getIsTtsEnabled().getValue() != null && solitaireViewModel.getIsTtsEnabled().getValue();

        // Add the stock pile (column 0, row 0)
        addStockPile(solitaireBoard, isLarge);

        // Add the waste pile (column 1, row 0)
        addWastePile(solitaireBoard, isLarge);

        // Add the foundation piles (columns 3-6, row 0)
        addFoundationPiles(solitaireBoard, isLarge);

        // Set vertical offset to move tableau piles below stock/waste/foundation piles
        int verticalOffset = getScaledHeight() - 200; // Adjust as needed

        // Get total screen width to distribute columns evenly
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int tableauColumnWidth = screenWidth / 7; // Divide width into 7 columns

        // Render tableau piles
        for (int i = 0; i < tableauPiles.size(); i++) {
            TableauPile tableauPile = tableauPiles.get(i);
            FrameLayout tableauLayout = new FrameLayout(getContext());

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.columnSpec = GridLayout.spec(i);
            layoutParams.rowSpec = GridLayout.spec(1);
            layoutParams.width = tableauColumnWidth;
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.topMargin = verticalOffset;

            tableauLayout.setLayoutParams(layoutParams);

            // Add cards to the tableau
            addCardsToTableau(tableauLayout, tableauPile, isLarge, isTtsEnabled, tts);

            // Call `addTableauPileDragListeners` here
            addTableauPileDragListeners(tableauLayout, tableauPile);

            solitaireBoard.addView(tableauLayout);
        }
    }
/*
    private FrameLayout createTableauLayout(int columnIndex, int tableauColumnWidth, int verticalOffset) {
        FrameLayout tableauLayout = new FrameLayout(getContext());
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();

        layoutParams.columnSpec = GridLayout.spec(columnIndex); // Column 0-6
        layoutParams.rowSpec = GridLayout.spec(1); // Row 1 (for tableau)
        layoutParams.width = getScaledWidth(); // Keep consistent with foundation pile size
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT; // Allow height to adjust based on content
        layoutParams.topMargin = verticalOffset; // Adjust for card overlap
        layoutParams.leftMargin = 0; // No left margin to prevent shifting

        tableauLayout.setLayoutParams(layoutParams);

        // Set the same background as foundation piles for consistency
        tableauLayout.setBackgroundResource(R.drawable.backgroundtransparent);

        return tableauLayout;
    }
*/
private void addCardsToTableau(FrameLayout tableauLayout, TableauPile tableauPile, Boolean isLarge, boolean isTtsEnabled, TextToSpeech tts) {
    // Clear existing views in the layout to avoid overlaps or duplication
    tableauLayout.removeAllViews();

    // Size calculations: ensure the background and cards have consistent sizes
    int scaledWidth = getScaledWidth(); // Same width as foundation piles
    int scaledHeight = getScaledHeight(); // Same height as foundation piles

    // Case 1: The tableau pile is empty
    if (tableauPile.isEmpty()) {
        // Create and configure the background for an empty tableau pile
        ImageView emptyPileBackground = new ImageView(getContext());
        emptyPileBackground.setImageResource(R.drawable.backgroundtransparent); // Use correct background resource

        // Set layout parameters for positioning at the top of the tableau pile
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(scaledWidth, scaledHeight);
        layoutParams.topMargin = 0; // Ensure it stays at the top
        emptyPileBackground.setLayoutParams(layoutParams);

        // Add the background to the tableau layout
        tableauLayout.addView(emptyPileBackground);

    } else {
        // Case 2: The tableau pile has cards, so render them

        // Loop through each card in the pile and add it to the layout
        for (int j = 0; j < tableauPile.getCards().size(); j++) {
            Card card = tableauPile.getCards().get(j);

            // Create the card view (now passing the tableauPile as the second argument)
            ImageView cardView = createCardView(card, tableauPile, isLarge);

            // Create layout parameters for the card, ensuring proper stacking (overlap)
            FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(scaledWidth, scaledHeight);
            cardParams.topMargin = j * 35; // Adjust to stack cards downward with overlap
            cardView.setLayoutParams(cardParams);

            // Set up Text-to-Speech (TTS) click listener if the card is face-up and TTS is enabled
            if (card.isFaceUp() && isTtsEnabled) {
                cardView.setOnClickListener(v -> tts.speak(card.getValue() + " of " + card.getSuit(), TextToSpeech.QUEUE_FLUSH, null, null));
            }

            // Add the card view to the tableau layout
            tableauLayout.addView(cardView);
        }
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
        Log.d("SolitaireFragment", "isTtsEnabled: " + isTtsEnabled);

        ImageView wastePileView = new ImageView(getContext());

        if (!wastePile.isEmpty()) {
            // Get the top card of the waste pile
            Card topCard = wastePile.peek();

            // Set the image for the top card
            wastePileView.setImageResource(getCardDrawableResource(topCard, isLarge));

            // Set OnTouchListener to handle both click and drag events
            wastePileView.setOnTouchListener(new View.OnTouchListener() {
                private long touchStartTime;
                private boolean isDragging = false;

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touchStartTime = System.currentTimeMillis(); // Record touch start time
                            isDragging = false; // Reset dragging flag
                            return true;

                        case MotionEvent.ACTION_UP:
                            long touchDuration = System.currentTimeMillis() - touchStartTime;
                            if (touchDuration < 200 && !isDragging) {
                                // This is a click, not a drag
                                if (topCard.isFaceUp() && isTtsEnabled) { // Only speak if the card is face-up and TTS is enabled
                                    String cardName = topCard.getValue() + " of " + topCard.getSuit();
                                    Log.d("SolitaireFragment", "Speaking card: " + cardName);
                                    if (tts != null) {
                                        tts.speak(cardName, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            }
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            long moveDuration = System.currentTimeMillis() - touchStartTime;
                            if (moveDuration > 200) {
                                // This is a drag event
                                isDragging = true;
                                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(wastePileView);
                                view.startDragAndDrop(null, shadowBuilder, wastePile.peek(), 0); // Pass the top card as localState
                                Log.d("SolitaireFragment", "Dragging top card from waste pile: " + topCard.getValue() + " of " + topCard.getSuit());
                            }
                            return true;

                        default:
                            return false;
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
        Log.d("SolitaireFragment", "Attempting to move card: " + card.getValue() + " to tableau pile.");
        return tableauPile.canAddCard(card);
    }

    public boolean canMoveToFoundationPile(Card card, FoundationPile foundationPile) {
        // If the foundation pile is empty, only allow Aces
        if (foundationPile.isEmpty()) {
            return "Ace".equals(card.getValue());
        } else {
            // If the pile is not empty, check if the card can be added in ascending order and same suit
            Card topCard = foundationPile.peekTopCard();
            return card.isOneRankHigher(topCard) && card.getSuit().equals(topCard.getSuit());
        }
    }

    private boolean moveCardToTableau(TableauPile targetPile, Pile sourcePile) {
        // Check if the source pile has any cards
        if (sourcePile.isEmpty()) {
            Log.d("SolitaireFragment", "Source pile is empty after moving card.");
            return false;
        }

        // Get the top card of the source pile
        Card cardToMove = sourcePile.peekTopCard();
        Log.d("SolitaireFragment", "Attempting to move " + cardToMove.getValue() + " of " + cardToMove.getSuit() + " from source pile.");

        // Check if the card can be added to the tableau pile
        if (targetPile.canAddCard(cardToMove)) {
            // Remove the card from the source pile and add it to the target pile
            targetPile.addCard(sourcePile.removeCard());
            Log.d("SolitaireFragment", "Card " + cardToMove.getValue() + " moved to tableau pile.");

            // Check if the card that was just moved is already face-up
            if (!cardToMove.isFaceUp()) {
                cardToMove.flip();  // Flip to face-up if it was face-down
                Log.d("SolitaireFragment", "Flipped card to face-up: " + cardToMove.getValue() + " of " + cardToMove.getSuit());
            }

            // After moving, check if the next top card in the source pile needs to be flipped
            if (!sourcePile.isEmpty()) {
                Card topCard = sourcePile.peekTopCard();
                if (!topCard.isFaceUp()) {
                    topCard.flip();  // Flip the newly revealed top card of the source pile to face-up
                    Log.d("SolitaireFragment", "Flipped new top card of source pile: " + topCard.getValue() + " of " + topCard.getSuit());
                }
            }

            // Re-render the board to reflect changes
            renderBoard(solitaireBoard, solitaireViewModel.getIsLargeCard().getValue());
            return true;
        }

        return false;  // Move is invalid
    }

    public boolean moveCardToFoundation(FoundationPile targetPile, Pile sourcePile) {
        // Check if the source pile has any cards
        if (sourcePile.isEmpty()) {
            return false;
        }

        // Get the top card of the source pile
        Card cardToMove = sourcePile.peekTopCard();
        Log.d("SolitaireFragment", "Attempting to move " + cardToMove.getValue() + " of " + cardToMove.getSuit() + " to foundation.");

        // Check if the card can be added to the foundation pile
        if (canMoveToFoundationPile(cardToMove, targetPile)) {
            // Move the card from the source pile to the foundation
            targetPile.addCard(sourcePile.removeCard());
            Log.d("SolitaireFragment", "Moved " + cardToMove.getValue() + " to foundation pile.");

            // If there are more cards in the source pile, flip the next card if needed
            if (!sourcePile.isEmpty()) {
                Card topCard = sourcePile.peekTopCard();
                if (!topCard.isFaceUp()) {
                    topCard.flip();
                    Log.d("SolitaireFragment", "Flipped top card of source pile: " + topCard.getValue() + " of " + topCard.getSuit());
                }
            }

            // Re-render the board to reflect the changes
            renderBoard(solitaireBoard, solitaireViewModel.getIsLargeCard().getValue());

            // Check if the player has won the game
            if (checkWinCondition()) {
                // Congratulate the player on winning
                congratulatePlayer();
            }

            return true;
        }

        Log.d("SolitaireFragment", "Move to foundation failed. Cannot place " + cardToMove.getValue() + " on foundation pile.");
        return false;
    }

/*
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
    */


    private ImageView createCardView(Card card, TableauPile tableauPile, Boolean isLarge) {
        ImageView cardView = new ImageView(getContext());

        // Set the card image (face-up or face-down)
        if (card.isFaceUp()) {
            int resId = getCardDrawableResource(card, isLarge);
            cardView.setImageResource(resId);
        } else {
            cardView.setImageResource(R.drawable.cardsback);
        }

        // Get TTS enabled state
        boolean isTtsEnabled = solitaireViewModel.getIsTtsEnabled().getValue() != null && solitaireViewModel.getIsTtsEnabled().getValue();
        Log.d("SolitaireFragment", "isTtsEnabled: " + isTtsEnabled);

        // Set OnTouchListener to differentiate between click and drag
        cardView.setOnTouchListener(new View.OnTouchListener() {
            private long touchStartTime;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchStartTime = System.currentTimeMillis(); // Record the time when touch starts
                        return true;

                    case MotionEvent.ACTION_UP:
                        long touchDuration = System.currentTimeMillis() - touchStartTime;
                        if (touchDuration < 200) {
                            // This is a click, not a drag
                            if (card.isFaceUp() && isTtsEnabled) {
                                String cardName = card.getValue() + " of " + card.getSuit();
                                Log.d("SolitaireFragment", "Speaking card: " + cardName); // Log the card to be spoken
                                MainActivity mainActivity = (MainActivity) getActivity();
                                TextToSpeech tts = mainActivity.getTextToSpeech();
                                if (tts != null) {
                                    tts.speak(cardName, TextToSpeech.QUEUE_FLUSH, null, null);
                                }
                            }
                        }
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        long moveDuration = System.currentTimeMillis() - touchStartTime;
                        if (moveDuration > 200) {
                            // This is a drag event
                            if (tableauPile.getFaceUpCardsFrom(card).size() == 1) {
                                // Single card drag
                                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(cardView);
                                view.startDragAndDrop(null, shadowBuilder, card, 0);
                                Log.d("SolitaireFragment", "Dragging single card: " + card.getValue() + " of " + card.getSuit());
                            } else {
                                // Group of cards drag
                                List<Card> faceUpCards = tableauPile.getFaceUpCardsFrom(card);
                                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(cardView);
                                view.startDragAndDrop(null, shadowBuilder, faceUpCards, 0);
                                Log.d("SolitaireFragment", "Dragging group of cards from " + card.getValue());
                            }
                        }
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
                Log.d("SolitaireFragment", "Card found in tableau pile.");
                return tableauPile;
            }
        }

        // Check if the card is in the waste pile
        if (!wastePile.isEmpty() && wastePile.peek().equals(card)) {
            Log.d("SolitaireFragment", "Card found in waste pile.");
            return new Pile() {
                @Override
                public Card removeCard() {
                    return wastePile.pop();
                }

                @Override
                public boolean isEmpty() {
                    return wastePile.isEmpty();
                }

                @Override
                public Card peekTopCard() {
                    return wastePile.peek();
                }
            };
        }

        Log.d("SolitaireFragment", "Source pile for card not found.");
        return null;
    }

    private void addTableauPileDragListeners(FrameLayout tableauLayout, TableauPile tableauPile) {
        tableauLayout.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DROP:
                    Object draggedObject = event.getLocalState();

                    // Handle the case where the dragged object is a list of cards (a sequence of face-up cards)
                    if (draggedObject instanceof List) {
                        List<Card> draggedCards = (List<Card>) draggedObject;
                        Card firstCard = draggedCards.get(0); // The first card in the sequence

                        // If tableau pile is empty, only allow a King to be dropped
                        if (tableauPile.isEmpty()) {
                            if ("King".equals(firstCard.getValue())) {
                                // Move the group of cards to the empty tableau pile
                                moveCardsToTableau(tableauPile, getSourcePileForCard(firstCard), draggedCards);
                                renderBoard(solitaireBoard, solitaireViewModel.getIsLargeCard().getValue());
                                return true;
                            } else {
                                return false; // Only Kings can be placed in an empty tableau pile
                            }
                        }

                        // If tableau pile is not empty, check if the sequence can be added
                        if (canMoveToTableauPile(firstCard, tableauPile)) {
                            // Move the group of cards to the tableau pile
                            moveCardsToTableau(tableauPile, getSourcePileForCard(firstCard), draggedCards);
                            renderBoard(solitaireBoard, solitaireViewModel.getIsLargeCard().getValue());
                            return true;
                        }

                        return false;
                    }

                    // Handle the case where the dragged object is a single card (like from the waste pile)
                    if (draggedObject instanceof Card) {
                        Card draggedCard = (Card) draggedObject;

                        // If tableau pile is empty, only allow a King to be dropped
                        if (tableauPile.isEmpty()) {
                            if ("King".equals(draggedCard.getValue())) {
                                moveCardToTableau(tableauPile, getSourcePileForCard(draggedCard));
                                renderBoard(solitaireBoard, solitaireViewModel.getIsLargeCard().getValue());
                                return true;
                            } else {
                                return false; // Only Kings can be placed in an empty tableau pile
                            }
                        }

                        // If tableau pile is not empty, check if the card can be added
                        if (canMoveToTableauPile(draggedCard, tableauPile)) {
                            moveCardToTableau(tableauPile, getSourcePileForCard(draggedCard));
                            renderBoard(solitaireBoard, solitaireViewModel.getIsLargeCard().getValue());
                            return true;
                        }

                        return false;
                    }

                    return false;

                default:
                    return false;
            }
        });
    }



    private void moveCardsToTableau(TableauPile targetPile, Pile sourcePile, List<Card> cardsToMove) {
        // Move each card to the target pile
        for (Card card : cardsToMove) {
            Log.d("SolitaireFragment", "Moving card: " + card.getValue() + " of " + card.getSuit() + " Face up: " + card.isFaceUp());
            // Only flip the card if it's face down; ensure we don't re-flip cards
            if (!card.isFaceUp()) {
                card.flip();  // Flip to face up only if needed
                Log.d("SolitaireFragment", "Flipping card: " + card.getValue() + " of " + card.getSuit());
            }
            targetPile.addCard(card);  // Add the card to the target pile
        }

        // Remove cards from the source pile
        for (int i = 0; i < cardsToMove.size(); i++) {
            sourcePile.removeCard();
        }

        // After moving, check if we need to flip the new top card of the source pile
        if (!sourcePile.isEmpty()) {
            Card newTopCard = sourcePile.peekTopCard();
            if (!newTopCard.isFaceUp()) {
                newTopCard.flip();  // Flip the newly revealed top card of the source pile
                Log.d("SolitaireFragment", "Flipped new top card: " + newTopCard.getValue() + " of " + newTopCard.getSuit());
            }
        }
    }

    private void addFoundationPileDragListeners(ImageView foundationPileView, FoundationPile foundationPile) {
        foundationPileView.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d("SolitaireFragment", "Drag started on foundation pile.");
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d("SolitaireFragment", "Drag entered foundation pile.");
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    Log.d("SolitaireFragment", "Drag exited foundation pile.");
                    return true;

                case DragEvent.ACTION_DROP:
                    Log.d("SolitaireFragment", "Attempting to drop on foundation pile.");
                    Object draggedObject = event.getLocalState();

                    // Check if the dragged object is a single card
                    if (draggedObject instanceof Card) {
                        Card draggedCard = (Card) draggedObject;
                        Log.d("SolitaireFragment", "Trying to drop " + draggedCard.getValue() + " of " + draggedCard.getSuit() + " on foundation pile.");

                        // Check if the card can be added to the foundation pile
                        if (canMoveToFoundationPile(draggedCard, foundationPile)) {
                            boolean moved = moveCardToFoundation(foundationPile, getSourcePileForCard(draggedCard));
                            Log.d("SolitaireFragment", "Move to foundation " + (moved ? "succeeded" : "failed"));

                            // Re-render the board
                            renderBoard(solitaireBoard, solitaireViewModel.getIsLargeCard().getValue());
                            return moved;
                        } else {
                            Log.d("SolitaireFragment", "Move failed: card cannot be placed on foundation.");
                        }
                        return false;
                    }

                    // Prevent dropping a sequence of cards onto a foundation pile
                    if (draggedObject instanceof List) {
                        Log.e("SolitaireFragment", "Error: Cannot drop a list of cards onto a foundation pile.");
                        return false;
                    }

                    return false;

                case DragEvent.ACTION_DRAG_ENDED:
                    Log.d("SolitaireFragment", "Drag ended on foundation pile.");
                    return true;

                default:
                    return false;
            }
        });
    }

    private boolean checkWinCondition() {
        // Check if all foundation piles are complete
        for (FoundationPile foundationPile : foundationPiles) {
            if (foundationPile.getCards().size() != 13) {
                return false; // Not all foundation piles are complete
            }
        }

        // Check if all tableau piles, stock, and waste piles are empty
        for (TableauPile tableauPile : tableauPiles) {
            if (!tableauPile.isEmpty()) {
                return false; // A tableau pile is not empty
            }
        }

        if (!stockPile.isEmpty() || !wastePile.isEmpty()) {
            return false; // Either the stock or waste pile is not empty
        }

        // The player has won the game
        return true;
    }

    private void congratulatePlayer() {
        // Show a toast message
        Toast.makeText(getContext(), "Congratulations! You've won the game!", Toast.LENGTH_LONG).show();

        // Use Text-to-Speech (TTS) if enabled
        boolean isTtsEnabled = solitaireViewModel.getIsTtsEnabled().getValue() != null && solitaireViewModel.getIsTtsEnabled().getValue();
        MainActivity mainActivity = (MainActivity) getActivity();
        TextToSpeech tts = mainActivity.getTextToSpeech();

        if (isTtsEnabled && tts != null) {
            tts.speak("Congratulations! You've won the game!", TextToSpeech.QUEUE_FLUSH, null, null);
        }
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