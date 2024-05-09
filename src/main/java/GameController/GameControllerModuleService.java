package GameController;

import java.util.ArrayList;
import model.Voting.Voting;

/** Interface GameControllerModuleService contains all methods of GaneConteroller that can be used in Model */
public interface GameControllerModuleService {
    /** method to requst voting 
     * @param voting voting obj
     * @param presidentId id of president that requested voting
     * @param chancellorId id of the candidate
    */
    public void requestVoting(Voting voting, int presidentId, int chancellorId);

    /** finishes the game 
     * @param result the game result
     * @param shadowLeaderId id of the shadow leader
     * @param spyesId list of the spyes ids
    */
    public void finishGame(boolean result, int shadowLeaderId, ArrayList<Integer> spyesId);
}
