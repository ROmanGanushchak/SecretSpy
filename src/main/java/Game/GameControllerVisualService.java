package Game;

import ChangebleRole.President;

public interface GameControllerVisualService {
    public void yesVote();
    public void noVote();
    public void presidntRequest(President.presidntRights request);
}