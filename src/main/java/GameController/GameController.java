package GameController;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import PlayerGameManager.*;
import User.UserData;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.Chancellor;
import model.ChangebleRole.Right;
import model.ChangebleRole.President;
import model.ChangebleRole.Right.ExecutionStatus;
import model.ChangebleRole.Right.ExecutionStatusWrapper;
import model.ChangebleRole.President.RightTypes;
import model.Game.Game;
import model.Game.PlayerModel;
import model.Voting.VoteObserver;
import model.Voting.Voting;
import test_ui.GameVisualization;
import test_ui.Components.PlayerPaneController.Icons;

public class GameController implements GameControllerModuleService, GameControllerVisualService {
    private ArrayList<PlayerGameManager> players;
    private ArrayList<HumanPlayerGameManager> humanPlayers;
    private Game gameModel;
    private GameVisualization gameVisualization;
    private GameControllerVisualService visualProxy;
    private GameControllerModuleService moduleProxy;
    private Voting currentVoting;
    private Integer currentPresident = -1;
    private Integer currentChancellor = -1;

    private Scanner scanner = new Scanner(System.in);

    private void makePresident(Integer player) {
        if (currentPresident != -1)
            players.get(currentPresident).unmakePresident();
        
        players.get(player).makePresident(this.gameModel.getPresident().getCurrentRights());
        for (PlayerGameManager humanPlayer : players) {
            humanPlayer.changePresident(currentPresident, player);
        }

        currentPresident = player; 
    }

    private void makeChancellor(Integer player) {
        if (currentChancellor != -1)
            players.get(currentChancellor).unmakeChancellor();

        for (PlayerGameManager humanPlayer : players) {
            humanPlayer.changeChancellor(currentChancellor, player);
        }
        
        if (player != -1)
            players.get(player).makeChancellor(this.gameModel.getChancellor().getCurrentRights());
        currentChancellor = player;
    }

    public GameController(ArrayList<HumanPlayerGameManager> humanPlayers, int botsCount) {
        this.moduleProxy = (GameControllerModuleService) Proxy.newProxyInstance(
            GameControllerModuleService.class.getClassLoader(), 
            new Class<?>[]{GameControllerModuleService.class}, 
            new InvocationHandlerGameContrl(this)
        );

        this.visualProxy = (GameControllerVisualService) Proxy.newProxyInstance(
            GameControllerVisualService.class.getClassLoader(), 
            new Class<?>[]{GameControllerVisualService.class}, 
            new InvocationHandlerGameContrl(this)
        );

        ArrayList<PlayerGameManager> players = new ArrayList<>(humanPlayers.size()+botsCount);
        players.addAll(humanPlayers);

        this.gameModel = new Game(humanPlayers.size() + botsCount, moduleProxy, 17, -1);
        int spyCount = this.gameModel.getSpyCount(humanPlayers.size() + botsCount);
        Map.Entry<ArrayList<Integer>, Integer> spyes = this.gameModel.getSpyes();
        
        int ids[] = this.gameModel.getPlayersIds();

        this.players = players;
        this.humanPlayers = humanPlayers;

        Map<Integer, UserData.VisualData> visualData = new HashMap<>();
        for (HumanPlayerGameManager player : humanPlayers) 
            visualData.put(player.getPlayerID(), player.getVisualData());
        
        for (int i=humanPlayers.size(); i < humanPlayers.size()+botsCount; i++) {
            PlayerGameManager player;
            if (this.gameModel.getRole(ids[i]) == PlayerModel.mainRoles.Liberal) {
                player = new LiberalBotPlayerGameManager(ids[i], spyCount, ids);
                player.showRole(this.gameModel.getRole(ids[i]));
            } else { 
                player = new SpyBotPlayerGameManager(ids[i], spyCount, ids);
                player.showRole(this.gameModel.getRole(ids[i]), spyes.getKey(), spyes.getValue());
            }
            
            players.add(player);
            player.setProxyGameController(visualProxy);
            visualData.put(player.getPlayerID(), player.getVisualData());
        }

        for (HumanPlayerGameManager player : humanPlayers) {
            player.setProxyGameController(this.visualProxy);

            player.setPlayersVisuals(visualData);
            if (this.gameModel.getRole(player.getPlayerID()) == PlayerModel.mainRoles.Liberal)
                player.showRole(this.gameModel.getRole(player.getPlayerID()));
            else 
                player.showRole(this.gameModel.getRole(player.getPlayerID()), spyes.getKey(), spyes.getValue());

            player.initializeGame();
        }

        this.gameModel.getPresident().getCardAddingObserver().subscribe(
            (ArrayList<Card> cards) -> this.players.get(currentPresident).giveCardsToRemove(cards));
        
        this.gameModel.getChancellor().getCardAddingObserver().subscribe(
            (ArrayList<Card> cards) -> this.players.get(currentChancellor).giveCardsToRemove(cards));

        this.gameModel.getPresident().getPlayerChangesObservers().subscribe((Integer player) -> {makePresident(player);});
        this.gameModel.getChancellor().getPlayerChangesObservers().subscribe((Integer player) -> makeChancellor(player));
        
        this.gameModel.getPresident().getPowerChangerObserver().subscribe(
            (Map.Entry<President.RightTypes, Right> right) -> {
                    if (currentPresident != -1) 
                        players.get(currentPresident).changePresidentRight(right);
                });
            
        this.gameModel.getChancellor().getPowerChangerObserver().subscribe(
            (Map.Entry<Chancellor.RightTypes, Right> right) -> {
                    if (currentChancellor != -1) 
                        players.get(currentChancellor).changeChancellorRight(right);
                });

        this.gameModel.getCardAddingToBoardObservers().subscribe(
            (Card card) -> {
                for (PlayerGameManager player : players) {
                    player.addCardToBoard(card.state);
                }
            });
        
        this.gameModel.getFailedElectionObservers().subscribe(
            (Integer count) -> {
                for (HumanPlayerGameManager player : humanPlayers) {
                    player.changeFailedVotingCount(count);
                }
            });

        makePresident(this.gameModel.getPresident().getPlayer().getId());
    }

    public void requestVoting(Voting voting, int presidentId, int chancellorId) {
        voting.getEndingObservers().subscribe(
            new VoteObserver( (boolean result, int candidateId, Map<Integer, Boolean> votes) ->
                this.showVotingResults(result, candidateId, votes))
        );

        for (PlayerGameManager player : this.players) {
            if (voting.isInGroup(player.getPlayerID()))
                player.voteForChancellor(voting, presidentId, chancellorId);
        }
    }

    private void showVotingResults(boolean result, int candidateId, Map<Integer, Boolean> votes) {
        for (HumanPlayerGameManager player : this.humanPlayers) {
            player.showVotingResult(result, candidateId, votes);
        }
    }

    public void executeCommand(String command) {
        int num;
        ExecutionStatusWrapper executionStatus = new ExecutionStatusWrapper();
        switch (command) {
            // president
            case "cr":
                num = scanner.nextInt();
                this.gameModel.getPresident().useRight(President.RightTypes.RevealingRoles, executionStatus);
                break;
            case "rr":
                this.gameModel.getPresident().expandPower(President.RightTypes.RevealingRoles, 2);
                break;
            case "pcheck3":
                Card cards[] = (Card[]) this.gameModel.getPresident().useRight(President.RightTypes.CheckingUpperThreeCards, executionStatus);
                if (cards != null) 
                    System.out.println(cards[0].state + " " + cards[1].state + " " + cards[2].state);
                break;
            case "pchecka":
                this.gameModel.getPresident().expandPower(RightTypes.CheckingUpperThreeCards, 2);
                break;
            case "chooseChancellor":
                num = scanner.nextInt();
                this.gameModel.getPresident().useRight(President.RightTypes.ChoosingChancellor, executionStatus, players.get(num).getPlayerID());
                break;
            case "setNextPres":
                num = scanner.nextInt();
                this.gameModel.getPresident().useRight(President.RightTypes.ChoosingNextPresident, executionStatus, players.get(num).getPlayerID());
                break;
            case "activeNextPres":
                this.gameModel.getPresident().expandPower(RightTypes.ChoosingNextPresident, 1);
                break;
            case "kill":
                num = scanner.nextInt();
                this.executePresidentRight(gameModel.getPresident().getPlayer().getId(), President.RightTypes.KillingPlayers, num);
                // this.gameModel.getPresident().useRight(President.RightTypes.KillingPlayers, num);
                break;
            case "killActivate":
                this.gameModel.getPresident().expandPower(President.RightTypes.KillingPlayers, 1);
                break;
            
            //chancellor
            case "veto":
                this.gameModel.getChancellor().useRight(Chancellor.RightTypes.VetoPower, executionStatus);
                break;
            case "vetoActive":
                this.gameModel.getChancellor().expandPower(Chancellor.RightTypes.VetoPower, 1);
                break;
            
            // electing
            case "ChooseChanCard":
                num = scanner.nextInt();
                break;
            case "ChoosePresCard":
                num = scanner.nextInt();
                break;
            
            case "AddLiberalCardOnScreen":
                humanPlayers.get(0).addCardToBoard(Card.states.Liberal);
                break;
            
            case "AddSpyCardOnScreen":
                humanPlayers.get(0).addCardToBoard(Card.states.Spy);
                break;
            
            // case "RealeCardsShow":
            //     Card card1 = new Card(); Card card2 = new Card(); Card card3 = new Card();
            //     card1.state = Card.states.Liberal; card2.state = Card.states.Liberal; card3.state = Card.states.Liberal;
            //     ArrayList<Card> cards1 = new ArrayList<>(Arrays.asList(card1, card2, card3));
            //     humanPlayers.get(0).revealCards(cards1);
            //     break;
            
            case "ShowPlayerKilling":
                humanPlayers.get(0).showDeathMessge();
                break;
        }
    }
    
    public void finishGame(boolean result, int shadowLeaderId, ArrayList<Integer> spyesId) {
        for (HumanPlayerGameManager player : humanPlayers) 
            player.finishGame(result, shadowLeaderId, spyesId);
    }

    public void informCardRemoved(Integer card, Integer playerID) {
        if (playerID == currentPresident) {
            this.gameModel.getPresident().chooseCardToRemove(card);
        } else if (playerID == currentChancellor) {
            this.gameModel.getChancellor().chooseCardToRemove(card);
        }
    }

    public void executePresidentRight(Integer playerID, President.RightTypes right, Object... parametrs) {
        if (playerID != gameModel.getPresident().getPlayer().getId())
            return;
        
        ExecutionStatusWrapper executionStatus = new ExecutionStatusWrapper();
        Object result = this.gameModel.getPresident().useRight(right, executionStatus, parametrs);

        if (executionStatus.status != ExecutionStatus.Executed) {
            this.players.get(playerID).informPresidentRightUsage(right, executionStatus);
            return;
        }
        
        switch (right) {
            case ChoosingChancellor:
                Boolean isSucces1 = (Boolean) result;
                break;

            case CheckingUpperThreeCards:
                this.players.get(playerID).revealCards((Card[]) result);
                break;
            
            case KillingPlayers:
                if (result != null) {
                    Integer index = (Integer) result;
                    System.out.printf("Player %d killed\n", index);
                    players.get(index).kill();
                    for (HumanPlayerGameManager player : humanPlayers) {
                        player.setIconPlayerPane(Icons.KILLED, index);
                    }
                }
                break;
            
            case RevealingRoles:
                PlayerModel.mainRoles role = (PlayerModel.mainRoles) result;
                if (role != PlayerModel.mainRoles.Undefined) 
                    players.get(playerID).showRole(role);
                break;
            
            case ChoosingNextPresident:
                Boolean isSucces = (Boolean) result;
                break;
            

            default: 
                break;
        }

        for (PlayerGameManager player : players) 
            player.informPresidentRightUsage(right, executionStatus);
    }

    public void executeChancellorRight(Integer playerID, Chancellor.RightTypes right, Object... parametrs) {
        ExecutionStatusWrapper executionStatus = new ExecutionStatusWrapper();
        if (playerID == gameModel.getChancellor().getPlayer().getId()) {
            this.gameModel.getChancellor().useRight(right, executionStatus, parametrs);

            if (executionStatus.status != ExecutionStatus.Executed) {
                this.players.get(playerID).informChancellorRightUsage(right, executionStatus);
            } else {
                for (PlayerGameManager player : players) 
                    player.informChancellorRightUsage(right, executionStatus);
            }
        }
        
    }

    public ArrayList<Integer> getNonChooseblePlayers(Integer playerID, President.RightTypes right) {
        return gameModel.getNonChooseblePlayers(playerID, right);
    }

    public ArrayList<Integer> getNonChooseblePlayers(Integer playerID, Chancellor.RightTypes right) {
        return gameModel.getNonChooseblePlayers(playerID, right);
    }
}
