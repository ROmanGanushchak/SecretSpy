package test_ui.Components.Component;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/** classes that uodate the basic plane for component */
public class ParentUpdaters {
    /** basic apdater, only stores the base */
    public static class ParentUpdater<T> {
        private T base;

        /** Method to inform about the component being hided */
        public void hide() {}
        /** Method to inform about the component being revealed */
        public void reveal() {}
        
        /** Method to set the base holder */
        public void setBase(T base) {
            this.base = base;
        }
    
        /** @return the base */
        public T getBase() {
            return this.base;
        }
    }
    
    /** pane apdater, stores the base, hides and reveals the surface with the component, only one component being activa in one moment */
    public static class PaneParentUpdater extends ParentUpdater<Pane> {
        /** Creates new instance of  the class. Set the base 
         * @param surface the base of the component
         */
        public PaneParentUpdater(Pane surface) {
            super.setBase(surface);
        }

        /** Method hides the base surface alongside the obj */
        @Override
        public void hide() {
            Component.hide(super.getBase());
        }
    
        /** Method hides the base surface alongside the obj */
        @Override
        public void reveal() {
            Component.reveal(super.getBase());
        }
    }

    /** vbox apdater, stores the base */
    public static class VBoxParentUpdater extends ParentUpdater<VBox> {
        /** Creates new instance of  the class. Set the base 
         * @param vbox the base holder of the component
         */
        public VBoxParentUpdater(VBox vbox) {
            super.setBase(vbox);
        }
    }
}
