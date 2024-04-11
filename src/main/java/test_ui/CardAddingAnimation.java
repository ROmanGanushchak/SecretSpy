package test_ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.util.Pair;
import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;

public class CardAddingAnimation{
    private ActObservers<Integer> animationFinished;

    public CardAddingAnimation() {
        animationFinished = new ActObservers<>();
    }

    public void start(double duration, Pair<Double, Double> midPosition, Pair<Double, Double> endPosition, 
    Pair<Double, Double> midSize, Pair<Double, Double> endSize, Image texture, ImageView obj) {
        Timeline animation = new Timeline();
        KeyFrame fliping1 = new KeyFrame(Duration.seconds(duration / 3),
            event -> {
                if (texture != null)
                    obj.setImage(texture);
            },
            new KeyValue(obj.scaleXProperty(), 0),
            new KeyValue(obj.scaleYProperty(), midSize.getValue() / (2 * obj.getFitHeight())),
            new KeyValue(obj.translateXProperty(), (midPosition.getKey() - obj.getX()) / 2),
            new KeyValue(obj.translateYProperty(), (midPosition.getValue() - obj.getY()) / 2)
        );

        KeyFrame fliping2 = new KeyFrame(Duration.seconds(duration * 2 / 3),
            new KeyValue(obj.scaleXProperty(), midSize.getKey() / obj.getFitWidth()),
            new KeyValue(obj.scaleYProperty(), midSize.getValue() / obj.getFitHeight()),
            new KeyValue(obj.translateXProperty(), midPosition.getKey() - (obj.getX() + obj.getTranslateX())),
            new KeyValue(obj.translateYProperty(), midPosition.getValue() - (obj.getY() + obj.getTranslateY()))
        );
        
        double realEndXPos = endPosition.getKey() - (obj.getFitWidth() - endSize.getKey()) / 2;
        double realEndYPos = endPosition.getValue() - (obj.getFitHeight() - endSize.getValue()) / 2;
        KeyFrame goingToEnd = new KeyFrame(Duration.seconds(duration), 
            event -> animationFinished.informAll(null),
            new KeyValue(obj.scaleXProperty(), endSize.getKey() / obj.getFitWidth()),
            new KeyValue(obj.scaleYProperty(), endSize.getValue() / obj.getFitHeight()),
            new KeyValue(obj.translateXProperty(), realEndXPos - (obj.getX() + obj.getTranslateX())),
            new KeyValue(obj.translateYProperty(), realEndYPos - (obj.getY() + obj.getTranslateY()))
        );

        animation.getKeyFrames().addAll(fliping1, fliping2, goingToEnd);
        animation.play();
    }

    public ActObserversAccess<Integer> getFinishObservers() {
        return this.animationFinished;
    }
}
