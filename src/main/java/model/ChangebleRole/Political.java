package model.ChangebleRole;

import java.util.ArrayList;

import model.Cards.CardsArray.Card;
import model.Game.PlayerModel;
import model.Observers.ActObservers;
import model.Observers.ActionObserver;
import model.Observers.ObserversPublicAccess;

abstract class ChangebleRole {
    private PlayerModel player;

    public void change(PlayerModel player) {
        this.player = player;
    }

    public PlayerModel getPlayer() {
        return this.player;
    }
}


public abstract class Political<R extends Enum<R>> extends ChangebleRole {
    private int currentRights[]; // -1 -> activated infinite count of use, 0 -> isnt activated, >=1 activated and has limited usage
    private ArrayList<Card> cards;
    private int cardsCount;

    private ActObservers<ArrayList<Card>> cardChoosenObservers;
    private ActObservers<ArrayList<Card>> cardAddingObservers;
    private ActObservers<R> powerChangesObserver;

    public Political(Class<R> rights, int cardsCount) {
        this.currentRights = new int[rights.getEnumConstants().length];
        this.cardsCount = cardsCount;

        this.cardAddingObservers = new ActObservers<>();
        this.cardChoosenObservers = new ActObservers<>();
        this.powerChangesObserver = new ActObservers<>();
    }

    public void giveCards(ArrayList<Card> cards) {
        System.out.println("Cards where given to political " + cards.size());

        if (cards.size() != this.cardsCount)
            System.out.println("Uncorrect cards count");
        else if (this.cards != null) {
            System.out.println("Trying to add cards while last cards wasnt deleted");
        } else {
            this.cards = cards;
            this.cardAddingObservers.informAll(cards);
        }
    }

    // if card is null then removes all cards
    public boolean chooseCardToRemove(Card card) {
        System.out.println("Card was removed");

        if (this.cards == null) return false;
        if (card == null) {
            this.cardChoosenObservers.informAll(null);
            return true;
        }

        if (!this.cards.remove(card)) {
            System.out.println("Removed card is not present in cards or presented twice");
            return false;
        }

        this.cardChoosenObservers.informAll(cards);
        this.cards = null;
        return true;
    }

    public ObserversPublicAccess<ActionObserver<ArrayList<Card>>> getCardChoosedObserver() {
        return this.cardChoosenObservers;
    }

    public ObserversPublicAccess<ActionObserver<ArrayList<Card>>> getCardAddingObserver() {
        return this.cardAddingObservers;
    }

    public ObserversPublicAccess<ActionObserver<R>> getPowerChangerObserver() {
        return this.powerChangesObserver;
    }

    public void expandPower(R newRight, int maxUsageCount) {
        this.currentRights[newRight.ordinal()] = maxUsageCount;
        this.powerChangesObserver.informAll(newRight);
    }

    public void lowerPower(R newRight) {
        this.currentRights[newRight.ordinal()] = 0;
        this.powerChangesObserver.informAll(newRight);
    }

    public boolean isRightActivated(R right) {
        return this.currentRights[right.ordinal()] != 0;
    }

    public int getRemainedRightUsage(R right) {
        return this.currentRights[right.ordinal()];
    }

    public void increaseRightUsage(R right, int increasment) {
        if (this.currentRights[right.ordinal()] != -1) {
            this.currentRights[right.ordinal()] += increasment;
            this.powerChangesObserver.informAll(right);
        }
    }

    public boolean tryUseRight(R right) {
        if (!this.isRightActivated(right)) {
            System.out.println("Trying to use political right that isnt activated");
            return false;
        } else {
            if (this.currentRights[right.ordinal()] != -1 && this.currentRights[right.ordinal()] != 0)
                this.currentRights[right.ordinal()]--;
            
            this.powerChangesObserver.informAll(right);
            return true;
        }
    }

    public boolean areCardsInHands() {
        return this.cards != null;
    }
}
