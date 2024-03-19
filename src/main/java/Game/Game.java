package Game;

import Player.*;
import ChangebleRole.*;
import java.util.ArrayList;
import Cards.*;
import Cards.CardsArray.Card;
import javafx.util.Pair;

public class Game {
    private int randomSeed = 0;
    private ArrayList<PlayerData> players;

    private int presidentIndex;
    private President president;
    private Chancellor chancellor;
    
    private int spysInParlamentCount;
    private int liberalsInParlamentCount;

    private int requiredSpyCount = 6;
    private int requiredLiberalCount = 5;

    private CardsArray cards;
    
    public Game(ArrayList<PlayerData> players, int spyCount, int liberalsCount, int spyCardCount, int liberalCardCount) {
        this.cards = new CardsArray(spyCardCount + liberalCardCount);
        this.cards.shaffle(liberalCardCount, spyCardCount, randomSeed);
        this.cards.outputCards();
        System.out.println("Cards created");

        // PlayerData.mainRoles roles[] = new PlayerData.mainRoles[players.size()];
        // for (int i=0; i<spyCount;i++) roles[i] = PlayerData.mainRoles.Spy;
        // for (int i=0; i<liberalCardCount;i++) roles[i] = PlayerData.mainRoles.Liberal;
        // roles[spyCount + liberalsCount - 1] = PlayerData.mainRoles.ShadowLeader;
        // ArrayShaffle.shuffle(roles, 0);
        // for (int i=0; i<players.size(); i++) {
        //     players.get(i).role = roles[i];
        // }
        // this.players = players;
        // System.out.println("Roles distributed");

        this.spysInParlamentCount = 0;
        this.liberalsInParlamentCount = 0;

        this.presidentIndex = 0;
        this.president = new President();
        // this.president.change(players.get(presidentIndex));
    }

    private void increaseSpyCount() {
        this.spysInParlamentCount++;

        if (this.spysInParlamentCount == 1) this.president.expandPower(President.presidntRights.RevealingRoles);
        if (this.spysInParlamentCount == 3) this.president.expandPower(President.presidntRights.CheckingUpperThreeCards);
        if (this.spysInParlamentCount == 3) this.president.expandPower(President.presidntRights.ChoosingNextPresident);
        if (this.spysInParlamentCount == 4) this.president.expandPower(President.presidntRights.KillingPlayers);
        if (this.spysInParlamentCount == 5) this.president.expandPower(President.presidntRights.VetoPower);

        if (this.spysInParlamentCount == this.requiredSpyCount) {} //finish the game
    }

    private void increaseLiberalCount() {
        this.liberalsInParlamentCount++;
        
        if (this.spysInParlamentCount == this.requiredSpyCount) {} //finish the game
    }

    public Card takeCard() {
        if (this.cards.getCardsCount() == 0)
            throw new RuntimeException("Tring to take the card from empty CardsArray");
        
        Card card = this.cards.pop();
        if (card.state == Card.states.Liberal) this.increaseLiberalCount();
        else if (card.state == Card.states.Spy) this.increaseSpyCount();

        return card;
    }

    public void goToNextPresinent() {
        this.presidentIndex = (this.presidentIndex + 1) % this.players.size();
        this.president.change(players.get(this.presidentIndex));
    }

    public void changePresident(PlayerData player) {
        this.president.change(player);
        this.presidentIndex = this.players.indexOf(player);
    }

    public void killPlayer(PlayerData player) {
        this.players.remove(player);
    }

    public void changeRequiredMembersCount(int requiredSpyCount, int requiredLiberalCount) {
        this.requiredLiberalCount = requiredLiberalCount;
        this.requiredSpyCount = requiredSpyCount;
    }

    public void presidntRequest(President.presidntRights request) {

    }

    public int getCardCount() {
        return this.cards.getCardsCount();
    }

    public Pair<Integer, Integer> getParlamentMembership() {
        return new Pair<>(this.spysInParlamentCount, this.liberalsInParlamentCount);
    }

    public President getPresident() {
        return this.president;
    }

    // public static void main(String[] args) {
    //     Game game = getInstance(7, 4, 3, 11, 6);
    // }
}
