package model.ChangebleRole;

import java.util.ArrayList;

import model.Cards.CardsArray.Card;
import model.ChangebleRole.Chancellor.rights;
import model.Game.PlayerModel;
import model.Observers.ActionObserver;
import model.Observers.ObserversPublicAccess;

public interface ChancellorAccess {
    public void vetoPower();
    public PlayerModel getPlayer();

    public ObserversPublicAccess<ActionObserver<ArrayList<Card>>> getCardChoosedObserver();
    public ObserversPublicAccess<ActionObserver<ArrayList<Card>>> getCardAddingObserver();
    public ObserversPublicAccess<ActionObserver<rights>> getPowerChangerObserver();
    public boolean chooseCardToRemove(Card card);

    public void expandPower(rights newRight, int maxUsageCount);
    public void lowerPower(rights newRight);
}
