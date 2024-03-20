package GameController;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Scanner;

import PlayerGameManager.*;
import javafx.scene.Scene;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.Chancellor;
import model.ChangebleRole.President;
import model.ChangebleRole.President.rights;
import model.Game.Game;
import model.Observers.ActionObserver;
import model.Voting.Voting;
import test_ui.GameVisualization;

public class GameController implements GameControllerModuleService {
    private ArrayList<PlayerGameManager> players;
    private Game gameModel;
    private GameVisualization gameVisualization;
    private GameControllerVisualService visualProxy;
    private GameControllerModuleService moduleProxy;
    private Scene scene;
    private Voting currentVoting;

    private ArrayList<Card> cards;

    private void getCards(ArrayList<Card> cards) {
        for (int i=0; i<cards.size(); i++) {
            System.out.print(cards.get(i).state + " ");
        }System.out.println();

        this.cards = cards;
    }

    public GameController(ArrayList<HumanPlayerGameManager> humanPlayers, int botsCount) {
        this.moduleProxy = (GameControllerModuleService) Proxy.newProxyInstance(
            GameControllerModuleService.class.getClassLoader(), 
            new Class<?>[]{GameControllerModuleService.class}, 
            new InvocationHandlerGameContrl(this)
        );

        ArrayList<PlayerGameManager> players = new ArrayList<>(humanPlayers.size()+botsCount);
        players.addAll(humanPlayers);
        for (int i=0; i<botsCount; i++) {
            players.add(new BotPlayerGameManager());
        }

        this.gameModel = new Game(players.size(), moduleProxy, 17, -1);
        
        this.gameModel.getPresident().getCardAddingObserver().subscribe(
            new ActionObserver<ArrayList<Card>>((ArrayList<Card> cards) -> this.getCards(cards)));
        
        this.gameModel.getChancellor().getCardAddingObserver().subscribe(
            new ActionObserver<ArrayList<Card>>((ArrayList<Card> cards) -> this.getCards(cards)));
        
        Integer ids[] = this.gameModel.getPlayersIds();
        for (int i=0; i<ids.length; i++) {
            players.get(i).setModelID(ids[i]);
        }

        this.players = players;

        this.visualProxy = (GameControllerVisualService) Proxy.newProxyInstance(
            GameControllerVisualService.class.getClassLoader(), 
            new Class<?>[]{GameControllerVisualService.class}, 
            new InvocationHandlerGameContrl(this)
        );

        for (HumanPlayerGameManager player : humanPlayers)
            player.setProxyGameController(this.visualProxy);
    }

    public void requestVoting(Voting voting) {
        for (PlayerGameManager player : this.players) {
            if (voting.isInGroup(player.getModelID()))
                player.voteForChancellor(voting);
        }
    }

    public Scene getScene() {
        return this.scene;
    }

    public void test() {
        Scanner scanner = new Scanner(System.in); 
        String input;
        int num;
        while (true) {
            input = scanner.nextLine();
            switch (input) {
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
                    this.gameModel.getPresident().suggestingChancellor(players.get(num).getModelID());
                    break;
                case "setNextPres":
                    num = scanner.nextInt();
                    this.gameModel.getPresident().choosingNextPresident(players.get(num).getModelID());
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
                
                case "f":
                    scanner.close();
                    return;
            }
        }
    }

    /*public static void main(String[] args) {
        GameController game = new GameController();
        game.test();
    }*/

    public void updateFailedElectionCount(int count) {}


    public void finishGame(boolean result) {}
}
