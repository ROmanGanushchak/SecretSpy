package model.Voting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import model.Observers.Observers;
import model.Observers.ObserversAccess;

import java.util.HashSet;

public class Voting {
    private Observers<VoteObserver> observersOfEnding;  
    private Set<Integer> participators;
    private Map<Integer, Boolean> votes;
    private Integer candidate;
    private int remainedCount;

    public Voting(int candidate, ArrayList<Integer> participators) {
        this.participators = new HashSet<>(participators);
        this.votes = new HashMap<>();
        this.candidate = candidate;
        this.observersOfEnding = new Observers<>();

        this.remainedCount = participators.size();
    }

    // public ArrayList<PlayerData> getRemainedPlayers() {return this.participators;}

    public static boolean determineTheResult(Map<Integer, Boolean> voteResults) {
        int outcome = 0;
        for (Boolean vote : voteResults.values()) {
            outcome += (vote ? 1 : 0) * 2 - 1;
        }

        System.out.println("the voting ended, the result " + outcome);

        if (outcome > 0)
            return true;
        return false;
    }

    private void informTheResult() {
        System.out.println("Voting ending");
        boolean result = determineTheResult(this.votes);
        for (VoteObserver observer : observersOfEnding.getFollowers()) {
            observer.inform(result, this.candidate, this.votes);
        }
    }

    public boolean isInGroup(int player) {
        return this.participators.contains(player);
    }

    public void vote(int player, boolean vote) {
        System.out.println(player + " Voted");
        if (!this.participators.contains(player)) {
            System.out.println("Trying to vote from member not in group");
            return;
        }

        boolean wasVoting = this.votes.containsKey(player);
        this.votes.put(player, vote);

        if (!wasVoting && --this.remainedCount == 0) 
            this.informTheResult();
    }

    public ObserversAccess<VoteObserver> getEndingObservers() {
        return this.observersOfEnding;
    }
}
