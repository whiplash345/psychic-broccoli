package com.example.myapplication.ui.solitaire;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.myapplication.model.Card;
import com.example.myapplication.model.FoundationPile;
import com.example.myapplication.model.TableauPile;

import java.util.List;
import java.util.Stack;

public class SolitaireViewModel extends ViewModel {
    // Existing game state
    private List<TableauPile> tableauPiles;
    private Stack<Card> stockPile;
    private Stack<Card> wastePile;
    private List<FoundationPile> foundationPiles;

    // Card size state
    private final MutableLiveData<Boolean> isLargeCard = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isTtsEnabled = new MutableLiveData<>(false);

    // Getters and Setters for game state
    public List<TableauPile> getTableauPiles() {
        return tableauPiles;
    }

    public void setTableauPiles(List<TableauPile> tableauPiles) {
        this.tableauPiles = tableauPiles;
    }

    public Stack<Card> getStockPile() {
        return stockPile;
    }

    public void setStockPile(Stack<Card> stockPile) {
        this.stockPile = stockPile;
    }

    public Stack<Card> getWastePile() {
        return wastePile;
    }

    public void setWastePile(Stack<Card> wastePile) {
        this.wastePile = wastePile;
    }

    public List<FoundationPile> getFoundationPiles() {
        return foundationPiles;
    }

    public void setFoundationPiles(List<FoundationPile> foundationPiles) {
        this.foundationPiles = foundationPiles;
    }

    // Getters and setters for card size state
    public void setCardSizeLarge(boolean isLarge) {
        isLargeCard.setValue(isLarge);
    }

    public LiveData<Boolean> getIsLargeCard() {
        return isLargeCard;
    }
    public LiveData<Boolean> getIsTtsEnabled() {
        return isTtsEnabled;
    }

    public void setTtsEnabled(boolean isEnabled) {
        isTtsEnabled.setValue(isEnabled);
    }
}