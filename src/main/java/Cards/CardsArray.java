package Cards;

import java.util.ArrayList;

public class CardsArray {
    public static class Card {
        public static enum states {
            Undecleared,
            Spy,
            Liberal
        }

        public states state = states.Undecleared;
    }

    private int cardsCount;
    private ArrayList<Card> cards;

    public CardsArray(int cardsCount) {
        this.cardsCount = cardsCount;
        this.cards = new ArrayList<Card>();
        for (int i=0; i < cardsCount; i++) 
            this.cards.add(new Card());
    }

    public int getCardsCount() {
        return this.cardsCount;
    }

    public Card get(int index) {
        return this.cards.get(index);
    }

    public void set(int index, Card card) {
        this.cards.set(index, card);
    }

    public Card pop() {
        return this.cards.remove(--this.cardsCount);
    }

    public void shaffle (int liberalCount, int spyCount, int seed) {
        if (liberalCount + spyCount != cardsCount) 
            throw new RuntimeException("Uncorrect count of cards for filling the cards array");
        
        int index=0;
        for (int i=0; i<liberalCount; i++) this.cards.get(index++).state = Card.states.Liberal;
        for (int i=0; i<spyCount; i++) this.cards.get(index++).state = Card.states.Spy;
        
        ArrayShaffle.shuffle(this.cards, seed);
    }

    public void outputCards() {
        System.out.print(this.cardsCount + " ");
        for (int i=0; i<this.cardsCount; i++) {
            System.out.print(this.cards.get(i).state + " ");
        }System.out.print("\n");
    }
}
