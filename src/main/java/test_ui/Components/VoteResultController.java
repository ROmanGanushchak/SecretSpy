package test_ui.Components;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import model.Observers.ActObservers;
import model.Observers.ActionObserver;
import model.Observers.ObserversAccess;

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

    public void initialize() {
        this.continueButtonPressedObservers = new ActObservers<>();
        this.textVoteResult.setWrapText(true);
        this.textVoteResult.setAlignment(Pos.CENTER);
    }

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

    @FXML
    void continueButtonPressed(MouseEvent event) {
        System.out.println("Continue pressed");
        this.continueButtonPressedObservers.informAll(0);
    }

    public ObserversAccess<ActionObserver<Integer>> getContinueButtonObservers() {
        return this.continueButtonPressedObservers;
    }
}
