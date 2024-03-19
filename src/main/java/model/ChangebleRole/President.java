package model.ChangebleRole;

import model.Cards.CardsArray.Card;
import model.Game.GamePresidentAccess;
import model.Game.PlayerModel;

public class President extends Political<President.rights> implements PresidentAccess { // add observer for rights changings
    public static enum rights {
        ChoosingChancellor,         
        RevealingRoles,             
        CheckingUpperThreeCards,    
        ChoosingNextPresident,      
        KillingPlayers              
    }

    private GamePresidentAccess game;

    public President(GamePresidentAccess game) {
        super(rights.class, 3);
        this.game = game;

        this.expandPower(rights.ChoosingChancellor, -1);
    }
    
    public boolean suggestingChancellor(int playerID) {
        if (this.tryUseRight(rights.ChoosingChancellor))
            return this.game.presidentSuggestChancellor(playerID);
        return false;
    }

    public PlayerModel.mainRoles revealeTheRole(int playerId) {
        if (this.tryUseRight(rights.RevealingRoles)) {
            PlayerModel.mainRoles role = this.game.revealePlayerRole(playerId);
            if (role == PlayerModel.mainRoles.Undefined)
                this.increaseRightUsage(rights.RevealingRoles, 1);
            
            if (role == PlayerModel.mainRoles.ShadowLeader) 
                return PlayerModel.mainRoles.Spy;
            return role;
        }

        return null;
    }

    public Card[] checkingUpperThreeCards() {
        if (this.tryUseRight(rights.CheckingUpperThreeCards))
            return this.game.revealeUpperCards(3);
        return null;
    }

    public boolean choosingNextPresident(int playerId) {
        if (this.tryUseRight(rights.ChoosingNextPresident)) {
            boolean result = this.game.setNextPresidentCandidate(playerId);
            if (result == false) 
                this.increaseRightUsage(rights.ChoosingNextPresident, 1);
            return result;
        }
        return false;
    }

    public void killingPlayers(int playerId) {
        if (this.tryUseRight(rights.KillingPlayers)) {
            if (!this.game.killPlayer(playerId))
                this.increaseRightUsage(rights.KillingPlayers, 1);
        }
    }
}


/*private static Map<rights, Method> methodTypeToName;
    static {
        try {
            methodTypeToName = Map.of(
                rights.RevealingRoles, President.class.getMethod("revealTheRole", new Class<?>[]{PlayerData.class})
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void request(rights method, Object... parametrs) {
        try {
            methodTypeToName.get(method).invoke(this, parametrs);
        } catch (Exception e) {
            System.err.println("The invoked method in request threw an exception: " + e.getMessage());
        }
    }*/