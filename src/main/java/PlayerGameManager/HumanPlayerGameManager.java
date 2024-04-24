package PlayerGameManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import GameController.GameControllerVisualService;
import User.UserData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import model.Cards.CardsArray;
import model.ChangebleRole.Chancellor;
import model.ChangebleRole.Political;
import model.ChangebleRole.Political.Right;
import model.ChangebleRole.President;
import model.Voting.Voting;
import test_ui.App;
import test_ui.GameVisualization;
import test_ui.Components.AbilityController;
import test_ui.Components.PlayerPaneController;

public class HumanPlayerGameManager extends GameVisualization implements PlayerGameManager {
    private GameControllerVisualService gameController;

    private UserData userData;
    private static enum CurrentRoles {
        President, Chancellor, None
    }

    private CurrentRoles currentRole;
    private President.RightTypes presidentRightToUse;
    private Chancellor.RightTypes chancellorRightToUse;

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
        currentRole = CurrentRoles.None;
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
    }

    public void informRightPressed(Integer value) {
        System.out.println("type -> " + value);
    }

    public void kill() {
        super.showDeathMessge();
    }

    public void makePresident(EnumMap<President.RightTypes, Political.Right> rights) {
        VBox rightsHolder = super.getRightsHolder();

        for (Map.Entry<President.RightTypes, Political.Right> right : rights.entrySet()) {
            if (right.getValue().getUseCount() != 0) {
                AbilityController rightController = new AbilityController(rightsHolder);
                rightController.setup(right.getKey().toString(), right.getValue().getUseCount(), right.getKey().ordinal());
                rightController.getUseButtonObservers().subscribe((Integer value) -> {
                    if (this.presidentRightToUse != null)
                        return;
                    
                    System.out.println("Right lambda executed");

                    Right rightClass = rights.get(President.RightTypes.get(value));
                    System.out.println("Right to execute " + President.RightTypes.get(value).toString());
                    switch (rightClass.getRequest()) {
                        case None:
                            System.out.println("None right");
                            gameController.executePresidentRight(userData.getID(), right.getKey());
                            break;
                        case ChoosePlayer:
                            this.presidentRightToUse = right.getKey();
                            super.getPlayerChosenObservers().subscribe(
                                (Integer player) -> {
                                    System.out.println("Player chosen");
                                    gameController.executePresidentRight(userData.getID(), this.presidentRightToUse, player); 
                                    presidentRightToUse = null;
                                }, 1);
                            break;
                    }
                });
            }
        }
    }

    public void unmakePresident() {
        VBox rightsHolder = super.getRightsHolder();
        rightsHolder.getChildren().clear();
    }

    public void changePresident(Integer oldPresident, Integer newPresident) {
        if (oldPresident != -1)
            super.setIconPlayerPane(PlayerPaneController.Icons.NONE, oldPresident);
        if (newPresident != -1)
            super.setIconPlayerPane(PlayerPaneController.Icons.PRESIDENT, newPresident);
    }

    public void makeChancellor(EnumMap<Chancellor.RightTypes, Political.Right> rights) {
        VBox rightsHolder = super.getRightsHolder();

        for (Map.Entry<Chancellor.RightTypes, Political.Right> right : rights.entrySet()) {
            if (right.getValue().getUseCount() != 0) {
                AbilityController rightController = new AbilityController(rightsHolder);
                rightController.setup(right.getKey().toString(), right.getValue().getUseCount(), right.getKey().ordinal());
                rightController.getUseButtonObservers().subscribe((Integer value) -> informRightPressed(value));
            }
        }
    }

    public void unmakeChancellor() {
        VBox rightsHolder = super.getRightsHolder();
        rightsHolder.getChildren().clear();
    }

    public void changeChancellor(Integer oldChancellor, Integer newChancellor) {
        System.out.printf("Chancellors -> %d %d\n", oldChancellor, newChancellor);

        if (oldChancellor != -1)
            super.setIconPlayerPane(PlayerPaneController.Icons.NONE, oldChancellor);
        if (newChancellor != -1)
            super.setIconPlayerPane(PlayerPaneController.Icons.CHANCELLOR, newChancellor);
    }

    public void giveCardsToRemove(ArrayList<CardsArray.Card> cards) {
        super.getCardRemovalChooseObservers().subscribe((Integer i) -> gameController.informCardRemoved(i, userData.getID()));
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
