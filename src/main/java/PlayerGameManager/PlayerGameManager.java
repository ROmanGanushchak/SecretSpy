package PlayerGameManager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import GameController.GameControllerVisualService;
import User.UserData;
import model.Cards.CardsArray;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.Chancellor;
import model.ChangebleRole.Right.ExecutionStatusWrapper;
import model.ChangebleRole.Right;
import model.ChangebleRole.President;
import model.Game.PlayerModel;
import model.Voting.Voting;

public interface PlayerGameManager {
    public static enum CurrentRoles {
        President, Chancellor, None
    }

    public UserData.VisualData getVisualData();
    public int getPlayerID();

    public void setProxyGameController(GameControllerVisualService gameController);
    public void voteForChancellor(Voting voting);
    public void voteForChancellor(Voting voting, int presidentID, int chancellorID);

    public void makePresident(EnumMap<President.RightTypes, Right> rights);
    public void unmakePresident();
    public void changePresidentRight(Map.Entry<President.RightTypes, Right> right);
    public void changePresident(Integer oldPresident, Integer newPresident);

    public void makeChancellor(EnumMap<Chancellor.RightTypes, Right> rights);
    public void unmakeChancellor();
    public void changeChancellorRight(Map.Entry<Chancellor.RightTypes, Right> right);
    public void changeChancellor(Integer oldChancellor, Integer newChancellor);

    public void giveCardsToRemove(ArrayList<CardsArray.Card> cards);

    public void revealCards(Card[] cards);
    public void kill();
    public void killOtherPlayer(int playerID);
    public void showRole(PlayerModel.mainRoles role);
    public void showRole(PlayerModel.mainRoles role, ArrayList<Integer> spyes, Integer shadowLeader);
    public void addCardToBoard(Card.states type);

    public void informPresidentRightUsage(President.RightTypes right, ExecutionStatusWrapper status);
    public void informChancellorRightUsage(Chancellor.RightTypes right, ExecutionStatusWrapper status);
}
