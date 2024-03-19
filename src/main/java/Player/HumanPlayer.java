package Player;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import model.Voting.Voting;
import test_ui.App;
import test_ui.GameVisualization;

public class HumanPlayer extends Player {
    private GameVisualization gameVisualization;
    private Scene scene;

    public HumanPlayer(int id, String name, String iconImageUrl) {
        super(id, name, iconImageUrl);
    }

    public void initializeScreen() {
        try {
            FXMLLoader sceneLoader = new FXMLLoader(App.class.getResource("gameVisualization.fxml"));
            this.scene = new Scene(sceneLoader.load(), 1200, 650);
            this.gameVisualization = sceneLoader.getController();
            this.gameVisualization.setGameContrlProxy(this.proxyGameController);
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

    public void voteForChancler(Voting voting) {

    }
}
