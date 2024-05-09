package test_ui.Components;

import java.util.EnumMap;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;
import test_ui.App;
import test_ui.ImageLoader;
import test_ui.Components.Component.Component;
import test_ui.Components.Component.ParentUpdaters.*;

/** Class PlayerPaneController shows the information about a layer such as its icon, name, current role, and allows to choose it. It extends class {@link Component} */
public class PlayerPaneController extends Component {
    private static EnumMap<Icons, Image> iconImages;
    public static enum Icons {
        PRESIDENT, CHANCELLOR, KILLED, NONE
    }

    static {
        iconImages = new EnumMap<>(Icons.class);

        likeImg = ImageLoader.getInstance().getImage("like.png");
        dislikeImg = ImageLoader.getInstance().getImage("dislike.png");
        lockedPlayerButChose = ImageLoader.getInstance().getImage("lockedPlayerChose.png");
        unlockedPlayerButChose = ImageLoader.getInstance().getImage("unlockedPlayerChose.png");

        iconImages.put(Icons.KILLED, ImageLoader.getInstance().getImage("killedIcon.png"));
        iconImages.put(Icons.PRESIDENT, ImageLoader.getInstance().getImage("presidentIcon.png"));
        iconImages.put(Icons.CHANCELLOR, ImageLoader.getInstance().getImage("chancellorIcon.png"));
        iconImages.put(Icons.NONE, null);
    }

    @FXML
    private ImageView choosePlayerBut;

    @FXML
    private ImageView likeImage;
    private static Image likeImg;
    private static Image dislikeImg;
    private static Image lockedPlayerButChose;
    private static Image unlockedPlayerButChose;

    @FXML
    private Label name;

    @FXML
    private ImageView roleIcon;
    @FXML
    private AnchorPane iconSurface;

    @FXML
    private AnchorPane playerIcon;
    private ActObservers<Integer> chooseButObservers;
    private Integer informValue;
    private boolean playerChoseLocked;

    /** Constructtor, creates instance of the class
     * @param vbox the holder where the obj will be placed, automaticly resize itself to fit in
     */
    public PlayerPaneController(VBox vbox) {
        super(new VBoxParentUpdater(vbox));
        super.initialize(App.class.getResource("fxml/player.fxml"), vbox);

        chooseButObservers = new ActObservers<>();
    }

    /** Initializes the obj
     * @param name              the name of the player
     * @param iconImageName     the name of the icon
     * @param informValue       the value that will be inform if player will be chosen
     */
    public void initialize(String name, String iconImageName, Integer informValue) {
        this.name.setText(name); 
        this.choosePlayerBut.setImage(unlockedPlayerButChose);
        this.playerChoseLocked = false;

        Image image = ImageLoader.getInstance().getImage(iconImageName);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false));
        Background background = new Background(backgroundImage);
        playerIcon.setBackground(background);

        this.informValue = informValue;
        Component.hide(iconSurface);
    }

    /** Blocks the possibility to choose the player by blocking the choose button, the button image will be changed */
    public void lockPlayerChose() {
        this.playerChoseLocked = true;
        this.choosePlayerBut.setImage(lockedPlayerButChose);
    }

    /** Unlocks the possibility to choose the player by unlocking the choose button, the button image will be changed */
    public void unlockPlayerChose() {
        this.playerChoseLocked = false;
        this.choosePlayerBut.setImage(unlockedPlayerButChose);
    }

    /** Shows the like icon */
    public void showLike() {
        this.likeImage.setImage(likeImg);
    }

    /** Shows the dislike icon */
    public void showDislike() {
        this.likeImage.setImage(dislikeImg);
    }

    /** Hide the like or dislike icon */
    public void hideLike() {
        this.likeImage.setImage(null);
    }

    /** Shows the role or death icon
     * @param icon icon type that will be shown
     */
    public void showIcon(Icons icon) {
        this.roleIcon.setImage(iconImages.get(icon));
        if (icon == Icons.NONE)
            Component.hide(iconSurface);
        else
            Component.reveal(iconSurface);
    }

    /** Settes the value that will be informed when the choose button will be activated
     * @param value Inform value
     */
    public void setInformValue(Integer value) {
        this.informValue = value;
    }

    /** Method is called when the choose button is pressed and infroms all folovers about the pressing
     * @param event
     */
    @FXML
    private void choosePlayerButPressed(MouseEvent event) {
        if (!playerChoseLocked)
            this.chooseButObservers.informAll(informValue);
    }

    /**
     * @return The {@link ActObserversAccess} of the choose player button
     */
    public ActObserversAccess<Integer> getChooseButObservers() {
        return this.chooseButObservers;
    }
}
