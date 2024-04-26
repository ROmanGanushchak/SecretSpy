package model.ChangebleRole;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import model.Cards.CardsArray.Card;
import model.ChangebleRole.Chancellor.RightTypes;
import model.Game.PlayerModel;
import model.Observers.ActObserversAccess;

public interface ChancellorAccess {
    public boolean isRightActivated(RightTypes right);
    public PlayerModel getPlayer();
    public EnumMap<RightTypes, Political.Right> getCurrentRights();

    public Object useRight(Chancellor.RightTypes right, Object... parametrs);
    public ActObserversAccess<ArrayList<Card>> getCardChoosedObserver();
    public ActObserversAccess<ArrayList<Card>> getCardAddingObserver();
    public ActObserversAccess<Map.Entry<RightTypes, Political.Right>> getPowerChangerObserver();
    public ActObserversAccess<Integer> getPlayerChangesObservers();
    public boolean chooseCardToRemove(Integer card);

    public void expandPower(RightTypes newRight, int maxUsageCount);
    public void lowerPower(RightTypes newRight);
}
