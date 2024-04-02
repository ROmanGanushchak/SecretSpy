package test_ui;

import java.util.ArrayList;

import javafx.animation.TranslateTransition;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import model.Observers.ActionObserver;
import model.Observers.ObserversAccess;
import test_ui.Components.VoteComponentController;
import test_ui.Components.VoteResultController;

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

        
        
        this.voteResultController.getContinueButtonObservers().subscribe(
            new ActionObserver<>((Integer num) -> end()) );

        AnchorPane.clearConstraints(voteSurface);
        Component.hide(this.voteSurface);
        Component.hide(voteComponent);
        Component.hide(voteResult);
    }

    public void showResult(boolean voteResult, String presidentName, ArrayList<String> yesVoteNames, ArrayList<String> noVoteNames) {
        Component.hide(this.voteComponent);
        Component.reveal(this.voteResult);
        this.voteResultController.setup(voteResult, presidentName, yesVoteNames, noVoteNames);
    }

    public void start(String presidentName, String chancellorName) {
        TranslateTransition animation = new TranslateTransition(Duration.seconds(this.voteSurfaceAnimationTime), this.voteSurface);
        animation.setFromY(((AnchorPane) this.voteSurface.getParent()).getHeight());
        animation.setToY(0); 

        Component.reveal(this.voteSurface);
        Component.reveal(this.voteComponent);
        Component.hide(this.voteResult);
        animation.setOnFinished(e -> {
            this.voteSurface.setTranslateX(0);
            this.voteSurface.setTranslateY(0);
        });
        animation.play();     

        this.voteController.setPresidentName(presidentName);
        this.voteController.setChancellorName(chancellorName);
    }

    public void end() {
        this.voteComponent.setVisible(true);
        TranslateTransition animation = new TranslateTransition(Duration.seconds(this.voteSurfaceAnimationTime), this.voteSurface);
        animation.setFromY(0);
        animation.setToY(((AnchorPane) this.voteSurface.getParent()).getHeight()); 
        System.out.println(((AnchorPane) this.voteSurface.getParent()).getHeight());

        animation.setOnFinished(e -> {
            Component.hide(this.voteSurface);
            Component.hide(this.voteComponent);
            Component.hide(this.voteResult);
            this.voteSurface.setTranslateX(0);
            this.voteSurface.setTranslateY(0);
        });

        animation.play();  
    }

    public ObserversAccess<ActionObserver<Boolean>> getVotingResultObservers() {
        return this.voteController.getVoteResultObservers();
    }

    public void setPresidentName(String name) {
        this.voteController.setPresidentName(name);
    }
    
    public void setChancellorName(String name) {
        this.voteController.setChancellorName(name);
    }

    public ObserversAccess<ActionObserver<Integer>> getEndObservers() {
        return this.voteResultController.getContinueButtonObservers();
    }
}
