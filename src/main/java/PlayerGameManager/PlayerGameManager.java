package PlayerGameManager;

import java.util.ArrayList;
import java.util.EnumMap;

import GameController.GameControllerVisualService;
import User.UserData;
import model.Cards.CardsArray;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.Chancellor;
import model.ChangebleRole.Political;
import model.ChangebleRole.President;
import model.Game.PlayerModel;
import model.Voting.Voting;

public interface PlayerGameManager {
    public UserData.VisualData getVisualData();
    public int getPlayerID();

    public void setProxyGameController(GameControllerVisualService gameController);
    public void voteForChancellor(Voting voting);
    public void voteForChancellor(Voting voting, int presidentID, int chancellorID);

    public void makePresident(EnumMap<President.RightTypes, Political.Right> rights);
    public void unmakePresident();

    public void makeChancellor(EnumMap<Chancellor.RightTypes, Political.Right> rights);
    public void unmakeChancellor();

    public void choosePlayer(String text);
    public void giveCardsToRemove(ArrayList<CardsArray.Card> cards);

    public void revealCards(Card[] cards);
    public void kill();
    public void showRole(PlayerModel.mainRoles role);
}
