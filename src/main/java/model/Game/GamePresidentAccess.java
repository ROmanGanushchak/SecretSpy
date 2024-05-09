package model.Game;

import model.Cards.CardsArray.Card;
import model.ChangebleRole.Right.ExecutionStatusWrapper;

public interface GamePresidentAccess {
    /** reveals the role of the player
     * @param executionResult the status of the method execution
     * @param playerID the id of the player whitch role will be revealed
     * @return returns the role of the player
     */
    public PlayerModel.mainRoles revealePlayerRole(ExecutionStatusWrapper executionResult, int playerID);

    /** returns the upper cards from the deck
     * @param executionResult the status of the method execution
     * @param count the count of cards that will be revealed
     * @return return the list of the upper cards
     */
    public Card[] revealeUpperCards(ExecutionStatusWrapper executionResult, int count);

    /**
     * the method to suggest chancellor by a president
     * @param executionResult the result of the execution
     * @param playerID the id of the player that is suggested to be chancellor
     */
    public void presidentSuggestChancellor(ExecutionStatusWrapper executionResult, int playerID);

    /** the metod to set the next president that will be chosen after gameCycleRest
     * @param executionResult the status of the method execution
     * @param playerID the id of the player that will become the next president
    */
    public void setNextPresidentCandidate(ExecutionStatusWrapper executionResult, int playerID);

    /** method to kill the player
     * @param executionResult the status of the method execution
     * @param playerID the id of the player that will be killed
     * @return the id of the player that was killed
     */
    public Integer killPlayer(ExecutionStatusWrapper executionResult, int playerID);

    /** president request to finish his turn
     * @param executionResult the status of the method execution
     */
    public void presidentFinishGameCycle(ExecutionStatusWrapper executionResult);
}
