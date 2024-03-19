package Player;

import java.io.IOException;

import Game.GameControllerVisualService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import test_ui.App;
import test_ui.GameVisualization;

public class HumanPlayer extends Player {
    private GameVisualization visualizator;
    private Scene scene;

    public HumanPlayer(String name, String iconImageUrl, GameControllerVisualService proxyGameController) {
        super(name, iconImageUrl);

        this.proxyGameController = proxyGameController;
        
        try {
            FXMLLoader sceneLoader = new FXMLLoader(App.class.getResource("gameVisualization.fxml"));
            this.scene = new Scene(sceneLoader.load(), 1200, 650);
            this.visualizator = sceneLoader.getController();
            this.visualizator.setGameContrlProxy(proxyGameController);
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void voteForChancler() {
        this.visualizator.startVoting();
    }

    public GameVisualization getGameVisualization() {
        return this.visualizator;
    }

    public Scene getScene() {
        return this.scene;
    }
}
