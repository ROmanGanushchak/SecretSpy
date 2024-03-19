package model.Cards;

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

    private int minimalCardCount;
    private int startSpyCardCount;
    private int startLiberalCardCount;

    private int seed;

    public CardsArray(int spyCardCount, int liberalCardCount, int minimalCardCount, int seed) {
        this.startSpyCardCount = spyCardCount;
        this.startLiberalCardCount = liberalCardCount;
        this.seed = seed;
        this.minimalCardCount = minimalCardCount;
        
        shaffle();
    }

    public int getCardsCount() {
        return this.cardsCount;
    }

    public Card[] revealUpperCards(int count) {
        Card cards[] = new Card[count];
        for (int i=this.cards.size()-1; i>=Math.max(0, this.cards.size()-count); i--) {
            cards[this.cards.size()-1 - i] = this.cards.get(i);
        }

        return cards;
    }

    public Card pop() {
        Card card = this.cards.remove(--this.cardsCount);

        if (this.cardsCount < this.minimalCardCount)
            shaffle();

        return card;
    }

    public ArrayList<Card> popUppers(int count) {
        int length = Math.min(count, this.cardsCount);
        ArrayList<Card> cards = new ArrayList<Card>(count);
        for (int i=0; i<length; i++) cards.add(this.cards.remove(--this.cardsCount));

        if (this.cardsCount < this.minimalCardCount)
            shaffle(); 

        return cards;
    }

    private void shaffle () {
        this.cardsCount = this.startSpyCardCount + this.startLiberalCardCount;

        this.cards = new ArrayList<Card>(this.cardsCount);
        for (int i=0; i < this.cardsCount; i++) 
            this.cards.add(new Card());
        
        int index=0;
        for (int i=0; i<this.startLiberalCardCount; i++) this.cards.get(index++).state = Card.states.Liberal;
        for (int i=0; i<this.startSpyCardCount; i++) this.cards.get(index++).state = Card.states.Spy;
        
        ArrayShaffle.shuffle(this.cards, seed);
        seed += 7;
        outputCards();
    }

    public void outputCards() {
        System.out.print(this.cardsCount + " ");
        for (int i=0; i<this.cardsCount; i++) {
            System.out.print(this.cards.get(i).state + " ");
        }System.out.print("\n");
    }
}
