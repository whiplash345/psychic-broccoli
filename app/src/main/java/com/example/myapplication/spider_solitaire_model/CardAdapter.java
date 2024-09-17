package com.example.myapplication.spider_solitaire_model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Map;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.PileViewHolder> {

    private ArrayList<ArrayList<Card>> boardPiles;
    private static Map<String, Integer> cardImageMap;  // Map containing card images

    public CardAdapter(ArrayList<ArrayList<Card>> boardPiles, Map<String, Integer> cardImageMap) {
        this.boardPiles = boardPiles;
        this.cardImageMap = cardImageMap;  // Initialize with the card image map
    }

    @NonNull
    @Override
    public PileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_spider, parent, false);
        return new PileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PileViewHolder holder, int position) {
        ArrayList<Card> pile = boardPiles.get(position);
        holder.bindPile(pile);
    }

    @Override
    public int getItemCount() {
        return boardPiles.size();
    }

    static class PileViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout pileLayout;

        public PileViewHolder(@NonNull View itemView) {
            super(itemView);
            pileLayout = itemView.findViewById(R.id.gameBoard);  // FrameLayout for the pile
        }

        public void bindPile(ArrayList<Card> pile) {
            pileLayout.removeAllViews();  // Clear any previous card views

            for (Card card : pile) {
                ImageView cardView = new ImageView(pileLayout.getContext());

                pileLayout.addView(cardView);  // Add the card view to the pile
            }
        }
    }
}
