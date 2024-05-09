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

/** Class is related to visualizing the player in gane screen */
public class GameVisualization {
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

    /** Executes the command from the terminal field on the screen */
    @FXML
    void executComand(ActionEvent event) {
        this.gameControllerProxy.executeCommand(this.comandLine.getText());
        this.comandLine.setText("");
    }

    /**Resizes the screen horizontally
     * @param oldNumber old screen x size
     * @param newNumber new screen x size
     */
    public void resizeMainMuneX(Number oldNumber, Number newNumber) {
        if (oldNumber.doubleValue() == 0)
            lastTransformation.setX(1);
        else
            lastTransformation.setX((newNumber.doubleValue() / oldNumber.doubleValue()) * lastTransformation.getX());
    }

    /**Resizes the screen vertically
     * @param oldNumber old screen y size
     * @param newNumber new screen y size
     */
    public void resizeMainMuneY(Number oldNumber, Number newNumber) {
        if (oldNumber.doubleValue() == 0)
            lastTransformation.setX(1);
        else
            lastTransformation.setY((newNumber.doubleValue() / oldNumber.doubleValue()) * lastTransformation.getY());
    }

    /** Initializes the object, all observers and fields */
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

    /**Returns the icon of the players role
     * @param role the role of the player
     * @return the image of the role
     */
    private Image getRoleImage(PlayerModel.mainRoles role) {
        Image cardImage;
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (role == PlayerModel.mainRoles.Liberal)
            cardImage = imageLoader.getImage("liberalRoleCard.png");
        else if (role == PlayerModel.mainRoles.Spy)
            cardImage = imageLoader.getImage("spyRoleCard.png");
        else if (role == PlayerModel.mainRoles.ShadowLeader)
            cardImage = imageLoader.getImage("shadowLeader.png");
        else
            cardImage = imageLoader.getImage("continue-circle.png");
        return cardImage;
    }

    /**Shows the players role
     * @param role          players role
     * @param spyes         the list of ids of spyes, can be empty
     * @param shadowLeader  the id of shadowLeader
     */
    public void showRole(PlayerModel.mainRoles role, ArrayList<Integer> spyes, Integer shadowLeader) {
        Image cardImage = getRoleImage(role);

        RevealeRoleController revealRole = new RevealeRoleController(this.revealeRolePane);
        revealRole.setup(cardImage, spyes, shadowLeader, this.playersVisualData);
        revealRole.getExitObservers().subscribe((Integer p) -> popupLayerManager.finishCurent());

        popupLayerManager.askActivation(revealRole);
    }

    /**Shows the players role
     * @param role  players role
     */
    public void showRole(PlayerModel.mainRoles role) {
        Image cardImage = getRoleImage(role);

        RevealeRoleController revealRole = new RevealeRoleController(this.revealeRolePane);
        revealRole.setup(cardImage);
        revealRole.getExitObservers().subscribe((Integer p) -> popupLayerManager.finishCurent());

        popupLayerManager.askActivation(revealRole);
    }

    /**Initializes all players visual data
     * @param playersVisualData visual data
     */
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

    /**Shows cards that will be removed, if othe popup component is active will be shown with delay
     * @param cards cards to choose from
     */
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

    /**Method to activate the chancellors veto power
     * @param informValue the value that will be informed with veto power activation
     */
    public void activateVetoPower(Integer informValue) {
        cardRemovalController.activateVetoUsage(informValue);
    }

    /**Method to diactivate the chancellors veto power*/
    public void diactivateVetoPower() {
        cardRemovalController.diactivateVetoUsage();
    }

    /**Method sets the gameControllerVisualServise, should be called after initialization
     * @param gameContrlProxy game controller visual proxy
     */
    public void setGameContrlProxy(GameControllerVisualService gameContrlProxy) {
        this.gameControllerProxy = gameContrlProxy;
    }

    /**Method that shows upper cards from the deck
     * @param cards cards to be shown
     */
    public void revealCards(Card[] cards) {
        revealingCards.initialize(new ArrayList<>(Arrays.asList(cards)));

        Component.hide(onBoardPane);
        this.popupLayerManager.askActivation(revealingCards);
    }

    /**Method to start voting
     * @param presidentID   id of the current president
     * @param chancellorID  id of the chancellor candidate
     */
    public void startVoting(int presidentID, int chancellorID) {
        String presidentName = playersVisualData.get(presidentID).getName();
        String chancellorName = playersVisualData.get(chancellorID).getName();
        if (presidentName == null)
            presidentName = "President";
        if (chancellorName == null)
            chancellorName = "Chancellor";

        this.voteManeger.start(playersVisualData.get(presidentID).getName(),
                playersVisualData.get(chancellorID).getName());
    }

    /** Method to end the voting */
    public void endVoting() {
        this.voteManeger.end();
    }

    /**Method to show the voting result, if other popup component is activated then will be shown woth delay
     * @param result        the voting result
     * @param candidateID   the id of the candidate
     * @param votes         all partisipators votes
     */
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

    /**Sets the icon to the players icom
     * @param icon      type of icon to be setted
     * @param playerID  the id of the player whitch icon will be changed
     */
    public void setIconPlayerPane(PlayerPaneController.Icons icon, int playerID) {
        this.playersIcons.get(playerID).showIcon(icon);
    }

    /** Method allows to choose all players from the players field */
    public void unlockAllPlayersChose() {
        for (PlayerPaneController player : playersIcons.values())
            player.unlockPlayerChose();
    }

    /** Method locks all players from the players field
     * @param players players whitch choose button will be blocked
     */
    public void lockPlayersChose(ArrayList<Integer> players) {
        for (Integer player : players)
            playersIcons.get(player).lockPlayerChose();
    }

    /** Method shows the finish pane
     * @param result            the game result
     * @param shadowLeaderId    the id of the shadowLeader
     * @param spyesID           list of ids of the spyes
     */
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

    /** Shows the death message for this player, afterwords the player can only watch the game */
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

    /** Method to force player to choose the player
     * @param forbidenPlayers players that are not allowed to choose
     */
    public void forceToChoosePlayer(ArrayList<Integer> forbidenPlayers) {
        for (Integer player : forbidenPlayers) {
            PlayerPaneController pane = playersIcons.get(player);
            if (pane != null) 
                pane.lockPlayerChose();
        }

        Component.turnOff(onBoardPane);
        Component.turnOff(presidentRightsPane);
    }

    /** Method to finish player choose
     * @param forbidenPlayers players that were not allowed to choose
     */
    public void finishPlayerChoose(ArrayList<Integer> forbidenPlayers) {
        for (Integer player : forbidenPlayers) {
            PlayerPaneController pane = playersIcons.get(player);
            if (pane != null) 
                pane.unlockPlayerChose();
        }

        Component.turnOn(onBoardPane);
        Component.turnOn(presidentRightsPane);
    }

    /** Method to show the log of president right usage
     * @param right     right type that was used
     * @param status    right execution status
     */
    public void informPresidentRightUsage(President.RightTypes right, ExecutionStatusWrapper status) {
        logeField.setText("The president right " + right.toString() + " " + Right.execaptionStatusText.get(status.status));
    }

    /** Method to show the log of chancellor right usage
     * @param right     right type that was used
     * @param status    right execution status
     */
    public void informChancellorRightUsage(Chancellor.RightTypes right, ExecutionStatusWrapper status) {
        logeField.setText("The chancellor right " + right.toString() + " " + Right.execaptionStatusText.get(status.status));
    }

    /** Method adds card to the board and shpows the adding animation after the end of all popups components
     * @param type card type being added
     */
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

    /** Method shows the count of failed ellections
     * @param failedCount the number of failed votings
     */
    public void changeFailedVotingCount(int failedCount) {
        this.liberalBoardController.moveVotingCircle(failedCount);
    }

    /** Method returns the vote manager
     * @return the vote manager
     */
    public VoteManeger getVoteManager() {
        return this.voteManeger;
    }

    /** Method returns the right holder
     * @return vbox whitch contains all abilities
     */
    public VBox getRightsHolder() {
        return this.rightsHolder;
    }

    /** Method returns the holder of players icons
     * @return the holder of players icons
     */
    public VBox getPlayersIconsHolder() {
        return this.playerIconHodler;
    }

    /**
     * @return observer access of player being chosen
     */
    public ActObserversAccess<Integer> getPlayerChosenObservers() {
        return this.playerChosenObservers;
    }

    /**
     * @return observer access of card removal
     */
    public ActObserversAccess<Integer> getCardRemovalChooseObservers() {
        return this.cardRemovalChooseObservers;
    }

    /**
     * @return the map of players icons
     */
    public Map<Integer, PlayerPaneController> getPlayerIcons() {
        return this.playersIcons;
    }

    /**
     * @return observer of veto power usage
     */
    public ActObserversAccess<Integer> getVetoPowerPressed() {
        return this.cardRemovalController.getVetoPowerPressed();
    }

    /**
     * @return observers access of voting result
     */
    public ActObserversAccess<Boolean> getVotingResultObservers() {
        return this.voteManeger.getVotingResultObservers();
    }

    /**
     * @return the size of the game scene
     */
    public Pair<Number, Number> getMainSurfaceSize() {
        return new Pair<Number, Number>(this.mainPlane.getWidth(), this.mainPlane.getHeight());
    }
}