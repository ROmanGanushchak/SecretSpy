package PlayerGameManager;

import java.io.IOException;

import GameController.GameControllerVisualService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import model.Voting.Voting;
import test_ui.App;
import test_ui.GameVisualization;

public class HumanPlayerGameManager extends PlayerGameManager {
    private GameControllerVisualService gameController;

    private GameVisualization gameVisualization;
    private Scene scene;

    public void initializeScreen() {
        try {
            System.out.println("Player initialize");
            FXMLLoader sceneLoader = new FXMLLoader(App.class.getResource("gameVisualization.fxml"));
            this.scene = new Scene(sceneLoader.load());
            this.gameVisualization = sceneLoader.getController();
            this.gameVisualization.setGameContrlProxy(this.gameController);
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }

        // remove when add multiple screens
        this.scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            this.gameVisualization.resizeMainMuneX(oldValue, newValue);
        });

        this.scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            this.gameVisualization.resizeMainMuneY(oldValue, newValue);
        });
    }

    public void voteForChancellor(Voting voting) {
        voting.vote(0, true);
    }

    public Scene getScene() {
        System.out.println(scene);
        return this.scene;
    }

    public void setProxyGameController(GameControllerVisualService gameController) {
        this.gameController = gameController;
    }

    public int getPlayerID() {return 0;}
}
