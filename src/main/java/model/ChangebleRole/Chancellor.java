package model.ChangebleRole;

import java.util.EnumMap;

import model.ChangebleRole.Political.Request;

public class Chancellor extends Political<Chancellor.RightTypes> implements ChancellorAccess {
    public static enum RightTypes {
        VetoPower
    }

    private void vetoPower() {
        super.chooseCardToRemove(null);
    }

    public Chancellor() {
        super(2);

        EnumMap<RightTypes, Political.Right> rights = new EnumMap<>(RightTypes.class);
        VetoPower vetoPowerRight = new VetoPower();
        vetoPowerRight.setMethod(() -> vetoPower());
        rights.put(RightTypes.VetoPower, vetoPowerRight);

        super.initializeRights(rights);
    }
}

class VetoPower extends Political.Right {
    @FunctionalInterface
    public static interface Method {
        void vetoExecute();
    }

    private Method method;

    public VetoPower() { super(Request.None); }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object execute(Object... params) {
        this.method.vetoExecute();
        return null;
    }
}
