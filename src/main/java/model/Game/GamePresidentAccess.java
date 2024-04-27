package model.Game;

import model.Cards.CardsArray.Card;
import model.ChangebleRole.Right.ExecutionStatusWrapper;

public interface GamePresidentAccess {
    public PlayerModel.mainRoles revealePlayerRole(ExecutionStatusWrapper executionResult, int playerID);
    public Card[] revealeUpperCards(ExecutionStatusWrapper executionResult, int count);
    public void presidentSuggestChancellor(ExecutionStatusWrapper executionResult, int playerID);
    public void setNextPresidentCandidate(ExecutionStatusWrapper executionResult, int playerID);
    public Integer killPlayer(ExecutionStatusWrapper executionResult, int playerID);
    public void presidentFinishGameCycle(ExecutionStatusWrapper executionResult);
}
