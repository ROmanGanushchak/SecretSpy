package test_ui.Components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;

public class VoteComponentController {
    @FXML
    private ImageView backgroundImage;

    @FXML
    private ImageView yesVoteImage;
    @FXML
    private ImageView noVoteImage;

    @FXML
    private Label presidentName;
    @FXML
    private Label chanclerName;

    private ActObservers<Boolean> voteResultObserves;

    @FXML
    public void initialize() {
        this.voteResultObserves = new ActObservers<>();
    }

    public void setPresidentName(String name) {
        this.presidentName.setText(name);
    }

    public void setChancellorName(String name) {
        this.chanclerName.setText(name);
    }

    @FXML
    void yesVotePressed(MouseEvent event) {
        this.voteResultObserves.informAll(true);
    }

    @FXML
    void noVotePressed(MouseEvent event) {
        this.voteResultObserves.informAll(false);
    }

    public ActObserversAccess<Boolean> getVoteResultObservers() {
        return this.voteResultObserves;
    }
}