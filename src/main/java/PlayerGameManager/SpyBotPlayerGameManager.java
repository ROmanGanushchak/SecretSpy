package PlayerGameManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import GameController.GameControllerVisualService;
import User.UserData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import model.Cards.CardsArray;
import model.Cards.CardsArray.Card;
import model.ChangebleRole.Chancellor;
import model.ChangebleRole.Political;
import model.ChangebleRole.President;
import model.Game.PlayerModel;
import model.Voting.Voting;

public class SpyBotPlayerGameManager implements PlayerGameManager {
    private GameControllerVisualService gameController;
    private Random rand = new Random();

    private UserData userData;
    private CurrentRoles role;
    private PlayerModel.mainRoles mainRole;

    private int currentSpyCardCount;
    private int currentLiberalCardCount;

    private int presidentID;
    private int chancellorID;

    private int playerCount;

    private HashSet<Integer> spyeTeamets;
    private HashSet<Integer> playersID;
    private Integer shadowLedear;

    EnumMap<President.RightTypes, Political.Right> presidentRights;
    EnumMap<Chancellor.RightTypes, Political.Right> chancellorRights;

    public SpyBotPlayerGameManager(int id, int spyesCount, int[] playerIDs) {
        this.userData = new UserData(id, Integer.toString(id), "defaultPlayerImage.png");
        this.playersID = new HashSet<>();
        for (int playerid : playerIDs)
            this.playersID.add(playerid);

        role = CurrentRoles.None;
        this.playerCount = playerIDs.length;
    }

    public void setProxyGameController(GameControllerVisualService gameController) {
        this.gameController = gameController;
    }

    public void voteForChancellor(Voting voting) {
        voteForChancellor(voting, presidentID, chancellorID);
    }

    public void voteForChancellor(Voting voting, int presidentID, int chancellorID) {
        if (currentLiberalCardCount < 4 || spyeTeamets.contains(chancellorID) || chancellorID == shadowLedear) 
            voting.vote(userData.getID(), true);
        else 
            voting.vote(userData.getID(), false);
    }

    public void addCardToBoard(Card.states type) {
        if (type == Card.states.Liberal)
            this.currentLiberalCardCount++;
        else if (type == Card.states.Spy)
            this.currentSpyCardCount++;
    }

    public void makePresident(EnumMap<President.RightTypes, Political.Right> rights) {
        System.out.println("Spy is president");
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

        System.out.println("Player to choose " + playerToChoose);
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

    public void unmakePresident() {
        role = CurrentRoles.None;
    }

    public void changePresident(Integer oldPresident, Integer newPresident) {
        presidentID = newPresident;
    }

    public void makeChancellor(EnumMap<Chancellor.RightTypes, Political.Right> rights) {
        role = CurrentRoles.Chancellor;
        this.chancellorRights = rights;
    }

    public void unmakeChancellor() {
        role = CurrentRoles.None;
    }

    public void changeChancellor(Integer oldChancellor, Integer newChancellor) {
        chancellorID = newChancellor;
    }

    public void giveCardsToRemove(ArrayList<CardsArray.Card> cards) {
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

    public void killOtherPlayer(int playerID) {
        spyeTeamets.remove(playerID);
        playersID.remove(playerID);
    }

    public void revealCards(Card[] cards) {

    }

    public void kill() {}
    
    public void showRole(PlayerModel.mainRoles role) {}
    public void showRole(PlayerModel.mainRoles role, ArrayList<Integer> spyes, Integer shadowLeader) {
        this.spyeTeamets = new HashSet<>(spyes);
        this.shadowLedear = shadowLeader;
    }

    public UserData.VisualData getVisualData() {
        return this.userData.visualData;
    }

    public int getPlayerID() {
        return userData.getID();
    }

    public void changePresidentRight(Map.Entry<President.RightTypes, Political.Right> right) {
        if (right.getKey() == President.RightTypes.FinishCycle && this.role == CurrentRoles.President && presidentRights.get(President.RightTypes.FinishCycle).isActivate()) {
            Timeline delay = new Timeline(new KeyFrame(
                Duration.seconds(5),
                ae -> {
                    gameController.executePresidentRight(userData.getID(), President.RightTypes.FinishCycle);
                }
            ));
            delay.play();
        }
    }
    public void changeChancellorRight(Map.Entry<Chancellor.RightTypes, Political.Right> right) {}
}