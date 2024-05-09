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

/**
 * Designed to simplify the work with components, stores the Parent obj, updates the basic component, provides extra functionality
 */
public class Component {
    /** fxml loaded component */
    private Parent component;
    /** obj that will update the basic obj */
    private ParentUpdater<?> parentUpdater;
    /** for easy resizing */
    private Scale scale;

    /**
     * 
     * @param parentUpdater the updater of the basic element, allows to update the surface with the component changes like hide 
     */
    public Component(ParentUpdater<?> parentUpdater) {
        this.parentUpdater = parentUpdater;
    }

    protected Parent load(URL fxml) {
        FXMLLoader loader = new FXMLLoader(fxml);
        loader.setController(this);

        try {
            return loader.load();
        } catch(IOException e) {
            System.out.println("Mistake while initializing component with controller setted");
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

    /** setes a component, can be overrided */
    protected void setComponent(Parent component) {
        this.component = component;
    }

    /**
     * @return the fxml loaded Parent obj */
    public Parent getComponent() {
        return this.component;
    }

    public Scale getScale() {
        return this.scale;
    }

    /** disables and hides the obj, uses parentUpdater to synchronize the basic plane */
    public void hide() {
        parentUpdater.hide();
        Component.hide(this.component);
    }

    /** activates and reveals the obj, uses parentUpdater to synchronize the basic plane */
    public void reveal() {
        parentUpdater.reveal();
        Component.reveal(this.component);
    }

    /** simular to hide, but remains the visibility of the obj */
    public void turnOff() {
        Component.turnOff(this.component);
    }

    /** simular to reveal, but doesnt change the visibility of the obj */
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

    /**
     * if obj doesnt fill in the edges then resizes it, maintains proportions
     * @param obj the obj that will be resized
     * @param edges edges
     * @return scale that was applied to the obj
     */
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