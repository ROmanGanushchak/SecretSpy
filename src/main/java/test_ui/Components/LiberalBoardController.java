package test_ui.Components;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import test_ui.App;
import test_ui.Components.Component.Component;
import test_ui.Components.Component.ParentUpdaters.PaneParentUpdater;

/** Class LiberalBoardController is responsible for showing the liberal board surface and providing slotes for cards and the failing election count infornation
 * Extends from {@link Component}
 */
public class LiberalBoardController extends Component {
    @FXML
    private ImageView boardImage;

    @FXML
    private AnchorPane plane;

    @FXML
    private ImageView liberalImage0;
    @FXML
    private ImageView liberalImage1;
    @FXML
    private ImageView liberalImage2;
    @FXML
    private ImageView liberalImage3;
    @FXML
    private ImageView liberalImage4;
    private ImageView cardSlotes[];

    @FXML
    private Circle circle0;
    @FXML
    private Circle circle1;
    @FXML
    private Circle circle2;
    @FXML
    private Circle circle3;
    private Circle circles[];

    private Image cardImage;

    private int index = 0;
    private int cardSlotCount;

    private int circesCount;
    private int lastCircleActivated;

    /** Constructor, creates new instance of the class
     * @param surface the basic surface where the component will be displaied
     */
    public LiberalBoardController(Pane surface) {
        super(new PaneParentUpdater(surface));
        super.initialize(App.class.getResource("fxml/liberalBoard.fxml"), surface);
    }

    /** Initializes the object, is called by javafx */
    @FXML
    public void initialize() {
        Image image = new Image(App.class.getResourceAsStream("images/liberalsCardsBoard.png"));
        this.boardImage.setImage(image);

        this.cardSlotes = new ImageView[]{liberalImage0, liberalImage1, liberalImage2, liberalImage3, liberalImage4};
        this.circles = new Circle[]{circle0, circle1, circle2, circle3};

        this.cardSlotCount = this.cardSlotes.length;
        this.circesCount = this.circles.length;

        this.circles[0].setOpacity(1);
        this.lastCircleActivated = 0;
        for (int i=1; i<circesCount; i++) {
            this.circles[i].setOpacity(0);
        }

        this.cardImage = new Image(App.class.getResourceAsStream("images/liberalCard.png"));
    }

    /** Shows the card in first free card slote, if there arent free slote throws a mistake */
    public void showNextCard() {
        this.cardSlotes[index++].setImage(this.cardImage);
    }

    /**Shows the card in specific index, throws mistake IndexOutOfRange 
     * @param index index of slote that will be revealed
     */
    public void showCard(int index) {
        if (index >= cardSlotCount || index < 0) throw new RuntimeException("Uncorrect index of array");
        this.cardSlotes[index].setImage(this.cardImage);
    }

    /** Chenges the position of voting circle that show how many election failed in a raw
     * @param index Index where the circle will be shown
     */
    public void moveVotingCircle(int index) {
        if (index < 0 || index >= this.circesCount) return;
        this.circles[index].setOpacity(1);
        this.circles[lastCircleActivated].setOpacity(0);
        this.lastCircleActivated = index;
    }

    /** Returns the {@link Rectangle} that stores the information about the next slote position and size 
     * @return the {@link Rectangle} that stores the information about the next slote position and size 
     */
    public Rectangle getNextSloteRectangle() {
        Rectangle rec = new Rectangle();

        rec.setX(cardSlotes[index].getLayoutX() * super.getScale().getX());
        rec.setY(cardSlotes[index].getLayoutY() * super.getScale().getY());

        rec.setWidth(cardSlotes[index].getFitWidth() * super.getScale().getX());
        rec.setHeight(cardSlotes[index].getFitHeight() * super.getScale().getY());

        return rec;
    }
}