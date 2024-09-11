package com.example.myapplication.ui.settings;

import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {
    // Field to store the card size or other data
    private String cardSizeState;

    // Getter and Setter for the card size state
    public String getCardSizeState() {
        return cardSizeState;
    }

    public void setCardSizeState(String cardSizeState) {
        this.cardSizeState = cardSizeState;
    }
}