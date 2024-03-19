package model.Voting;

import java.util.Map;

public class VoteObserver {
    @FunctionalInterface
    public interface VoteObserverFuncToCall {
        void execute(boolean result, int candidateId, Map<Integer, Boolean> votes);
    }

    private VoteObserverFuncToCall methodToCall;

    public VoteObserver(VoteObserverFuncToCall method) {
        this.methodToCall = method;
    }

    public void inform(boolean result, int candidate, Map<Integer, Boolean> votes) {
        this.methodToCall.execute(result, candidate, votes);
    }
}  
