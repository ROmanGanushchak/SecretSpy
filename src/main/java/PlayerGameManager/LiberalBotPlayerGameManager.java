package PlayerGameManager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
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

    public void setProxyGameController(GameControllerVisualService gameController) {
        this.gameController = gameController;
    }

    public void voteForChancellor(Voting voting) {
        voteForChancellor(voting, presidentID, chancellorID);
    }

    public void voteForChancellor(Voting voting, int presidentID, int chancellorID) {
        if (spyChances.get(chancellorID) >= avarangeSpyPercent + 0.15 + rand.nextInt(3) / 10.0) {
            voting.vote(userData.getID(), false);
        } else 
            voting.vote(userData.getID(), true);
    }

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

    public void makePresident(EnumMap<President.RightTypes, Right> rights) {
        role = CurrentRoles.President;

        // choosing chancellor
        double minSpyChance = Double.MAX_VALUE;
        int minSpyChanceIndex = -1;
        HashSet<Integer> forbiden = new HashSet<Integer>(this.gameController.getNonChooseblePlayers(userData.getID(), President.RightTypes.ChoosingChancellor));

        for (Map.Entry<Integer, Double> spyChance : spyChances.entrySet()) {
            if (!forbiden.contains(spyChance.getKey()) && spyChance.getValue() < minSpyChance) {
                minSpyChance = spyChance.getValue();
                minSpyChanceIndex = spyChance.getKey();
            }
        }

        final int resultIndex = minSpyChanceIndex;
        Timeline delay = new Timeline(new KeyFrame(
            Duration.seconds(1),
            ae -> {
                gameController.executePresidentRight(userData.getID(), President.RightTypes.ChoosingChancellor, Math.max(resultIndex, 0));
                gameController.executePresidentRight(userData.getID(), President.RightTypes.FinishCycle);
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

    public void makeChancellor(EnumMap<Chancellor.RightTypes, Right> rights) {
        role = CurrentRoles.Chancellor;
        this.chancellorRights = rights;
    }

    public void unmakeChancellor() {
        role = CurrentRoles.None;
        System.out.println("Lberal unmade president");
    }

    public void changeChancellor(Integer oldChancellor, Integer newChancellor) {
        chancellorID = newChancellor;
    }

    public void giveCardsToRemove(ArrayList<CardsArray.Card> cards) {
        System.out.println("Liberal got cards");

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

    public void killOtherPlayer(int playerID) {
        if (spyChances.get(playerID) == null) 
            return;

        percentSum -= spyChances.get(playerID);
        playerCount--;
        avarangeSpyPercent = percentSum / (double) playerCount;
        spyChances.put(playerID, null);
    }

    public void revealCards(Card[] cards) {

    }

    public void kill() {}
    
    public void showRole(PlayerModel.mainRoles role) {}

    public void showRole(PlayerModel.mainRoles role, ArrayList<Integer> spyes, Integer shadowLeader) {}

    public UserData.VisualData getVisualData() {
        return this.userData.visualData;
    }

    public int getPlayerID() {
        return userData.getID();
    }

    public void changePresidentRight(Map.Entry<President.RightTypes, Right> right) {
        if (right.getKey() == President.RightTypes.FinishCycle && this.role == CurrentRoles.President && presidentRights.get(President.RightTypes.FinishCycle).isActivate()) {
            Timeline delay = new Timeline(new KeyFrame(
                Duration.seconds(1),
                ae -> {
                    gameController.executePresidentRight(userData.getID(), President.RightTypes.FinishCycle);
                }
            ));
            delay.play();
        }
    }
    public void changeChancellorRight(Map.Entry<Chancellor.RightTypes, Right> right) {}

    public void informPresidentRightUsage(President.RightTypes right, ExecutionStatusWrapper status) {}
    public void informChancellorRightUsage(Chancellor.RightTypes right, ExecutionStatusWrapper status) {

    }
}
