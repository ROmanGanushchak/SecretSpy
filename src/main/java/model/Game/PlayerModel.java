package model.Game;

public class PlayerModel {
    public static enum mainRoles {
        Undefined,
        Spy,
        Liberal,
        ShadowLeader
    }

    private int id;
    private mainRoles role;

    public PlayerModel(int id, mainRoles role) {
        this.id = id;
        this.role = role;
    }

    public int getId() {
        return this.id;
    }

    public mainRoles getRole() {
        return this.role;
    }
}
