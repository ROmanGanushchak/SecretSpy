package PlayerGameManager;

import java.io.IOException;
import java.util.ArrayList;

import GameController.GameControllerVisualService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import model.Cards.CardsArray.Card;
import model.Observers.ActionObserver;
import model.Voting.Voting;
import test_ui.App;
import test_ui.GameVisualization;

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
            this.gameVisualization.setGameContrlProxy(this.gameController);
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

    public void makePresident() {

    }
    
    public void changeFailedVotingCount(int failedCount) {
        this.gameVisualization.getLiberalBoardController().moveVotingCircle(failedCount);
    }

    public void addCardToBoard(Card.states type) {
        System.out.println("Adding card request");
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
        System.out.println(scene);
        return this.scene;
    }

    public int getPlayerID() {return 0;}
}
