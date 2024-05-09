package test_ui;

import java.util.LinkedList;
import java.util.Queue;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import model.Observers.ActObservers;
import test_ui.Components.Component.Component;
import test_ui.Components.Component.ParentUpdaters.PaneParentUpdater;

/**
 * class Designed to controll the popups on some specific surface, calls first when gets a request activate a plane and then when gets finish requst calls finish method of currect popup and then activate next one. Method activate and finish can be overrided.
 * @author Roman Hanushchak
 * @version 1.17
 */
public class PopupLayerManager {
    /** All popup classes that have to extend this class  */
    public static abstract class PopupComponent extends Component {
        public PopupComponent(Pane parent) {
            super(new PaneParentUpdater(parent));
        }

        @Override
        /** overrides Components setComponent to by default hide the serface, if override the method then recomend to call super method */
        protected void setComponent(Parent component) {
            super.setComponent(component);
            super.hide();
        }

        /** method that is called to activate the poppup, can be overrided but recomended to call super method activate */
        protected void activate() {
            super.reveal();
        }

        /** method that is finish to activate the poppup, can be overrided but recomended to call super method activate */
        protected void finish() {
            super.hide();
        }
    }
    /** queue of the components to activate */
    private Queue<PopupComponent> componentsToActivate;
    /** currectly acticated component */
    private PopupComponent currentComponent;
    /** observerst that will be activated when last popup from queue will be finished */
    private ActObservers<Boolean> callWhenNoPopup; 
    /** surface on whitch the popup components will be shown, if there is no popup at that moment then the surface will be hided */
    private Pane popupSurface;

    public PopupLayerManager(Pane popupSurface) {
        componentsToActivate = new LinkedList<>();
        callWhenNoPopup = new ActObservers<>();

        this.popupSurface = popupSurface;
        Component.hide(popupSurface);
    }

    /** method adds a component to the queue, if queue is empty then imidietly activates it @@
     * @param component component that will be activeted
    */
    public void askActivation(PopupComponent component) {
        if (currentComponent == null) {
            Component.reveal(popupSurface);
            component.activate();
            currentComponent = component;
        }
        else
            componentsToActivate.add(component);
    }

    /** finishes the currectly activated popup and imidietly activates next */
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

    /** finish currect popup and clears the queue */
    public void finishAll() {
        componentsToActivate.clear();
        currentComponent.finish();
        currentComponent = null;
        callWhenNoPopup.informAll(null);

        Component.hide(popupSurface);
    }

    /** @return true if a component is active othewise false */
    public boolean isActive() {
        return currentComponent == null;
    }

    /** subscription for an callWhenNoPopup observer */
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

