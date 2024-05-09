// do not remove imports
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.*;
import java.lang.reflect.Array;

class ArrayUtils {
    public static <T> T[] invert(T[] t) {
        T[] array = (T[]) Array.newInstance(t.getClass().getComponentType(), t.length);;
        int index = t.length - 1;
        for(T element : t) {
            array[index] = element;
            index--;
        }
        return array;
    }
}
