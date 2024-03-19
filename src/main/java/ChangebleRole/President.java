package ChangebleRole;

import Player.*;

public class President implements ChangebleRole {
    public static enum presidntRights {
        RevealingRoles,
        CheckingUpperThreeCards,
        ChoosingNextPresident,
        KillingPlayers,
        VetoPower
    }

    private boolean currentRights[]; 
    private PlayerData player;

    public President() {
        this.currentRights = new boolean[presidntRights.values().length];
    }

    public void expandPower(presidntRights newRight) {
        this.currentRights[newRight.ordinal()] = true;
    }

    public void lowerPower(presidntRights newRight) {
        this.currentRights[newRight.ordinal()] = false;
    }

    public boolean isRightActivated(presidntRights right) {
        return this.currentRights[right.ordinal()];
    }

    public void change(PlayerData newPrisent) {
        this.player = newPrisent;
    }

    public PlayerData getPlayer() {
        return player;
    }
}
