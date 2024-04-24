package GameController;

import java.util.ArrayList;
import model.Voting.Voting;

public interface GameControllerModuleService {
    public void requestVoting(Voting voting, int presidentId, int chancellorId);
    public void finishGame(boolean result, int shadowLeaderId, ArrayList<Integer> spyesId);
}
