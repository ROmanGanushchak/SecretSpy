package test_ui;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;

public class Component {
    public static Scale resize(Parent component, double width, double height) {
        double scaleX = width / component.prefWidth(-1);
        double scaleY = height / component.prefHeight(-1);

        Scale scale = new Scale(scaleX, scaleY);
        component.getTransforms().add(scale);

        return scale;
    }

    public static void resize(Parent component, double width, double height, Scale scale) {
        double scaleX = width / component.getBoundsInLocal().getWidth();
        double scaleY = height / component.getBoundsInLocal().getHeight();

        scale.setX(scale.getX() * scaleX);
        scale.setY(scale.getY() * scaleY);
    }

    public static Parent initialize(URL fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(fxml);
            Parent component = loader.load();
            component.getProperties().put("controller", loader.getController());

            return component;
        } catch (IOException e) {
            System.out.println("Error while loading component");
            e.printStackTrace();
            return null;
        }
    }

    public static Parent initialize(URL fxml, AnchorPane surface) {
        try {
            FXMLLoader loader = new FXMLLoader(fxml);
            Parent component = loader.load();

            component.getProperties().put("controller", loader.getController());
            component.getProperties().put("scale", resize(component, surface.getPrefWidth(), surface.getPrefHeight()));

            surface.getChildren().add(component);
            return component;
        } catch (IOException e) {
            System.out.println("Error while loading component");
            e.printStackTrace();
            return null;
        }
    }

    public static Scale resizeToFitIn(Node obj, Node edges) {
        System.out.println("fiting");

        double adjustment = 1;

        System.out.println(obj.prefWidth(-1) + " " + edges.prefWidth(-1));

        if (obj.prefWidth(-1) > edges.prefWidth(-1))
            adjustment = edges.prefWidth(-1) / obj.prefWidth(-1);
           
        if (obj.prefHeight(-1) > edges.prefHeight(-1))
            adjustment = Math.min(edges.prefHeight(-1) / obj.prefHeight(-1), adjustment);
        
        if (adjustment == 1) return null;
        
        Scale scale = new Scale(adjustment, adjustment);
        obj.getTransforms().add(scale);

        return scale;
    }

    public static Parent initialize(URL fxml, VBox box) {
        try {
            FXMLLoader loader = new FXMLLoader(fxml);
            Parent component = loader.load();

            component.getProperties().put("controller", loader.getController());
            resizeToFitIn(component, box);

            box.getChildren().add(component);
            return component;
        } catch (IOException e) {
            System.out.println("Error while loading component");
            e.printStackTrace();
            return null;
        }
    }

    public static Parent initialize(URL fxml, double width, double height) {
        try {
            FXMLLoader loader = new FXMLLoader(fxml);
            Parent component = loader.load();
            resize(component, width, height);
            component.getProperties().put("controller", loader.getController());

            return component;
        } catch (IOException e) {
            System.out.println("Error while loading component");
            e.printStackTrace();
            return null;
        }
    }

    // public static Parent initialize(URL fxml, double width, double height) {
    //     Parent component = initialize(fxml);
    //     resize(component, width, height);
    //     return component;
    // }

    public static Parent load(URL fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(fxml);
            Parent component = loader.load();

            return component;
        } catch (IOException e) {
            System.out.println("Error while loading component");
            e.printStackTrace();
            return null;
        }
    }

    public static void hide(AnchorPane surface) {
        surface.setMouseTransparent(true);
        surface.setManaged(false);
        surface.setVisible(false);
    }

    public static void reveal(AnchorPane surface) {
        surface.setMouseTransparent(false);
        surface.setManaged(true);
        surface.setVisible(true);
    }

    public static void hide(Node component) {
        component.setMouseTransparent(true);
        component.setManaged(false);
        component.setVisible(false);
    }

    public static void reveal(Node component) {
        component.setMouseTransparent(false);
        component.setManaged(true);
        component.setVisible(true);
    }

    public static void turnOff(Node component) {
        component.setMouseTransparent(true);
        component.setManaged(false);
    }

    public static void turnOn(Node component) {
        component.setMouseTransparent(false);
        component.setManaged(true);
    }
}
