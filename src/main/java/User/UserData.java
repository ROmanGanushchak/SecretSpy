package User;

public class UserData {
    public static class VisualData { // for outpside code only read methods
        private String name;
        private String iconImageUrl;

        private VisualData(String name, String iconImageUrl) {
            this.name = name;
            this.iconImageUrl = iconImageUrl;
        }

        public String getName() {
            return name;
        }

        public String getImageURL() {
            return this.iconImageUrl;
        }
    }

    private int id;
    public VisualData visualData;

    public UserData(int id, String name, String iconImageUrl) {
        visualData = new VisualData(name, iconImageUrl);
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.visualData.name = name;
    }

    public void setIconImageURL(String url) {
        this.visualData.iconImageUrl = url;
    }
}
