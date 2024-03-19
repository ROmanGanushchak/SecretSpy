package test_ui;

import Game.GameControllerVisualService;
import javafx.animation.TranslateTransition;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class VoteManeger {
    private AnchorPane voteSurface;
    private double voteSurfaceAnimationTime = 1.5;

    private Parent voteComponent;
    private Parent voteResult;
    private VoteComponentController voteController;
    private VoteResultController voteResultController;

    public VoteManeger(AnchorPane voteSurface) {
        this.voteSurface = voteSurface;
        this.voteComponent = Component.initialize(getClass().getResource("voteComponent.fxml"), this.voteSurface);
        this.voteController = (VoteComponentController) this.voteComponent.getProperties().get("controller");

        System.out.println("before result load");
        this.voteResult = Component.initialize(getClass().getResource("voteResult.fxml"), this.voteSurface);
        this.voteResultController = (VoteResultController) this.voteResult.getProperties().get("controller");
        this.voteResultController.initialize();
        
        Component.hide(this.voteSurface);
        Component.hide(voteComponent);
        Component.hide(voteResult);
    }

    public void setGameContrlProxy(GameControllerVisualService gameControllerProxy) {
        this.voteController.setGameContrlProxy(gameControllerProxy);
    }

    public void showResult(boolean voteResult, String presidentName, String yesVoteNames[], String noVoteNames[]) {
        Component.hide(this.voteComponent);
        Component.reveal(this.voteResult);
        this.voteResultController.setup(voteResult, presidentName, yesVoteNames, noVoteNames);
    }

    public void start() {
        TranslateTransition animation = new TranslateTransition(Duration.seconds(this.voteSurfaceAnimationTime), this.voteComponent);
        animation.setFromY(((AnchorPane) this.voteSurface.getParent()).getHeight());
        animation.setToY(0); 
        Component.reveal(this.voteSurface);
        Component.reveal(this.voteComponent);
        animation.play();     
    }

    public void end() {
        TranslateTransition animation = new TranslateTransition(Duration.seconds(this.voteSurfaceAnimationTime), this.voteComponent);
        animation.setFromY(0);
        animation.setToY(((AnchorPane) this.voteSurface.getParent()).getHeight()); 

        animation.setOnFinished(e -> {
            Component.hide(this.voteSurface);
            Component.hide(this.voteComponent);
            Component.hide(this.voteResult);
            this.voteSurface.setTranslateX(0);
            this.voteSurface.setTranslateY(0);
        });

        animation.play();  
    }
}
