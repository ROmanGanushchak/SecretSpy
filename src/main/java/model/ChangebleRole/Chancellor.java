package model.ChangebleRole;

import java.util.EnumMap;

import model.ChangebleRole.Right.ExecutionStatus;
import model.ChangebleRole.Right.ExecutionStatusWrapper;

/** stores all the chancellor rights, and the cards that it is holding */
public class Chancellor extends Political<Chancellor.RightTypes> implements ChancellorAccess {
    /** all chancellor rights types */
    public static enum RightTypes {
        VetoPower
    }

    /** method to execute chancellor right VetoPower 
     * @param executionResult the execution status, changes the .status parametr
    */
    private void vetoPower(ExecutionStatusWrapper executionResult) {
        if (super.chooseCardToRemove(null))
            executionResult.status = ExecutionStatus.Executed;
        else 
            executionResult.status = ExecutionStatus.IsntAllowedToUse;
    }

    public Chancellor() {
        super(2);

        EnumMap<RightTypes, Right> rights = new EnumMap<>(RightTypes.class);
        VetoPower vetoPowerRight = new VetoPower();
        vetoPowerRight.setMethod((ExecutionStatusWrapper executionResult) -> vetoPower(executionResult));
        rights.put(RightTypes.VetoPower, vetoPowerRight);

        super.initializeRights(rights);
    }
}

/** Chancellors Right */
class VetoPower extends Right {
    @FunctionalInterface
    public static interface Method {
        void vetoExecute(ExecutionStatusWrapper executionResult);
    }

    private Method method;

    public VetoPower() { super(Request.None); }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object execute(ExecutionStatusWrapper executionResult, Object... params) {
        this.method.vetoExecute(executionResult);
        return null;
    }
}
