package PlayerGameManager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import GameController.GameControllerVisualService;
import User.UserData;
import model.Cards.CardsArray;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.Chancellor;
import model.ChangebleRole.Right.ExecutionStatusWrapper;
import model.ChangebleRole.Right;
import model.ChangebleRole.President;
import model.Game.PlayerModel;
import model.Voting.Voting;

/** Interface declares all metohd that player game managers have to implement */
public interface PlayerGameManager {
    /** all possible roles */
    public static enum CurrentRoles {
        President, Chancellor, None
    }
    
    /**Returns the player visual data
     * @return the player visual data
     */
    public UserData.VisualData getVisualData();
    /**Returns the id of the player
     * @return the id of the player
     */
    public int getPlayerID();

    /**sets ProxyGameController 
     * @param gameController the proxy of the game controller
     */
    public void setProxyGameController(GameControllerVisualService gameController);

    /**the request to vote for chancellor
     * @param voting the voting obj
     */
    public void voteForChancellor(Voting voting);

    /**the request to vote for chancellor
     * @param voting the voting obj
     * @param presidentID id of the president 
     * @param chancellorID id of the candidate
     */
    public void voteForChancellor(Voting voting, int presidentID, int chancellorID);

    /** Method to make the player the president
     * @param rights the rights of the president
     */
    public void makePresident(EnumMap<President.RightTypes, Right> rights);
    /** Method to unmake player the president */
    public void unmakePresident();
    /** Method to update a right 
     * @param right pair of right type and right itself
    */
    public void changePresidentRight(Map.Entry<President.RightTypes, Right> right);
    /** Method to change the look of the current preident */
    public void changePresident(Integer oldPresident, Integer newPresident);

    /** Method to make the player the chancellor
     * @param rights the rights of the chamcellor
     */
    public void makeChancellor(EnumMap<Chancellor.RightTypes, Right> rights);
    /** Method to unmake player the chancellor */
    public void unmakeChancellor();
    /** Method to update a right 
     * @param right pair of right type and right itself
    */
    public void changeChancellorRight(Map.Entry<Chancellor.RightTypes, Right> right);
    /** Method to change the look of the current chancellor */
    public void changeChancellor(Integer oldChancellor, Integer newChancellor);

    /**Gives the list of cards, to let remove one of then
     * @param cards cards that are given to be removed
     */
    public void giveCardsToRemove(ArrayList<CardsArray.Card> cards);

    /**Reveal the cards to the player
     * @param cards cards that will be shown
     */
    public void revealCards(Card[] cards);
    /**Shows the death message*/
    public void kill();
    /** Shows the death of the other player
     * @param playerID id of the player that was killed
     */
    public void killOtherPlayer(int playerID);
    /**Shows the player role
     * @param role role of the player
     */
    public void showRole(PlayerModel.mainRoles role);
    /**Shows the player role
     * @param role role of the player
     * @param spyes list of ids of spyes
     * @param shadowLeader id of the shadowLeader
     */
    public void showRole(PlayerModel.mainRoles role, ArrayList<Integer> spyes, Integer shadowLeader);
    /** shows card being added to the board
     * @param type type of the card
     */
    public void addCardToBoard(Card.states type);

    /**method to inform the president right usage
     * @param right the right type
     * @param status the execution status
     */
    public void informPresidentRightUsage(President.RightTypes right, ExecutionStatusWrapper status);
    /**method to inform the chancellor right usage
     * @param right the right type
     * @param status the execution status
     */
    public void informChancellorRightUsage(Chancellor.RightTypes right, ExecutionStatusWrapper status);
}
