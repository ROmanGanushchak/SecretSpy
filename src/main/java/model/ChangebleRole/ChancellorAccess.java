package model.ChangebleRole;

import java.util.ArrayList;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.Chancellor.rights;
import model.ChangebleRole.Political.Right;
import model.Game.PlayerModel;
import model.Observers.ActObserversAccess;

public interface ChancellorAccess {
    public void vetoPower();
    public PlayerModel getPlayer();
    public Right<Chancellor.rights>[] getCurrentRights();

    public ActObserversAccess<ArrayList<Card>> getCardChoosedObserver();
    public ActObserversAccess<ArrayList<Card>> getCardAddingObserver();
    public ActObserversAccess<rights> getPowerChangerObserver();
    public ActObserversAccess<Integer> getPlayerChangesObservers();
    public boolean chooseCardToRemove(Card card);

    public void expandPower(rights newRight, int maxUsageCount);
    public void lowerPower(rights newRight);
}
