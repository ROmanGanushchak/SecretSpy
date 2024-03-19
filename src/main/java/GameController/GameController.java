package GameController;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Scanner;

import Player.*;
import Player.Player.UserData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.Chancellor;
import model.ChangebleRole.President;
import model.ChangebleRole.President.rights;
import model.Game.Game;
import model.Observers.ActionObserver;
import model.Voting.Voting;
import test_ui.App;
import test_ui.GameVisualization;

public class GameController implements GameControllerModuleService {
    private Player players[];
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

    public GameController() {
        this.visualProxy = (GameControllerVisualService) Proxy.newProxyInstance(
            GameControllerVisualService.class.getClassLoader(), 
            new Class<?>[]{GameControllerVisualService.class}, 
            new InvocationHandlerGameContrl(this)
        );

        this.moduleProxy = (GameControllerModuleService) Proxy.newProxyInstance(
            GameControllerModuleService.class.getClassLoader(), 
            new Class<?>[]{GameControllerModuleService.class}, 
            new InvocationHandlerGameContrl(this)
        );
        System.out.println("Proxy created");

        int spyCount = 2;
        int liberalCount = 5;
        ArrayList<UserData> playersData = new ArrayList<>(spyCount+liberalCount);
        this.players = new Player[spyCount+liberalCount+1];
        for (int i=0; i<spyCount+liberalCount+1; i++) {
            playersData.add(new UserData(i, Integer.toString(i), ""));
            this.players[i] = new BotPlayer(playersData.get(i));
            this.players[i].getPlayerData().id = i;
        }
        System.out.println("players created");

        this.gameModel = new Game(playersData.size(), moduleProxy, 5, -1);
        
        this.gameModel.getPresident().getCardAddingObserver().subscribe(
            new ActionObserver<ArrayList<Card>>((ArrayList<Card> cards) -> this.getCards(cards)));
        
        this.gameModel.getChancellor().getCardAddingObserver().subscribe(
            new ActionObserver<ArrayList<Card>>((ArrayList<Card> cards) -> this.getCards(cards)));
        
        Integer ids[] = this.gameModel.getPlayersIds();
        for (int i=0; i<ids.length; i++) {
            this.players[i].getPlayerData().modelID = ids[i];
        }

        try {
            FXMLLoader sceneLoader = new FXMLLoader(App.class.getResource("gameVisualization.fxml"));
            this.scene = new Scene(sceneLoader.load(), 1200, 650);
            this.gameVisualization = sceneLoader.getController();
            this.gameVisualization.setGameContrlProxy(this.visualProxy);
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }

        this.scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            this.gameVisualization.resizeMainMuneX(oldValue, newValue);
        });

        this.scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            this.gameVisualization.resizeMainMuneY(oldValue, newValue);
        });

        System.out.println("Game model created");
        // this.test();
    }

    public void requestVoting(Voting voting) {
        for (Player player : this.players) {
            if (voting.isInGroup(player.getPlayerData().modelID))
                player.voteForChancler(voting);
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
                    this.gameModel.getPresident().suggestingChancellor(players[num].getPlayerData().modelID);
                    break;
                case "setNextPres":
                    num = scanner.nextInt();
                    this.gameModel.getPresident().choosingNextPresident(players[num].getPlayerData().modelID);
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
