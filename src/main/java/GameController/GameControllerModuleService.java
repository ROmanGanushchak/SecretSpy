package GameController;

import model.Voting.Voting;

public interface GameControllerModuleService {
    public void requestVoting(Voting voting);
    public void finishGame(boolean result);
}
