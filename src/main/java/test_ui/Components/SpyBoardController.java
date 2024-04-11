package test_ui.Components;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import test_ui.App;
import test_ui.Components.Component.Component;
import test_ui.Components.Component.ParentUpdaters.PaneParentUpdater;

public class SpyBoardController extends Component {

    @FXML
    private ImageView boardImage;

    @FXML
    private ImageView cardImage0;
    @FXML
    private ImageView cardImage1;
    @FXML
    private ImageView cardImage2;
    @FXML
    private ImageView cardImage3;
    @FXML
    private ImageView cardImage4;
    @FXML
    private ImageView cardImage5;
    private ImageView cardSlotes[];

    private Image spyCardImage;

    private int index = 0;

    public SpyBoardController(Pane surface) {
        super(new PaneParentUpdater(surface));
        super.initialize(App.class.getResource("fxml/spyBoard.fxml"), surface);
    }

    @FXML
    public void initialize() {
        Image image = new Image(App.class.getResourceAsStream("images/spyCardsBoard.png"));
        this.boardImage.setImage(image);

        this.cardSlotes = new ImageView[]{cardImage0, cardImage1, cardImage2, cardImage3, cardImage4, cardImage5};
        this.spyCardImage = new Image(App.class.getResourceAsStream("images/spyCard.png"));
    }

    public void showNextCard() {
        this.cardSlotes[index++].setImage(this.spyCardImage);
    }

    public void showCard(int index) {
        this.cardSlotes[index].setImage(this.spyCardImage);
    }

    public Rectangle getNextSloteRectangle() {
        Rectangle rec = new Rectangle();

        rec.setX(cardSlotes[index].getLayoutX() * super.getScale().getX());
        rec.setY(cardSlotes[index].getLayoutY() * super.getScale().getY());

        rec.setWidth(cardSlotes[index].getFitWidth() * super.getScale().getX());
        rec.setHeight(cardSlotes[index].getFitHeight() * super.getScale().getY());

        return rec;
    }
}
