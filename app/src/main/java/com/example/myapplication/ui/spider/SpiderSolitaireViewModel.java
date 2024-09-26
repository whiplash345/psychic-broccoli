package com.example.myapplication.ui.spider;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SpiderSolitaireViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isTtsEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLargeCard = new MutableLiveData<>(false);

    public LiveData<Boolean> getIsTtsEnabled() {
        return isTtsEnabled;
    }

    public void setTtsEnabled(boolean isEnabled) {
        isTtsEnabled.setValue(isEnabled);
    }

    public LiveData<Boolean> getIsLargeCard() {
        return isLargeCard;
    }

    public void setCardSizeLarge(boolean isLarge) {
        isLargeCard.setValue(isLarge);
    }
}
//TODO implement view model once game board is rendered to save data
