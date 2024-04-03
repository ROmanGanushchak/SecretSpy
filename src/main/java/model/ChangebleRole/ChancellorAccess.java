package model.ChangebleRole;

import java.util.ArrayList;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.Chancellor.rights;
import model.ChangebleRole.Political.Right;
import model.Game.PlayerModel;
import model.Observers.ActionObserver;
import model.Observers.ObserversAccess;

public interface ChancellorAccess {
    public void vetoPower();
    public PlayerModel getPlayer();
    public Right<Chancellor.rights>[] getCurrentRights();

    public ObserversAccess<ActionObserver<ArrayList<Card>>> getCardChoosedObserver();
    public ObserversAccess<ActionObserver<ArrayList<Card>>> getCardAddingObserver();
    public ObserversAccess<ActionObserver<rights>> getPowerChangerObserver();
    public boolean chooseCardToRemove(Card card);
    public ObserversAccess<ActionObserver<Integer>> getPlayerChangesObservers();

    public void expandPower(rights newRight, int maxUsageCount);
    public void lowerPower(rights newRight);
}
