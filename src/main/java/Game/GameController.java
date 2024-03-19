package Game;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import ChangebleRole.President;
import Player.BotPlayer;
import Player.PlayerData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import test_ui.App;
import test_ui.GameVisualization;

public class GameController implements GameControllerVisualService, GameControllerModuleService {
    private Game gameModel;
    private GameVisualization gameVisualization;
    private GameControllerVisualService visualProxy;
    private GameControllerModuleService moduleProxy;
    private Scene scene;

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

        int spyCount = 3;
        int liberalCount = 5;
        BotPlayer players[] = new BotPlayer[spyCount+liberalCount];
        ArrayList<PlayerData> playersData = new ArrayList<PlayerData>(players.length);
        for (int i=0; i<spyCount; i++) {
            players[i] = new BotPlayer(Integer.toString(i), "");
            playersData.add(players[i].getPlayerData());
        }

        for (int i=spyCount; i<spyCount+liberalCount; i++) {
            players[i] = new BotPlayer(Integer.toString(i), "");
            playersData.add(players[i].getPlayerData());
        }

        this.gameModel = new Game(playersData, 2, 3, 11, 6);
        System.out.println("Game model created");

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
    }

    public void yesVote() {
        this.gameVisualization.endVoting();
    }

    public void noVote() {
        // this.gameVisualization.showVotingResult(true, "Illucha", new String[]{"f", "fk", "fkkflkdlfdkfdkfld"}, new String[]{});
        this.gameVisualization.endVoting();
    }

    public void presidntRequest(President.presidntRights request) {
        this.gameModel.presidntRequest(request);
    }

    public void killPlayer(PlayerData player) {

    }

    public Scene getScene() {
        return this.scene;
    }
}
