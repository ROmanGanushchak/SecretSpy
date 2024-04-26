package model.Game;

import model.Cards.*;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.*;
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

public class Game implements GamePresidentAccess {
    private enum EventTypes {
        PresidentSuggestChancellor,
        SuggestNextPresident,
        PresidentChoosingCards,
        ChancellorChoosingCards,
        KillPlayer,
        RevealingRoles,
        RevealeUpperCards
    }

    private GameControllerModuleService gameContrlProxy;

    private int randomSeed = 0;
    private ArrayList<PlayerModel> players;
    private ArrayList<Integer> killedPlayers;

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

    private EnumMap<EventTypes, Boolean> possibleEvents;
    private EnumSet<EventTypes> requiredEventsBeforeCycleEnd;
    
    // if spy count == -1 game chooses automaticly
    public Game(int playersCount, GameControllerModuleService moduleProxy, int cardsCount, int spyCount) {
        this.gameContrlProxy = moduleProxy;
        System.out.println("Game init");

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

        System.out.println("Before possible events");
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

        this.gameContrlProxy.finishGame(result, shadowLeaderId, spyesId);
    }

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

    private void increaseLiberalCount() {
        this.liberalsInParlamentCount++;
        
        if (this.liberalsInParlamentCount == this.requiredLiberalCount) 
            finishGame(true);
    }

    private void addCardToBoard(Card card) {
        if (card != null && card.state != Card.states.Undecleared) {
            if (card.state == Card.states.Spy) this.increaseSpyCount();
            else this.increaseLiberalCount();
            
            this.cardAddingToBoardObservers.informAll(card);
        }
    }

    private void resetGameCycle() {
        this.lastPresident = this.president.getPlayer();
        if (this.chancellor != null) {
            this.lastChancellor = this.chancellor.getPlayer();
            this.chancellor.change(null);
        }

        this.president.setAllRightsIsAllowed(false, new HashSet<>(new ArrayList<>(Arrays.asList(President.RightTypes.ChoosingChancellor))));
        // this.president.setIsAllowedRightUsage(President.RightTypes.ChoosingChancellor, true);
        this.goToNextPresinent();
        possibleEvents.put(EventTypes.PresidentSuggestChancellor, true);
        System.out.println("The right chooseChancellor " + this.president.getCurrentRights().get(President.RightTypes.ChoosingChancellor).getIsAllowed());
    }
    
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

    // ---------events----------
    private boolean resultPresidentChoosingCards(ArrayList<Card> cards) {
        if (!this.possibleEvents.get(EventTypes.PresidentChoosingCards))
            return false;

        this.possibleEvents.put(EventTypes.ChancellorChoosingCards, true);
        this.possibleEvents.put(EventTypes.PresidentChoosingCards, false);
        
        this.chancellor.giveCards(cards);
        return true;
    }
    
    private boolean resultChancllerChoosingCards(ArrayList<Card> cards) {
        if (!this.possibleEvents.get(EventTypes.ChancellorChoosingCards))
            return false;
        
        this.possibleEvents.put(EventTypes.ChancellorChoosingCards, false);
        
        if (cards != null && cards.get(0) != null) 
            this.addCardToBoard(cards.get(0));
        else 
            this.addCardToBoard(null);
        
        
        this.president.setAllRightsIsAllowed(true, new HashSet<>(new ArrayList<>(Arrays.asList(President.RightTypes.ChoosingChancellor))));
        // this.president.setIsAllowedRightUsage(President.RightTypes.ChoosingChancellor, false);
        System.out.println("ChooseChancellor set to false by result chose chancellor");
        return true;
    }
    
    public boolean presidentSuggestChancellor(int playerID) {
        System.out.println("Choosing chancellor in game");
        if (!this.possibleEvents.get(EventTypes.PresidentSuggestChancellor))
            return false;
        if (!this.requiredEventsBeforeCycleEnd.isEmpty())
            return false;
        
        if (isVotingActive || this.players.get(playerID) == null) return false;
        if (wasInParlament(playerID) || this.president.getPlayer().getId() == playerID) {
            System.out.println("Chancellor candidate was in parlament");
            return false;
        }

        System.out.println("is succesfull");

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
        return true;
    }

    public boolean setNextPresidentCandidate(int playerID) {
        if (!this.possibleEvents.get(EventTypes.SuggestNextPresident))
            return false;
        
        if (!isInParlament(playerID)) {
            try {
                if (this.players.get(playerID) == null) {
                    System.out.println("Trying to use player that is dead");
                    return false;
                }
                this.nextPresident = this.players.get(playerID);

                requiredEventsBeforeCycleEnd.remove(EventTypes.SuggestNextPresident);
                return true;
            } 
            catch (IndexOutOfBoundsException e) {
                System.err.println("Trying to set next president to uncorrect index" + e.getMessage());
            }
        } else System.out.println("Trying to set a president that is in parlament");

        return false;
    }

    public Integer killPlayer(int playerID) {
        if (!this.possibleEvents.get(EventTypes.SuggestNextPresident))
            return null;
        if (this.players.get(playerID) == null || playerID == this.president.getPlayer().getId()) 
            return null;

        if (this.players.get(playerID).getRole() == PlayerModel.mainRoles.ShadowLeader) 
            finishGame(true);

        this.killedPlayers.add(playerID);
        this.players.set(playerID, null);
        this.playerKillingObservers.informAll(playerID);

        requiredEventsBeforeCycleEnd.remove(EventTypes.KillPlayer);
        return playerID;
    }

    public PlayerModel.mainRoles revealePlayerRole(int playerID) {
        if (!this.possibleEvents.get(EventTypes.RevealingRoles) || this.players.get(playerID) == null)
            return PlayerModel.mainRoles.Undefined;
        if (this.players.get(playerID).getRole() == PlayerModel.mainRoles.ShadowLeader)
            return PlayerModel.mainRoles.Spy;
        return this.players.get(playerID).getRole();
    }

    public Card[] revealeUpperCards(int count) {
        if (!this.possibleEvents.get(EventTypes.RevealeUpperCards))
            return null;
        
        requiredEventsBeforeCycleEnd.remove(EventTypes.RevealeUpperCards);
        return this.cards.revealUpperCards(count);
    }

    public boolean presidentFinishGameCycle() {
        if (this.requiredEventsBeforeCycleEnd.isEmpty()) {
            this.resetGameCycle();
            return true;
        }

        return false;
    }

    //--------getters----------
    public int getSpyCount(int playerCount) {
        return (playerCount - 5) / 2 + 1;
    }

    public PlayerModel.mainRoles getRole(int playerId) {
        return this.players.get(playerId).getRole();
    }

    public PresidentAccess getPresident() {
        return this.president;
    }

    public ChancellorAccess getChancellor() {
        return this.chancellor;
    }

    public int[] getPlayersIds() {
        int array[] = new int[this.players.size()];
        for (int i=0; i<this.players.size(); i++) {
            array[i] = this.players.get(i).getId();
        }

        return array;
    }

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

    public ArrayList<Integer> getNonChooseblePlayers(Integer playerID, Chancellor.RightTypes right) {
        switch (right) {
            default:
                return new ArrayList<>(0);
        }
    }

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

    public ActObservers<Card> getCardAddingToBoardObservers() {
        return this.cardAddingToBoardObservers;
    }

    public ActObservers<Integer> getFailedElectionObservers() {
        return this.failedElectionObservers;
    }
}
