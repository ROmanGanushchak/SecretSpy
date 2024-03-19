package test_ui.Components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import test_ui.App;

public class LiberalBoardController {
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

    @FXML
    void buttonPress(ActionEvent event) {
        System.out.println("Card was pressed");
        this.showCard(this.index);
        this.index++;
    }

    @FXML
    void circlePress(ActionEvent event) {
        System.out.println("Circle was pressed");
        this.moveVotingCircle(this.circleIndex);
        this.circleIndex = (this.circleIndex + 1) % this.circesCount;
    }

    @FXML
    public void initialize() {
        System.out.println("Initialized");
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

    public void showCard(int index) {
        if (index >= cardSlotCount || index < 0) throw new RuntimeException("Uncorrect index of array");
        this.liberalCardsImages[index].setImage(this.cardImage);
    }

    public void moveVotingCircle(int index) {
        if (index < 0 || index >= this.circesCount) throw new RuntimeException("Uncorrect index of array");
        this.circles[index].setOpacity(1);
        this.circles[lastCircleActivated].setOpacity(0);
        this.lastCircleActivated = index;
    }
}