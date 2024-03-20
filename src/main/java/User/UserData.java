package User;

public class UserData {
    public String name;
    public String iconImageUrl;
    public int id;

    public UserData(int id, String name, String iconImageUrl) {
        this.id = id;
        this.name = name;
        this.iconImageUrl = iconImageUrl;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getIconImageUrl() {
        return this.iconImageUrl;
    }

    public void load(int id) {
        
    }
}
