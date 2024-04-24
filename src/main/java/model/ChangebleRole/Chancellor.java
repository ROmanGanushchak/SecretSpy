package model.ChangebleRole;

import java.util.EnumMap;

public class Chancellor extends Political<Chancellor.RightTypes> implements ChancellorAccess {
    public static enum RightTypes {
        VetoPower
    }

    private void vetoPower() {
        
    }

    public Chancellor() {
        super(2);

        EnumMap<RightTypes, Political.Right<?>> rights = new EnumMap<>(getCurrentRights());
        rights.put(RightTypes.VetoPower, new Right<Integer>(Request.None, Integer.class));
        super.initializeRights(rights);
    }
}
