package test_ui.Components;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;
import test_ui.App;
import test_ui.PopupLayerManager;

public class ShowDeath extends PopupLayerManager.PopupComponent {
    private ActObservers<Integer> exitButObservers;

    public ShowDeath(Pane surface) {
        super(surface);
        super.initialize(App.class.getResource("fxml/deathMessage.fxml"), surface);
        this.exitButObservers = new ActObservers<>();
    }

    @FXML
    void exitButtonPressed(MouseEvent event) {
        exitButObservers.informAll(null);
    }

    public ActObserversAccess<Integer> getExitButObservers() {
        return this.exitButObservers;
    }
}
