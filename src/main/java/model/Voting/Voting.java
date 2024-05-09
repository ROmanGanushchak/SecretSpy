package model.Voting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import model.Observers.Observers;
import model.Observers.ObserversAccess;
import java.util.HashSet;

/** Class controles the voting, stores all participants and their votes, when all players voted calculate and inform the result */
public class Voting {
    /** Observers of the voring ending */
    private Observers<VoteObserver> observersOfEnding;  
    /** All players that participates in the voting */
    private Set<Integer> participators;
    /** All players votes */
    private Map<Integer, Boolean> votes;
    /** The candidate of the election */
    private Integer candidate;
    /** The count of players that didnt voted */
    private int remainedCount;

    /**
     * Creates new instance of the class
     * @param candidate     candidate that is ellected
     * @param participators list of players that will participate in the voting
     */
    public Voting(int candidate, ArrayList<Integer> participators) {
        this.participators = new HashSet<>(participators);
        this.votes = new HashMap<>();
        this.candidate = candidate;
        this.observersOfEnding = new Observers<>();

        this.remainedCount = participators.size();
    }

    /**
     * determines the result of the voting
     * @param voteResults votes
     * @return true if the ellection was succesfull
     */
    public static boolean determineTheResult(Map<Integer, Boolean> voteResults) {
        int outcome = 0;
        for (Boolean vote : voteResults.values()) {
            outcome += (vote ? 1 : 0) * 2 - 1;
        }

        if (outcome > 0)
            return true;
        return false;
    }

    /** inform the result of the voting */
    private void informTheResult() {
        boolean result = determineTheResult(this.votes);
        for (VoteObserver observer : observersOfEnding.getFollowers()) {
            observer.inform(result, this.candidate, this.votes);
        }
    }

    /**
     * checks whether or not player participate in the voting
     * @param player id of the player that is checked
     * @return true if player participate in the voting
     */
    public boolean isInGroup(int player) {
        return this.participators.contains(player);
    }

    /**
     * method to vote
     * @param player id of the player that want to vote
     * @param vote the vote
     */
    public void vote(int player, boolean vote) {
        if (!this.participators.contains(player)) {
            System.out.println("Trying to vote from member not in group");
            return;
        }

        boolean wasVoting = this.votes.containsKey(player);
        this.votes.put(player, vote);

        if (!wasVoting && --this.remainedCount == 0) 
            this.informTheResult();
    }

    /**
     * returns the observer of the voting ending
     * @return the observer of the voting ending
     */
    public ObserversAccess<VoteObserver> getEndingObservers() {
        return this.observersOfEnding;
    }
}
