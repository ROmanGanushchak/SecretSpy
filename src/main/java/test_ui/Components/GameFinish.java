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

public class GameFinish extends Component {
    @FXML
    private Label resultText;
    @FXML
    private ImageView roleWinnerImage;

    @FXML
    private Label spyNamesLabel;
    @FXML
    private Label shadowLeaderNameLabel;

    public GameFinish(Pane surface) {
        super(new PaneParentUpdater(surface));
        super.initialize(App.class.getResource("fxml/gameFinish.fxml"), surface);
    }

    public void setUp(boolean result, String shadowLeaderName, ArrayList<String> spyNames) {
        if (!result) {
            this.roleWinnerImage.setImage(ImageLoader.getInstance().getImage("spyRoleCard.png"));
            resultText.setText("Spys");
        } else { 
            this.roleWinnerImage.setImage(ImageLoader.getInstance().getImage("liberalRoleCard.png"));
            resultText.setText("Liberals");
        }
        
        shadowLeaderNameLabel.setText(shadowLeaderName);

        String text = "";
        for (String spyName : spyNames) 
            text += spyName + "\n";
        spyNamesLabel.setText(text);
    }
}
