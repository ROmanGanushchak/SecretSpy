package PlayerGameManager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import GameController.GameControllerVisualService;
import User.UserData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import model.Cards.CardsArray;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.Chancellor;
import model.ChangebleRole.Right;
import model.ChangebleRole.Right.ExecutionStatusWrapper;
import model.ChangebleRole.President;
import model.Game.PlayerModel;
import model.Voting.Voting;


/**
 * Manages game logic and interactions for a spy bot player in a political simulation game.
 * This class implements the PlayerGameManager interface to handle player decisions, 
 * such as voting and role assignments, based on calculated spy chances.
 */
public class SpyBotPlayerGameManager implements PlayerGameManager {
    private GameControllerVisualService gameController;
    private UserData userData;
    private CurrentRoles role;

    private int currentSpyCardCount;
    private int currentLiberalCardCount;

    private int presidentID;
    private int chancellorID;

    private HashSet<Integer> spyeTeamets;
    private HashSet<Integer> playersID;
    private Integer shadowLedear;

    EnumMap<President.RightTypes, Right> presidentRights;
    EnumMap<Chancellor.RightTypes, Right> chancellorRights;

    /**
     * Initializes a new instance of the LiberalBotPlayerGameManager.
     * 
     * @param id The unique identifier for the player.
     * @param spyesCount The count of spy players in the game.
     * @param playerIDs An array containing the IDs of all players.
     */
    public SpyBotPlayerGameManager(int id, int spyesCount, int[] playerIDs) {
        this.userData = new UserData(id, Integer.toString(id), "defaultPlayerImage.png");
        this.playersID = new HashSet<>();
        for (int playerid : playerIDs)
            this.playersID.add(playerid);

        role = CurrentRoles.None;
    }

    /** sets the prixy game controller, has to be called before the other methods
     * @param gameController game controller visal proxy
     */
    public void setProxyGameController(GameControllerVisualService gameController) {
        this.gameController = gameController;
    }

    /**the request to vote for chancellor
     * @param voting the voting obj
     */
    public void voteForChancellor(Voting voting) {
        voteForChancellor(voting, presidentID, chancellorID);
    }

    /**the request to vote for chancellor
     * @param voting the voting obj
     * @param presidentID id of the president 
     * @param chancellorID id of the candidate
     */
    public void voteForChancellor(Voting voting, int presidentID, int chancellorID) {
        if (currentLiberalCardCount < 4 || spyeTeamets.contains(chancellorID) || chancellorID == shadowLedear) 
            voting.vote(userData.getID(), true);
        else 
            voting.vote(userData.getID(), false);
    }

    /** increases the count of liberals or spyes cards based on card type
     * @param type type of card being added
     */
    public void addCardToBoard(Card.states type) {
        if (type == Card.states.Liberal)
            this.currentLiberalCardCount++;
        else if (type == Card.states.Spy)
            this.currentSpyCardCount++;
    }

    /** Method to make the player the president
     * @param rights the rights of the president
     */
    public void makePresident(EnumMap<President.RightTypes, Right> rights) {
        role = CurrentRoles.President;

        HashSet<Integer> forbidenPlayers = new HashSet<>(gameController.getNonChooseblePlayers(userData.getID(), President.RightTypes.ChoosingChancellor));
        Integer playerToChoose = null;

        if (currentSpyCardCount > 3 && !forbidenPlayers.contains(shadowLedear)) {
            playerToChoose = shadowLedear;
        } else if (currentSpyCardCount > 1) {
            for (Integer player : spyeTeamets) {
                if (!forbidenPlayers.contains(player)) {
                    playerToChoose = player;
                    break;
                }
            }

            if (playerToChoose == null) {
                for (Integer player : playersID) {
                    if (!forbidenPlayers.contains(player)) {
                        playerToChoose = player;
                        break;
                    }
                }
            }
        } else {
            for (Integer player : playersID) {
                if (!forbidenPlayers.contains(player)) {
                    playerToChoose = player;
                    break;
                }
            }
        }
        final Integer resultIndex = playerToChoose;
        Timeline delay = new Timeline(new KeyFrame(
            Duration.seconds(5),
            ae -> {
                gameController.executePresidentRight(userData.getID(), President.RightTypes.ChoosingChancellor, Math.max(resultIndex, 0));
            }
        ));
        delay.play();
        this.presidentRights = rights;
    }

    /** Method to unmake player the president */
    public void unmakePresident() {
        role = CurrentRoles.None;
        System.out.println("Spy unmade president");
    }

    /** Method to change the look of the current preident */
    public void changePresident(Integer oldPresident, Integer newPresident) {
        presidentID = newPresident;
    }

    /** Method to make the player the chancellor
     * @param rights the rights of the chamcellor
     */
    public void makeChancellor(EnumMap<Chancellor.RightTypes, Right> rights) {
        role = CurrentRoles.Chancellor;
        this.chancellorRights = rights;
    }

    /** Method to unmake player the chancellor */
    public void unmakeChancellor() {
        role = CurrentRoles.None;
    }

    /** Method to change the look of the current chancellor */
    public void changeChancellor(Integer oldChancellor, Integer newChancellor) {
        chancellorID = newChancellor;
    }

    /**Gives the list of cards, to let remove one of then
     * @param cards cards that are given to be removed
     */
    public void giveCardsToRemove(ArrayList<CardsArray.Card> cards) {
        System.out.println("Spy got cards");

        if (role == CurrentRoles.President) {
            for (int i=0; i<cards.size(); i++) {
                if (cards.get(i).state == Card.states.Liberal) {
                    this.gameController.informCardRemoved(i, userData.getID());
                    return;
                }
            }

            this.gameController.informCardRemoved(0, userData.getID());
        }
        else if (role == CurrentRoles.Chancellor) {
            int liberalCardsCount = 0, spyCardIndex = 0;
            for (int i=0; i<cards.size(); i++) {
                if (cards.get(i).state == Card.states.Liberal) {
                    liberalCardsCount++;
                    spyCardIndex = i;
                }
            }

            if (liberalCardsCount == 0 || liberalCardsCount == 1 || !chancellorRights.get(Chancellor.RightTypes.VetoPower).isActivate()) 
                gameController.informCardRemoved(spyCardIndex, userData.getID());
            else 
                gameController.executeChancellorRight(userData.getID(), Chancellor.RightTypes.VetoPower);
        } else 
            System.out.println("Trying to ask to remove card while the relo isnt setted");
    }

    /** shows the death of the other player
     * @param playerID id of the player that was killed
     */
    public void killOtherPlayer(int playerID) {
        spyeTeamets.remove(playerID);
        playersID.remove(playerID);
    }

    /**reveal the cards to the player
     * @param cards cards that will be shown
     */
    public void revealCards(Card[] cards) {

    }

    /**shows the death message*/
    public void kill() {}
    
    /**shows the player role
     * @param role role of the player
     */
    public void showRole(PlayerModel.mainRoles role) {}
    /**Shows the player role
     * @param role role of the player
     * @param spyes list of ids of spyes
     * @param shadowLeader id of the shadowLeader
     */
    public void showRole(PlayerModel.mainRoles role, ArrayList<Integer> spyes, Integer shadowLeader) {
        this.spyeTeamets = new HashSet<>(spyes);
        this.shadowLedear = shadowLeader;
    }

    /**Returns the player visual data
     * @return the player visual data
     */
    public UserData.VisualData getVisualData() {
        return this.userData.visualData;
    }

    /**Returns the id of the player
     * @return the id of the player
     */
    public int getPlayerID() {
        return userData.getID();
    }

    /** Method to update a right 
     * @param right pair of right type and right itseld
    */
    public void changePresidentRight(Map.Entry<President.RightTypes, Right> right) {
        if (right.getKey() == President.RightTypes.FinishCycle && this.role == CurrentRoles.President && presidentRights.get(President.RightTypes.FinishCycle).isActivate()) {
            Timeline delay = new Timeline(new KeyFrame(
                Duration.seconds(6),
                ae -> {
                    gameController.executePresidentRight(userData.getID(), President.RightTypes.FinishCycle);
                }
            ));
            delay.play();
        }
    }

    /** Method to update a right 
     * @param right pair of right type and right itself
    */
    public void changeChancellorRight(Map.Entry<Chancellor.RightTypes, Right> right) {}

    /**method to inform the president right usage
     * @param right the right type
     * @param status the execution status
     */
    public void informPresidentRightUsage(President.RightTypes right, ExecutionStatusWrapper status) {}

    /**method to inform the chancellor right usage
     * @param right the right type
     * @param status the execution status
     */
    public void informChancellorRightUsage(Chancellor.RightTypes right, ExecutionStatusWrapper status) {}
}