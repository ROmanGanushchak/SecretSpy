package test_ui.Components.Component;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import test_ui.Components.Component.ParentUpdaters.ParentUpdater;

public class Component {
    private Parent component;
    private ParentUpdater<?> parentUpdater;
    private Scale scale;

    public Component(ParentUpdater<?> parentUpdater) {
        this.parentUpdater = parentUpdater;
    }

    protected Parent load(URL fxml) {
        FXMLLoader loader = new FXMLLoader(fxml);
        loader.setController(this);

        try {
            return loader.load();
        } catch(IOException e) {
            System.out.println("Misake while initializing component with controller setted");
            throw new RuntimeException(e);
        }
    }

    protected Parent initialize(URL fxml, double width, double height) {
        setComponent(this.load(fxml));
        this.scale = Component.resize(component, width, height);

        return component;
    }

    protected Parent initialize(URL fxml, Pane surface) {
        setComponent(this.load(fxml));
        this.scale = resize(component, surface.getPrefWidth(), surface.getPrefHeight());
        surface.getChildren().add(component);

        return component;
    }

    protected Parent initialize(URL fxml, VBox box) {
        setComponent(this.load(fxml));
        this.scale = resizeToFitIn(component, box);
        box.getChildren().add(component);

        return component;
    }

    protected void setComponent(Parent component) {
        this.component = component;
    }

    public Parent getComponent() {
        return this.component;
    }

    public Scale getScale() {
        return this.scale;
    }

    public void hide() {
        parentUpdater.hide();
        Component.hide(this.component);
    }

    public void reveal() {
        parentUpdater.reveal();
        Component.reveal(this.component);
    }

    public void turnOff() {
        Component.turnOff(this.component);
    }

    public void turnOn() {
        Component.turnOn(this.component);
    }

    public static Scale resize(Node component, double width, double height) {
        double scaleX = width / component.prefWidth(-1);
        double scaleY = height / component.prefHeight(-1);

        Scale scale = new Scale(scaleX, scaleY);
        component.getTransforms().add(scale);

        return scale;
    }

    public static void resize(Node component, double width, double height, Scale scale) {
        double scaleX = width / component.getBoundsInLocal().getWidth();
        double scaleY = height / component.getBoundsInLocal().getHeight();

        scale.setX(scale.getX() * scaleX);
        scale.setY(scale.getY() * scaleY);
    }

    public static Scale resizeToFitIn(Node obj, Node edges) {
        double adjustment = 1;

        if (obj.prefWidth(-1) > edges.prefWidth(-1))
            adjustment = edges.prefWidth(-1) / obj.prefWidth(-1);
           
        if (obj.prefHeight(-1) > edges.prefHeight(-1))
            adjustment = Math.min(edges.prefHeight(-1) / obj.prefHeight(-1), adjustment);
        
        if (adjustment == 1) return null;
        
        Scale scale = new Scale(adjustment, adjustment);
        obj.getTransforms().add(scale);

        return scale;
    }

    public static void hide(Node component) {
        component.setMouseTransparent(true);
        component.setManaged(false);
        component.setVisible(false);
        component.setDisable(true);
    }

    public static void reveal(Node component) {
        component.setMouseTransparent(false);
        component.setManaged(true);
        component.setVisible(true);
        component.setDisable(false);
    }

    public static void turnOff(Node component) {
        component.setMouseTransparent(true);
        component.setManaged(false);
        component.setDisable(true);
    }

    public static void turnOn(Node component) {
        component.setMouseTransparent(false);
        component.setManaged(true);
        component.setDisable(false);
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
}