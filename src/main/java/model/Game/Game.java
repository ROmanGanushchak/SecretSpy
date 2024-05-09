package model.Game;

import model.Cards.*;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.*;
import model.ChangebleRole.Right.ExecutionStatus;
import model.ChangebleRole.Right.ExecutionStatusWrapper;
import model.Observers.ActObservers;
import model.Voting.VoteObserver;
import model.Voting.Voting;
import GameController.GameControllerModuleService;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;

/** The main games logic class */
public class Game implements GamePresidentAccess {
    /** event types */
    private enum EventTypes {
        PresidentSuggestChancellor,
        SuggestNextPresident,
        PresidentChoosingCards,
        ChancellorChoosingCards,
        KillPlayer,
        RevealingRoles,
        RevealeUpperCards
    }

    /** game controller proxy for requests */
    private GameControllerModuleService gameContrlProxy;

    /** the seed for the random usage, if 0 then seed will be chosen randomly */
    private int randomSeed = 0;
    /** list of players, if the player is killed then its index stores null */
    private ArrayList<PlayerModel> players;
    /** list of killed players */
    private ArrayList<Integer> killedPlayers;

    /** current president */
    private President president;
    /** current chancellor */
    private Chancellor chancellor;

    /** next president */
    private PlayerModel nextPresident;
    /** last president, can be null */
    private PlayerModel lastPresident;
    /** last chancellor, can be null */
    private PlayerModel lastChancellor;
    
    /** current number of spys in the parlament */
    private int spysInParlamentCount;
    /** current number of spys in the parlament */
    private int liberalsInParlamentCount;

    /** required spy card count for spy win */
    private int requiredSpyCount = 6;
    /** required liberal card count for liberal win */
    private int requiredLiberalCount = 5;
    /** Required spy card count that will allow spies to win, based on the Shadowleader becoming the Chancellor */
    private int spyCountToLetChancellorFinishTheGame = 4;
    /** The count of failed ellection, if the number reaches 4 then it gets to 0 and the card from the deck is added to the board */
    private int failedElectionsCount = 0;

    /** informs the subscribers about the card adding to the board */
    private ActObservers<Card> cardAddingToBoardObservers;
    /** informs the subscribers failed election */
    private ActObservers<Integer> failedElectionObservers; 
    /** informs the subscribers about the player getting killed */
    private ActObservers<Integer> playerKillingObservers; 

    /** the deck of the cards */
    private CardsArray cards;
    private boolean isVotingActive;

    /** all posible events */
    private EnumMap<EventTypes, Boolean> possibleEvents;
    /** the events that are required before the resetGameCycle */
    private EnumSet<EventTypes> requiredEventsBeforeCycleEnd;
    
    /**
     * 
     * @param playersCount the number of players
     * @param moduleProxy the proxy of ganecontroller to requset some actions
     * @param cardsCount the number of cards in the deck
     * @param spyCount the count of spy in the parlament without the shadowleader, the rest are going to be liberals
     */
    public Game(int playersCount, GameControllerModuleService moduleProxy, int cardsCount, int spyCount) {
        this.gameContrlProxy = moduleProxy;

        // cards
        if (spyCount == -1)
            spyCount = getSpyCount(playersCount); // remake
        int liberalCount = playersCount - spyCount - 1;
        if (liberalCount < 1) {
            System.out.println("Uncorrect spy count");
        }

        int spyCardCount = (int) (cardsCount * 11 / 17);
        this.cards = new CardsArray(spyCardCount, cardsCount-spyCardCount, 3, this.randomSeed);

        // role destribution
        PlayerModel.mainRoles roles[] = new PlayerModel.mainRoles[playersCount];
        for (int i=0; i<spyCount;i++) roles[i] = PlayerModel.mainRoles.Spy;
        for (int i=spyCount; i<liberalCount+spyCount; i++) roles[i] = PlayerModel.mainRoles.Liberal;
        roles[spyCount + liberalCount] = PlayerModel.mainRoles.ShadowLeader;
        ArrayShaffle.shuffle(roles, 0);

        this.players = new ArrayList<>(playersCount);
        this.killedPlayers = new ArrayList<>();
        for (int i=0; i<playersCount; i++) {
            this.players.add(new PlayerModel(i, roles[i]));
        }

        // politicals
        this.spysInParlamentCount = 0;
        this.liberalsInParlamentCount = 0;

        this.president = new President((GamePresidentAccess) this);
        this.president.change(players.get(0));
        this.president.expandPower(President.RightTypes.ChoosingChancellor, -1);
        this.president.expandPower(President.RightTypes.FinishCycle, -1);
        
        this.president.getCardChoosedObserver().subscribe((ArrayList<Card> cards) -> this.resultPresidentChoosingCards(cards));

        this.chancellor = new Chancellor();
        this.chancellor.getCardChoosedObserver().subscribe((ArrayList<Card> cards) -> this.resultChancllerChoosingCards(cards));

        // observers
        this.cardAddingToBoardObservers = new ActObservers<>();
        this.failedElectionObservers = new ActObservers<>();
        this.playerKillingObservers = new ActObservers<>();

        this.isVotingActive = false;
        this.nextPresident = null;

        this.possibleEvents = new EnumMap<>(EventTypes.class);
        this.requiredEventsBeforeCycleEnd = EnumSet.noneOf(EventTypes.class);

        possibleEvents.put(EventTypes.KillPlayer, true);
        possibleEvents.put(EventTypes.RevealingRoles, true);
        possibleEvents.put(EventTypes.RevealeUpperCards, true);
        possibleEvents.put(EventTypes.SuggestNextPresident, true);
        this.nextPresident = players.get(0);
        this.resetGameCycle();

        System.out.print("Players roles -> ");
        for (int i=0; i<this.players.size(); i++) {
            System.out.print(roles[i] + " ");
        }System.out.println();
        System.out.println("Roles distributed");
    }

    /**
     * finishes the game
     * @param result true if the liberal won, otherwise false
     */
    private void finishGame(boolean result) {
        int shadowLeaderId = -1;
        ArrayList<Integer> spyesId = new ArrayList<>();

        for (PlayerModel player : this.players) {
            if (player != null) {
                if (player.getRole() == PlayerModel.mainRoles.ShadowLeader)
                    shadowLeaderId = player.getId();
                else if (player.getRole() == PlayerModel.mainRoles.Spy)
                    spyesId.add(player.getId());
            }
        }

        this.president.setAllRightsIsAllowed(false, new HashSet<President.RightTypes>());
        this.chancellor.setAllRightsIsAllowed(false, new HashSet<Chancellor.RightTypes>());
        this.gameContrlProxy.finishGame(result, shadowLeaderId, spyesId);
    }

    /** Increases the liberal card count. If the maximum is exceeded, the game ends. */
    private void increaseSpyCount() {
        this.spysInParlamentCount++;

        if (this.spysInParlamentCount == 1) this.president.expandPower(President.RightTypes.RevealingRoles, 1);
        if (this.spysInParlamentCount == 3) this.president.expandPower(President.RightTypes.CheckingUpperThreeCards, 1);
        if (this.spysInParlamentCount == 3) this.president.expandPower(President.RightTypes.ChoosingNextPresident, 1);
        if (this.spysInParlamentCount == 4) this.president.expandPower(President.RightTypes.KillingPlayers, 2);
        if (this.spysInParlamentCount == 5) this.chancellor.expandPower(Chancellor.RightTypes.VetoPower, 1);

        if (this.spysInParlamentCount == this.requiredSpyCount) 
            finishGame(false);
    }

    /** Increases the liberal card count. If the maximum is exceeded, the game ends. */
    private void increaseLiberalCount() {
        this.liberalsInParlamentCount++;
        
        if (this.liberalsInParlamentCount == this.requiredLiberalCount) 
            finishGame(true);
    }

    /** adds card to the board 
     * @param card card that will be added
    */
    private void addCardToBoard(Card card) {
        if (card != null && card.state != Card.states.Undecleared) {
            if (card.state == Card.states.Spy) this.increaseSpyCount();
            else this.increaseLiberalCount();
            
            this.cardAddingToBoardObservers.informAll(card);
        }
    }

    /** goes to nexr game cycle with choosing new president */
    private void resetGameCycle() {
        this.lastPresident = this.president.getPlayer();
        if (this.chancellor != null) {
            this.lastChancellor = this.chancellor.getPlayer();
            this.chancellor.change(null);
        }

        this.president.setAllRightsIsAllowed(false, new HashSet<>(new ArrayList<>(Arrays.asList(President.RightTypes.ChoosingChancellor))));
        this.president.setIsAllowedRightUsage(President.RightTypes.ChoosingChancellor, true);
        this.goToNextPresinent();
        
        possibleEvents.put(EventTypes.PresidentSuggestChancellor, true);
    }
    
    /** method is called when the chanvellor voting is finished, based on the result go to next cycle or start the card removing
     * @param result result of the voting
     * @param candidate the candidate to become chancellor
     * @param votes the map of the playerIDs and their votes
     */
    private void choosingChancellorResult(boolean result, int candidate, Map<Integer, Boolean> votes) {
        if (result == false) {
            if (++this.failedElectionsCount == 4) {
                this.failedElectionsCount = 0;
                this.addCardToBoard(this.cards.pop());
            } 

            this.resetGameCycle();
            possibleEvents.put(EventTypes.PresidentSuggestChancellor, true);
            failedElectionObservers.informAll(this.failedElectionsCount);
        } else {
            if (this.spysInParlamentCount >= this.spyCountToLetChancellorFinishTheGame && 
                this.players.get(candidate).getRole() == PlayerModel.mainRoles.ShadowLeader) {

                finishGame(false);
                return;
            }

            this.chancellor.change(this.players.get(candidate));
            if (this.failedElectionsCount != 0) {
                this.failedElectionsCount = 0;
                this.failedElectionObservers.informAll(this.failedElectionsCount);
            }
            
            possibleEvents.put(EventTypes.PresidentChoosingCards, true);
            ArrayList<Card> cards = this.cards.popUppers(3);
            this.president.giveCards(cards);
        }
    } 

    /** changes the president towords the next one */
    private void goToNextPresinent() {
        if (this.nextPresident == null || this.players.get(this.nextPresident.getId()) == null) {
            int presidentIndex = this.president.getPlayer().getId();
            PlayerModel presidentCandidate;
            
            do {
                presidentIndex = (presidentIndex + 1) % this.players.size();
                presidentCandidate = this.players.get(presidentIndex);
            } while (
                presidentCandidate == null || wasInParlament(presidentIndex)
            );
            this.president.change(presidentCandidate);
        } 
        else this.president.change(this.nextPresident);

        this.nextPresident = null;
    }

    /** @return true if the player was in parlament in cycle before */
    private boolean wasInParlament(int playerID) {
        if ((this.lastChancellor != null && this.lastChancellor.getId() == playerID) || 
            (this.lastPresident != null && this.lastPresident.getId() == playerID))
            
            return true;
        return false;
    }

    /** @return true if the player is in parlament */
    private boolean isInParlament(int playerId) {
        if (this.president.getPlayer().getId() == playerId) 
            return true;
        if (this.chancellor.getPlayer() != null && this.chancellor.getPlayer().getId() == playerId) 
            return true;    

        return false;
    }

    // ---------events----------
    /** method to inform the result of chancellor election 
     * @param cards the cards that remained in the president hand
    */
    private boolean resultPresidentChoosingCards(ArrayList<Card> cards) {
        if (!this.possibleEvents.get(EventTypes.PresidentChoosingCards))
            return false;

        this.possibleEvents.put(EventTypes.ChancellorChoosingCards, true);
        this.possibleEvents.put(EventTypes.PresidentChoosingCards, false);
        
        this.chancellor.giveCards(cards);
        return true;
    }
    
    /** the result of chancellor choosing card to remove
     * @param cards the cards that remained after the card being removed
     */
    private boolean resultChancllerChoosingCards(ArrayList<Card> cards) {
        if (!this.possibleEvents.get(EventTypes.ChancellorChoosingCards))
            return false;
        
        this.possibleEvents.put(EventTypes.ChancellorChoosingCards, false);
        
        if (cards != null && cards.get(0) != null) 
            this.addCardToBoard(cards.get(0));
        else 
            this.addCardToBoard(null);
        
        
        this.president.setAllRightsIsAllowed(true, new HashSet<>(new ArrayList<>(Arrays.asList(President.RightTypes.ChoosingChancellor))));
        return true;
    }
    
    /**
     * the method to suggest chancellor by a president
     * @param executionResult the result of the execution
     * @param playerID the id of the player that is suggested to be chancellor
     */
    public void presidentSuggestChancellor(ExecutionStatusWrapper executionResult, int playerID) {
        if (!this.possibleEvents.get(EventTypes.PresidentSuggestChancellor) || !this.requiredEventsBeforeCycleEnd.isEmpty()) {
            executionResult.status = ExecutionStatus.IsntAllowedToUse;
            return;
        }
        
        if (isVotingActive || this.players.get(playerID) == null) {
            executionResult.status = ExecutionStatus.UnexpectedError;
            return;
        }
        if (wasInParlament(playerID) || this.president.getPlayer().getId() == playerID) {
            executionResult.status = ExecutionStatus.PlayerWasInParlament;
            return;
        }

        ArrayList<Integer> partisipators = new ArrayList<Integer>(this.players.size());
        for (int i=0; i<this.players.size(); i++) {
            if (this.players.get(i) != null)
                partisipators.add(this.players.get(i).getId());
        }

        Voting voting = new Voting(playerID, partisipators);
        voting.getEndingObservers().subscribe(
            new VoteObserver((boolean result, int candidate, Map<Integer, Boolean> votes) -> this.choosingChancellorResult(result, candidate, votes))
        );
        this.gameContrlProxy.requestVoting(voting, this.president.getPlayer().getId(), playerID);

        possibleEvents.put(EventTypes.PresidentSuggestChancellor, false);
        executionResult.status = ExecutionStatus.Executed;
    }

    /** the metod to set the next president that will be chosen after gameCycleRest
     * @param executionResult the status of the method execution
     * @param playerID the id of the player that will become the next president
    */
    public void setNextPresidentCandidate(ExecutionStatusWrapper executionResult, int playerID) {
        if (!this.possibleEvents.get(EventTypes.SuggestNextPresident)) {
            executionResult.status = ExecutionStatus.IsntAllowedToUse;
            return;
        }
        
        if (!isInParlament(playerID) && !wasInParlament(playerID)) {
            try {
                if (this.players.get(playerID) == null) {
                    executionResult.status = ExecutionStatus.NotChosenPlayer;
                    return;
                }
                this.nextPresident = this.players.get(playerID);

                requiredEventsBeforeCycleEnd.remove(EventTypes.SuggestNextPresident);
                executionResult.status = ExecutionStatus.Executed;
                return;
            } 
            catch (IndexOutOfBoundsException e) {
                executionResult.status = ExecutionStatus.UnexpectedError;
                System.err.println("Trying to set next president to uncorrect index" + e.getMessage());
            }
        } else 
            executionResult.status = ExecutionStatus.PlayerWasInParlament;
    }

    /** method to kill the player
     * @param executionResult the status of the method execution
     * @param playerID the id of the player that will be killed
     * @return the id of the player that was killed
     */
    public Integer killPlayer(ExecutionStatusWrapper executionResult, int playerID) {
        if (!this.possibleEvents.get(EventTypes.SuggestNextPresident)) {
            executionResult.status = ExecutionStatus.IsntAllowedToUse;
            return null;
        } if (this.players.get(playerID) == null || playerID == this.president.getPlayer().getId()) {
            executionResult.status = ExecutionStatus.NotChosenPlayer;
            return null;
        }

        if (this.players.get(playerID).getRole() == PlayerModel.mainRoles.ShadowLeader) {
            finishGame(true);
            return playerID;
        }

        this.killedPlayers.add(playerID);
        this.players.set(playerID, null);
        this.playerKillingObservers.informAll(playerID);

        requiredEventsBeforeCycleEnd.remove(EventTypes.KillPlayer);
        executionResult.status = ExecutionStatus.Executed;
        return playerID;
    }

    /** reveals the role of the player
     * @param executionResult the status of the method execution
     * @param playerID the id of the player whitch role will be revealed
     * @return returns the role of the player
     */
    public PlayerModel.mainRoles revealePlayerRole(ExecutionStatusWrapper executionResult, int playerID) {
        if (!this.possibleEvents.get(EventTypes.RevealingRoles) || this.players.get(playerID) == null) {
            executionResult.status = ExecutionStatus.UnexpectedError;
            return PlayerModel.mainRoles.Undefined;
        }
        
        executionResult.status = ExecutionStatus.Executed;
        if (this.players.get(playerID).getRole() == PlayerModel.mainRoles.ShadowLeader)
            return PlayerModel.mainRoles.Spy;
        return this.players.get(playerID).getRole();
    }

    /** returns the upper cards from the deck
     * @param executionResult the status of the method execution
     * @param count the count of cards that will be revealed
     * @return return the list of the upper cards
     */
    public Card[] revealeUpperCards(ExecutionStatusWrapper executionResult, int count) {
        if (!this.possibleEvents.get(EventTypes.RevealeUpperCards)) {
            executionResult.status = ExecutionStatus.IsntAllowedToUse;
            return null;
        }
        
        requiredEventsBeforeCycleEnd.remove(EventTypes.RevealeUpperCards);
        executionResult.status = ExecutionStatus.Executed;
        return this.cards.revealUpperCards(count);
    }

    /** president request to finish his turn
     * @param executionResult the status of the method execution
     */
    public void presidentFinishGameCycle(ExecutionStatusWrapper executionResult) {
        if (this.requiredEventsBeforeCycleEnd.isEmpty()) {
            this.resetGameCycle();
            executionResult.status = ExecutionStatus.Executed;
        } else 
            executionResult.status = ExecutionStatus.IsntAllowedToUse;
    }

    //--------getters----------
    /** @param playerCount the count of players
     * @return the supposed spy count for the playerCount */
    public int getSpyCount(int playerCount) {
        return (playerCount - 5) / 2 + 1;
    }

    /** reveals the role
     * @param playerId the id of the player whitch role will be revealed
     * @return the role
     */
    public PlayerModel.mainRoles getRole(int playerId) {
        return this.players.get(playerId).getRole();
    }

    /**
     * returns the president
     * @return the president obj, with access limitation
     */
    public PresidentAccess getPresident() {
        return this.president;
    }

    /**
     * returns the president
     * @return the president obj, with access limitation
     */
    public ChancellorAccess getChancellor() {
        return this.chancellor;
    }

    /**
     * 
     * @return the ids of the players
     */
    public int[] getPlayersIds() {
        int array[] = new int[this.players.size()];
        for (int i=0; i<this.players.size(); i++) {
            array[i] = this.players.get(i).getId();
        }

        return array;
    }

    /**
     * returns the list of the players that cant be chosen to be in the parlament
     * @return the list of the players that cant be chosen to be in the parlament
     */
    public ArrayList<Integer> getNonEligablePlayers() {
        ArrayList<Integer> players = new ArrayList<>();
        players.add(president.getPlayer().getId());
        if (lastPresident != null)
            players.add(lastPresident.getId());
        if (lastChancellor != null)
            players.add(lastChancellor.getId());
        if (chancellor.getPlayer() != null)
            players.add(chancellor.getPlayer().getId());
        if (nextPresident != null)
            players.add(nextPresident.getId());
        return players;
    }

    /**
     * returns the players that cant be chosen for a president right
     * @param playerID the id of the player that asks for the method
     * @param right the president right that is asked
     * @return the list of players that cant be chosen
     */
    public ArrayList<Integer> getNonChooseblePlayers(Integer playerID, President.RightTypes right) {
        ArrayList<Integer> forbidenPlayers = new ArrayList<>(killedPlayers);

        switch (right) {
            case ChoosingChancellor:
                forbidenPlayers.addAll(getNonEligablePlayers());
                forbidenPlayers.add(playerID);
                if (nextPresident != null)
                    forbidenPlayers.add(nextPresident.getId());
                break;

            case ChoosingNextPresident:
                forbidenPlayers.addAll(getNonEligablePlayers());
                forbidenPlayers.add(playerID);
                break;

            case KillingPlayers:
            case RevealingRoles:
                forbidenPlayers.add(playerID);
                break;
            
            default: break;
        }

        return forbidenPlayers;
    }

    /**
     * returns the players that cant be chosen for a chancellor right
     * @param playerID the id of the player that asks for the method
     * @param right the chancellor right that is asked
     * @return the list of players that cant be chosen
     */
    public ArrayList<Integer> getNonChooseblePlayers(Integer playerID, Chancellor.RightTypes right) {
        switch (right) {
            default:
                return new ArrayList<>(0);
        }
    }

    /**
     * returns the all spyes players
     * @return the all spyes players
     */
    public Map.Entry<ArrayList<Integer>, Integer> getSpyes() {
        ArrayList<Integer> spyes = new ArrayList<>();
        Integer shadowLeader = null;

        for (PlayerModel player : players) {
            if (player.getRole() == PlayerModel.mainRoles.ShadowLeader)
                shadowLeader = player.getId();
            else if (player.getRole() == PlayerModel.mainRoles.Spy)
                spyes.add(player.getId());
        }
        
        return new AbstractMap.SimpleEntry<ArrayList<Integer>, Integer> (spyes, shadowLeader);
    }
    
    /**
     * return the observers of the card adiing to the board
     * @return the observers of the card adiing to the board
     */
    public ActObservers<Card> getCardAddingToBoardObservers() {
        return this.cardAddingToBoardObservers;
    }

    /**
     * return the observers of the election failur
     * @return the observers of the election failur
     */
    public ActObservers<Integer> getFailedElectionObservers() {
        return this.failedElectionObservers;
    }
}
