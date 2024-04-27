package model.ChangebleRole;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;

import model.Cards.CardsArray.Card;
import model.ChangebleRole.Right.ExecutionStatusWrapper;
import model.Game.PlayerModel;
import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;

abstract class ChangebleRole {
    private PlayerModel player;
    private ActObservers<Integer> playerChanges;

    public ChangebleRole() {
        this.playerChanges = new ActObservers<>();
    }

    public void change(PlayerModel player) {
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
    private EnumMap<R, Right> currentRights; // -1 -> activated infinite count of use, 0 -> isnt activated, >=1 activated and has limited usage
    private ArrayList<Card> cards;
    private int cardsCount;

    private ActObservers<ArrayList<Card>> cardChoosenObservers;
    private ActObservers<ArrayList<Card>> cardAddingObservers;
    private ActObservers<Map.Entry<R, Right>> powerChangesObserver;

    public Political(int cardsCount) {
        this.cardsCount = cardsCount;

        this.cardAddingObservers = new ActObservers<>();
        this.cardChoosenObservers = new ActObservers<>();
        this.powerChangesObserver = new ActObservers<>();
    }

    protected void initializeRights(EnumMap<R, Right> rights) {
        this.currentRights = rights;
        for (Map.Entry<R, Right> right : rights.entrySet()) {
            final R rightType = right.getKey();
            right.getValue().getUseCountChanges().subscribe( (Integer count) -> 
                this.powerChangesObserver.informAll(new AbstractMap.SimpleEntry<R,Right>(rightType, currentRights.get(rightType))));
        }
    }

    public Object useRight(R rightType, ExecutionStatusWrapper executionResult, Object... parametrs) {
        return currentRights.get(rightType).tryUseRight(executionResult, parametrs);
    }

    public void giveCards(ArrayList<Card> cards) {
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
    public boolean chooseCardToRemove(Integer card) {
        if (this.cards == null) return false;
        if (card == null) {
            this.cards = null;
            this.cardChoosenObservers.informAll(null);
            return true;
        }
        if (card > cards.size()) return false;

        this.cards.remove((int) card);

        this.cardChoosenObservers.informAll(cards);
        this.cards = null;
        return true;
    }

    public void expandPower(R newRight, int maxUsageCount) {
        this.currentRights.get(newRight).setUseCount(maxUsageCount);
        this.powerChangesObserver.informAll(new AbstractMap.SimpleEntry<>(newRight, this.currentRights.get(newRight)));
    }

    public void lowerPower(R newRight) {
        this.currentRights.get(newRight).setUseCount(0);
        this.powerChangesObserver.informAll(new AbstractMap.SimpleEntry<>(newRight, this.currentRights.get(newRight)));
    }

    public boolean isRightActivated(R right) {
        return this.currentRights.get(right).isActivate();
    }

    public int getRemainedRightUsage(R right) {
        return this.currentRights.get(right).getUseCount();
    }

    public void increaseRightUsage(R right, int increasment) {
        currentRights.get(right).changeUseCount(increasment);
    }

    public ActObserversAccess<ArrayList<Card>> getCardChoosedObserver() {
        return this.cardChoosenObservers;
    }

    public ActObserversAccess<ArrayList<Card>> getCardAddingObserver() {
        return this.cardAddingObservers;
    }

    public ActObserversAccess<Map.Entry<R, Right>> getPowerChangerObserver() {
        return this.powerChangesObserver;
    }

    public EnumMap<R, Right> getCurrentRights() {
        return this.currentRights;
    }

    public boolean areCardsInHands() {
        return this.cards != null;
    }

    public void setIsAllowedRightUsage(R right, boolean isAllowed) {
        if (this.currentRights.get(right).getIsAllowed() != isAllowed) {
            this.currentRights.get(right).setIsAllowed(isAllowed);
            powerChangesObserver.informAll(new AbstractMap.SimpleEntry<R, Right>(right, currentRights.get(right)));
        }
    }

    public void setAllRightsIsAllowed(boolean isAllowed, HashSet<R> exceptions) {
        for (R exeption : exceptions) {
            Right right = currentRights.get(exeption);
            if (right.getIsAllowed() != !isAllowed) {
                right.setIsAllowed(!isAllowed);
                powerChangesObserver.informAll(new AbstractMap.SimpleEntry<R, Right>(exeption, right));
            }
        }

        for (Map.Entry<R, Right> right : currentRights.entrySet()) {
            if (exceptions.contains(right.getKey()))
                continue;

            if (right.getValue().getIsAllowed() != isAllowed) {
                right.getValue().setIsAllowed(isAllowed);
                powerChangesObserver.informAll(new AbstractMap.SimpleEntry<R, Right>(right.getKey(), right.getValue()));
            }
        }
    }
}
