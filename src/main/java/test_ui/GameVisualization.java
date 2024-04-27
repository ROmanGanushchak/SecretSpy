package test_ui;

import model.Cards.CardsArray;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.Chancellor;
import model.ChangebleRole.President;
import model.ChangebleRole.Right;
import model.ChangebleRole.Right.ExecutionStatusWrapper;
import model.Game.PlayerModel;
import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;
import test_ui.Components.CardRemovalController;
import test_ui.Components.GameFinish;
import test_ui.Components.LiberalBoardController;
import test_ui.Components.PlayerPaneController;
import test_ui.Components.RevealeRoleController;
import test_ui.Components.RevealingCards;
import test_ui.Components.ShowDeath;
import test_ui.Components.SpyBoardController;
import test_ui.Components.Component.Component;
import GameController.GameControllerVisualService;
import User.UserData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.util.Duration;
import javafx.util.Pair;

public class GameVisualization { // game visualization має компоненти спільні для всіх гравців

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
    private Label logeField;

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
    private AnchorPane gameFinishPane;

    @FXML
    private ImageView cardToAdd;

    @FXML
    private VBox playerIconHodler;
    @FXML
    private VBox rightsHolder;

    private VoteManeger voteManeger;

    private Scale lastTransformation;
    private GameControllerVisualService gameControllerProxy;
    private RevealingCards revealingCards;

    private LiberalBoardController liberalBoardController;
    private SpyBoardController spyBoardController;

    private ActObservers<Integer> cardRemovalChooseObservers;
    private ActObservers<Integer> playerChosenObservers;

    private Map<Integer, UserData.VisualData> playersVisualData;
    private Map<Integer, PlayerPaneController> playersIcons;

    private CardRemovalController cardRemovalController;

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
        return new Pair<Number, Number>(this.mainPlane.getWidth(), this.mainPlane.getHeight());
    }

    @FXML
    private void initialize() {
        try {
            this.popupLayerManager = new PopupLayerManager(popupPlane);

            this.lastTransformation = new Scale(1, 1, 0, 0);
            mainPlane.getTransforms().add(lastTransformation);
            popupPlane.getTransforms().add(lastTransformation);

            this.liberalBoardController = new LiberalBoardController(liberalBoard);
            this.spyBoardController = new SpyBoardController(spyBoard);

            this.voteManeger = new VoteManeger(this.voteSurface, popupLayerManager);
            this.cardRemovalChooseObservers = new ActObservers<>();

            this.playerIconHodler.setSpacing(10);
            this.playersIcons = new HashMap<>();

            this.revealingCards = new RevealingCards(cardRemovingPane);
            this.revealingCards.getExitButtonObservers().subscribe(
                (Integer val) -> {
                        Component.reveal(onBoardPane);
                        this.popupLayerManager.finishCurent();
                });
            
            this.playerChosenObservers = new ActObservers<>();
            this.cardRemovalController = new CardRemovalController(this.cardRemovingPane);
        } catch (Exception e) {
            System.out.println("unsuccesfull initilize of main controller");
            e.printStackTrace();
        }
    }

    private Image getRoleImage(PlayerModel.mainRoles role) {
        Image cardImage;
        if (role == PlayerModel.mainRoles.Liberal)
            cardImage = new Image(App.class.getResourceAsStream("images/liberalRoleCard.png"));
        else if (role == PlayerModel.mainRoles.Spy)
            cardImage = new Image(App.class.getResourceAsStream("images/spyRoleCard.png"));
        else if (role == PlayerModel.mainRoles.ShadowLeader)
            cardImage = new Image(App.class.getResourceAsStream("images/shadowLeader.png"));
        else
            cardImage = new Image(App.class.getResourceAsStream("images/continue-circle.png"));
        
        return cardImage;
    }

    public void showRole(PlayerModel.mainRoles role, ArrayList<Integer> spyes, Integer shadowLeader) {
        Image cardImage = getRoleImage(role);

        RevealeRoleController revealRole = new RevealeRoleController(this.revealeRolePane);
        revealRole.setup(cardImage, spyes, shadowLeader, this.playersVisualData);
        revealRole.getExitObservers().subscribe((Integer p) -> popupLayerManager.finishCurent());

        popupLayerManager.askActivation(revealRole);
    }

    public void showRole(PlayerModel.mainRoles role) {
        Image cardImage = getRoleImage(role);

        RevealeRoleController revealRole = new RevealeRoleController(this.revealeRolePane);
        revealRole.setup(cardImage);
        revealRole.getExitObservers().subscribe((Integer p) -> popupLayerManager.finishCurent());

        popupLayerManager.askActivation(revealRole);
    }

    public void setPlayersVisuals(Map<Integer, UserData.VisualData> playersVisualData) {
        this.playersVisualData = playersVisualData;

        this.playerIconHodler.getChildren().clear();
        this.playersIcons.clear();
        for (Map.Entry<Integer, UserData.VisualData> player : playersVisualData.entrySet()) {
            PlayerPaneController playerPane = new PlayerPaneController(playerIconHodler);
            playerPane.initialize(player.getValue().getName(), player.getValue().getImageURL(), player.getKey());
            playersIcons.put(player.getKey(), playerPane);
            playerPane.getChooseButObservers().subscribe((Integer val) -> {this.playerChosenObservers.informAll(val);});
        }
    }

    public void showCardsToRemove(ArrayList<CardsArray.Card> cards) {
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        final ActObservers.MethodToCall<Integer> method = (Integer index) -> {
            this.cardRemovalChooseObservers.informAll(index);
            pause.play();
        };

        pause.setOnFinished(e -> {
            Component.reveal(onBoardPane);
            cardRemovalController.diactivateVetoUsage();
            cardRemovalController.getCardSlotePressed().unsubscribe(method);
            cardRemovalController.getVetoPowerPressed().unsubscribe(method);
            popupLayerManager.finishCurent();
        });

        cardRemovalController.setup(cards);
        cardRemovalController.getCardSlotePressed().subscribe(method);
        cardRemovalController.getVetoPowerPressed().subscribe(method);

        Component.hide(onBoardPane);
        popupLayerManager.askActivation(cardRemovalController);
    }

    public void activateVetoPower(Integer informValue) {
        cardRemovalController.activateVetoUsage(informValue);
    }

    public void diactivateVetoPower() {
        cardRemovalController.diactivateVetoUsage();
    }

    public ActObserversAccess<Integer> getVetoPowerPressed() {
        return this.cardRemovalController.getVetoPowerPressed();
    }

    public void setGameContrlProxy(GameControllerVisualService gameContrlProxy) {
        this.gameControllerProxy = gameContrlProxy;
    }

    public void revealCards(Card[] cards) {
        revealingCards.initialize(new ArrayList<>(Arrays.asList(cards)));

        Component.hide(onBoardPane);
        this.popupLayerManager.askActivation(revealingCards);
    }

    public void startVoting(int presidentID, int chancellorID) {
        String presidentName = playersVisualData.get(presidentID).getName();
        String chancellorName = playersVisualData.get(chancellorID).getName();
        if (presidentName == null)
            presidentName = "President";
        if (chancellorName == null)
            presidentName = "President";

        this.voteManeger.start(playersVisualData.get(presidentID).getName(),
                playersVisualData.get(chancellorID).getName());
    }

    public void endVoting() {
        this.voteManeger.end();
    }

    public void showVotingResult(boolean result, int candidateID, Map<Integer, Boolean> votes) {
        ArrayList<String> yesVoteNames = new ArrayList<>(), noVoteNames = new ArrayList<>();

        for (Map.Entry<Integer, Boolean> vote : votes.entrySet()) {
            Integer player = vote.getKey();

            if (vote.getValue() == false) {
                noVoteNames.add(playersVisualData.get(player).getName());
                playersIcons.get(player).showDislike();
            } else {
                yesVoteNames.add(playersVisualData.get(player).getName());
                playersIcons.get(player).showLike();
            }
        }

        this.voteManeger.showResult(result, playersVisualData.get(candidateID).getName(), yesVoteNames, noVoteNames);
    }

    public void setIconPlayerPane(PlayerPaneController.Icons icon, int playerID) {
        this.playersIcons.get(playerID).showIcon(icon);
    }

    public void unlockAllPlayersChose() {
        for (PlayerPaneController player : playersIcons.values())
            player.unlockPlayerChose();
    }

    public void lockPlayersChose(ArrayList<Integer> players) {
        for (Integer player : players)
            playersIcons.get(player).lockPlayerChose();
    }

    public void finishGame(boolean result, int shadowLeaderId, ArrayList<Integer> spyesID) {
        this.popupLayerManager.subscribeForCallWhenFree((Boolean p) -> {
            Component.hide(onBoardPane);
            GameFinish gameFinish = new GameFinish(gameFinishPane);

            ArrayList<String> names = new ArrayList<>(spyesID.size());
            for (Integer spyID : spyesID)
                names.add(this.playersVisualData.get(spyID).getName());

            gameFinish.setUp(result, this.playersVisualData.get(shadowLeaderId).getName(), names);
        }, 1);
    }

    public void showDeathMessge() {
        ShowDeath showDeath = new ShowDeath(cardRemovingPane);
        showDeath.getExitButObservers().subscribe(
            (Integer val) -> {
                Component.reveal(onBoardPane);
                popupLayerManager.finishCurent();
            }, 1);

        Component.hide(onBoardPane);
        popupLayerManager.askActivation(showDeath);
    }

    public void forceToChoosePlayer(ArrayList<Integer> forbidenPlayers) {
        for (Integer player : forbidenPlayers) {
            PlayerPaneController pane = playersIcons.get(player);
            if (pane != null) 
                pane.lockPlayerChose();
        }

        Component.turnOff(onBoardPane);
        Component.turnOff(presidentRightsPane);
    }

    public void finishPlayerChoose(ArrayList<Integer> forbidenPlayers) {
        for (Integer player : forbidenPlayers) {
            PlayerPaneController pane = playersIcons.get(player);
            if (pane != null) 
                pane.unlockPlayerChose();
        }

        Component.turnOn(onBoardPane);
        Component.turnOn(presidentRightsPane);
    }

    public void informPresidentRightUsage(President.RightTypes right, ExecutionStatusWrapper status) {
        logeField.setText("The president right " + right.toString() + " " + Right.execaptionStatusText.get(status.status));
    }

    public void informChancellorRightUsage(Chancellor.RightTypes right, ExecutionStatusWrapper status) {
        logeField.setText("The chancellor right " + right.toString() + " " + Right.execaptionStatusText.get(status.status));
    }

    public void addCardToBoard(Card.states type) {
        this.popupLayerManager.subscribeForCallWhenFree((Boolean f) -> {
            ImageView card = new ImageView();
            card.setX(cardToAdd.getLayoutX());
            card.setY(cardToAdd.getLayoutY());
            card.setFitHeight(cardToAdd.getFitHeight());
            card.setFitWidth(cardToAdd.getFitWidth());
            card.setImage(cardToAdd.getImage());
            onBoardPane.getChildren().add(card);

            Image texture;
            Rectangle endRec;
            if (type == Card.states.Liberal) {
                texture = ImageLoader.getInstance().getImage("liberalCard.png");
                endRec = liberalBoardController.getNextSloteRectangle();
                endRec.setX(endRec.getX() + liberalBoard.getLayoutX());
                endRec.setY(endRec.getY() + liberalBoard.getLayoutY());
            } else {
                texture = ImageLoader.getInstance().getImage("spyCard.png");
                endRec = spyBoardController.getNextSloteRectangle();
                endRec.setX(endRec.getX() + spyBoard.getLayoutX());
                endRec.setY(endRec.getY() + spyBoard.getLayoutY());
            }

            Scene scene = onBoardPane.getScene();
            Pair<Double, Double> midSize = new Pair<>(scene.getHeight() / 5, scene.getHeight() / 3);
            Pair<Double, Double> endSize = new Pair<>(endRec.getWidth(), endRec.getHeight());

            Pair<Double, Double> midPosition = new Pair<>(
                    scene.getWidth() / 2 - (midSize.getKey() / 2 + onBoardPane.getLayoutX()),
                    scene.getHeight() / 2 - (midSize.getValue() / 2 + onBoardPane.getLayoutY()));

            Pair<Double, Double> endPosition = new Pair<>(endRec.getX(), endRec.getY());

            CardAddingAnimation cardAddingAnimation = new CardAddingAnimation();
            
            cardAddingAnimation.getFinishObservers().subscribe(
                (Integer val) -> {
                    if (type == Card.states.Liberal)
                        this.liberalBoardController.showNextCard();
                    else if (type == Card.states.Spy)
                        this.spyBoardController.showNextCard();
                    
                    onBoardPane.getChildren().remove(card);
                }, 1);

            cardAddingAnimation.start(4.0, midPosition, endPosition, midSize, endSize, texture, card);
        }, 1);
    }

    public void changeFailedVotingCount(int failedCount) {
        this.liberalBoardController.moveVotingCircle(failedCount);
    }

    public VoteManeger getVoteManager() {
        return this.voteManeger;
    }

    public VBox getRightsHolder() {
        return this.rightsHolder;
    }

    public VBox getPlayersIconsHolder() {
        return this.playerIconHodler;
    }

    public ActObserversAccess<Integer> getPlayerChosenObservers() {
        return this.playerChosenObservers;
    }

    public ActObserversAccess<Integer> getCardRemovalChooseObservers() {
        return this.cardRemovalChooseObservers;
    }

    public Map<Integer, PlayerPaneController> getPlayerIcons() {
        return this.playersIcons;
    }

    public ActObserversAccess<Boolean> getVotingResultObservers() {
        return this.voteManeger.getVotingResultObservers();
    }
}

/*
 * Label fpsLabel = new Label("FPS: ");
 * new AnimationTimer() {
 * private long lastUpdate = 0;
 * private long frameCount = 0;
 * private double fps = 0;
 * 
 * @Override
 * public void handle(long now) {
 * if (lastUpdate > 0) {
 * double elapsedTime = (now - lastUpdate) / 1_000_000_000.0;
 * double currentFPS = 1/elapsedTime;
 * fps += (currentFPS - fps) / ++frameCount; // Simple moving average
 * fpsLabel.setText(String.format("FPS: %.2f", fps));
 * }
 * if (frameCount % 500 == 0) { // Reset every 60 frames
 * frameCount = 0;
 * fps = 0;
 * }
 * lastUpdate = now;
 * }
 * }.start();
 * this.mainPlane.getChildren().add(fpsLabel);
 */
