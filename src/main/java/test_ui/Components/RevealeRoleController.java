package test_ui.Components;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
import model.Observers.ActObservers;
import model.Observers.ActionObserver;
import model.Observers.ObserversAccess;

public class RevealeRoleController {
    @FXML
    private AnchorPane hidePane;
    @FXML
    private ImageView roleImage;
    @FXML
    private AnchorPane roleImagePlane;

    private Scale surfaceScale;

    private double initialOffSetY;
    private double startingPosition;
    private double minLayoutY;

    private ActObservers<Integer> exitButtonObservers;

    @FXML
    private void initialize() {
        exitButtonObservers = new ActObservers<>();
    }

    @FXML
    private void mouseMove(MouseEvent event) {
        this.hidePane.setLayoutY(Math.min(Math.max(event.getSceneY() / surfaceScale.getY() + initialOffSetY, minLayoutY), startingPosition));
    }

    @FXML
    private void mousePress(MouseEvent event) {
        this.initialOffSetY = hidePane.getLayoutY() - event.getSceneY() / surfaceScale.getY();
        startingPosition = hidePane.getLayoutY();
        minLayoutY = hidePane.getLayoutY() - hidePane.getHeight();
    }

    @FXML
    void mouseRelease(MouseEvent event) {
        this.hidePane.setLayoutY(startingPosition);
    }

    @FXML
    void exitMousePressed(MouseEvent event) {
        exitButtonObservers.informAll(null);
    }

    public ObserversAccess<ActionObserver<Integer>> getExitObservers() {
        return exitButtonObservers;
    }

    public void setup(Image roleImage, Scale surfaceScale) {
        this.roleImage.setImage(roleImage);
        this.surfaceScale = surfaceScale;
    }
}