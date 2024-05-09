package test_ui;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;

/** Class that loads the image, all images will be loaded at most once, singleton */
public class ImageLoader {
    private static ImageLoader instance;
    private Map<String, Image> images;

    /** Constructor that creates the instance of ImageLoader */
    private ImageLoader() {
        images = new HashMap<>();
    }

    /**returns the instance of the class ImageLoader
     * @return the instance of the class ImageLoader
     */
    public static ImageLoader getInstance() {
        if (instance == null)
            return new ImageLoader();
        return instance;
    }

    /** loads the image or takes it from map of loaded images and returns it
     * @param imageName the url of the image
     * @return the loaded image
     */
    public Image getImage(String imageName) {
        Image image = images.get(imageName);
        if (image == null) {
            image = new Image(App.class.getResourceAsStream("images/" + imageName));
            images.put(imageName, image);
        }

        return image;
    }
}
