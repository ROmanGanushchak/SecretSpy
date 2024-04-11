package PlayerGameManager;

import java.util.ArrayList;
import GameController.GameControllerVisualService;
import User.UserData;
import model.Cards.CardsArray;
import model.ChangebleRole.Political.Right;
import model.ChangebleRole.President;
import model.Voting.Voting;

public interface PlayerGameManager {
    public UserData.VisualData getVisualData();
    public int getPlayerID();

    public void setProxyGameController(GameControllerVisualService gameController);
    public void voteForChancellor(Voting voting);
    public void voteForChancellor(Voting voting, int presidentID, int chancellorID);

    public void makePresident(Right<President.rights> rights[]);
    public void unmakePresident();

    public void choosePlayer(String text);
    public void giveCardsToRemove(ArrayList<CardsArray.Card> cards);
}
