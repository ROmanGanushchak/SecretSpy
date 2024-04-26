package test_ui;

import java.util.LinkedList;
import java.util.Queue;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import model.Observers.ActObservers;
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

        protected void activate() {
            super.reveal();
        }

        protected void finish() {
            super.hide();
        }
    }

    private Queue<PopupComponent> componentsToActivate;
    private PopupComponent currentComponent;
    private ActObservers<Boolean> callWhenNoPopup; 

    private Pane popupSurface;
    public PopupLayerManager(Pane popupSurface) {
        componentsToActivate = new LinkedList<>();
        callWhenNoPopup = new ActObservers<>();

        this.popupSurface = popupSurface;
        Component.hide(popupSurface);
    }

    public void askActivation(PopupComponent component) {
        if (currentComponent == null) {
            Component.reveal(popupSurface);
            component.activate();
            currentComponent = component;
        }
        else
            componentsToActivate.add(component);
    }

    public void finishCurent() {
        currentComponent.finish();

        currentComponent = componentsToActivate.poll();
        if (currentComponent != null) 
            currentComponent.activate();
        else {
            callWhenNoPopup.informAll(null);
            Component.hide(popupSurface);
            currentComponent = null;
        }
    }

    public void finishAll() {
        componentsToActivate.clear();
        currentComponent.finish();
        currentComponent = null;
        callWhenNoPopup.informAll(null);

        Component.hide(popupSurface);
    }

    public boolean isActive() {
        return currentComponent == null;
    }

    public void subscribeForCallWhenFree(ActObservers.MethodToCall<Boolean> methodToCall, int callCount) {
        if (currentComponent == null && callCount == 1) {
            methodToCall.execute(null);
        } else if (currentComponent == null) {
            methodToCall.execute(null);
            callWhenNoPopup.subscribe(methodToCall, callCount-1);
        } else {
            callWhenNoPopup.subscribe(methodToCall, callCount);
        }
    }
}

