package test_ui;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class VoteResultController {

    @FXML
    private Label textVoteResult;
    private String defaultYesVoteResult = "The election was successful, the new president is ";
    private String defaultNoVoteResult = "The election was unsuccessful";

    @FXML
    private VBox yesNamesHolder;

    @FXML
    private VBox noNamesHolder;

    public void initialize() {
        this.textVoteResult.setWrapText(true);
        this.textVoteResult.setAlignment(Pos.CENTER);
    }

    public void setup(boolean voteResult, String presidentName, String yesVoteNames[], String noVoteNames[]) {
        for (int i=0; i < yesVoteNames.length; i++) {
            Label name = new Label();
            name.setText(yesVoteNames[i]);
            name.setMaxWidth(this.yesNamesHolder.getWidth());
            name.setAlignment(Pos.CENTER);
            this.yesNamesHolder.getChildren().add(name);
        }

        for (int i=0; i < noVoteNames.length; i++) {
            Label name = new Label();
            name.setText(noVoteNames[i]);
            name.setMaxWidth(this.noNamesHolder.getWidth());
            name.setAlignment(Pos.CENTER);
            this.noNamesHolder.getChildren().add(name);
        }

        if (voteResult) this.textVoteResult.setText(this.defaultYesVoteResult + presidentName);
        else this.textVoteResult.setText(this.defaultNoVoteResult);
    }
}
