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
import test_ui.Components.Component.Component;

public class VoteManeger extends PopupLayerManager.PopupComponent {
    private AnchorPane voteSurface;
    private double voteSurfaceAnimationTime = 1.5;

    private Parent voteComponent;
    private Parent voteResult;
    private VoteComponentController voteController;
    private VoteResultController voteResultController;
    
    private PopupLayerManager popupLayerManager;

    public VoteManeger(AnchorPane voteSurface, PopupLayerManager popupLayerManager) {
        super(voteSurface);
        super.setComponent(voteSurface);

        this.popupLayerManager = popupLayerManager;
        this.voteSurface = voteSurface;

        this.voteComponent = Component.initialize(getClass().getResource("fxml/voteComponent.fxml"), this.voteSurface);
        this.voteController = (VoteComponentController) this.voteComponent.getProperties().get("controller");

        this.voteResult = Component.initialize(getClass().getResource("fxml/voteResult.fxml"), this.voteSurface);
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
        this.voteController.setPresidentName(presidentName);
        this.voteController.setChancellorName(chancellorName);

        popupLayerManager.askActivation(this);    
    }

    @Override
    public void activate() {
        super.activate();
        TranslateTransition animation = new TranslateTransition(Duration.seconds(this.voteSurfaceAnimationTime), this.voteSurface);
        animation.setFromY(((AnchorPane) this.voteSurface.getParent()).getHeight());
        animation.setToY(0); 

        Component.reveal(this.voteComponent);
        Component.hide(this.voteResult);
        animation.setOnFinished(e -> {
            this.voteSurface.setTranslateX(0);
            this.voteSurface.setTranslateY(0);
        });
        animation.play(); 
    }

    public void end() {
        TranslateTransition animation = new TranslateTransition(Duration.seconds(this.voteSurfaceAnimationTime), this.voteSurface);
        animation.setFromY(0);
        animation.setToY(((AnchorPane) this.voteSurface.getParent()).getHeight()); 
        
        animation.setOnFinished(e -> {
            Component.hide(this.voteComponent);
            Component.hide(this.voteResult);
            this.voteSurface.setTranslateX(0);
            this.voteSurface.setTranslateY(0);
            popupLayerManager.finishCurent();
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
