package model.ChangebleRole;

import java.util.EnumMap;
import model.ChangebleRole.Political.Request;
import model.Game.GamePresidentAccess;

public class President extends Political<President.RightTypes> implements PresidentAccess {
    public static enum RightTypes {
        ChoosingChancellor,         
        RevealingRoles,             
        CheckingUpperThreeCards,    
        ChoosingNextPresident,      
        KillingPlayers              
    }

    private GamePresidentAccess game;
    private EnumMap<RightTypes, Right> rights;

    public President() {
        super(3);
        rights = new EnumMap<>(RightTypes.class);

        rights.put(RightTypes.ChoosingChancellor, new ChoosingChancellorRight());
        rights.put(RightTypes.RevealingRoles, new RevealingRoles());
        rights.put(RightTypes.CheckingUpperThreeCards, new CheckingUpperThreeCards());
        rights.put(RightTypes.ChoosingNextPresident, new ChoosingNextPresident());
        rights.put(RightTypes.KillingPlayers, new KillingPlayers());

        super.initializeRights(rights);
    }
}

class ChoosingChancellorRight extends Political.Right {
    public ChoosingChancellorRight() { super(Request.ChoosePlayer); }

    @Override
    public Object execute(GamePresidentAccess game, Object... params) {
        if (params.length == 1) 
            return game.presidentSuggestChancellor((Integer) params[0]);
        System.out.println("Uncorrect paramters count for Right");
        return null;
    }
}

class RevealingRoles extends Political.Right {
    public RevealingRoles() { super(Request.ChoosePlayer); }

    @Override
    public Object execute(GamePresidentAccess game, Object... params) {
        if (params.length == 1) 
            return game.revealePlayerRole((Integer) params[0]);
        System.out.println("Uncorrect paramters count for Right");
        return null;
    }
}

class CheckingUpperThreeCards extends Political.Right {
    public CheckingUpperThreeCards() { super(Request.None); }

    @Override
    public Object execute(GamePresidentAccess game, Object... params) {
        if (params.length == 1) 
            return game.revealeUpperCards(3);
        System.out.println("Uncorrect paramters count for Right");
        return null;
    }
}

class ChoosingNextPresident extends Political.Right {
    public ChoosingNextPresident() { super(Request.ChoosePlayer); }

    @Override
    public Object execute(GamePresidentAccess game, Object... params) {
        if (params.length == 1) 
            return game.setNextPresidentCandidate((Integer) params[0]);
        System.out.println("Uncorrect paramters count for Right");
        return null;
    }
}

class KillingPlayers extends Political.Right {
    public KillingPlayers() { super(Request.ChoosePlayer); }

    @Override
    public Object execute(GamePresidentAccess game, Object... params) {
        if (params.length == 1) 
            return game.killPlayer((Integer) params[0]);
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