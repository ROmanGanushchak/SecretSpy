package test_ui;

import model.Cards.CardsArray;
import model.Observers.ActObservers;
import model.Observers.ActionObserver;
import model.Observers.ObserversAccess;
import test_ui.Components.AbilityController;
import test_ui.Components.CardRemovalController;
import test_ui.Components.LiberalBoardController;
import test_ui.Components.RevealeRoleController;
import test_ui.Components.SpyBoardController;
import test_ui.Components.Component.Component;
import GameController.GameControllerVisualService;

import java.util.ArrayList;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.util.Duration;
import javafx.util.Pair;

public class GameVisualization{ // game visualization має компоненти спільні для всіх гравців
    
    @FXML
    private AnchorPane basePane;

    @FXML
    private AnchorPane mainPlane;

    @FXML
    private AnchorPane popupPlane;
    private PopupLayerManager popupLayerManager;

    @FXML
    private AnchorPane onBoardPane;
    @FXML
    private AnchorPane cardRemovingPane;

    @FXML
    private Button comandExcut;
    @FXML
    private TextField comandLine;

    @FXML
    private AnchorPane liberalBoard;
    @FXML
    private AnchorPane spyBoard;

    @FXML
    private AnchorPane presidentRightsPane;
    @FXML
    private AnchorPane revealeRolePane;
    @FXML
    private AnchorPane voteSurface;

    @FXML
    private VBox rightsHolder;

    private VoteManeger voteManeger;

    private Scale lastTransformation;
    private GameControllerVisualService gameControllerProxy;

    private LiberalBoardController liberalBoardController;
    private SpyBoardController spyBoardController;

    private ActObservers<Integer> cardRemovalChooseObservers;

    @FXML
    void executComand(ActionEvent event) {
        this.gameControllerProxy.executeCommand(this.comandLine.getText());
        this.comandLine.setText("");
    }

    public void resizeMainMuneX(Number oldNumber, Number newNumber) {
        if (oldNumber.doubleValue() == 0)
            lastTransformation.setX(1);
        else 
            lastTransformation.setX((newNumber.doubleValue() / oldNumber.doubleValue()) * lastTransformation.getX());
    }

    public void resizeMainMuneY(Number oldNumber, Number newNumber) {
        if (oldNumber.doubleValue() == 0)
            lastTransformation.setX(1);
        else 
            lastTransformation.setY((newNumber.doubleValue() / oldNumber.doubleValue()) * lastTransformation.getY());
    }

    public Pair<Number, Number> getMainSurfaceSize() {
        return new Pair<Number,Number>(this.mainPlane.getWidth(), this.mainPlane.getHeight());
    }

    @FXML
    public void initialize() {
        try {
            this.popupLayerManager = new PopupLayerManager(popupPlane);

            this.lastTransformation = new Scale(1, 1, 0, 0);
            mainPlane.getTransforms().add(lastTransformation);
            popupPlane.getTransforms().add(lastTransformation);

            this.liberalBoardController = new LiberalBoardController(liberalBoard);
            this.spyBoardController = new SpyBoardController(spyBoard);

            this.voteManeger = new VoteManeger(this.voteSurface, popupLayerManager);
            this.cardRemovalChooseObservers = new ActObservers<>();
        } catch (Exception e) {
            System.out.println("unsuccesfull initilize of main controller");
            e.printStackTrace();
        }
    }

    public void revealingRoleCardAnimation(Image image) {
        RevealeRoleController revealRole = new RevealeRoleController(this.revealeRolePane);
        revealRole.setup(image, (Scale)revealRole.getComponent().getProperties().get("scale"));
        revealRole.getExitObservers().subscribe(new ActionObserver<>((Integer p) -> popupLayerManager.finishCurent()));

        popupLayerManager.askActivation(revealRole);
    }

    public void showCardsToRemove(ArrayList<CardsArray.Card> cards) {        
        CardRemovalController cardRemovalController = new CardRemovalController(this.cardRemovingPane);

        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e -> {
            Component.reveal(onBoardPane);
            popupLayerManager.finishCurent();
        });

        cardRemovalController.setup(cards);
        cardRemovalController.getCardSlotePressed().subscribe(
            new ActionObserver<>((Integer index) -> {
                this.cardRemovalChooseObservers.informAll(index);
                pause.play(); }) );
        
        Component.hide(onBoardPane);
        popupLayerManager.askActivation(cardRemovalController);
    }

    public void setGameContrlProxy(GameControllerVisualService gameContrlProxy) {
        this.gameControllerProxy = gameContrlProxy;
    }

    public void startVoting(String presidentName, String chancellorName) {
        System.out.println("Start voting");
        this.voteManeger.start(presidentName, chancellorName);
    }

    public void endVoting() {
        this.voteManeger.end();
    }

    public ObserversAccess<ActionObserver<Boolean>> getVotingResultObservers() {
        return this.voteManeger.getVotingResultObservers();
    }

    public void showVotingResult(boolean result, String candidateName, ArrayList<String> yesVotes, ArrayList<String> noVotes) {
        this.voteManeger.showResult(result, candidateName, yesVotes, noVotes);
    }

    public LiberalBoardController getLiberalBoardController() {
        return this.liberalBoardController;
    }

    public SpyBoardController getSpyBoardController() {
        return this.spyBoardController;
    }

    public VoteManeger getVoteManager() {
        return this.voteManeger;
    }

    public VBox getRightsHolder() {
        return this.rightsHolder;
    }

    public ObserversAccess<ActionObserver<Integer>> getCardRemovalChooseObservers() {
        return this.cardRemovalChooseObservers;
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
