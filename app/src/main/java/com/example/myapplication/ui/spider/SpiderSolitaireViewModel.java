package com.example.myapplication.ui.spider;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.myapplication.spider_solitaire_model.Card;

import java.util.ArrayList;

public class SpiderSolitaireViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isTtsEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<ArrayList<ArrayList<Card>>> boardPilesLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Card>> mainDeckLiveData = new MutableLiveData<>();

    public LiveData<Boolean> getIsTtsEnabled() {
        return isTtsEnabled;
    }

    public void setTtsEnabled(boolean isEnabled) {
        isTtsEnabled.setValue(isEnabled);
    }

    public LiveData<ArrayList<ArrayList<Card>>> getBoardPilesLiveData() {
        return boardPilesLiveData;
    }

    public void setBoardPiles(ArrayList<ArrayList<Card>> boardPiles) {
        boardPilesLiveData.setValue(boardPiles);
    }

    public LiveData<ArrayList<Card>> getMainDeckLiveData() {
        return mainDeckLiveData;
    }

    public void setMainDeck(ArrayList<Card> mainDeck) {
        mainDeckLiveData.setValue(mainDeck);
    }
}
