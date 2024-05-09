package model.Cards;

import java.util.Random;
import java.util.ArrayList;

/** Class provides different implementations of the shuffle method */
public class ArrayShaffle {
    /** the random obj */
    public static Random rand;
    static {
        rand = new Random();
    }

    /**
     * shuffles the arrayList
     * @param <T> tne type of the obj element
     * @param array the array that will be shuffled
     * @param seed the random seed
     */
    public static <T> void shuffle(ArrayList<T> array, int seed) {
        if (array.size() == 0 || array.size() == 1) return;

        T temp;
        if (seed == 0) seed = Math.abs(rand.nextInt())+1;

        int indexToSwap;
        for (int i=0; i < array.size(); i++) {
            indexToSwap = rand.nextInt(seed + i) % array.size();
            temp = array.get(indexToSwap);
            array.set(indexToSwap, array.get(i));
            array.set(i, temp);
        }
    }

    /**
     * shuffles the arrayList
     * @param <T> tne type of the obj element
     * @param array the array that will be shuffled
     * @param seed the random seed
     */
    public static <T> void shuffle(T array[], int seed) {
        if (array.length == 0 || array.length == 1) return;

        T temp;
        if (seed == 0) seed = Math.abs(rand.nextInt())+1;

        int indexToSwap;
        for (int i=0; i < array.length; i++) {
            indexToSwap = rand.nextInt(seed + i) % array.length;
            temp = array[indexToSwap];
            array[indexToSwap] = array[i];
            array[i] = temp;
        }
    }
}
