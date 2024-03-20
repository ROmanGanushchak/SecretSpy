package test_ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

import GameController.GameController;
import PlayerGameManager.HumanPlayerGameManager;

public class App extends Application {
    private GameController gameController;
    private HumanPlayerGameManager player;
    

    @Override
    public void start(Stage stage) throws IOException {
        HumanPlayerGameManager player = new HumanPlayerGameManager();
        player.initializeScreen();
        this.player = player;

        ArrayList<HumanPlayerGameManager> players = new ArrayList<>();
        players.add(player);

        this.gameController = new GameController(players, 9);

        System.out.println("Before set");

        stage.setScene(player.getScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}