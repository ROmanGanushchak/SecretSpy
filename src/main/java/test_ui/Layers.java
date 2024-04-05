package test_ui;

import java.util.ArrayList;

import javafx.scene.layout.AnchorPane;

public class Layers {
    private ArrayList<AnchorPane> layers;
    private AnchorPane currentLayer;

    public Layers(){
        this.layers = new ArrayList<>();
    }

    public Layers(AnchorPane... layers) {
        System.out.println("Layer changed");
        this.layers = new ArrayList<>(layers.length);
        for (AnchorPane layer : layers) {
            Component.turnOff(layer);
            this.layers.add(layer);
        }
    }

    public void changeLayer(AnchorPane newLayer) {
        if (this.currentLayer != null) {
            this.currentLayer.setMouseTransparent(true);
            this.currentLayer.setManaged(false);
        }
        
        Component.reveal(newLayer);
        this.currentLayer = newLayer;
    }

    public void changeLayerWithHide(AnchorPane newLayer) {
        if (this.currentLayer != null) 
            Component.hide(this.currentLayer);
        
        Component.turnOn(newLayer);
        this.currentLayer = newLayer;
    }

    public void changeLayer(int index) {
        this.changeLayer(layers.get(index));
    }

    public void changeLayerWithHide(int index) {
        this.changeLayerWithHide(layers.get(index));
    }

    public void addLayer(AnchorPane layer) {
        this.layers.add(layer);
    }

    public void removeLayer(AnchorPane layer) {
        this.layers.remove(layer);
    }

    public AnchorPane getCurrentLayer() {
        return this.currentLayer;
    }
}
