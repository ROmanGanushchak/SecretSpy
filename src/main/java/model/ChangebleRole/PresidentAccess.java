package model.ChangebleRole;

import java.util.ArrayList;
import java.util.Map;

import model.Cards.CardsArray.Card;
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
    public Map.Entry<President.rights, Integer>[] getCurrentRights();

    public ObserversAccess<ActionObserver<ArrayList<Card>>> getCardChoosedObserver();
    public ObserversAccess<ActionObserver<ArrayList<Card>>> getCardAddingObserver();
    public ObserversAccess<ActionObserver<rights>> getPowerChangerObserver();
    public boolean chooseCardToRemove(Card card);

    public void expandPower(rights newRight, int maxUsageCount);
    public void lowerPower(rights newRight);
}
