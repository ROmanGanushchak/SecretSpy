package Cards;

import java.util.Random;
import java.util.ArrayList;

public class ArrayShaffle {
    public static <T> void shuffle(ArrayList<T> array, int seed) {
        if (array.size() == 0 || array.size() == 1) return;

        T temp;
        Random rand = new Random();
        if (seed == 0) seed = rand.nextInt(array.size());

        int indexToSwap;
        for (int i=0; i < array.size(); i++) {
            indexToSwap = rand.nextInt(seed + i) % array.size();
            temp = array.get(indexToSwap);
            array.set(indexToSwap, array.get(i));
            array.set(i, temp);
        }
    }

    public static <T> void shuffle(T array[], int seed) {
        if (array.length == 0 || array.length == 1) return;

        T temp;
        Random rand = new Random();
        if (seed == 0) seed = rand.nextInt(array.length);

        int indexToSwap;
        for (int i=0; i < array.length; i++) {
            indexToSwap = rand.nextInt(seed + i) % array.length;
            temp = array[indexToSwap];
            array[indexToSwap] = array[i];
            array[i] = temp;
        }
    }
}
