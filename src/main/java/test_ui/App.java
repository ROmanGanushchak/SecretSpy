package test_ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import Game.GameController;

public class App extends Application {

    private static Scene scene;
    private GameController gameController;

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println(App.class.getResource(""));
        
        this.gameController = new GameController();

        System.out.println("Before set");

        stage.setScene(this.gameController.getScene());
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}