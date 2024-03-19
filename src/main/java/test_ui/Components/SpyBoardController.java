package test_ui.Components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import test_ui.App;

public class SpyBoardController {

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

    @FXML
    public void initialize() {
        System.out.println("Initialized");
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

    public void showCard(int index) {
        this.spysCardsImages[index].setImage(this.spyCardImage);
    }
}
