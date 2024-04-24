package model.ChangebleRole;

import java.util.ArrayList;
import java.util.EnumMap;

import model.Cards.CardsArray.Card;
import model.ChangebleRole.President.RightTypes;
import model.Game.PlayerModel;
import model.Observers.ActObserversAccess;

public interface PresidentAccess {
    public boolean isRightActivated(RightTypes right);
    public PlayerModel getPlayer();
    public EnumMap<President.RightTypes, Political.Right> getCurrentRights();

    public ActObserversAccess<ArrayList<Card>> getCardChoosedObserver();
    public ActObserversAccess<ArrayList<Card>> getCardAddingObserver();
    public ActObserversAccess<RightTypes> getPowerChangerObserver();
    public ActObserversAccess<Integer> getPlayerChangesObservers();
    public Object useRight(President.RightTypes right, Object... parametrs);
    public boolean chooseCardToRemove(Integer card);

    public void expandPower(RightTypes newRight, int maxUsageCount);
    public void lowerPower(RightTypes newRight);
}
