package test_ui.Components;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.animation.Animation.Status;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;
import test_ui.App;
import test_ui.Components.Component.Component;
import test_ui.Components.Component.ParentUpdaters.VBoxParentUpdater;

/**
 * AbilityController handles the user interface logic for abilities, managing animations and interactions within a UI component.
 * This class extends {@link Component}, and it assumes the component is related to abilities, possibly in a game or similar application.
 * This class manages various animations related to the display of abilities and handles user interactions like mouse hover and click.
 */
public class AbilityController extends Component {
    
    /**
     * Functional interface for handling animation stop notifications.
     */
    @FunctionalInterface
    public interface AnimationFinishedNoification {
        /**
         * Called when an animation has stopped.
         */
        void animationStoped();
    }

    @FXML
    private AnchorPane circlePane;
    @FXML
    private AnchorPane iconPane;
    @FXML
    private AnchorPane usePane;

    @FXML
    private Circle circle;
    @FXML
    private Circle useCircle;
    @FXML
    private AnchorPane circleEndPoint;

    @FXML
    private Label countLabel;
    @FXML
    private Label titleLabel;

    @FXML
    private AnchorPane textEndPoint;

    private Integer value;
    private ActObservers<Integer> useButtonObservers;

    private double animationTime = 0.65;
    private TranslateTransition currentCircleAnimation;
    private TranslateTransition currentTextAnimation;

    private AnimationFinishedNoification currentCircleAnimationStop;
    private AnimationFinishedNoification currentTextAnimationStop = () -> {};

    /**
     * Constructs an AbilityController with a specified VBox to manage.
     * Initializes the controller, loads the necessary FXML, and sets up observers.
     *
     * @param vbox The VBox to be managed by this controller.
     */
    public AbilityController(VBox vbox) {
        super(new VBoxParentUpdater(vbox));
        super.initialize(App.class.getResource("fxml/ability.fxml"), vbox);
        useButtonObservers = new ActObservers<>();
    }

    /**
     * Initializes the controller components and sets up animations and their handlers.
     * This method is called automatically after the FXML fields are injected.
     */
    @FXML
    private void initialize() {
        currentCircleAnimation = new TranslateTransition();
        currentTextAnimation = new TranslateTransition();

        Component.hide(usePane);
        usePane.setOpacity(0);

        currentCircleAnimation.statusProperty().addListener(new ChangeListener<Animation.Status>() {
            @Override
            public void changed(ObservableValue<? extends Animation.Status> observable, Animation.Status oldValue, Animation.Status newValue) {
                if (newValue == Status.STOPPED) currentCircleAnimationStop.animationStoped();
            }
        });

        currentTextAnimation.statusProperty().addListener(new ChangeListener<Animation.Status>() {
            @Override
            public void changed(ObservableValue<? extends Animation.Status> observable, Animation.Status oldValue, Animation.Status newValue) {
                if (newValue == Status.STOPPED) currentTextAnimationStop.animationStoped();
            }
        });
    }

    /**
     * Configures the ability controller with the specified ability name, usage count, and value.
     *
     * @param name The name of the ability.
     * @param countOfUsage The number of times the ability can be used.
     * @param value The value associated with the ability.
     */
    public void setup(String name, int countOfUsage, Integer value) {
        this.countLabel.setText(Integer.toString(countOfUsage));
        this.titleLabel.setText(name);
        this.value = value;
    }

    /**
     * Updates the usage count displayed on the UI.
     *
     * @param value The new usage count.
     */
    public void setUsageCount(int value) {
        this.countLabel.setText(Integer.toString(value));
    }

    /**
     * Configures and starts the animation for moving a specified Node to the location of another Node.
     *
     * @param obj The node to animate.
     * @param target The target node the animated node moves towards.
     * @param animation The TranslateTransition to use for the animation.
     */
    private void setMoveAnimation(Node obj, Node target, TranslateTransition animation) {
        if (animation.getStatus() != Status.STOPPED) {
            Duration duration = animation.getCurrentTime();
            animation.stop();
            animation.setDuration(duration);
        } else animation.setDuration(Duration.seconds(animationTime));

        animation.setNode(obj);
        animation.setByX(target.getLayoutX() - (obj.getLayoutX() + obj.getTranslateX()));
    }

    /**
     * Creates and returns an AnimationTimer that gradually changes the opacity of a given Node to a specified final opacity over a given time.
     *
     * @param obj The node whose opacity will be changed.
     * @param time The duration over which to change the opacity.
     * @param finaleOpacity The final opacity value.
     * @return An instance of AnimationTimer that performs the opacity change.
     */
    private static AnimationTimer getSlowOpacityChanger(Node obj, double time, double finaleOpacity) {
        return new AnimationTimer() {
            long lastNow=-1;
            double changePerSecond = (1 / time) * ((obj.getOpacity() > finaleOpacity) ? -1 : 1);

            @Override
            public void handle(long now) {
                if (lastNow == -1) 
                    lastNow = now-1;
                
                obj.setOpacity(obj.getOpacity() + changePerSecond * ((now - lastNow) / 1_000_000_000.0));
                lastNow = now;
            }
        };
    }

    /**
     * Handles the mouse on component event by starting animations and opacity changes.
     *
     * @param event The MouseEvent triggered when the mouse enters the component.
     */
    @FXML
    void mouseOnComponent(MouseEvent event) {
        Component.turnOn(usePane);
        Component.turnOff(iconPane);

        setMoveAnimation(circlePane, circleEndPoint, currentCircleAnimation);
        setMoveAnimation(titleLabel, textEndPoint, currentTextAnimation);
        
        usePane.setVisible(true);
        AnimationTimer opacityChanger = getSlowOpacityChanger(usePane, currentCircleAnimation.getDuration().toSeconds(), 1);

        currentCircleAnimationStop = () -> {
            opacityChanger.stop();
            usePane.setOpacity(1);
        };

        currentCircleAnimation.play();
        currentTextAnimation.play();
        opacityChanger.start();
    }

    /**
     * Handles the mouse out component event by reversing animations and reducing opacity to zero.
     *
     * @param event The MouseEvent triggered when the mouse exits the component.
     */
    @FXML
    void mouseOutCopmonent(MouseEvent event) {
        Component.turnOff(usePane);
        Component.turnOn(iconPane);

        setMoveAnimation(circlePane, circlePane, currentCircleAnimation);
        setMoveAnimation(titleLabel, titleLabel, currentTextAnimation);

        AnimationTimer opacityChanger = getSlowOpacityChanger(usePane, currentCircleAnimation.getDuration().toSeconds(), 0);

        currentCircleAnimationStop = () -> {
            opacityChanger.stop();
            usePane.setOpacity(0);
            usePane.setVisible(false);
        };
            
        currentCircleAnimation.play();
        currentTextAnimation.play();
        opacityChanger.start();
    }

    /**
     * Handles the mouse click event on the use button, informing all observers about the current value.
     *
     * @param event The MouseEvent triggered by clicking the use button.
     */
    @FXML
    void usePressed(MouseEvent event) {
        useButtonObservers.informAll(value);
    }

    /**
     * Returns the observers of the use button actions.
     *
     * @return An accessor for the observers watching the use button.
     */
    public ActObserversAccess<Integer> getUseButtonObservers() {
        return this.useButtonObservers;
    }
}