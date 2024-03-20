package PlayerGameManager;

import GameController.GameControllerVisualService;
import model.Voting.Voting;

public class BotPlayerGameManager extends PlayerGameManager {
    private GameControllerVisualService gameController;

    public void voteForChancellor(Voting voting) {
        voting.vote(0, true);
    }

    public void setProxyGameController(GameControllerVisualService gameController) {
        this.gameController = gameController;
    }

    public int getPlayerID() {return 0;}
}
