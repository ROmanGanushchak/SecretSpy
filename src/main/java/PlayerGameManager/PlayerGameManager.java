package PlayerGameManager;

import GameController.GameControllerVisualService;
import model.ChangebleRole.Political.Right;
import model.ChangebleRole.President;
import model.Voting.Voting;

public abstract class PlayerGameManager {
    private int modelID;
    private String name;

    public void setModelID(int modelID) {
        this.modelID = modelID;
    }

    public int getModelID() {
        return this.modelID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract void setProxyGameController(GameControllerVisualService gameController);
    public abstract int getPlayerID();
    public abstract void voteForChancellor(Voting voting);
    public abstract void voteForChancellor(Voting voting, String presidentName, String chancellorName);
    public abstract void makePresident(Right<President.rights> rights[]);
    public abstract void unmakePresident();
    public abstract void choosePlayer(String text);
}
