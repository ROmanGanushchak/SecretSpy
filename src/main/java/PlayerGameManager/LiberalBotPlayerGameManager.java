package PlayerGameManager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import GameController.GameControllerVisualService;
import User.UserData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import model.Voting.Voting;
import model.Cards.CardsArray;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.Chancellor;
import model.ChangebleRole.President;
import model.Game.PlayerModel;
import model.ChangebleRole.Right.ExecutionStatusWrapper;
import model.ChangebleRole.Right;

/**
 * Manages game logic and interactions for a liberal bot player in a political simulation game.
 * This class implements the PlayerGameManager interface to handle player decisions, 
 * such as voting and role assignments, based on calculated spy chances.
 */
public class LiberalBotPlayerGameManager implements PlayerGameManager {
    private GameControllerVisualService gameController;
    private Random rand = new Random();
    private HashMap<Integer, Double> spyChances;
    private double avarangeSpyPercent = 0, percentSum;
    private UserData userData;
    private CurrentRoles role;
    private PlayerModel.mainRoles mainRole;
    private int currentSpyCardCount;
    private int currentLiberalCardCount;
    private int presidentID;
    private int chancellorID;
    private int playerCount;
    EnumMap<President.RightTypes, Right> presidentRights;
    EnumMap<Chancellor.RightTypes, Right> chancellorRights;

    /**
     * Initializes a new instance of the LiberalBotPlayerGameManager.
     * 
     * @param id The unique identifier for the player.
     * @param spyesCount The count of spy players in the game.
     * @param playerIDs An array containing the IDs of all players.
     */
    public LiberalBotPlayerGameManager(int id, int spyesCount, int[] playerIDs) {
        this.userData = new UserData(id, Integer.toString(id), "defaultPlayerImage.png");
        spyChances = new HashMap<>();

        avarangeSpyPercent = spyesCount / (double) playerIDs.length;
        percentSum = playerIDs.length * avarangeSpyPercent;
        double values[] = new double[playerIDs.length];

        for (int i=0; i<playerIDs.length; i++) {
            spyChances.put(playerIDs[i], Double.valueOf(avarangeSpyPercent));
        }
        role = CurrentRoles.None;
        this.playerCount = playerIDs.length;
    }

    /**
     * Sets the game controller for this player manager.
     *
     * @param gameController The game controller to be used by this manager.
     */
    public void setProxyGameController(GameControllerVisualService gameController) {
        this.gameController = gameController;
    }

    /**
     * Votes for a chancellor based on spy chances.
     *
     * @param voting The voting system to process this vote.
     */
    public void voteForChancellor(Voting voting) {
        voteForChancellor(voting, presidentID, chancellorID);
    }

    /**
     * Votes for a chancellor with specified president and chancellor IDs.
     *
     * @param voting The voting system to process this vote.
     * @param presidentID The ID of the president proposing the chancellor.
     * @param chancellorID The ID of the chancellor being voted on.
     */
    public void voteForChancellor(Voting voting, int presidentID, int chancellorID) {
        if (spyChances.get(chancellorID) >= avarangeSpyPercent + 0.15 + rand.nextInt(3) / 10.0) {
            voting.vote(userData.getID(), false);
        } else 
            voting.vote(userData.getID(), true);
    }

    /**
     * Adds a card to the board and updates spy chances accordingly.
     *
     * @param type The type of card being added (Liberal or Spy).
     */
    public void addCardToBoard(Card.states type) {
        if (type == Card.states.Liberal)
            this.currentLiberalCardCount++;
        else if (type == Card.states.Spy)
            this.currentSpyCardCount++;
        
        percentSum -= this.spyChances.get(presidentID) + this.spyChances.get(chancellorID);
        if (type == Card.states.Spy) {
            this.spyChances.put(presidentID, this.spyChances.get(presidentID) + 0.5 - this.spyChances.get(presidentID) * 0.5); 
            this.spyChances.put(chancellorID, this.spyChances.get(chancellorID) + 0.5 - this.spyChances.get(chancellorID) * 0.5); 
        } else {
            this.spyChances.put(presidentID, this.spyChances.get(presidentID) - 0.5 + this.spyChances.get(presidentID) * 0.5); 
            this.spyChances.put(chancellorID, this.spyChances.get(chancellorID) - 0.5 + this.spyChances.get(chancellorID) * 0.5); 
        }

        percentSum += this.spyChances.get(presidentID) + this.spyChances.get(chancellorID);
        avarangeSpyPercent = percentSum / (double) playerCount;
    }

    /**
     * Assigns the role of President to this player and chooses a Chancellor.
     *
     * @param rights The rights available to the president.
     */
    public void makePresident(EnumMap<President.RightTypes, Right> rights) {
        // Implementation for making a president
    }

    /**
     * Removes the President role from this player.
     */
    public void unmakePresident() {
        role = CurrentRoles.None;
    }

    /**
     * Changes the president from one player to another.
     *
     * @param oldPresident The ID of the outgoing president.
     * @param newPresident The ID of the incoming president.
     */
    public void changePresident(Integer oldPresident, Integer newPresident) {
        presidentID = newPresident;
    }

    /**
     * Assigns the role of Chancellor to this player.
     *
     * @param rights The rights available to the chancellor.
     */
    public void makeChancellor(EnumMap<Chancellor.RightTypes, Right> rights) {
        role = CurrentRoles.Chancellor;
        this.chancellorRights = rights;
    }

    /**
     * Removes the Chancellor role from this player.
     */
    public void unmakeChancellor() {
        role = CurrentRoles.None;
    }

    /**
     * Changes the chancellor from one player to another.
     *
     * @param oldChancellor The ID of the outgoing chancellor.
     * @param newChancellor The ID of the incoming chancellor.
     */
    public void changeChancellor(Integer oldChancellor, Integer newChancellor) {
        chancellorID = newChancellor;
    }

    /**chooses the card that will be removed
     * @param cards cards from what card to remove will be chosen
     */
    public void giveCardsToRemove(ArrayList<CardsArray.Card> cards) {
        if (role == CurrentRoles.President) {
            for (int i=0; i<cards.size(); i++) {
                if (cards.get(i).state == Card.states.Spy) {
                    this.gameController.informCardRemoved(i, userData.getID());
                    return;
                }
            }

            this.gameController.informCardRemoved(0, userData.getID());
        }
        else if (role == CurrentRoles.Chancellor) {
            int spyCardsCount=0, spyCardIndex = 0;
            for (int i=0; i<cards.size(); i++) {
                if (cards.get(i).state == Card.states.Spy) {
                    spyCardsCount++;
                    spyCardIndex = i;
                }
            }

            if (spyCardsCount == 0 || spyCardsCount == 1) 
                gameController.informCardRemoved(spyCardIndex, userData.getID());
            else {
                if (chancellorRights.get(Chancellor.RightTypes.VetoPower).isActivate()) {
                    gameController.executeChancellorRight(userData.getID(), Chancellor.RightTypes.VetoPower);
                } else 
                    gameController.informCardRemoved(spyCardIndex, userData.getID());
            }
        } else 
            System.out.println("Trying to ask to remove card while the relo isnt setted");
    }

    /**shows the death of other player
     * @param playerID id of the player
     */
    public void killOtherPlayer(int playerID) {
        if (spyChances.get(playerID) == null) 
            return;

        percentSum -= spyChances.get(playerID);
        playerCount--;
        avarangeSpyPercent = percentSum / (double) playerCount;
        spyChances.put(playerID, null);
    }

    /** Reveal upper cards from the deck */
    public void revealCards(Card[] cards) {}

    /** Kills the player */
    public void kill() {}
    
    /** Shows the players role */
    public void showRole(PlayerModel.mainRoles role) {}

    /** Shows the players role */
    public void showRole(PlayerModel.mainRoles role, ArrayList<Integer> spyes, Integer shadowLeader) {}

    /** Returns the visual data of the player
     * @return the visual data */
    public UserData.VisualData getVisualData() {
        return this.userData.visualData;
    }

    /** Returns the id of the player
     * @return id */
    public int getPlayerID() {
        return userData.getID();
    }

    /** Updates the president right 
     * @param right pair of right type and the right itself*/
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
    /** Updates the chancellor right 
     * @param right pair of right type and the right itself*/
    public void changeChancellorRight(Map.Entry<Chancellor.RightTypes, Right> right) {}

    /** Method to inform the president right usage and its status
     * @param right right type
     * @param status execution status
     */
    public void informPresidentRightUsage(President.RightTypes right, ExecutionStatusWrapper status) {}

    /** Method to inform the chancellor right usage and its status
     * @param right right type
     * @param status execution status
     */
    public void informChancellorRightUsage(Chancellor.RightTypes right, ExecutionStatusWrapper status) {}
}