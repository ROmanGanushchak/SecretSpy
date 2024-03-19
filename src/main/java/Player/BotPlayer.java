package Player;

import model.Voting.Voting;

public class BotPlayer extends Player{
    public BotPlayer(int id, String name, String iconImageUrl) {
        super(id, name, iconImageUrl);
    }

    public BotPlayer(UserData playerData) {
        super(playerData);
    }

    public void voteForChancler(Voting voting) {
        voting.vote(this.getPlayerData().modelID, true);
    }
}
