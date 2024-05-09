package test_ui.Components;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import test_ui.App;
import test_ui.Components.Component.Component;
import test_ui.Components.Component.ParentUpdaters.PaneParentUpdater;

/**
 * The SpyBoardController class extends Component to manage and display a board with spy cards in a GUI application.
 * This class handles the visualization and interactions with spy cards placed on a board.
 * It is designed to be used in a JavaFX application, with bindings to FXML-defined UI components.
 */
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

    private ImageView[] cardSlotes;

    private Image spyCardImage;

    private int index = 0;

    /** Constructs a new class instance to manage spy cards on a specified pane.
     * @param surface the Pane on which the spy board will be placed, allowing for visual interaction.
     */
    public SpyBoardController(Pane surface) {
        super(new PaneParentUpdater(surface));
        super.initialize(App.class.getResource("fxml/spyBoard.fxml"), surface);
    }

    /**Initializes the controller class.
     * This method sets up the board image and initializes the card slots with the spy card image.
     */
    @FXML
    public void initialize() {
        Image image = new Image(App.class.getResourceAsStream("images/spyCardsBoard.png"));
        this.boardImage.setImage(image);
        this.cardSlotes = new ImageView[]{cardImage0, cardImage1, cardImage2, cardImage3, cardImage4, cardImage5};
        this.spyCardImage = new Image(App.class.getResourceAsStream("images/spyCard.png"));
    }

    /**
     * Shows the next spy card on the board by placing it in the next available slot.
     * This method updates the {@code index} to point to the next slot after placing a card.
     */
    public void showNextCard() {
        this.cardSlotes[index++].setImage(this.spyCardImage);
    }

    /**Shows a spy card at a specified slot index.
     * @param index the index of the slot where the card will be displayed.
     * @throws ArrayIndexOutOfBoundsException if the index is out of range.
     */
    public void showCard(int index) {
        this.cardSlotes[index].setImage(this.spyCardImage);
    }

    /**Returns the dimensions and position of the next available card slot as a {@code Rectangle}.
     * This method takes into account the scaling factors provided by the {@code Component} superclass.
     * @return a {@code Rectangle} that describes the next card slot's position and size.
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