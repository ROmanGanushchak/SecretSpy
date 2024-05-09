package test_ui.Components;

import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import model.Observers.ActObservers;
import model.Observers.ActObserversAccess;

/**
 * This class controls the voting result display. It is responsible for updating the user interface based on the results of the vote,
 * displaying names of voters who voted yes or no, and managing actions related to the continue button.
 */
public class VoteResultController {

    @FXML
    private Label textVoteResult;
    private String defaultYesVoteResult = "The election was successful, the new president is ";
    private String defaultNoVoteResult = "The election was unsuccessful";

    private ActObservers<Integer> continueButtonPressedObservers;

    @FXML
    private VBox yesNamesHolder;
    @FXML
    private VBox noNamesHolder;

    /**
     * Initializes the controller class. This method is called after the FXML file has been loaded.
     * It sets up necessary properties for elements such as text wrapping and alignment.
     */
    @FXML
    public void initialize() {
        this.continueButtonPressedObservers = new ActObservers<>();
        this.textVoteResult.setWrapText(true);
        this.textVoteResult.setAlignment(Pos.CENTER);
    }

    /**
     * Sets up the voting results display.
     * 
     * @param voteResult The result of the vote as a boolean; true indicates a successful election.
     * @param presidentName The name of the elected president, to be displayed if the election is successful.
     * @param yesVoteNames A list of names who voted 'yes' in the election.
     * @param noVoteNames A list of names who voted 'no' in the election.
     */
    public void setup(boolean voteResult, String presidentName, ArrayList<String> yesVoteNames, ArrayList<String> noVoteNames) {
        this.yesNamesHolder.getChildren().clear();
        this.noNamesHolder.getChildren().clear();

        for (int i=0; i < yesVoteNames.size(); i++) {
            Label name = new Label();
            name.setText(yesVoteNames.get(i));
            name.setMaxWidth(this.yesNamesHolder.getWidth());
            name.setAlignment(Pos.CENTER);
            this.yesNamesHolder.getChildren().add(name);
        }

        for (int i=0; i < noVoteNames.size(); i++) {
            Label name = new Label();
            name.setText(noVoteNames.get(i));
            name.setMaxWidth(this.noNamesHolder.getWidth());
            name.setAlignment(Pos.CENTER);
            this.noNamesHolder.getChildren().add(name);
        }

        if (voteResult) this.textVoteResult.setText(this.defaultYesVoteResult + presidentName);
        else this.textVoteResult.setText(this.defaultNoVoteResult);
    }

    /**
     * Handles the press of the continue button, informing all observers of the event.
     * 
     * @param event The mouse event associated with the button press.
     */
    @FXML
    void continueButtonPressed(MouseEvent event) {
        this.continueButtonPressedObservers.informAll(0);
    }

    /**
     * Provides access to observers of the continue button press event.
     * 
     * @return An interface to manage observers of the continue button event.
     */
    public ActObserversAccess<Integer> getContinueButtonObservers() {
        return this.continueButtonPressedObservers;
    }
}