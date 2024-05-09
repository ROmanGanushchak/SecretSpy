package User;

/** stores the userdata of the player */
public class UserData {
    /** stores the visual data of the player, for outpside code only read methods */
    public static class VisualData {
        /** name of the user */
        private String name;
        /** url of the icon if the player */
        private String iconImageUrl;

        private VisualData(String name, String iconImageUrl) {
            this.name = name;
            this.iconImageUrl = iconImageUrl;
        }

        /**return the name of the user
         * @return the name of the user
         */
        public String getName() {
            return name;
        }

        /**returns the url of the player icom
         * @return the url of the player icom
         */
        public String getImageURL() {
            return this.iconImageUrl;
        }
    }
    /** id of the user */
    private int id;
    /** the visual data */
    public VisualData visualData;

    public UserData(int id, String name, String iconImageUrl) {
        visualData = new VisualData(name, iconImageUrl);
        this.id = id;
    }

    /**returns the id of the user
     * @return the id of the user
     */
    public int getID() {
        return this.id;
    }

    /**set the id
     * @param id new id
     */
    public void setID(int id) {
        this.id = id;
    }

    /**set the name
     * @param name new name
     */
    public void setName(String name) {
        this.visualData.name = name;
    }

    /**set the url
     * @param url new url
     */
    public void setIconImageURL(String url) {
        this.visualData.iconImageUrl = url;
    }
}
