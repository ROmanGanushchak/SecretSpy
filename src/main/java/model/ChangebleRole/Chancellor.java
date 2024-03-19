package model.ChangebleRole;

public class Chancellor extends Political<Chancellor.rights> implements ChancellorAccess {
    public static enum rights {
        VetoPower
    }

    public Chancellor() {
        super(rights.class, 2);
    }

    public void vetoPower() {
        System.out.println("Veto triggered");
        if (super.areCardsInHands() && super.tryUseRight(rights.VetoPower)) { 
            System.out.println("In 1st if");
            if (!super.chooseCardToRemove(null))
                super.increaseRightUsage(rights.VetoPower, 1);
        }
    }
}
