package model.ChangebleRole;

import java.util.HashMap;

import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;

public abstract class Right {
    public static enum Request {
        None, ChoosePlayer
    }

    public static enum ExecutionStatus { // only Executed counts as succesfully used right
        Executed, IsntAllowedToUse, NotChosenPlayer, PlayerWasInParlament, UnexpectedError
    }

    public static HashMap<ExecutionStatus, String> execaptionStatusText;
    public static class ExecutionStatusWrapper {
        public ExecutionStatus status;
    }

    static {
        execaptionStatusText = new HashMap<>();
        execaptionStatusText.put(ExecutionStatus.Executed, "was used");
        execaptionStatusText.put(ExecutionStatus.IsntAllowedToUse, "is not allowed to use");
        execaptionStatusText.put(ExecutionStatus.NotChosenPlayer, "was not executed the player cant be chosen");
        execaptionStatusText.put(ExecutionStatus.PlayerWasInParlament, "was not executed, the player was in parlamant");
        execaptionStatusText.put(ExecutionStatus.UnexpectedError, "was failed to execute");
    }

    private int useCount;
    private Request request;
    private boolean isAllowed;
    private ActObservers<Integer> useCountChanges;

    public Right(Request request) {
        this.request = request;
        this.isAllowed = true;
        this.useCountChanges = new ActObservers<>();
    }

    public int getUseCount() {
        return this.useCount;
    }

    public Request getRequest() {
        return this.request;
    }

    public void setUseCount(int newValue) {
        this.useCount = newValue;
        useCountChanges.informAll(useCount);
    }

    public void changeUseCount(int value) {
        if (useCount != -1) {
            this.useCount += value;
            useCountChanges.informAll(useCount);
        }
    }

    public void setIsAllowed(boolean isAllowed) {
        this.isAllowed = isAllowed;
    }

    public boolean getIsAllowed() {
        return this.isAllowed;
    }

    public boolean isActivate() {
        return isAllowed && (useCount != 0);
    }
    
    public Object tryUseRight(ExecutionStatusWrapper resultStatus, Object... paramters) {
        Object result = null;

        if (isAllowed && useCount != 0) {
            useCountChanges.informAll(useCount);
            result = execute(resultStatus, paramters);
            if (resultStatus.status == ExecutionStatus.Executed)
                changeUseCount(-1);
        } else 
            resultStatus.status = ExecutionStatus.IsntAllowedToUse;

        return result;
    }

    public ActObserversAccess<Integer> getUseCountChanges() {
        return this.useCountChanges;
    }

    protected abstract Object execute(ExecutionStatusWrapper resultStatus, Object... params);
}