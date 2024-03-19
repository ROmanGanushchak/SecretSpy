package Player;

public class PlayerData {
    public static enum mainRoles {
        Undefined,
        Spy,
        Liberal,
        ShadowLeader
    }

    public mainRoles role;
    public String name;
    public String iconImageUrl;

    public PlayerData(String name, String iconImageUrl) {
        this.name = name;
        this.iconImageUrl = iconImageUrl;
    }
}
