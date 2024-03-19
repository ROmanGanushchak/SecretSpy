package model.Game;

import model.Cards.*;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.*;
import model.Observers.ActObservers;
import model.Observers.ActionObserver;
import model.Observers.ObserversPublicAccess;
import model.Observers.ActionObserver.MethodToCall;
import model.Voting.VoteObserver;
import model.Voting.Voting;

import java.util.ArrayList;
import java.util.Map;

import GameController.GameControllerModuleService;

public class Game implements GamePresidentAccess {
    private GameControllerModuleService gameContrlProxy;

    private int randomSeed = 0;
    private ArrayList<PlayerModel> players;

    private President president;
    private Chancellor chancellor;

    private PlayerModel nextPresident;
    private PlayerModel lastPresident;
    private PlayerModel lastChancellor;
    
    private int spysInParlamentCount;
    private int liberalsInParlamentCount;

    private int requiredSpyCount = 6;
    private int requiredLiberalCount = 5;
    private int spyCountToLetChancellorFinishTheGame = 4;
    private int failedElectionsCount = 0;

    private ActObservers<Card> cardAddingToBoardObservers;
    private ActObservers<Integer> failedElectionObservers; 
    private ActObservers<Integer> playerKillingObservers; 

    private CardsArray cards;
    private boolean isVotingActive;
    
    // if spy count == -1 game chooses automaticly
    public Game(int playersCount, GameControllerModuleService moduleProxy, int cardsCount, int spyCount) {
        this.gameContrlProxy = moduleProxy;

        // cards
        if (spyCount == -1)
            spyCount = (playersCount-5) / 2 + 1; // remake
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
        for (int i=0; i<playersCount; i++) {
            this.players.add(new PlayerModel(i, roles[i]));
        }

        // politicals
        this.spysInParlamentCount = 0;
        this.liberalsInParlamentCount = 0;

        this.president = new President((GamePresidentAccess) this);
        this.president.change(players.get(0));
        
        MethodToCall<ArrayList<Card>> method = (ArrayList<Card> cards) -> this.resultPresidentChoosingCards(cards);
        this.president.getCardChoosedObserver().subscribe(new ActionObserver<ArrayList<Card>>(method));

        this.chancellor = new Chancellor();
        method = (ArrayList<Card> cards) -> this.resultChancllerChoosingCards(cards);
        this.chancellor.getCardChoosedObserver().subscribe(new ActionObserver<ArrayList<Card>>(method));

        // observers
        this.cardAddingToBoardObservers = new ActObservers<>();
        this.failedElectionObservers = new ActObservers<>();
        this.playerKillingObservers = new ActObservers<>();

        this.isVotingActive = false;
        this.nextPresident = null;

        System.out.print("Players roles -> ");
        for (int i=0; i<this.players.size(); i++) {
            System.out.print(roles[i] + " ");
        }System.out.println();
        System.out.println("Roles distributed");
    }

    private void increaseSpyCount() {
        System.out.println("Spy added to the board");
        this.spysInParlamentCount++;

        if (this.spysInParlamentCount == 1) this.president.expandPower(President.rights.RevealingRoles, 3);
        if (this.spysInParlamentCount == 3) this.president.expandPower(President.rights.CheckingUpperThreeCards, 1);
        if (this.spysInParlamentCount == 3) this.president.expandPower(President.rights.ChoosingNextPresident, 1);
        if (this.spysInParlamentCount == 4) this.president.expandPower(President.rights.KillingPlayers, 2);
        if (this.spysInParlamentCount == 5) this.chancellor.expandPower(Chancellor.rights.VetoPower, 1);

        if (this.spysInParlamentCount == this.requiredSpyCount) {
            this.gameContrlProxy.finishGame(true);
        }
    }

    private void increaseLiberalCount() {
        System.out.println("Liberal added to the board");
        this.liberalsInParlamentCount++;
        
        if (this.liberalsInParlamentCount == this.requiredLiberalCount) 
            this.gameContrlProxy.finishGame(false);
    }

    private void addCardToBoard(Card card) {
        if (card.state == Card.states.Spy) this.increaseSpyCount();
        else this.increaseLiberalCount();
        
        this.cardAddingToBoardObservers.informAll(card);
    }

    private void resetGameCycle() {
        System.out.println("reset start");
        this.lastPresident = this.president.getPlayer();
        if (this.chancellor == null)
            this.lastChancellor = null;
        else {
            this.lastChancellor = this.chancellor.getPlayer();
            this.chancellor.change(null);
        }
        System.out.println("Go to next pres");
        this.goToNextPresinent();
        System.out.println("New president setted " + this.president.getPlayer().getId());
    }

    public boolean presidentSuggestChancellor(int playerID) {
        System.out.println(this.lastChancellor);
        if (isVotingActive || this.players.get(playerID) == null) return false;
        if (isInParlament(playerID)) {
            System.out.println("Chancellor candidate is in parlament");
            return false;
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
        this.gameContrlProxy.requestVoting(voting);
        return true;
    }

    private void choosingChancellorResult(boolean result, int candidate, Map<Integer, Boolean> votes) {
        System.out.println("chosing candidate " + result);
        if (result == false) {
            if (++this.failedElectionsCount == 4) {
                this.failedElectionsCount = 0;
                this.addCardToBoard(this.cards.pop());
            }

            this.resetGameCycle();
            this.failedElectionObservers.informAll(this.failedElectionsCount);
        } else {
            if (this.spysInParlamentCount >= this.spyCountToLetChancellorFinishTheGame && 
                this.players.get(candidate).getRole() == PlayerModel.mainRoles.ShadowLeader) {

                this.gameContrlProxy.finishGame(false);
                return;
            }

            this.chancellor.change(this.players.get(candidate));
            System.out.println("Chancellot changed");
            if (this.failedElectionsCount != 0) {
                this.failedElectionsCount = 0;
                this.failedElectionObservers.informAll(this.failedElectionsCount);
            }

            ArrayList<Card> cards = this.cards.popUppers(3);
            this.president.giveCards(cards);
        }
    } 

    private void resultPresidentChoosingCards(ArrayList<Card> cards) {
        System.out.println("president return cards");
        this.chancellor.giveCards(cards);
    }
    
    private void resultChancllerChoosingCards(ArrayList<Card> cards) {
        System.out.println("chancle returned cards");
        if (cards != null && cards.get(0) != null) {
            this.addCardToBoard(cards.get(0));
        }
        this.resetGameCycle();
    }

    private void goToNextPresinent() {
        System.out.println("setting next pres");
        if (this.nextPresident == null || this.players.get(this.nextPresident.getId()) == null) {
            System.out.println("inside if");
            int presidentIndex = this.president.getPlayer().getId();
            PlayerModel presidentCandidate;
            
            do {
                presidentIndex = (presidentIndex + 1) % this.players.size();
                presidentCandidate = this.players.get(presidentIndex);
            } while (
                presidentCandidate == null || wasInParlament(presidentIndex)
            );
            this.president.change(this.players.get(presidentIndex));
        } 
        else this.president.change(this.nextPresident);

        this.nextPresident = null;
        System.out.println("Setting pres end");
    }

    private boolean wasInParlament(int playerID) {
        if ((this.lastChancellor != null && this.lastChancellor.getId() == playerID) || 
            (this.lastPresident != null && this.lastPresident.getId() == playerID))
            
            return true;
        return false;
    }

    private boolean isInParlament(int playerId) {
        System.out.println(playerId);
        if (this.president.getPlayer().getId() == playerId) return true;
        if (this.chancellor.getPlayer() == null) {
            if (this.lastChancellor != null && this.lastChancellor.getId() == playerId) 
                return true;
        } else {
            if (this.chancellor.getPlayer().getId() == playerId) return true;
        }

        return false;
    }

    public boolean setNextPresidentCandidate(int playerID) {
        System.out.println("next president is going to be " + playerID);
        if (!isInParlament(playerID)) {
            try {
                if (this.players.get(playerID) == null) {
                    System.out.println("Trying to use player that is dead");
                    return false;
                }
                this.nextPresident = this.players.get(playerID);
                return true;
            } 
            catch (IndexOutOfBoundsException e) {
                System.err.println("Trying to set next president to uncorrect index" + e.getMessage());
            }
        } else System.out.println("Trying to set a president that is in parlament");
        return false;
    }

    public boolean killPlayer(int playerID) {
        if (this.players.get(playerID) == null || playerID == this.president.getPlayer().getId()) return false;

        if (this.players.get(playerID).getRole() == PlayerModel.mainRoles.ShadowLeader) 
            this.gameContrlProxy.finishGame(true);

        System.out.println("Player " + playerID + " is killed");
        this.players.set(playerID, null);
        this.playerKillingObservers.informAll(playerID);
        return true;
    }

    public void suggestNextPresident(PlayerModel president) {
        this.nextPresident = president;
    }

    public PlayerModel.mainRoles revealePlayerRole(int playerID) {
        if (this.players.get(playerID) == null) return PlayerModel.mainRoles.Undefined;
        System.out.println(this.players.get(playerID).getRole());
        return this.players.get(playerID).getRole();
    }

    public Card[] revealeUpperCards(int count) {
        return this.cards.revealUpperCards(count);
    }

    public PresidentAccess getPresident() {
        return this.president;
    }

    public ChancellorAccess getChancellor() {
        return this.chancellor;
    }

    public Integer[] getPlayersIds() {
        Integer array[] = new Integer[this.players.size()];
        for (int i=0; i<this.players.size(); i++) {
            array[i] = this.players.get(i).getId();
        }

        return array;
    }

    public PlayerModel[] getNonEligablePlayers() {
        return new PlayerModel[]{this.lastChancellor, this.lastPresident};
    }

    public ObserversPublicAccess<ActionObserver<Card>> getCardAddingToBoardObservers() {
        return this.cardAddingToBoardObservers;
    }

    public ObserversPublicAccess<ActionObserver<Integer>> getFailedElectionObservers() {
        return this.failedElectionObservers;
    }
}
