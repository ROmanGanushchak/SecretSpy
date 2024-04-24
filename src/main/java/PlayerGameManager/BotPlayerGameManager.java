package PlayerGameManager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

import GameController.GameControllerVisualService;
import User.UserData;
import model.ChangebleRole.President;
import model.Game.PlayerModel;
import model.Cards.CardsArray;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.Chancellor;
import model.Voting.Voting;
import model.ChangebleRole.Political;

public class BotPlayerGameManager implements PlayerGameManager {
    private GameControllerVisualService gameController;
    private Random rand = new Random();

    private UserData userData;
    public UserData.VisualData getVisualData() {
        return this.userData.visualData;
    }

    public int getPlayerID() {
        return userData.getID();
    }

    public BotPlayerGameManager(int id) {
        this.userData = new UserData(id, Integer.toString(id), "board.png");
    }

    public void voteForChancellor(Voting voting) {
        voting.vote(this.getPlayerID(), true);
    }

    public void voteForChancellor(Voting voting, int presidentName, int chancellorName) {
        voteForChancellor(voting);
    }

    @Override
    public void makePresident(EnumMap<President.RightTypes, Political.Right> rights) {
        System.out.println("kfdfl");
        // int startIndex = rand.nextInt(rights.length), index;

        // for (int i=0; i<rights.length; i++) {
        //     index = (startIndex + i) % rights.length;
        //     if (rights[i].getValue() == 0) continue;

        //     switch (rights[i].getKey()) {
        //         case ChoosingChancellor:
        //             // this.gameController.
        //             break;
            
        //         default:
        //             break;
        //     }
        // }
    }

    public void makeChancellor(EnumMap<Chancellor.RightTypes, Political.Right> rights) {

    }

    public void unmakeChancellor() {

    }

    public void unmakePresident() {

    }

    public void choosePlayer(String text) {

    }

    public void setProxyGameController(GameControllerVisualService gameController) {
        this.gameController = gameController;
    }

    public void giveCardsToRemove(ArrayList<CardsArray.Card> cards) {

    }

    @Override
    public void revealCards(Card[] cards) {
        System.out.println("fklfdfklfd");
    }

    public void kill() {

    }

    public void showRole(PlayerModel.mainRoles role) {
        
    }
}
