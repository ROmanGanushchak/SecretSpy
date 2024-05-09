package model.Game;

/** the player class that is used in game model to store the information about the plauer */
public class PlayerModel {
    /** all posible players roles */
    public static enum mainRoles {
        Undefined,
        Spy,
        Liberal,
        ShadowLeader
    }
    /** the players id */
    private int id;
    /** the players role */
    private mainRoles role;

    public PlayerModel(int id, mainRoles role) {
        this.id = id;
        this.role = role;
    }

    /**
     * returns the id of the player
     * @return the id of the player
     */
    public int getId() {
        return this.id;
    }

    /**
     * returns the role of the player
     * @return the role
     */
    public mainRoles getRole() {
        return this.role;
    }
}
