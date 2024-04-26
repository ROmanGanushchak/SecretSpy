package PlayerGameManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
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
import model.Game.PlayerModel;
import model.Voting.Voting;
import test_ui.App;
import test_ui.GameVisualization;
import test_ui.Components.AbilityController;
import test_ui.Components.PlayerPaneController;
import test_ui.Components.PlayerPaneController.Icons;

public class HumanPlayerGameManager extends GameVisualization implements PlayerGameManager {
    private GameControllerVisualService gameController;

    private UserData userData;

    private CurrentRoles currentRole;
    private President.RightTypes presidentRightToUse;
    private Chancellor.RightTypes chancellorRightToUse;
    private HashMap<Integer, AbilityController> abilities;

    public UserData.VisualData getVisualData() {
        return this.userData.visualData;
    }

    public int getPlayerID() {
        return userData.getID();
    }

    private Scene scene;

    private Voting currentVoting;

    public HumanPlayerGameManager(int id) {
        this.userData = new UserData(id, "Player " + Integer.toString(id), "defaultPlayerImage.png");
        currentRole = CurrentRoles.None;

        abilities = new HashMap<>();
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

    public void kill() {
        super.showDeathMessge();
    }

    private void addAbility(President.RightTypes rightType, Right right, VBox rightsHolder) {
        if (right.isActivate()) {
            AbilityController rightController = new AbilityController(rightsHolder);
            rightController.setup(rightType.toString(), right.getUseCount(), rightType.ordinal());
            rightController.getUseButtonObservers().subscribe((Integer value) -> {
                if (this.presidentRightToUse != null)
                    return;

                switch (right.getRequest()) {
                    case None:
                        gameController.executePresidentRight(userData.getID(), rightType);
                        break;
                    case ChoosePlayer:
                        ArrayList<Integer> nonChoosablePlayers = gameController.getNonChooseblePlayers(userData.getID(), rightType);
                        super.getPlayerChosenObservers().subscribe(
                            (Integer player) -> {
                                gameController.executePresidentRight(userData.getID(), rightType, player); 
                                presidentRightToUse = null;
                                super.finishPlayerChoose(nonChoosablePlayers);
                            }, 1);
                        
                        super.forceToChoosePlayer(nonChoosablePlayers);
                        break;
                }

                super.setChosenRight(rightType.toString());
            });
            abilities.put(rightType.ordinal(), rightController);
        }
    }

    private void addAbility(Chancellor.RightTypes rightType, Right right, VBox rightsHolder) {
        if (right.isActivate()) {
            AbilityController rightController = new AbilityController(rightsHolder);
            rightController.setup(rightType.toString(), right.getUseCount(), rightType.ordinal());
            rightController.getUseButtonObservers().subscribe((Integer value) -> {
                switch (right.getRequest()) {
                    case None:
                        gameController.executeChancellorRight(userData.getID(), rightType);
                        break;
                    case ChoosePlayer:
                        break; // there is no right with choose player if needed adapt the code from makePresident
                }

                super.setChosenRight(rightType.toString());
            });

            abilities.put(rightType.ordinal(), rightController);
        }
    }

    public void makePresident(EnumMap<President.RightTypes, Political.Right> rights) {
        VBox rightsHolder = super.getRightsHolder();

        for (Map.Entry<President.RightTypes, Political.Right> right : rights.entrySet()) {
            if (right.getValue().isActivate()) 
                addAbility(right.getKey(), right.getValue(), rightsHolder);
        }
    }

    public void unmakePresident() {
        System.out.println("Unmake president");
        
        VBox rightsHolder = super.getRightsHolder();
        rightsHolder.getChildren().clear();
        super.setChosenRight("");
        abilities.clear();
    }

    public void changePresidentRight(Map.Entry<President.RightTypes, Right> right) {
        AbilityController ability = abilities.get(right.getKey().ordinal());
        if (ability != null) {
            if (right.getValue().isActivate())
                ability.setUsageCount(right.getValue().getUseCount());
            else {
                getRightsHolder().getChildren().remove(ability.getComponent());
            }
        } 
        else
            addAbility(right.getKey(), right.getValue(), getRightsHolder());
    }

    public void killOtherPlayer(int playerID) {
        super.getPlayerIcons().get(playerID).showIcon(Icons.KILLED);
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
            if (right.getValue().isActivate()) 
                addAbility(right.getKey(), right.getValue(), rightsHolder);
        }
    }

    public void unmakeChancellor() {
        VBox rightsHolder = super.getRightsHolder();
        rightsHolder.getChildren().clear();
        super.setChosenRight("");
        abilities.clear();
    }

    public void changeChancellorRight(Map.Entry<Chancellor.RightTypes, Right> right) {
        AbilityController ability = abilities.get(right.getKey().ordinal());
        if (ability != null)
            ability.setUsageCount(right.getValue().getUseCount());
        else
            addAbility(right.getKey(), right.getValue(), getRightsHolder());
    }

    public void changeChancellor(Integer oldChancellor, Integer newChancellor) {
        if (oldChancellor != -1)
            super.setIconPlayerPane(PlayerPaneController.Icons.NONE, oldChancellor);
        if (newChancellor != -1)
            super.setIconPlayerPane(PlayerPaneController.Icons.CHANCELLOR, newChancellor);
    }

    public void giveCardsToRemove(ArrayList<CardsArray.Card> cards) {
        super.getCardRemovalChooseObservers().subscribe((Integer i) -> gameController.informCardRemoved(i, userData.getID()));
        super.showCardsToRemove(cards);
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
