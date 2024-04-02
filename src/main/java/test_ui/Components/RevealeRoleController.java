package test_ui.Components;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;

public class RevealeRoleController {
    @FXML
    private AnchorPane hidePane;

    @FXML
    private ImageView roleImage;

    @FXML
    private AnchorPane roleImagePlane;

    private Scale surfaceScale;
    private Scale screenScale;

    private double initialOffSetY;
    private double startingPosition;
    private double minLayoutY;

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

    public void setup(Image roleImage, Scale surfaceScale, Scale screenScale) {
        this.roleImage.setImage(roleImage);

        double scaleX = roleImagePlane.getPrefWidth() / roleImage.getWidth() * surfaceScale.getX();
        double scaleY = roleImagePlane.getPrefHeight() / roleImage.getHeight();
        this.roleImage.getTransforms().add(new Scale(scaleX, scaleY));

        this.surfaceScale = surfaceScale;
        this.screenScale = screenScale;
    }
}