package model.ChangebleRole;

import java.util.AbstractMap;
import java.util.ArrayList;
import model.Cards.CardsArray.Card;
import model.Game.PlayerModel;
import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;
import model.Observers.ActionObserver;
import model.Observers.ObserversAccess;

abstract class ChangebleRole {
    private PlayerModel player;
    private ActObservers<Integer> playerChanges;

    public ChangebleRole() {
        this.playerChanges = new ActObservers<>();
    }

    public void change(PlayerModel player) {
        System.out.println("Political chanded");
        this.player = player;
        if (player != null)
            this.playerChanges.informAll(player.getId());
        else this.playerChanges.informAll(-1);
    }

    public PlayerModel getPlayer() {
        return this.player;
    }

    public ActObserversAccess<Integer> getPlayerChangesObservers() {
        return this.playerChanges;
    }
}


public abstract class Political<R extends Enum<R>> extends ChangebleRole {
    public static class Right<R> extends AbstractMap.SimpleEntry<R, Integer> {
        public Right(R r, Integer count) {
            super(r, count);
        }
    }

    private Right<R> currentRights[]; // -1 -> activated infinite count of use, 0 -> isnt activated, >=1 activated and has limited usage
    private ArrayList<Card> cards;
    private int cardsCount;

    private ActObservers<ArrayList<Card>> cardChoosenObservers;
    private ActObservers<ArrayList<Card>> cardAddingObservers;
    private ActObservers<R> powerChangesObserver;

    public Political(Class<R> rights, int cardsCount) {
        this.cardsCount = cardsCount;
        R[] constants = rights.getEnumConstants();

        this.currentRights = new Right[constants.length];
        for (int i=0; i < constants.length; i++) {
            currentRights[i] = new Right<R>(constants[i], 0);
        }

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

    public ActObserversAccess<ArrayList<Card>> getCardChoosedObserver() {
        return this.cardChoosenObservers;
    }

    public ActObserversAccess<ArrayList<Card>> getCardAddingObserver() {
        return this.cardAddingObservers;
    }

    public ActObserversAccess<R> getPowerChangerObserver() {
        return this.powerChangesObserver;
    }

    public void expandPower(R newRight, int maxUsageCount) {
        this.currentRights[newRight.ordinal()].setValue(maxUsageCount);
        this.powerChangesObserver.informAll(newRight);
    }

    public void lowerPower(R newRight) {
        this.currentRights[newRight.ordinal()].setValue(0);
        this.powerChangesObserver.informAll(newRight);
    }

    public boolean isRightActivated(R right) {
        return this.currentRights[right.ordinal()].getValue() != 0;
    }

    public int getRemainedRightUsage(R right) {
        return this.currentRights[right.ordinal()].getValue();
    }

    public void increaseRightUsage(R right, int increasment) {
        if (this.currentRights[right.ordinal()].getValue() != -1) {

            this.currentRights[right.ordinal()].setValue(
                increasment + this.currentRights[right.ordinal()].getValue());
            
            this.powerChangesObserver.informAll(right);
        }
    }

    public boolean tryUseRight(R right) {
        if (!this.isRightActivated(right)) {
            System.out.println("Trying to use political right that isnt activated");
            return false;
        } else {
            if (this.currentRights[right.ordinal()].getValue() != -1 && this.currentRights[right.ordinal()].getValue() != 0)
                this.currentRights[right.ordinal()].setValue(this.currentRights[right.ordinal()].getValue()-1);
            
            this.powerChangesObserver.informAll(right);
            return true;
        }
    }

    public Right<R>[] getCurrentRights() {
        return this.currentRights;
    }

    public boolean areCardsInHands() {
        return this.cards != null;
    }
}
