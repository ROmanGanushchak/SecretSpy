package GameController;

import java.util.ArrayList;
import model.ChangebleRole.Chancellor;
import model.ChangebleRole.President;

/** Interface GameControllerModuleService contains all methods of GaneConteroller that can be used in Visualization */
public interface GameControllerVisualService {
    /**executes a command, debug only, scans the parametrs from the console
     * @param command the command name
     */
    public void executeCommand(String command);

    /** allows player to infrom card that was removed
     * @param card index of the card that was asked to remove
     * @param playerID the id of the player that called the method
     */
    public void informCardRemoved(Integer card, Integer playerID);

    /** executes the presedint right
     * @param playerID id of the player that asked for the right execution
     * @param right right type
     * @param parametrs parametrs of the right
     */
    public void executePresidentRight(Integer playerID, President.RightTypes right, Object... parametrs);

    /** executes the chancellor right
     * @param playerID id of the player that asked for the right execution
     * @param right right type
     * @param parametrs parametrs of the right
     */
    public void executeChancellorRight(Integer playerID, Chancellor.RightTypes right, Object... parametrs);

    /** returns non chooseble players for the president right 
     * @param playerID id of the player that called method
     * @param right right type
    */
    public ArrayList<Integer> getNonChooseblePlayers(Integer playerID, President.RightTypes right);

    /** returns non chooseble players for the chancellor right 
     * @param playerID id of the player that called method
     * @param right right type
    */
    public ArrayList<Integer> getNonChooseblePlayers(Integer playerID, Chancellor.RightTypes right);
}