package model.Voting;

import java.util.Map;

/** the observer of the voting */
public class VoteObserver {
    /** the function that will be called to infrom the voting result */
    @FunctionalInterface
    public interface VoteObserverFuncToCall {
        void execute(boolean result, int candidateId, Map<Integer, Boolean> votes);
    }

    /** method that will be called */
    private VoteObserverFuncToCall methodToCall;

    public VoteObserver(VoteObserverFuncToCall method) {
        this.methodToCall = method;
    }

    /**
     * method to inform the result 
     * @param result the result of the voting
     * @param candidate the candidate of ellection
     * @param votes votes of the participants
     */
    public void inform(boolean result, int candidate, Map<Integer, Boolean> votes) {
        this.methodToCall.execute(result, candidate, votes);
    }
}  
