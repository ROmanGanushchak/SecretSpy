package test_ui.Components;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.animation.ScaleTransition;
import javafx.animation.Animation.Status;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import model.Cards.CardsArray;
import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;
import test_ui.App;
import test_ui.PopupLayerManager;
import test_ui.Components.Component.Component;

public class CardRemovalController extends PopupLayerManager.PopupComponent {
    @FXML
    private ImageView cardSlote1;
    @FXML
    private ImageView cardSlote2;
    @FXML
    private ImageView cardSlote3;
    @FXML
    private ImageView cardSlote4;
    @FXML
    private ImageView cardSlote5;

    @FXML
    private AnchorPane vetoPowerSurface;

    private Image liberalImage;
    private Image spyImage;
    private ArrayList<ImageView> slotes;
    private ArrayList<ScaleTransition> sloteAnimations;    
    private ArrayList<ImageView> activeSlotes;

    private double scaleAnimationTime = 0.1;
    private double scaleAnimationValue = 0.2;

    private ActObservers<Integer> cardSlotePressed;
    private ActObservers<Integer> vetoPowerPressed;
    private Integer vetoIngormValue;

    public CardRemovalController(Pane surface) {
        super(surface);
        super.initialize(App.class.getResource("fxml/cardRemoval.fxml"), surface);

        this.cardSlotePressed = new ActObservers<>();
        this.vetoPowerPressed = new ActObservers<>();
        Component.hide(vetoPowerSurface);
    }

    @FXML
    private void initialize() {
        slotes = new ArrayList<>(Arrays.asList(cardSlote1, cardSlote2, cardSlote3, cardSlote4, cardSlote5));

        sloteAnimations = new ArrayList<>(slotes.size());
        for (int i=0; i<slotes.size(); i++) {
            sloteAnimations.add(new ScaleTransition());
            sloteAnimations.get(sloteAnimations.size()-1).setNode(slotes.get(i));
        }

        this.liberalImage = new Image(App.class.getResourceAsStream("images/liberalCard.png"));
        this.spyImage = new Image(App.class.getResourceAsStream("images/spyCard.png"));

        for (int i=0; i<slotes.size(); i++) {
            final int index = i;
            slotes.get(i).setOnMousePressed((MouseEvent e) -> slotePressed(index));
            slotes.get(i).setOnMouseEntered((MouseEvent e) -> mouseInSlote(index));
            slotes.get(i).setOnMouseExited((MouseEvent e) -> mouseOutSlote(index));
        }
    }

    public void setup(ArrayList<CardsArray.Card> cards) {
        if (cards.size() < 2 || cards.size() > 3) {
            System.out.println("uncorrectCardsCount");
            return;
        }

        for (ImageView slote : slotes)
            Component.hide(slote);
        
        if (cards.size() == 2) 
            activeSlotes = new ArrayList<>(Arrays.asList(cardSlote2, cardSlote4));
        else if (cards.size() == 3)
            activeSlotes = new ArrayList<>(Arrays.asList(cardSlote1, cardSlote3, cardSlote5));
        
        for (int i=0; i<cards.size(); i++) {
            if (cards.get(i).state == CardsArray.Card.states.Liberal)
                activeSlotes.get(i).setImage(liberalImage);
            else if (cards.get(i).state == CardsArray.Card.states.Spy)
                activeSlotes.get(i).setImage(spyImage);

            Component.reveal(activeSlotes.get(i));
        }
    }

    private void mouseInSlote(int index) {
        ScaleTransition animation = sloteAnimations.get(index);

        if (animation.statusProperty().getValue() == Status.RUNNING) {
            Duration duration = animation.getCurrentTime();
            animation.stop();
            animation.setDuration(duration);
        } else animation.setDuration(Duration.seconds(scaleAnimationTime));

        double scaleValue = (scaleAnimationValue * animation.getDuration().toSeconds() / scaleAnimationTime);
        animation.setByX(scaleValue);
        animation.setByY(scaleValue);
        animation.play();
    }

    private void mouseOutSlote(int index) {
        ScaleTransition animation = sloteAnimations.get(index);

        if (animation.statusProperty().getValue() == Status.RUNNING) {
            Duration duration = animation.getCurrentTime();
            animation.stop();
            animation.setDuration(duration);
        } else animation.setDuration(Duration.seconds(scaleAnimationTime));

        double scaleValue = -(scaleAnimationValue * animation.getDuration().toSeconds() / scaleAnimationTime);
        animation.setByX(scaleValue);
        animation.setByY(scaleValue);
        animation.play();
    }

    private void slotePressed(int index) {
        this.cardSlotePressed.informAll((Integer) index / 2);
    }

    @FXML
    private void vetoPowerPressed(MouseEvent event) {
        this.vetoPowerPressed.informAll(vetoIngormValue);
    }

    public void activateVetoUsage(Integer valueToInfrom) {
        Component.reveal(vetoPowerSurface);
        this.vetoIngormValue = valueToInfrom;
    }

    public void diactivateVetoUsage() {
        Component.hide(vetoPowerSurface);
    }

    public ActObserversAccess<Integer> getCardSlotePressed() {
        return this.cardSlotePressed;
    }

    public ActObserversAccess<Integer> getVetoPowerPressed() {
        return this.vetoPowerPressed;
    }
}
