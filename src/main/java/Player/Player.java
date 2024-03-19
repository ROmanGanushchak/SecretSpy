package Player;

import Game.GameControllerVisualService;

public abstract class Player {
    private PlayerData data;
    protected GameControllerVisualService proxyGameController;

    public Player(String name, String iconImageUrl) {
        data = new PlayerData(name, iconImageUrl);
    }

    public abstract void voteForChancler();

    public void setGameContrlProxy(GameControllerVisualService proxyGameController) {
        this.proxyGameController = proxyGameController;
    }

    public PlayerData.mainRoles getRole() {
        return this.data.role;
    }
    
    public void setRole(PlayerData.mainRoles role) {
        this.data.role = role;
    }

    public PlayerData getPlayerData() {
        return data;
    }

    public String getName() {
        return this.data.name;
    }

    public String getIconImageUrl() {
        return this.data.iconImageUrl;
    }
}
