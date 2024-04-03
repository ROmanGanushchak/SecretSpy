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
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import model.Observers.ActObservers;
import model.Observers.ActionObserver;
import model.Observers.ObserversAccess;
import test_ui.Component;

public class AbilityController {
    @FunctionalInterface
    public interface AnimationFinishedNoification {
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

    @FXML
    private void initialize() {
        currentCircleAnimation = new TranslateTransition();
        currentTextAnimation = new TranslateTransition();
        useButtonObservers = new ActObservers<>();

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

    public void setup(String name, int countOfUsage, Integer value) {
        this.countLabel.setText(Integer.toString(countOfUsage));
        this.titleLabel.setText(name);
        this.value = value;
    }

    public void setUsageCount(int value) {
        this.countLabel.setText(Integer.toString(value));
    }

    private void setMoveAnimation(Node obj, Node target, TranslateTransition animation) {
        if (animation.getStatus() != Status.STOPPED) {
            Duration duration = animation.getCurrentTime();
            animation.stop();
            animation.setDuration(duration);
        } else animation.setDuration(Duration.seconds(animationTime));

        animation.setNode(obj);
        animation.setByX(target.getLayoutX() - (obj.getLayoutX() + obj.getTranslateX()));
    }

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

    @FXML
    void usePressed(MouseEvent event) {
        System.out.println("Use pressed");
        useButtonObservers.informAll(value);
    }

    public ObserversAccess<ActionObserver<Integer>> getUseButtonObservers() {
        return this.useButtonObservers;
    }
}
