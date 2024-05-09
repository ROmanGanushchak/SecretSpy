package model.ChangebleRole;

import java.util.HashMap;

import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;

/** basic class for all politicall rights, contains information about usagecount, requestType */
public abstract class Right {
    /** the execute method requst types */
    public static enum Request {
        None, ChoosePlayer
    }

    /** the execution status of the rights, executed is the only sucsesful status */
    public static enum ExecutionStatus {
        Executed, IsntAllowedToUse, NotChosenPlayer, PlayerWasInParlament, UnexpectedError
    }

    /** the text of each execution status */
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

    /** the max use count of the right, if -1 then can be used infinetly */
    private int useCount;
    /** the requst that execute method requires */
    private Request request;
    /** is the right allowed */
    private boolean isAllowed;
    /** the observer of the rights use count changes */
    private ActObservers<Integer> useCountChanges;

    public Right(Request request) {
        this.request = request;
        this.isAllowed = true;
        this.useCountChanges = new ActObservers<>();
    }

    /**
     * returns the use count of the right
     * @return the use count of the right
     */
    public int getUseCount() {
        return this.useCount;
    }

    /** @return the request that execute method needs */
    public Request getRequest() {
        return this.request;
    }

    /** sets the maximum number of right usage, if -1 then the right can be used infinitely 
     * @param newValue new useCount value
    */
    public void setUseCount(int newValue) {
        this.useCount = newValue;
        useCountChanges.informAll(useCount);
    }

    /** changes the useCount 
     * @param value the useCount changes value
    */
    public void changeUseCount(int value) {
        if (useCount != -1) {
            this.useCount += value;
            useCountChanges.informAll(useCount);
        }
    }

    /**
     * sets is the right allowed
     * @param isAllowed is right allowed
     */
    public void setIsAllowed(boolean isAllowed) {
        this.isAllowed = isAllowed;
    }

    /** @return true if the right can be used at that moment, is not guaranteed that right will be executed even if the return is true */
    public boolean getIsAllowed() {
        return this.isAllowed;
    }

    /**
     * returns true if the right can be used
     * @return true if the right can be used
     */
    public boolean isActivate() {
        return isAllowed && (useCount != 0);
    }
    
    /**
     * checks whether the right can be executed, if so then calls method to execute it
     * @param resultStatus changes paramtrs .status to inform the execution status
     * @param paramters all paranters needed for the right execution
     * @return the execution result or null
     */
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

    /**
     * returns the observer of the useCount changes
     * @return the observer of the useCount changes
     */
    public ActObserversAccess<Integer> getUseCountChanges() {
        return this.useCountChanges;
    }
    
    /** method to execute the right 
     * @param resultStatus the .status parametr is changed to store the execution status
     * @param params the parametr for the execut method
    */
    protected abstract Object execute(ExecutionStatusWrapper resultStatus, Object... params);
}