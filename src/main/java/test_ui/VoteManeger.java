package test_ui;

import java.util.ArrayList;

import javafx.animation.TranslateTransition;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import model.Observers.ActObserversAccess;
import test_ui.Components.VoteComponentController;
import test_ui.Components.VoteResultController;
import test_ui.Components.Component.Component;

/** manages the vote surface, and shows the appearence animations */
public class VoteManeger extends PopupLayerManager.PopupComponent {
    private AnchorPane voteSurface;
    private double voteSurfaceAnimationTime = 1.5;

    private Parent voteComponent;
    private Parent voteResult;
    private VoteComponentController voteController;
    private VoteResultController voteResultController;
    
    private PopupLayerManager popupLayerManager;

    /**Constructor to create new instance of the class
     * @param voteSurface       the surface where the votting will appear, if no viting is active the surface is hided
    //  * @param popupLayerManager the manager of the popup layer
     */
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

        this.voteResultController.getContinueButtonObservers().subscribe((Integer num) -> end());

        AnchorPane.clearConstraints(voteSurface);
        Component.hide(this.voteSurface);
        Component.hide(voteComponent);
        Component.hide(voteResult);
    }

    /**Method to show the result of the voting
     * @param voteResult    the result of the voting
     * @param presidentName the name of the current president
     * @param yesVoteNames  all participators votes that were for the candidate
     * @param noVoteNames   all participators votes that were against the candidate
     */
    public void showResult(boolean voteResult, String presidentName, ArrayList<String> yesVoteNames, ArrayList<String> noVoteNames) {
        Component.hide(this.voteComponent);
        Component.reveal(this.voteResult);
        this.voteResultController.setup(voteResult, presidentName, yesVoteNames, noVoteNames);
    }

    /**Method to start the voting, in this stage ask player to vote
     * @param presidentName     name of the current president
     * @param chancellorName    name of the candidate
     */
    public void start(String presidentName, String chancellorName) {
        this.voteController.setPresidentName(presidentName);
        this.voteController.setChancellorName(chancellorName);

        popupLayerManager.askActivation(this);    
    }

    /**Overridef method that activates popup component and shows the appearance anuimation*/
    @Override
    public void activate() {
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
        super.activate();
    }

    /** Method to show the finish animation */
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

    /**sets the current president name
     * @param name president name
     */
    public void setPresidentName(String name) {
        this.voteController.setPresidentName(name);
    }
    
    /**sets the candidate chancellor name
     * @param name chancellor candidate name
     */
    public void setChancellorName(String name) {
        this.voteController.setChancellorName(name);
    }

    /** returns the voting result observer
     * @return the observer access
     */
    public ActObserversAccess<Boolean> getVotingResultObservers() {
        return this.voteController.getVoteResultObservers();
    }

    /**returns the observer of the voting end
     * @return the observer of the voting end
     */
    public ActObserversAccess<Integer> getEndObservers() {
        return this.voteResultController.getContinueButtonObservers();
    }
}
