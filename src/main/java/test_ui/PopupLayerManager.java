package test_ui;

import java.io.IOException;
import java.util.Queue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

public class PopupLayerManager {
    public static abstract class PopupComponent {
        private Parent component;

        protected void initialize(String fxmlName) {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlName));
            loader.setController(this);

            try {
                this.component = loader.load();
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        protected Parent initialize(String fxmlName, Pane surface) {
            initialize(fxmlName);

            component.getProperties().put("scale", Component.resize(component, surface.getPrefWidth(), surface.getPrefHeight()));
            surface.getChildren().add(component);

            return component;
        }

        public void activate() {
            Component.reveal(this.component);
        }

        public void finish() {
            Component.hide(this.component);
        }

        public Parent getComponent() {
            return this.component;
        }
    }

    private Queue<PopupComponent> componentsToActivate;
    private PopupComponent currentComponent;

    private Pane popupSurface;
    public PopupLayerManager(Pane popupSurface) {
        this.popupSurface = popupSurface;
    }

    public void askActivation(PopupComponent component) {
        if (currentComponent == null) {
            component.activate();
            currentComponent = component;
            Component.reveal(popupSurface);
        }
        else
            componentsToActivate.add(component);
    }

    public void finishCurent() {
        currentComponent.finish();

        currentComponent = componentsToActivate.poll();
        if (currentComponent != null) 
            currentComponent.activate();
        else
            Component.hide(popupSurface);
    }

    public void finishAll() {
        componentsToActivate.clear();
        currentComponent.finish();
        currentComponent = null;
        Component.hide(popupSurface);
    }
}

