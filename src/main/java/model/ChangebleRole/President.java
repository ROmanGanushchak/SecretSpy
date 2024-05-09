package model.ChangebleRole;

import java.util.EnumMap;
import model.Game.GamePresidentAccess;

/** stores all the president rights, and the cards that it is holding */
public class President extends Political<President.RightTypes> implements PresidentAccess {
    /** all right types */
    public static enum RightTypes {
        ChoosingChancellor,         
        RevealingRoles,             
        CheckingUpperThreeCards,    
        ChoosingNextPresident,      
        KillingPlayers,
        FinishCycle;
        
        public static RightTypes get(int index) {
            if (index >= 0 && index < values().length)
                return values()[index];
            else 
                throw new IllegalArgumentException("Invalid index");
        }
    }

    /** the game obj to execute some rights */
    private GamePresidentAccess game;
    /** all rights of the president */
    private EnumMap<RightTypes, Right> rights;

    public President(GamePresidentAccess game) {
        super(3);
        this.game = game;

        rights = new EnumMap<>(RightTypes.class);

        rights.put(RightTypes.ChoosingChancellor, new ChoosingChancellorRight(game));
        rights.put(RightTypes.RevealingRoles, new RevealingRoles(game));
        rights.put(RightTypes.CheckingUpperThreeCards, new CheckingUpperThreeCards(game));
        rights.put(RightTypes.ChoosingNextPresident, new ChoosingNextPresident(game));
        rights.put(RightTypes.KillingPlayers, new KillingPlayers(game));
        rights.put(RightTypes.FinishCycle, new FinishCycle(game));
        super.initializeRights(rights);
    }
}

/** the right ChoosingChancellorRight */
class ChoosingChancellorRight extends Right {
    /** the game proxy */
    private GamePresidentAccess game;
    public ChoosingChancellorRight(GamePresidentAccess game) { 
        super(Request.ChoosePlayer); 
        this.game = game;
    }

    @Override
    public Object execute(ExecutionStatusWrapper executionResult, Object... params) {
        if (params.length == 1) 
            game.presidentSuggestChancellor(executionResult, (Integer) params[0]);
        else 
            executionResult.status = ExecutionStatus.NotChosenPlayer;
        return null;
    }
}

/** the right ChoosingChancellorRight */
class RevealingRoles extends Right {
    /** the game proxy */
    private GamePresidentAccess game;

    public RevealingRoles(GamePresidentAccess game) { 
        super(Request.ChoosePlayer); 
        this.game = game;
    }

    @Override
    public Object execute(ExecutionStatusWrapper executionResult, Object... params) {
        if (params.length == 1) 
            return game.revealePlayerRole(executionResult, (Integer) params[0]);
        
        executionResult.status = ExecutionStatus.NotChosenPlayer;
        return null;
    }
}

/** the right ChoosingChancellorRight */
class CheckingUpperThreeCards extends Right {
    /** the game proxy */
    private GamePresidentAccess game;
    public CheckingUpperThreeCards(GamePresidentAccess game) { 
        super(Request.None); 
        this.game = game;
    }

    @Override
    public Object execute(ExecutionStatusWrapper executionResult, Object... params) {
        if (params.length == 0) 
            return game.revealeUpperCards(executionResult, 3);
        
        executionResult.status = ExecutionStatus.UnexpectedError;
        return null;
    }
}

/** the right ChoosingChancellorRight */
class ChoosingNextPresident extends Right {
    /** the game proxy */
    private GamePresidentAccess game;
    public ChoosingNextPresident(GamePresidentAccess game) { 
        super(Request.ChoosePlayer); 
        this.game = game;
    }

    @Override
    public Object execute(ExecutionStatusWrapper executionResult, Object... params) {
        if (params.length == 1) 
            game.setNextPresidentCandidate(executionResult, (Integer) params[0]);
        else 
            executionResult.status = ExecutionStatus.NotChosenPlayer;
        return null;
    }
}

/** the right ChoosingChancellorRight */
class KillingPlayers extends Right {
    /** the game proxy */
    private GamePresidentAccess game;
    public KillingPlayers(GamePresidentAccess game) { 
        super(Request.ChoosePlayer); 
        this.game = game;
    }

    @Override
    public Object execute(ExecutionStatusWrapper executionResult, Object... params) {
        if (params.length == 1 && params[0].getClass() == Integer.class) 
            return game.killPlayer(executionResult, (Integer) params[0]);
        
        executionResult.status = ExecutionStatus.NotChosenPlayer;
        return null;
    }
}

/** the right ChoosingChancellorRight */
class FinishCycle extends Right {
    /** the game proxy */
    private GamePresidentAccess game;
    public FinishCycle(GamePresidentAccess game) { 
        super(Request.None); 
        this.game = game;
    }

    @Override
    public Object execute(ExecutionStatusWrapper executionResult, Object... params) {
        if (params.length == 0)
            game.presidentFinishGameCycle(executionResult);
        else 
            executionResult.status = ExecutionStatus.UnexpectedError;
        return null;
    }
}