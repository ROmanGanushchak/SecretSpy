package model.ChangebleRole;

import java.util.ArrayList;
import java.util.EnumMap;
import model.Cards.CardsArray.Card;
import model.Game.PlayerModel;
import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;
import model.Observers.ActObservers.MethodToCall;

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
    public static enum Request {
        None(Integer.class), 
        ChoosePlayer(Integer.class);

        private Class<?> type;
        Request(Class<?> type) {
            this.type = type;
        }

        public Class<?> getType() {
            return type;
        }
    }

    public static abstract class Right {
        private int useCount;
        private Request request;

        public Right(Request request) {
            this.request = request;
        }

        public int getUseCount() {
            return this.useCount;
        }

        public Request getRequest() {
            return this.request;
        }

        protected void increaseUseCount(int value) {
            if (useCount != -1)
                this.useCount += value;
        }

        public abstract Object execute(Object... params);
    }

    private EnumMap<R, Right> currentRights; // -1 -> activated infinite count of use, 0 -> isnt activated, >=1 activated and has limited usage
    private ArrayList<Card> cards;
    private int cardsCount;

    private ActObservers<ArrayList<Card>> cardChoosenObservers;
    private ActObservers<ArrayList<Card>> cardAddingObservers;
    private ActObservers<R> powerChangesObserver;

    public Political(int cardsCount) {
        this.cardsCount = cardsCount;

        this.cardAddingObservers = new ActObservers<>();
        this.cardChoosenObservers = new ActObservers<>();
        this.powerChangesObserver = new ActObservers<>();
    }

    protected void initializeRights(EnumMap<R, Right> rights) {
        this.currentRights = rights;
    }

    public Object useRight(R right, Object... parametrs) {
        return currentRights.get(right).execute(parametrs);
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
    public boolean chooseCardToRemove(Integer card) {
        System.out.println("Card was removed");

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
        this.currentRights.get(newRight).useCount = maxUsageCount;
        this.powerChangesObserver.informAll(newRight);
    }

    public void lowerPower(R newRight) {
        this.currentRights.get(newRight).useCount = 0;
        this.powerChangesObserver.informAll(newRight);
    }

    public boolean isRightActivated(R right) {
        return this.currentRights.get(right).useCount != 0;
    }

    public int getRemainedRightUsage(R right) {
        return this.currentRights.get(right).useCount;
    }

    public void increaseRightUsage(R right, int increasment) {
        if (this.currentRights.get(right).useCount != -1) {
            currentRights.get(right).useCount = increasment + currentRights.get(right).useCount;
            this.powerChangesObserver.informAll(right);
        }
    }

    protected boolean tryUseRight(R right) {
        if (this.isRightActivated(right)) {
            if (this.currentRights.get(right).useCount != -1 && this.currentRights.get(right).useCount != 0)
                this.currentRights.get(right).useCount--;
            
            this.powerChangesObserver.informAll(right);
            return true;
        }
        return false;
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

    public EnumMap<R, Right> getCurrentRights() {
        return this.currentRights;
    }

    public boolean areCardsInHands() {
        return this.cards != null;
    }

    public <T> boolean subscribeForCall(R right, MethodToCall<T> method) {
        

        return true;
    }
}
