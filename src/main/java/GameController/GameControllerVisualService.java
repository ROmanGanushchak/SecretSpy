package GameController;

import model.ChangebleRole.President;

public interface GameControllerVisualService {
    public void yesVote();
    public void noVote();
    public void presidntRequest(President.rights request);
}