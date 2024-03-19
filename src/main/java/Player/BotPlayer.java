package Player;

public class BotPlayer extends Player{
    public BotPlayer(String name, String iconImageUrl) {
        super(name, iconImageUrl);
    }

    public void voteForChancler() {
        this.proxyGameController.yesVote();
    }
}
