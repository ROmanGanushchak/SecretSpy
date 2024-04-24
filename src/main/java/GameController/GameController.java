package GameController;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import PlayerGameManager.*;
import User.UserData;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.Chancellor;
import model.ChangebleRole.President;
import model.ChangebleRole.President.RightTypes;
import model.Game.Game;
import model.Game.PlayerModel;
import model.Game.PlayerModel.mainRoles;
import model.Voting.VoteObserver;
import model.Voting.Voting;
import test_ui.GameVisualization;

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
        System.out.printf("Make president %d", player);
        if (currentPresident != -1)
            players.get(currentPresident).unmakePresident();
        
        players.get(player).makePresident(this.gameModel.getPresident().getCurrentRights());
        for (HumanPlayerGameManager humanPlayer : humanPlayers) {
            humanPlayer.changePresident(currentPresident, player);
        }

        currentPresident = player; 
    }

    private void makeChancellor(Integer player) {
        if (currentPresident != -1)
            players.get(currentPresident).unmakeChancellor();

        for (HumanPlayerGameManager humanPlayer : humanPlayers) {
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
        for (int i=humanPlayers.size(); i<botsCount+humanPlayers.size(); i++) {
            players.add(new BotPlayerGameManager(i));
        }

        this.gameModel = new Game(players.size(), moduleProxy, 17, -1);
        
        this.gameModel.getPresident().getCardAddingObserver().subscribe(
            (ArrayList<Card> cards) -> {
                System.out.printf("Giving cards to president %d\n", currentPresident);
                this.players.get(currentPresident).giveCardsToRemove(cards);});
        
        this.gameModel.getChancellor().getCardAddingObserver().subscribe(
            (ArrayList<Card> cards) -> this.players.get(currentChancellor).giveCardsToRemove(cards));

        this.gameModel.getPresident().getPlayerChangesObservers().subscribe((Integer player) -> makePresident(player));
        this.gameModel.getChancellor().getPlayerChangesObservers().subscribe((Integer player) -> makeChancellor(player));

        this.gameModel.getCardAddingToBoardObservers().subscribe(
            (Card card) -> {
                for (HumanPlayerGameManager player : humanPlayers) {
                    player.addCardToBoard(card.state);
                }
            });
        
        this.gameModel.getFailedElectionObservers().subscribe(
            (Integer count) -> {
                for (HumanPlayerGameManager player : humanPlayers) {
                    player.changeFailedVotingCount(count);
                }
            });
        
        
        
        int ids[] = this.gameModel.getPlayersIds();

        this.players = players;
        this.humanPlayers = humanPlayers;

        System.out.println("Before visual data");
        Map<Integer, UserData.VisualData> visualData = new HashMap<>();
        for (PlayerGameManager player : players) 
            visualData.put(player.getPlayerID(), player.getVisualData());

        for (HumanPlayerGameManager player : humanPlayers) {
            player.setProxyGameController(this.visualProxy);

            player.showRole(this.gameModel.getRole(player.getPlayerID()));
            player.setPlayersVisuals(visualData);

            player.initializeGame();
        }

        // System.out.println("Before make president");
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
        switch (command) {
            // president
            case "cr":
                num = scanner.nextInt();
                this.gameModel.getPresident().useRight(President.RightTypes.RevealingRoles);
                break;
            case "rr":
                this.gameModel.getPresident().expandPower(President.RightTypes.RevealingRoles, 2);
                break;
            case "pcheck3":
                Card cards[] = (Card[]) this.gameModel.getPresident().useRight(President.RightTypes.CheckingUpperThreeCards);
                if (cards != null) 
                    System.out.println(cards[0].state + " " + cards[1].state + " " + cards[2].state);
                break;
            case "pchecka":
                this.gameModel.getPresident().expandPower(RightTypes.CheckingUpperThreeCards, 2);
                break;
            case "chooseChancellor":
                num = scanner.nextInt();
                this.gameModel.getPresident().useRight(President.RightTypes.ChoosingChancellor, players.get(num).getPlayerID());
                break;
            case "setNextPres":
                num = scanner.nextInt();
                this.gameModel.getPresident().useRight(President.RightTypes.ChoosingNextPresident, players.get(num).getPlayerID());
                break;
            case "activeNextPres":
                this.gameModel.getPresident().expandPower(RightTypes.ChoosingNextPresident, 1);
                break;
            case "kill":
                num = scanner.nextInt();
                this.gameModel.getPresident().useRight(President.RightTypes.KillingPlayers, num);
                break;
            case "killActivate":
                this.gameModel.getPresident().expandPower(President.RightTypes.KillingPlayers, 1);
                break;
            
            //chancellor
            case "veto":
                this.gameModel.getChancellor().useRight(Chancellor.RightTypes.VetoPower);
                break;
            case "vetoActive":
                this.gameModel.getChancellor().expandPower(Chancellor.RightTypes.VetoPower, 1);
                break;
            
            // electing
            case "ChooseChanCard":
                num = scanner.nextInt();
                System.out.println(this.gameModel.getChancellor().chooseCardToRemove(num));
                break;
            case "ChoosePresCard":
                num = scanner.nextInt();
                System.out.println(this.gameModel.getPresident().chooseCardToRemove(num));
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

    public void executePresidentRight(President.RightTypes right, Object... params) {
        Object result = this.gameModel.getPresident().useRight(right, params);

        // switch (right) {
        //     case Ch
        // }
    }

    public void executeChancellorRight(Chancellor.RightTypes right, Object... params) {
        this.gameModel.getChancellor().useRight(right, params);
    }

    public void finishGame(boolean result, int shadowLeaderId, ArrayList<Integer> spyesId) {
        for (HumanPlayerGameManager player : humanPlayers) 
            player.finishGame(result, shadowLeaderId, spyesId);
    }

    public void informCardRemoved(Integer card, Integer playerID) {
        System.out.printf("Inform deleted, %d %d, %d %d", card, playerID, currentPresident, currentChancellor);
        if (playerID == currentPresident) {
            this.gameModel.getPresident().chooseCardToRemove(card);
        } else if (playerID == currentChancellor) {
            this.gameModel.getChancellor().chooseCardToRemove(card);
        }
    }

    public void executePresidentRight(Integer playerID, President.RightTypes right, Object... parametrs) {
        System.out.println("Controller president right asked " + right.toString());
        if (playerID != gameModel.getPresident().getPlayer().getId())
            return;
        
        Object result = this.gameModel.getPresident().useRight(right, parametrs);
        System.out.println("Before Right switch");
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
                    players.get(index).kill();
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
    }

    public void executeChancellorRight(Integer playerID, Chancellor.RightTypes right, Object... parametrs) {
        System.out.println("Controller chancellor right asked " + right.toString());
        if (playerID == gameModel.getChancellor().getPlayer().getId())
            this.gameModel.getChancellor().useRight(right, parametrs);
    }
}
