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

/** Class ChangebleRole provides the ability to store some current player and change it */
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

/** Class Political designed to controll the right-usagem and work with cards in a hand */
public abstract class Political<R extends Enum<R>> extends ChangebleRole {
    /** all rights that the political has */
    private EnumMap<R, Right> currentRights;
    /** cards that are now available */
    private ArrayList<Card> cards;
    /** the number of cards that the political has to get */
    private int cardsCount;

    /** the observer of the card being chosen */
    private ActObservers<ArrayList<Card>> cardChoosenObservers;
    /** the observer of the cards being gaven to political */
    private ActObservers<ArrayList<Card>> cardAddingObservers;
    /** the observers of the power changes */
    private ActObservers<Map.Entry<R, Right>> powerChangesObserver;

    public Political(int cardsCount) {
        this.cardsCount = cardsCount;

        this.cardAddingObservers = new ActObservers<>();
        this.cardChoosenObservers = new ActObservers<>();
        this.powerChangesObserver = new ActObservers<>();
    }

    /**
     * initialize the rights of the political
     * @param rights the rights of the political
     */
    protected void initializeRights(EnumMap<R, Right> rights) {
        this.currentRights = rights;
        for (Map.Entry<R, Right> right : rights.entrySet()) {
            final R rightType = right.getKey();
            right.getValue().getUseCountChanges().subscribe( (Integer count) -> 
                this.powerChangesObserver.informAll(new AbstractMap.SimpleEntry<R,Right>(rightType, currentRights.get(rightType))));
        }
    }

    /**
     * metohd to use the right, not guaranteed to be executed
     * @param rightType the type of the right that will be used
     * @param executionResult changes the objects status parametr towords the right execution status
     * @param parametrs all the parametrs that right needs
     * @return the result of the right
     */
    public Object useRight(R rightType, ExecutionStatusWrapper executionResult, Object... parametrs) {
        return currentRights.get(rightType).tryUseRight(executionResult, parametrs);
    }

    /**
     * gives cards to political
     * @param cards the cards that is given for the political, the cards count has to be the same as cardsCount
     */
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

    /**
     * chooses the card that will be removed, if the input is null then removes all cards. After the card removing the cards will desapear from the political
     * @param card index of the card that will be removed
     * @return the execution status
     */
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

    /**
     * allows the right usage
     * @param newRight right type
     * @param maxUsageCount the usage count of the right, if -1 then inginite
     */
    public void expandPower(R newRight, int maxUsageCount) {
        this.currentRights.get(newRight).setUseCount(maxUsageCount);
        this.powerChangesObserver.informAll(new AbstractMap.SimpleEntry<>(newRight, this.currentRights.get(newRight)));
    }

    /**
     * blocks the right usage
     * @param newRight right type
     */
    public void lowerPower(R newRight) {
        this.currentRights.get(newRight).setUseCount(0);
        this.powerChangesObserver.informAll(new AbstractMap.SimpleEntry<>(newRight, this.currentRights.get(newRight)));
    }

    /**
     * returns whether or not the roght is activated
     * @param right right type
     * @return true if right is acticated otherwise false
     */
    public boolean isRightActivated(R right) {
        return this.currentRights.get(right).isActivate();
    }

    /**
     * returns the remained right usage count
     * @param right right type
     * @return the remained right usage count
     */
    public int getRemainedRightUsage(R right) {
        return this.currentRights.get(right).getUseCount();
    }

    /**
     * increases the right usage
     * @param right right type
     * @param increasment how much the right will be increased
     */
    public void increaseRightUsage(R right, int increasment) {
        currentRights.get(right).changeUseCount(increasment);
    }

    /**
     * 
     * @return the observer of the card being chosen
     */
    public ActObserversAccess<ArrayList<Card>> getCardChoosedObserver() {
        return this.cardChoosenObservers;
    }

    /**
     * 
     * @return the observer of the card being added
     */
    public ActObserversAccess<ArrayList<Card>> getCardAddingObserver() {
        return this.cardAddingObservers;
    }

    /**
     * 
     * @return the observer of the power changes
     */
    public ActObserversAccess<Map.Entry<R, Right>> getPowerChangerObserver() {
        return this.powerChangesObserver;
    }

    /**
     * 
     * @return enum of all rights
     */
    public EnumMap<R, Right> getCurrentRights() {
        return this.currentRights;
    }

    /**
     * 
     * @return the cards that political has
     */
    public boolean areCardsInHands() {
        return this.cards != null;
    }

    /**
     * allowes and bans the right usage
     * @param right right type
     * @param isAllowed the value of the right is allowed
     */
    public void setIsAllowedRightUsage(R right, boolean isAllowed) {
        if (this.currentRights.get(right).getIsAllowed() != isAllowed) {
            this.currentRights.get(right).setIsAllowed(isAllowed);
            powerChangesObserver.informAll(new AbstractMap.SimpleEntry<R, Right>(right, currentRights.get(right)));
        }
    }

    /**
     * sets all rights isAllowe to a value, except the exeptions
     * @param isAllowed the value of is rights allowed
     * @param exceptions this rights will be setted to the opposite value
     */
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
