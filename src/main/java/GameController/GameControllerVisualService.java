package GameController;

import java.util.ArrayList;

import model.ChangebleRole.Chancellor;
import model.ChangebleRole.President;

public interface GameControllerVisualService {
    public void executeCommand(String command);
    public void informCardRemoved(Integer card, Integer playerID);
    public void executePresidentRight(Integer playerID, President.RightTypes right, Object... parametrs);
    public void executeChancellorRight(Integer playerID, Chancellor.RightTypes right, Object... parametrs);
    public ArrayList<Integer> getNonChooseblePlayers(Integer playerID, President.RightTypes right);
    public ArrayList<Integer> getNonChooseblePlayers(Integer playerID, Chancellor.RightTypes right);
}