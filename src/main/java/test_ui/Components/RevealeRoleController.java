package test_ui.Components;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;
import test_ui.App;
import test_ui.PopupLayerManager;

public class RevealeRoleController extends PopupLayerManager.PopupComponent {
    @FXML
    private AnchorPane hidePane;
    @FXML
    private ImageView roleImage;
    @FXML
    private AnchorPane roleImagePlane;

    private double initialOffSetY;
    private double startingPosition;
    private double minLayoutY;

    private ActObservers<Integer> exitButtonObservers;

    public RevealeRoleController(Pane surface) {
        super(surface);
        super.initialize(App.class.getResource("fxml/revealRole.fxml"), surface);
        exitButtonObservers = new ActObservers<>();
    }

    @FXML
    private void mouseMove(MouseEvent event) {
        this.hidePane.setLayoutY(Math.min(Math.max(event.getSceneY() / super.getScale().getY() + initialOffSetY, minLayoutY), startingPosition));
    }

    @FXML
    private void mousePress(MouseEvent event) {
        this.initialOffSetY = hidePane.getLayoutY() - event.getSceneY() / super.getScale().getY();
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

    public ActObserversAccess<Integer> getExitObservers() {
        return exitButtonObservers;
    }

    public void setup(Image roleImage) {
        this.roleImage.setImage(roleImage);
    }
}