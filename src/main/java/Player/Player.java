package Player;

import GameController.GameControllerVisualService;
import model.Voting.Voting;

public abstract class Player {
    public static class UserData {
        public String name;
        public String iconImageUrl;
        public int id;
        public int modelID;
    
        public UserData(int id, String name, String iconImageUrl) {
            this.id = id;
            this.name = name;
            this.iconImageUrl = iconImageUrl;
        }
    }

    private UserData data;
    protected GameControllerVisualService proxyGameController;

    public Player(int id, String name, String iconImageUrl) {
        data = new UserData(id, name, iconImageUrl);
    }

    public Player(UserData playerData) {
        data = playerData;
    }

    public abstract void voteForChancler(Voting voting);

    public void setGameContrlProxy(GameControllerVisualService proxyGameController) {
        this.proxyGameController = proxyGameController;
    }

    public UserData getPlayerData() {
        return data;
    }

    public String getName() {
        return this.data.name;
    }

    public String getIconImageUrl() {
        return this.data.iconImageUrl;
    }
}
