package test_ui.Components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;

/**
 * Class VoteComponentController controlles the voting, allows player to vote and shows basic information.
 */
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

    /** initializes the obj, is called by javafx*/
    @FXML
    public void initialize() {
        this.voteResultObserves = new ActObservers<>();
    }

    /** Method informs all followers about the yes vote pressed, is called by javafx */
    @FXML
    void yesVotePressed(MouseEvent event) {
        this.voteResultObserves.informAll(true);
    }

    /** Method informs all followers about the no vote pressed, is called by javafx */
    @FXML
    void noVotePressed(MouseEvent event) {
        this.voteResultObserves.informAll(false);
    }

    /** Settes the president name
     * @param name president name
     */
    public void setPresidentName(String name) {
        this.presidentName.setText(name);
    }

    /** Settes chancellor name
     * @param name New name
     */
    public void setChancellorName(String name) {
        this.chanclerName.setText(name);
    }

    /**
     * @return observer {@link ActObserversAccess} for the vote result
     */
    public ActObserversAccess<Boolean> getVoteResultObservers() {
        return this.voteResultObserves;
    }
}