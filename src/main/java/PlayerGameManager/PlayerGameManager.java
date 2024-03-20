package PlayerGameManager;

import GameController.GameControllerVisualService;
import model.Voting.Voting;

public abstract class PlayerGameManager {
    private int modelID;

    public void setModelID(int modelID) {
        this.modelID = modelID;
    }

    public int getModelID() {
        return this.modelID;
    }

    public abstract void setProxyGameController(GameControllerVisualService gameController);
    public abstract int getPlayerID();
    public abstract void voteForChancellor(Voting voting);
}
