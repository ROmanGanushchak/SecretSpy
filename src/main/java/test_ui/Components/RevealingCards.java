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

public class RevealingCards extends PopupLayerManager.PopupComponent {
    @FXML
    private ImageView cardSlote1;
    @FXML
    private ImageView cardSlote2;
    @FXML
    private ImageView cardSlote3;

    private ArrayList<ImageView> cardSlotes;
    private ActObservers<Integer> exitButtonPresed;

    public RevealingCards(Pane surface) {
        super(surface);
        super.initialize(App.class.getResource("fxml/revealingCards.fxml"), surface);

        cardSlotes = new ArrayList<>(Arrays.asList(cardSlote1, cardSlote2, cardSlote3));
        exitButtonPresed = new ActObservers<>();
    }

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

    public ActObserversAccess<Integer> getExitButtonObservers() {
        return this.exitButtonPresed;
    }

    @FXML
    private void exitPressed(MouseEvent event) {
        this.exitButtonPresed.informAll(null);
    }
}
