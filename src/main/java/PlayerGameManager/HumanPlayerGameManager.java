package PlayerGameManager;

import java.io.IOException;
import java.util.ArrayList;

import GameController.GameControllerVisualService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.Political.Right;
import model.ChangebleRole.Political;
import model.ChangebleRole.President;
import model.Game.PlayerModel;
import model.Observers.ActionObserver;
import model.Voting.Voting;
import test_ui.App;
import test_ui.Component;
import test_ui.GameVisualization;
import test_ui.Components.AbilityController;

public class HumanPlayerGameManager extends PlayerGameManager {
    private GameControllerVisualService gameController;

    private GameVisualization gameVisualization;
    private Scene scene;

    private Voting currentVoting;

    public void initializeScreen() {
        try {
            System.out.println("Player initialize");
            FXMLLoader sceneLoader = new FXMLLoader(App.class.getResource("gameVisualization.fxml"));
            this.scene = new Scene(sceneLoader.load());
            
            this.gameVisualization = sceneLoader.getController();
        } catch(IOException e) {
            System.out.println("Catch");
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

    public void initializeGame() {
        this.currentVoting = null;

        this.gameVisualization.getVotingResultObservers().subscribe(
            new ActionObserver<>((Boolean result) -> this.vote(result))
        );
    }

    public void informRightPressed(Integer value) {
        System.out.println("type -> " + value);
    }

    public void makePresident(Political.Right<President.rights> rights[]) {
        System.out.println("Make president");
        VBox rightsHolder = gameVisualization.getRightsHolder();

        for (Right<President.rights> right : rights) {
            if (right.getValue() != 0) {
                Parent rightVisual = Component.initialize(App.class.getResource("ability.fxml"), rightsHolder);
                AbilityController rightController = (AbilityController) rightVisual.getProperties().get("controller");
                rightController.setup(right.getKey().toString(), right.getValue(), right.getKey().ordinal());
                rightController.getUseButtonObservers().subscribe(
                    new ActionObserver<>((Integer value) -> informRightPressed(value)));
            }
        }
    }

    public void unmakePresident() {
        System.out.println("not president");
        VBox rightsHolder = gameVisualization.getRightsHolder();
        rightsHolder.getChildren().clear();
    }
    
    public void showRole(PlayerModel.mainRoles role) {
        Image cardImage;
        if (role == PlayerModel.mainRoles.Liberal)
            cardImage = new Image(App.class.getResourceAsStream("images/liberalRoleCard.png"));
        else if (role == PlayerModel.mainRoles.Spy)
            cardImage = new Image(App.class.getResourceAsStream("images/spyRoleCard.png"));
        else if (role == PlayerModel.mainRoles.ShadowLeader)
            cardImage = new Image(App.class.getResourceAsStream("images/shadowLeader.png"));
        else 
            cardImage = new Image(App.class.getResourceAsStream("images/continue-circle.png"));
        
        this.gameVisualization.revealingRoleCardAnimation(cardImage);
    }

    public void choosePlayer(String text) {

    }

    public void changeFailedVotingCount(int failedCount) {
        this.gameVisualization.getLiberalBoardController().moveVotingCircle(failedCount);
    }

    public void addCardToBoard(Card.states type) {
        if (type == Card.states.Liberal) 
            this.gameVisualization.getLiberalBoardController().showNextCard();
        else if (type == Card.states.Spy)
            this.gameVisualization.getSpyBoardController().showNextCard();
    }

    private void vote(Boolean result) {
        if (currentVoting != null) {
            this.currentVoting.vote(getPlayerID(), result);
            this.currentVoting = null;
        }
    }

    public void voteForChancellor(Voting voting, String presidentName, String chancellorName) {
        if (voting != null) 
            System.out.println("Trying to add voting while other didnt ended");
        this.currentVoting = voting;
        this.gameVisualization.startVoting(presidentName, chancellorName);
    }

    public void voteForChancellor(Voting voting) {
        this.voteForChancellor(voting, "President", "Chancellor");
    }

    public void showVoteResult(boolean result, String candidateName, ArrayList<String> yesVotes, ArrayList<String> noVotes) {
        this.gameVisualization.showVotingResult(result, candidateName, yesVotes, noVotes);
    }

    public void setProxyGameController(GameControllerVisualService gameController) {
        this.gameController = gameController;
        this.gameVisualization.setGameContrlProxy(gameController);
    }

    public Scene getScene() {
        return this.scene;
    }

    public int getPlayerID() {return 0;}
}
