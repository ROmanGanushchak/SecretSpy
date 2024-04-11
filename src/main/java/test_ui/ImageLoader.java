package test_ui;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

public class ImageLoader {
    private static ImageLoader instance;

    private Map<String, Image> images;

    private ImageLoader() {
        images = new HashMap<>();
    }

    public static ImageLoader getInstance() {
        if (instance == null)
            return new ImageLoader();
        return instance;
    }

    public Image getImage(String imageName) {
        Image image = images.get(imageName);
        if (image == null) {
            image = new Image(App.class.getResourceAsStream("images/" + imageName));
            images.put(imageName, image);
        }

        return image;
    }
}
