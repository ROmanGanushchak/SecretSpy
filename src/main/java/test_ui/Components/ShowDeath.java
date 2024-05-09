package test_ui.Components;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;
import test_ui.App;
import test_ui.PopupLayerManager;

/**
 * The ShowDeath class extends PopupComponent and displays the death message for the player
 */
public class ShowDeath extends PopupLayerManager.PopupComponent {
    private ActObservers<Integer> exitButObservers;

    /** Constractor, creates a new instance of the class
     * @param surface the basic surface where component will be placed
     */
    public ShowDeath(Pane surface) {
        super(surface);
        super.initialize(App.class.getResource("fxml/deathMessage.fxml"), surface);
        this.exitButObservers = new ActObservers<>();
    }

    /**The method is called when the exit button is pressed and informs all observers about the exit request
     * @param event
     */
    @FXML
    void exitButtonPressed(MouseEvent event) {
        exitButObservers.informAll(null);
    }

    /** The methods returns the observe access of the exit button
     * @return the observe access of the exit button
     */
    public ActObserversAccess<Integer> getExitButObservers() {
        return this.exitButObservers;
    }
}
