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
import model.ChangebleRole.President.rights;
import model.Game.Game;
import model.Observers.ActionObserver;
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
    private Integer currentPresident;

    private Scanner scanner = new Scanner(System.in);

    private ArrayList<Card> cards;

    private void getCards(ArrayList<Card> cards) {
        for (int i=0; i<cards.size(); i++) {
            System.out.print(cards.get(i).state + " ");
        }System.out.println();

        this.cards = cards;
    }

    public void makePresident(Integer player) {
        System.out.println("Make president");
        if (currentPresident != null)
            players.get(currentPresident).unmakePresident();
        
        players.get(player).makePresident(this.gameModel.getPresident().getCurrentRights());
        currentPresident = player;
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
            (ArrayList<Card> cards) -> this.players.get(currentPresident).giveCardsToRemove(cards));
        
        this.gameModel.getChancellor().getCardAddingObserver().subscribe((ArrayList<Card> cards) -> this.getCards(cards));
        this.gameModel.getPresident().getPlayerChangesObservers().subscribe((Integer player) -> makePresident(player));
        
        int ids[] = this.gameModel.getPlayersIds();

        this.players = players;
        this.humanPlayers = humanPlayers;

        Map<Integer, UserData.VisualData> visualData = new HashMap<>();
        for (PlayerGameManager player : players) 
            visualData.put(player.getPlayerID(), player.getVisualData());

        for (HumanPlayerGameManager player : humanPlayers) {
            player.setProxyGameController(this.visualProxy);
            this.gameModel.getCardAddingToBoardObservers().subscribe(((Card card) -> player.addCardToBoard(card.state)));
            
            this.gameModel.getFailedElectionObservers().subscribe((Integer failed) -> player.changeFailedVotingCount(failed));
            
            player.showRole(this.gameModel.getRole(player.getPlayerID()));
            player.setPlayersVisuals(visualData);

            player.initializeGame();
        }

        makePresident(this.gameModel.getPresident().getPlayer().getId());
        currentPresident = this.gameModel.getPresident().getPlayer().getId();
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
                this.gameModel.getPresident().revealeTheRole(num);
                break;
            case "rr":
                this.gameModel.getPresident().expandPower(President.rights.RevealingRoles, 2);
                break;
            case "pcheck3":
                Card cards[] = this.gameModel.getPresident().checkingUpperThreeCards();
                if (cards != null) 
                    System.out.println(cards[0].state + " " + cards[1].state + " " + cards[2].state);
                break;
            case "pchecka":
                this.gameModel.getPresident().expandPower(rights.CheckingUpperThreeCards, 2);
                break;
            case "chooseChancellor":
                num = scanner.nextInt();
                this.gameModel.getPresident().suggestingChancellor(players.get(num).getPlayerID());
                break;
            case "setNextPres":
                num = scanner.nextInt();
                this.gameModel.getPresident().choosingNextPresident(players.get(num).getPlayerID());
                break;
            case "activeNextPres":
                this.gameModel.getPresident().expandPower(rights.ChoosingNextPresident, 1);
                break;
            case "kill":
                num = scanner.nextInt();
                this.gameModel.getPresident().killingPlayers(num);
                break;
            case "killActivate":
                this.gameModel.getPresident().expandPower(President.rights.KillingPlayers, 1);
                break;
            
            //chancellor
            case "veto":
                this.gameModel.getChancellor().vetoPower();
                break;
            case "vetoActive":
                this.gameModel.getChancellor().expandPower(Chancellor.rights.VetoPower, 1);
                break;
            
            // electing
            case "ChooseChanCard":
                num = scanner.nextInt();
                System.out.println(this.gameModel.getChancellor().chooseCardToRemove(this.cards.get(num)));
                break;
            case "ChoosePresCard":
                num = scanner.nextInt();
                System.out.println(this.gameModel.getPresident().chooseCardToRemove(this.cards.get(num)));
                break;
            
            case "AddLiberalCardOnScreen":
                humanPlayers.get(0).addCardToBoard(Card.states.Liberal);
                break;
            
            case "AddSpyCardOnScreen":
                humanPlayers.get(0).addCardToBoard(Card.states.Spy);
                break;
            
            case "RealeCardsShow":
                Card card1 = new Card(); Card card2 = new Card(); Card card3 = new Card();
                card1.state = Card.states.Liberal; card2.state = Card.states.Liberal; card3.state = Card.states.Liberal;
                ArrayList<Card> cards1 = new ArrayList<>(Arrays.asList(card1, card2, card3));
                humanPlayers.get(0).revealCards(cards1);
                break;
            
            case "ShowPlayerKilling":
                humanPlayers.get(0).showDeathMessge();
                break;
        }
    }

    /*public static void main(String[] args) {
        GameController game = new GameController();
        game.test();
    }*/

    public void updateFailedElectionCount(int count) {}


    public void finishGame(boolean result) {}
}
