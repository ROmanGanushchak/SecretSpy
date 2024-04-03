package model.ChangebleRole;

import java.util.ArrayList;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.Political.Right;
import model.ChangebleRole.President.rights;
import model.Game.PlayerModel;
import model.Observers.ActionObserver;
import model.Observers.ObserversAccess;

public interface PresidentAccess {
    public boolean isRightActivated(rights right);
    public PlayerModel.mainRoles revealeTheRole(int playerID);
    public PlayerModel getPlayer();
    public boolean suggestingChancellor(int playerId);
    public Card[] checkingUpperThreeCards();
    public boolean choosingNextPresident(int playerId);
    public void killingPlayers(int playerId);
    public Right<President.rights>[] getCurrentRights();

    public ObserversAccess<ActionObserver<ArrayList<Card>>> getCardChoosedObserver();
    public ObserversAccess<ActionObserver<ArrayList<Card>>> getCardAddingObserver();
    public ObserversAccess<ActionObserver<rights>> getPowerChangerObserver();
    public boolean chooseCardToRemove(Card card);
    public ObserversAccess<ActionObserver<Integer>> getPlayerChangesObservers();

    public void expandPower(rights newRight, int maxUsageCount);
    public void lowerPower(rights newRight);
}
