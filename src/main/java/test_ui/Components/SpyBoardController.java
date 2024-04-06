package test_ui.Components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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
    private ImageView spysCardsImages[];

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

        this.spysCardsImages = new ImageView[]{cardImage0, cardImage1, cardImage2, cardImage3, cardImage4, cardImage5};
        this.spyCardImage = new Image(App.class.getResourceAsStream("images/spyCard.png"));
    }

    @FXML
    void addCard(ActionEvent event) {
        System.out.println("card adding");
        showCard(this.index++);
    }

    public void showNextCard() {
        this.spysCardsImages[index++].setImage(this.spyCardImage);
    }

    public void showCard(int index) {
        this.spysCardsImages[index].setImage(this.spyCardImage);
    }
}
