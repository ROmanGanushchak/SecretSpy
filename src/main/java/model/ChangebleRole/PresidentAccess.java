package model.ChangebleRole;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import model.Cards.CardsArray.Card;
import model.ChangebleRole.Right.ExecutionStatusWrapper;
import model.ChangebleRole.President.RightTypes;
import model.Game.PlayerModel;
import model.Observers.ActObserversAccess;

public interface PresidentAccess {
    public boolean isRightActivated(RightTypes right);

    default public PlayerModel getPlayer() {
        return null;
    }
    
    public EnumMap<President.RightTypes, Right> getCurrentRights();

    public ActObserversAccess<ArrayList<Card>> getCardChoosedObserver();
    public ActObserversAccess<ArrayList<Card>> getCardAddingObserver();
    public ActObserversAccess<Map.Entry<RightTypes, Right>> getPowerChangerObserver();
    public ActObserversAccess<Integer> getPlayerChangesObservers();
    public Object useRight(President.RightTypes rightType, ExecutionStatusWrapper executionResult, Object... parametrs);
    public boolean chooseCardToRemove(Integer card);

    public void expandPower(RightTypes newRight, int maxUsageCount);
    public void lowerPower(RightTypes newRight);
}
