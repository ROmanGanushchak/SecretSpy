package model.Cards;

import java.util.ArrayList;

/** classes to store the deck of the card automaticly creates the array, shuffles it, and if cards count is lower the limit then recreates it */
public class CardsArray {
    /** the card type of the array */
    public static class Card {
        /** all posible card states */
        public static enum states {
            Undecleared,
            Spy,
            Liberal
        }

        /** currect state */
        public states state = states.Undecleared;
    }

    /** current cards count in the deck */
    private int cardsCount;
    /** cards in the deck*/
    private ArrayList<Card> cards;

    /** the minimum cards count in the deck */
    private int minimalCardCount;
    /** the initial count of spy cards */
    private int startSpyCardCount;
    /** the initial count of liberal cards */
    private int startLiberalCardCount;
    /** the random seed to shuffle the deck */
    private int seed;

    public CardsArray(int spyCardCount, int liberalCardCount, int minimalCardCount, int seed) {
        this.startSpyCardCount = spyCardCount;
        this.startLiberalCardCount = liberalCardCount;
        this.seed = seed;
        this.minimalCardCount = minimalCardCount;
        
        shaffle();
    }

    /**
     * returns the count of cards in the deck
     * @return the count of cards in the deck
     */
    public int getCardsCount() {
        return this.cardsCount;
    }

    /**
     * returns the array of upper cards
     * @param count cards count
     * @return the array of upper cards
     */
    public Card[] revealUpperCards(int count) {
        Card cards[] = new Card[count];
        for (int i=this.cards.size()-1; i>=Math.max(0, this.cards.size()-count); i--) {
            cards[this.cards.size()-1 - i] = this.cards.get(i);
        }

        return cards;
    }

    /**
     * pops the upper card, if needed recreates the deck
     * @return the upper card
     */
    public Card pop() {
        Card card = this.cards.remove(--this.cardsCount);

        if (this.cardsCount < this.minimalCardCount)
            shaffle();

        return card;
    }

    /**
     * pops the upper cards, if needed recreates the deck
     * @param count The count of cards that will be poped
     * @return the upper cards
     */
    public ArrayList<Card> popUppers(int count) {
        int length = Math.min(count, this.cardsCount);
        ArrayList<Card> cards = new ArrayList<Card>(count);
        for (int i=0; i<length; i++) cards.add(this.cards.remove(--this.cardsCount));

        if (this.cardsCount < this.minimalCardCount)
            shaffle(); 

        return cards;
    }

    /**shuffles the deck */
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

    /** outputs the cards */
    public void outputCards() {
        System.out.print(this.cardsCount + " ");
        for (int i=0; i<this.cardsCount; i++) {
            System.out.print(this.cards.get(i).state + " ");
        }System.out.print("\n");
    }
}
