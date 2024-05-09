package test_ui.Components;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import test_ui.App;
import test_ui.ImageLoader;
import test_ui.Components.Component.Component;
import test_ui.Components.Component.ParentUpdaters.PaneParentUpdater;
import java.util.ArrayList;
import javafx.fxml.FXML;

/**
 * This class represents the end screen of a game, displaying the result and relevant player information.
 * It extends {@link Component}.
 */
public class GameFinish extends Component {
    @FXML
    private Label resultText;

    @FXML
    private ImageView roleWinnerImage;

    @FXML
    private Label spyNamesLabel;

    @FXML
    private Label shadowLeaderNameLabel;

    /**
     * Creates classes new instance
     * @param surface The pane on which this component will be displayed.
     */
    public GameFinish(Pane surface) {
        super(new PaneParentUpdater(surface));
        super.initialize(App.class.getResource("fxml/gameFinish.fxml"), surface);
    }

    /**
     * Sets up the game finish screen with the result and relevant player details, displays the names of key players.
     *
     * @param result            True if the liberals win, false if the spies win.
     * @param shadowLeaderName  The name of the shadow leader to be displayed.
     * @param spyNames          A list of names of the players who were spies.
     */
    public void setUp(boolean result, String shadowLeaderName, ArrayList<String> spyNames) {
        if (!result) {
            this.roleWinnerImage.setImage(ImageLoader.getInstance().getImage("spyRoleCard.png"));
            resultText.setText("Spys");
        } else { 
            this.roleWinnerImage.setImage(ImageLoader.getInstance().getImage("liberalRoleCard.png"));
            resultText.setText("Liberals");
        }
        
        shadowLeaderNameLabel.setText(shadowLeaderName);

        StringBuilder text = new StringBuilder();
        for (String spyName : spyNames) {
            text.append(spyName).append("\n");
        }
        spyNamesLabel.setText(text.toString());
    }
}
