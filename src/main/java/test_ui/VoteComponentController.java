package test_ui;

import Game.GameControllerVisualService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

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
    private GameControllerVisualService gameControllerProxy;

    public void setPresidentName(String name) {
        this.presidentName.setText(name);
    }

    public void setChanclerName(String name) {
        this.chanclerName.setText(name);
    }

    @FXML
    void yesVotePressed(MouseEvent event) {
        System.out.println("Yes was pressed");
        gameControllerProxy.yesVote();
    }

    @FXML
    void noVotePressed(MouseEvent event) {
        gameControllerProxy.noVote();
    }  

    public void setGameContrlProxy(GameControllerVisualService gameControllerProxy) {
        this.gameControllerProxy = gameControllerProxy;
    }
}