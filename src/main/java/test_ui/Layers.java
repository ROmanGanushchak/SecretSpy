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
        System.out.println("Layers initiaize");
        this.layers = new ArrayList<>(layers.length);
        System.out.println("Array created");
        for (AnchorPane layer : layers) {
            layer.setMouseTransparent(true);
            layer.setManaged(false);
            this.layers.add(layer);
        }
        System.out.println("Init ended");
    }

    private static void activate(AnchorPane layer) {
        layer.setVisible(true);
        layer.setManaged(true);
        layer.setMouseTransparent(false);
    }

    public void changeLayer(AnchorPane newLayer) {
        if (this.currentLayer != null) {
            this.currentLayer.setMouseTransparent(true);
            this.currentLayer.setManaged(false);
        }
        
        activate(newLayer);
        this.currentLayer = newLayer;
    }

    public void changeLayerWithHide(AnchorPane newLayer) {
        if (this.currentLayer != null) {
            Component.hide(this.currentLayer);
        }
        
        activate(newLayer);
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
