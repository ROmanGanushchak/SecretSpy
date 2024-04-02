package PlayerGameManager;

import java.util.Random;

import GameController.GameControllerVisualService;
import model.ChangebleRole.President;
import model.ChangebleRole.Political.Right;
import model.Voting.Voting;

public class BotPlayerGameManager extends PlayerGameManager {
    private GameControllerVisualService gameController;
    private Random rand;

    public void voteForChancellor(Voting voting) {
        voting.vote(this.getModelID(), false);
    }

    public void makePresident(Right<President.rights> rights[]) {
        int startIndex = rand.nextInt(rights.length), index;

        for (int i=0; i<rights.length; i++) {
            index = (startIndex + i) % rights.length;
            if (rights[i].getValue() == 0) continue;

            switch (rights[i].getKey()) {
                case ChoosingChancellor:
                    // this.gameController.
                    break;
            
                default:
                    break;
            }
        }
    }

    public void setProxyGameController(GameControllerVisualService gameController) {
        this.gameController = gameController;
    }

    public int getPlayerID() {return 0;}
}
