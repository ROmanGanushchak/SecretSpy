package test_ui.Components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import test_ui.App;
import test_ui.Components.Component.Component;
import test_ui.Components.Component.ParentUpdaters.PaneParentUpdater;

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
    private ImageView liberalCardsImages[];

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
    private int circleIndex=1;
    private int cardSlotCount;

    private int circesCount;
    private int lastCircleActivated;

    public LiberalBoardController(Pane surface) {
        super(new PaneParentUpdater(surface));
        super.initialize(App.class.getResource("fxml/liberalBoard.fxml"), surface);
    }

    @FXML
    public void initialize() {
        Image image = new Image(App.class.getResourceAsStream("images/liberalsCardsBoard.png"));
        this.boardImage.setImage(image);

        this.liberalCardsImages = new ImageView[]{liberalImage0, liberalImage1, liberalImage2, liberalImage3, liberalImage4};
        this.circles = new Circle[]{circle0, circle1, circle2, circle3};

        this.cardSlotCount = this.liberalCardsImages.length;
        this.circesCount = this.circles.length;

        this.circles[0].setOpacity(1);
        this.lastCircleActivated = 0;
        for (int i=1; i<circesCount; i++) {
            this.circles[i].setOpacity(0);
        }

        this.cardImage = new Image(App.class.getResourceAsStream("images/liberalCard.png"));
    }

    @FXML
    void buttonPress(ActionEvent event) {
        this.showCard(this.index);
        this.index++;
    }

    @FXML
    void circlePress(ActionEvent event) {
        this.moveVotingCircle(this.circleIndex);
        this.circleIndex = (this.circleIndex + 1) % this.circesCount;
    }

    public void showNextCard() {
        this.liberalCardsImages[index++].setImage(this.cardImage);
    }

    public void showCard(int index) {
        if (index >= cardSlotCount || index < 0) throw new RuntimeException("Uncorrect index of array");
        this.liberalCardsImages[index].setImage(this.cardImage);
    }

    public void moveVotingCircle(int index) {
        if (index < 0 || index >= this.circesCount) return;
        this.circles[index].setOpacity(1);
        this.circles[lastCircleActivated].setOpacity(0);
        this.lastCircleActivated = index;
    }
}