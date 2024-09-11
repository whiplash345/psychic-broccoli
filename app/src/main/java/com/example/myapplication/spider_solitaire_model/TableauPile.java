package com.example.myapplication.spider_solitaire_model;

import java.util.ArrayList;
import java.util.List;

    public class TableauPile {
        private List<List<Card>> columns;

        public TableauPile(List<List<Card>> columns) {
            columns = new ArrayList<List<Card>>();

            for (int i = 0; i < 10; i++) {
                columns.add(new ArrayList<>());
            }
        }

        public void dealInitialCards(Deck deck) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 10; j++) {
                    Card card = deck.DrawCard();
                    if (card != null) {
                        columns.get(j).add(card);
                    }
                }
            }
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 4; j++) {
                    Card card = deck.DrawCard();
                    if (card != null) {
                        columns.get(j).add(card);
                    }
                }
            }
        }

        public void printTableau() {
            for (int i = 0; i < columns.size(); i++) {
                System.out.println("Column " + (i + 1) + ": " + columns.get(i));
            }
        }
    }
