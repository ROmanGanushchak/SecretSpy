package test_ui;

import model.ChangebleRole.President;
import model.ChangebleRole.President.rights;
import test_ui.Components.LiberalBoardController;
import test_ui.Components.SpyBoardController;
import GameController.GameControllerVisualService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
import javafx.util.Pair;

public class GameVisualization{ // game visualization має компоненти спільні для всіх гравців

    @FXML
    private AnchorPane mainPlane;

    @FXML
    private AnchorPane liberalBoard;

    @FXML
    private AnchorPane spyBoard;

    @FXML
    private AnchorPane voteSurface;
    private VoteManeger voteManeger;

    @FXML
    private AnchorPane presidntUiLayer;
    @FXML
    private Button button;

    private Scale lastTransformation;
    private GameControllerVisualService gameControllerProxy;

    private LiberalBoardController liberalBoardController;
    private SpyBoardController spyBoardController;

    @FXML
    void buttonPressed(ActionEvent event) {
        this.gameControllerProxy.presidntRequest(President.rights.RevealingRoles);
    }

    public void resizeMainMuneX(Number oldNumber, Number newNumber) {
        lastTransformation.setX((newNumber.doubleValue() / oldNumber.doubleValue()) * lastTransformation.getX());
    }

    public void resizeMainMuneY(Number oldNumber, Number newNumber) {
        lastTransformation.setY((newNumber.doubleValue() / oldNumber.doubleValue()) * lastTransformation.getY());
    }

    public Pair<Number, Number> getMainSurfaceSize() {
        return new Pair<Number,Number>(this.mainPlane.getWidth(), this.mainPlane.getHeight());
    }

    @FXML
    public void initialize() {
        try {
            this.lastTransformation = new Scale(1, 1, 0, 0);
            mainPlane.getTransforms().add(lastTransformation);

            System.out.println(App.class.getResource("liberalBoard.fxml"));
            Parent liberal = Component.initialize(App.class.getResource("liberalBoard.fxml"), this.liberalBoard);
            Parent spy = Component.initialize(App.class.getResource("spyBoard.fxml"), this.spyBoard);
            this.voteManeger = new VoteManeger(this.voteSurface);

            this.liberalBoardController = (LiberalBoardController) liberal.getProperties().get("controller");
            this.spyBoardController = (SpyBoardController) spy.getProperties().get("controller");
        } catch (Exception e) {
            System.out.println("unsuccesfull initilize of main controller");
            e.printStackTrace();
        }
    }

    public void setGameContrlProxy(GameControllerVisualService gameContrlProxy) {
        this.gameControllerProxy = gameContrlProxy;
        this.voteManeger.setGameContrlProxy(this.gameControllerProxy);
    }

    public void startVoting() {
        this.voteManeger.start();
    }

    public void endVoting() {
        this.voteManeger.end();
    }

    public void showVotingResult(boolean voteResult, String presidentName, String yesVoteNames[], String noVoteNames[]) {
        this.voteManeger.showResult(voteResult, presidentName, yesVoteNames, noVoteNames);
    }

    public LiberalBoardController getLiberalBoardController() {
        return this.liberalBoardController;
    }

    public SpyBoardController getSpyBoardController() {
        return this.spyBoardController;
    }
}

/*Label fpsLabel = new Label("FPS: ");
            new AnimationTimer() {
                private long lastUpdate = 0;
                private long frameCount = 0;
                private double fps = 0;

                @Override
                public void handle(long now) {
                    if (lastUpdate > 0) {
                        double elapsedTime = (now - lastUpdate) / 1_000_000_000.0;
                        double currentFPS = 1/elapsedTime;
                        fps += (currentFPS - fps) / ++frameCount; // Simple moving average
                        fpsLabel.setText(String.format("FPS: %.2f", fps));
                    }
                    if (frameCount % 500 == 0) { // Reset every 60 frames
                        frameCount = 0;
                        fps = 0;
                    }
                    lastUpdate = now;
                }
            }.start();
            this.mainPlane.getChildren().add(fpsLabel); */
