package PlayerGameManager;

import GameController.GameControllerVisualService;
import model.Voting.Voting;

public class BotPlayerGameManager extends PlayerGameManager {
    private GameControllerVisualService gameController;

    public void voteForChancellor(Voting voting) {
        voting.vote(this.getModelID(), false);
    }

    public void makePresident() {
        
    }

    public void setProxyGameController(GameControllerVisualService gameController) {
        this.gameController = gameController;
    }

    public int getPlayerID() {return 0;}
}
