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
import model.ChangebleRole.Right;
import model.ChangebleRole.President;
import model.Voting.Voting;
import test_ui.App;
import test_ui.GameVisualization;
import test_ui.Components.AbilityController;
import test_ui.Components.PlayerPaneController;
import test_ui.Components.PlayerPaneController.Icons;


/** Extends the functionality of GameVisualizaton to fulfill all PlayerGameManager requirements */
public class HumanPlayerGameManager extends GameVisualization implements PlayerGameManager {
    /** game controller proxy */
    private GameControllerVisualService gameController;
    /** userdata */
    private UserData userData;

    /** current player role */
    private CurrentRoles currentRole;
    /**  right that player requsted to use*/
    private President.RightTypes presidentRightToUse;
    /**  right that player requsted to use*/
    private Chancellor.RightTypes chancellorRightToUse;
    /**  possible abilities of the players*/
    private HashMap<Integer, AbilityController> abilities;

    /** returns players visual data */
    public UserData.VisualData getVisualData() {
        return this.userData.visualData;
    }

    /**returns the id of the player
     * @return the id of the player
     */
    public int getPlayerID() {
        return userData.getID();
    }

    /** scene of the screen */
    private Scene scene;
    /** voting that is currently going */
    private Voting currentVoting;

    public HumanPlayerGameManager(int id) {
        this.userData = new UserData(id, "Player " + Integer.toString(id), "defaultPlayerImage.png");
        currentRole = CurrentRoles.None;

        abilities = new HashMap<>();
    }

    /** method to initialize the screen */
    public void initializeScreen() {
        try {
            FXMLLoader sceneLoader = new FXMLLoader(App.class.getResource("fxml/gameVisualization.fxml"));
            sceneLoader.setController(this);
            this.scene = new Scene(sceneLoader.load());
        } catch(IOException e) {
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

    /** method to initialize the game */
    public void initializeGame() {
        this.currentVoting = null;

        super.getVotingResultObservers().subscribe((Boolean result) -> this.vote(result));
    }

    /**shows the death message*/
    public void kill() {
        super.showDeathMessge();
    }

    /**method to add president ability to the holder
     * @param rightType right type
     * @param right the right
     * @param rightsHolder vbox where right should be putted in
     */
    private void addAbility(President.RightTypes rightType, Right right, VBox rightsHolder) {
        if (right.isActivate()) {
            System.out.println("Right " + rightType.toString() + " is active");

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
            });
            abilities.put(rightType.ordinal(), rightController);
        }
    }

    /**method to add chancellor ability to the holder
     * @param rightType right type
     * @param right the right
     * @param rightsHolder vbox where right should be putted in
     */
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
            });

            abilities.put(rightType.ordinal(), rightController);
        }
    }

    /** Method to make the player the president
     * @param rights the rights of the president
     */
    public void makePresident(EnumMap<President.RightTypes, Right> rights) {
        VBox rightsHolder = super.getRightsHolder();

        for (Map.Entry<President.RightTypes, Right> right : rights.entrySet()) {
            if (right.getValue().isActivate()) 
                addAbility(right.getKey(), right.getValue(), rightsHolder);
        }
    }

    /** Method to unmake player the president */
    public void unmakePresident() {
        VBox rightsHolder = super.getRightsHolder();
        rightsHolder.getChildren().clear();
        abilities.clear();
    }

    /** Method to update a right */
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

    /** Method to change the look of the current preident */
    public void changePresident(Integer oldPresident, Integer newPresident) {
        if (oldPresident != -1)
            super.setIconPlayerPane(PlayerPaneController.Icons.NONE, oldPresident);
        if (newPresident != -1)
            super.setIconPlayerPane(PlayerPaneController.Icons.PRESIDENT, newPresident);
    }

    /** Method to make the player the chancellor
     * @param rights the rights of the chamcellor
     */
    public void makeChancellor(EnumMap<Chancellor.RightTypes, Right> rights) {
        VBox rightsHolder = super.getRightsHolder();

        if (rights.get(Chancellor.RightTypes.VetoPower).isActivate()) {
            super.activateVetoPower(Chancellor.RightTypes.VetoPower.ordinal());
            super.getVetoPowerPressed().subscribe((Integer informValue) -> {
                gameController.executeChancellorRight(userData.getID(), Chancellor.RightTypes.VetoPower);
            });
        }

        for (Map.Entry<Chancellor.RightTypes, Right> right : rights.entrySet()) {
            if (right.getValue().isActivate() && right.getKey() != Chancellor.RightTypes.VetoPower) 
                addAbility(right.getKey(), right.getValue(), rightsHolder);
        }
    }

    /** Method to unmake player the chancellor */
    public void unmakeChancellor() {
        VBox rightsHolder = super.getRightsHolder();
        rightsHolder.getChildren().clear();
        abilities.clear();
    }

    /** Method to update a right 
     * {@inheritDoc}
    */
    public void changeChancellorRight(Map.Entry<Chancellor.RightTypes, Right> right) {
        if (right.getKey() == Chancellor.RightTypes.VetoPower) {
            super.activateVetoPower(Chancellor.RightTypes.VetoPower.ordinal());
            return;
        }

        AbilityController ability = abilities.get(right.getKey().ordinal());
        if (ability != null)
            ability.setUsageCount(right.getValue().getUseCount());
        else
            addAbility(right.getKey(), right.getValue(), getRightsHolder());
    }

    /** Method to change the look of the current chancellor {@inheritDoc}*/
    public void changeChancellor(Integer oldChancellor, Integer newChancellor) {
        if (oldChancellor != -1)
            super.setIconPlayerPane(PlayerPaneController.Icons.NONE, oldChancellor);
        if (newChancellor != -1)
            super.setIconPlayerPane(PlayerPaneController.Icons.CHANCELLOR, newChancellor);
    }

    /**shows the player role {@inheritDoc}*/
    public void killOtherPlayer(int playerID) {
        super.getPlayerIcons().get(playerID).showIcon(Icons.KILLED);
    }

    /**
     * @param cards cards that are given to be removed
     */
    public void giveCardsToRemove(ArrayList<CardsArray.Card> cards) {
        super.getCardRemovalChooseObservers().subscribe((Integer i) -> gameController.informCardRemoved(i, userData.getID()), 1);
        super.showCardsToRemove(cards);
    }

    /**
     * method to vote in the voting
     * @param result the vote 
     */
    private void vote(Boolean result) {
        if (currentVoting != null) {
            this.currentVoting.vote(getPlayerID(), result);
            this.currentVoting = null;
        }
    }

    /**the request to vote for chancellor
     * @param voting the voting obj
     */
    public void voteForChancellor(Voting voting, int presidentID, int chancellorID) {
        if (this.currentVoting != null) 
            System.out.println("Trying to add voting while other didnt ended");
        this.currentVoting = voting;
        super.startVoting(presidentID, chancellorID);
    }

    /**the request to vote for chancellor
     * @param voting the voting obj
     */
    public void voteForChancellor(Voting voting) {
        this.voteForChancellor(voting, -1, -1);
    }

    /**sets ProxyGameController 
     * @param gameController the proxy of the game controller
     */
    public void setProxyGameController(GameControllerVisualService gameController) {
        this.gameController = gameController;
        super.setGameContrlProxy(gameController);
    }

    /**returns the games scene
     * @return the scene
     */
    public Scene getScene() {
        return this.scene;
    }
}
