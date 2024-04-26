package model.Game;

import model.Cards.CardsArray.Card;

public interface GamePresidentAccess {
    public PlayerModel.mainRoles revealePlayerRole(int playerID);
    public Card[] revealeUpperCards(int count);
    public boolean presidentSuggestChancellor(int playerID);
    public boolean setNextPresidentCandidate(int playerID);
    public Integer killPlayer(int playerID);
    public boolean presidentFinishGameCycle();
}
