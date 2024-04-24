package model.ChangebleRole;

import java.util.EnumMap;
import model.ChangebleRole.Political.Request;
import model.Game.GamePresidentAccess;
import model.Game.PlayerModel;

public class President extends Political<President.RightTypes> implements PresidentAccess {
    public static enum RightTypes {
        ChoosingChancellor,         
        RevealingRoles,             
        CheckingUpperThreeCards,    
        ChoosingNextPresident,      
        KillingPlayers;
        
        public static RightTypes get(int index) {
            if (index >= 0 && index < values().length)
                return values()[index];
            else 
                throw new IllegalArgumentException("Invalid index");
        }
    }

    private GamePresidentAccess game;
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
        super.initializeRights(rights);

        expandPower(RightTypes.ChoosingChancellor, -1);
        expandPower(RightTypes.CheckingUpperThreeCards, 3);
        expandPower(RightTypes.RevealingRoles, 3);
        expandPower(RightTypes.ChoosingNextPresident, 3);
        expandPower(RightTypes.KillingPlayers, 3);
    }
}

class ChoosingChancellorRight extends Political.Right {
    private GamePresidentAccess game;
    public ChoosingChancellorRight(GamePresidentAccess game) { 
        super(Request.ChoosePlayer); 
        this.game = game;
    }

    @Override
    public Object execute(Object... params) {
        System.out.println("For Choosing chanceelor count " + params.length);
        if (params.length == 1) 
            return game.presidentSuggestChancellor((Integer) params[0]);
        System.out.println("Uncorrect paramters count for Right");
        return null;
    }
}

class RevealingRoles extends Political.Right {
    private GamePresidentAccess game;

    public RevealingRoles(GamePresidentAccess game) { 
        super(Request.ChoosePlayer); 
        this.game = game;
    }

    @Override
    public Object execute(Object... params) {
        if (params.length == 1) {
            PlayerModel.mainRoles role = game.revealePlayerRole((Integer) params[0]);
            if (role == PlayerModel.mainRoles.Undefined)
                super.increaseUseCount(1);
            return role;
        }
        
        super.increaseUseCount(1);
        System.out.println("Uncorrect paramters count for Right");
        return null;
    }
}

class CheckingUpperThreeCards extends Political.Right {
    private GamePresidentAccess game;
    public CheckingUpperThreeCards(GamePresidentAccess game) { 
        super(Request.None); 
        this.game = game;
    }

    @Override
    public Object execute(Object... params) {
        if (params.length == 0) 
            return game.revealeUpperCards(3);
        
        super.increaseUseCount(1);
        System.out.println("Uncorrect paramters count for Right");
        return null;
    }
}

class ChoosingNextPresident extends Political.Right {
    private GamePresidentAccess game;
    public ChoosingNextPresident(GamePresidentAccess game) { 
        super(Request.ChoosePlayer); 
        this.game = game;
    }

    @Override
    public Object execute(Object... params) {
        if (params.length == 1) 
            return game.setNextPresidentCandidate((Integer) params[0]);
        System.out.println("Uncorrect paramters count for Right");
        return null;
    }
}

class KillingPlayers extends Political.Right {
    private GamePresidentAccess game;
    public KillingPlayers(GamePresidentAccess game) { 
        super(Request.ChoosePlayer); 
        this.game = game;
    }

    @Override
    public Object execute(Object... params) {
        if (params.length == 1) {
            if (game.killPlayer((Integer) params[0]))
                return (Integer) params[0];
            
            super.increaseUseCount(1);
            return null;
        }
        System.out.println("Uncorrect paramters count for Right");
        return null;
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