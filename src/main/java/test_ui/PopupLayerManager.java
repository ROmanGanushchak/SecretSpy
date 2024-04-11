package test_ui;

import java.util.LinkedList;
import java.util.Queue;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import test_ui.Components.Component.Component;
import test_ui.Components.Component.ParentUpdaters.PaneParentUpdater;

public class PopupLayerManager {
    public static abstract class PopupComponent extends Component {
        public PopupComponent(Pane parent) {
            super(new PaneParentUpdater(parent));
        }

        @Override
        protected void setComponent(Parent component) {
            super.setComponent(component);
            super.hide();
        }

        public void activate() {
            super.reveal();
        }

        public void finish() {
            super.hide();
        }
    }

    private Queue<PopupComponent> componentsToActivate;
    private PopupComponent currentComponent;

    private Pane popupSurface;
    public PopupLayerManager(Pane popupSurface) {
        componentsToActivate = new LinkedList<>();
        this.popupSurface = popupSurface;
        Component.hide(popupSurface);
    }

    public void askActivation(PopupComponent component) {
        System.out.println("Activation asked");
        if (currentComponent == null) {
            System.out.println("new activation");
            Component.reveal(popupSurface);
            component.activate();
            currentComponent = component;
        }
        else
            componentsToActivate.add(component);
    }

    public void finishCurent() {
        System.out.println("Finished");
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

