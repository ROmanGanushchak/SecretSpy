package PlayerGameManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import GameController.GameControllerVisualService;
import User.UserData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import model.Cards.CardsArray;
import model.ChangebleRole.Political.SimpleRight;
import model.ChangebleRole.Chancellor;
import model.ChangebleRole.Political;
import model.ChangebleRole.President;
import model.Voting.Voting;
import test_ui.App;
import test_ui.GameVisualization;
import test_ui.Components.AbilityController;
import test_ui.Components.PlayerPaneController;

public class HumanPlayerGameManager extends GameVisualization implements PlayerGameManager {
    private GameControllerVisualService gameController;

    private UserData userData;
    public UserData.VisualData getVisualData() {
        return this.userData.visualData;
    }

    public int getPlayerID() {
        return userData.getID();
    }

    private Scene scene;

    private Voting currentVoting;

    public HumanPlayerGameManager(int id) {
        this.userData = new UserData(id, "Player " + Integer.toString(id), "board.png");
    }

    public void initializeScreen() {
        try {
            FXMLLoader sceneLoader = new FXMLLoader(App.class.getResource("fxml/gameVisualization.fxml"));
            sceneLoader.setController(this);
            this.scene = new Scene(sceneLoader.load());
        } catch(IOException e) {
            System.out.println("Catch");
            System.out.println(e.getMessage());
        }

        // remove when add multiple screens
        this.scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            super.resizeMainMuneX(oldValue, newValue);
        });

        this.scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            super.resizeMainMuneY(oldValue, newValue);
        });
    }

    public void initializeGame() {
        this.currentVoting = null;

        super.getVotingResultObservers().subscribe((Boolean result) -> this.vote(result));

        // super.addCardToBoard(Card.states.Liberal);
    }

    public void informRightPressed(Integer value) {
        System.out.println("type -> " + value);
    }

    public void makePresident(Political.SimpleRight<President.RightTypes> rights[]) {
        VBox rightsHolder = super.getRightsHolder();

        for (SimpleRight<President.RightTypes> right : rights) {
            if (right.getValue() != 0) {
                AbilityController rightController = new AbilityController(rightsHolder);
                rightController.setup(right.getKey().toString(), right.getValue(), right.getKey().ordinal());
                rightController.getUseButtonObservers().subscribe((Integer value) -> informRightPressed(value));
            }
        }
    }

    public void unmakePresident() {
        VBox rightsHolder = super.getRightsHolder();
        rightsHolder.getChildren().clear();
    }

    public void changePresident(Integer oldPresident, Integer newPresident) {
        if (oldPresident != null)
            super.setIconPlayerPane(PlayerPaneController.Icons.NONE, oldPresident);
        super.setIconPlayerPane(PlayerPaneController.Icons.PRESIDENT, newPresident);
    }

    public void makeChancellor(SimpleRight<Chancellor.rights> rights[]) {
        VBox rightsHolder = super.getRightsHolder();

        for (SimpleRight<Chancellor.rights> right : rights) {
            if (right.getValue() != 0) {
                AbilityController rightController = new AbilityController(rightsHolder);
                rightController.setup(right.getKey().toString(), right.getValue(), right.getKey().ordinal());
                rightController.getUseButtonObservers().subscribe((Integer value) -> informRightPressed(value));
            }
        }
    }

    public void unmakeChancellor() {
        VBox rightsHolder = super.getRightsHolder();
        rightsHolder.getChildren().clear();
    }

    public void changeChancellor(Integer oldChancellor, Integer newChancellor) {
        if (oldChancellor != null)
            super.setIconPlayerPane(PlayerPaneController.Icons.NONE, oldChancellor);
        super.setIconPlayerPane(PlayerPaneController.Icons.CHANCELLOR, newChancellor);
    }

    @Override
    public void setPlayersVisuals(Map<Integer, UserData.VisualData> playersVisualData) {
        super.setPlayersVisuals(playersVisualData);
        Map<Integer, PlayerPaneController> icons = super.getPlayerIcons();
        for (Map.Entry<Integer, PlayerPaneController> icon : icons.entrySet()) {
            icon.getValue().getChooseButObservers().subscribe((Integer val) -> System.out.println("Player chosen " + val));
        }
    }

    public void giveCardsToRemove(ArrayList<CardsArray.Card> cards) {
        super.getCardRemovalChooseObservers().subscribe((Integer i) -> System.out.println("card was chosn " + i));
        super.showCardsToRemove(cards);
    }

    public void choosePlayer(String text) {

    }

    private void vote(Boolean result) {
        if (currentVoting != null) {
            this.currentVoting.vote(getPlayerID(), result);
            this.currentVoting = null;
        }
    }

    public void voteForChancellor(Voting voting, int presidentID, int chancellorID) {
        if (this.currentVoting != null) 
            System.out.println("Trying to add voting while other didnt ended");
        this.currentVoting = voting;
        super.startVoting(presidentID, chancellorID);
    }

    public void voteForChancellor(Voting voting) {
        this.voteForChancellor(voting, -1, -1);
    }

    public void setProxyGameController(GameControllerVisualService gameController) {
        this.gameController = gameController;
        super.setGameContrlProxy(gameController);
    }

    public Scene getScene() {
        return this.scene;
    }
}
