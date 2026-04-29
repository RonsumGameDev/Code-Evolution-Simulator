package sample_codebase;

/**
 * Sample utility class for array operations.
 * Demonstrates fitness scoring based on code structure.
 */
public class ArrayUtils {
    
    public static int[] sort(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        return array;
    }
    
    public static int sum(int[] array) {
        int total = 0;
        for (int num : array) {
            total += num;
        }
        return total;
    }
    
    public static double average(int[] array) {
        return (double) sum(array) / array.length;
    }
}
