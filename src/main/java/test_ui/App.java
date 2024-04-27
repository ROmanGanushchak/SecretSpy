package test_ui;

import javafx.application.Application;
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
        HumanPlayerGameManager player = new HumanPlayerGameManager(0);
        player.initializeScreen();
        this.player = player;

        ArrayList<HumanPlayerGameManager> players = new ArrayList<>();
        players.add(player);

        this.gameController = new GameController(players, 3);

        stage.setScene(player.getScene());
        stage.setMinWidth(1100);
        stage.setMinHeight(600);
        stage.show();
    }

    public void start() {
        launch();
    }
}