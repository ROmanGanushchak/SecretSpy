package PlayerGameManager;

import java.util.ArrayList;
import java.util.Random;

import GameController.GameControllerVisualService;
import User.UserData;
import model.ChangebleRole.President;
import model.Cards.CardsArray;
import model.ChangebleRole.Chancellor;
import model.ChangebleRole.Political.SimpleRight;
import model.Voting.Voting;

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

    public void makePresident(SimpleRight<President.RightTypes> rights[]) {
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

    public void makeChancellor(SimpleRight<Chancellor.rights> rights[]) {

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
}
