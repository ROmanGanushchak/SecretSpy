package test_ui.Components.Component;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ParentUpdaters {
    public static class ParentUpdater<T> {
        private T base;

        public void hide() {}
        public void reveal() {}
    
        public void setBase(T base) {
            this.base = base;
        }
    
        public T getBase() {
            return this.base;
        }
    }
    
    public static class PaneParentUpdater extends ParentUpdater<Pane> {
        public PaneParentUpdater(Pane surface) {
            super.setBase(surface);
        }

        public void hide() {
            Component.hide(super.getBase());
        }
    
        public void reveal() {
            System.out.println("Pane revealed");
            Component.reveal(super.getBase());
        }
    }

    public static class VBoxParentUpdater extends ParentUpdater<VBox> {
        public VBoxParentUpdater(VBox vbox) {
            super.setBase(vbox);
        }
    }
}
