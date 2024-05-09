package test_ui.Components;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import model.Cards.CardsArray.Card;
import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;
import test_ui.App;
import test_ui.ImageLoader;
import test_ui.PopupLayerManager;

/** Class RevealingCards extends PopupComponent and designed to show 3 cards */
public class RevealingCards extends PopupLayerManager.PopupComponent {
    @FXML
    private ImageView cardSlote1;
    @FXML
    private ImageView cardSlote2;
    @FXML
    private ImageView cardSlote3;

    private ArrayList<ImageView> cardSlotes;
    private ActObservers<Integer> exitButtonPresed;

    /** Constructor, create the instance of the class
     * @param surface the basic surface where component is placed
     */
    public RevealingCards(Pane surface) {
        super(surface);
        super.initialize(App.class.getResource("fxml/revealingCards.fxml"), surface);

        cardSlotes = new ArrayList<>(Arrays.asList(cardSlote1, cardSlote2, cardSlote3));
        exitButtonPresed = new ActObservers<>();
    }

    /** initializes the obj with the array of cards, can be used only for 3 cards.
     * @param cards the array of cards
     */
    public void initialize(ArrayList<Card> cards) {
        if (cards.size() != 3) 
            return;

        for (int i=0; i<cards.size(); i++) {
            if (cards.get(i).state == Card.states.Liberal)
                cardSlotes.get(i).setImage(ImageLoader.getInstance().getImage("liberalCard.png"));
            else if (cards.get(i).state == Card.states.Spy)
                cardSlotes.get(i).setImage(ImageLoader.getInstance().getImage("spyCard.png"));
        }
    }

    /** The methods returns the observe access of the exit button
     * @return the observe access of the exit button
     */
    public ActObserversAccess<Integer> getExitButtonObservers() {
        return this.exitButtonPresed;
    }

    /**The method is called when the exit button is pressed and informs all observers about the exit request
     * @param event
     */
    @FXML
    private void exitPressed(MouseEvent event) {
        this.exitButtonPresed.informAll(null);
    }
}
