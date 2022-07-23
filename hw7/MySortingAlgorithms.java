import net.sf.saxon.functions.Minimax;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Note that every sorting algorithm takes in an argument k. The sorting 
 * algorithm should sort the array from index 0 to k. This argument could
 * be useful for some of your sorts.
 *
 * Class containing all the sorting algorithms from 61B to date.
 *
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 *
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 *
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 *
 * All implementations except Counting Sort adopted from Algorithms,
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }
    }

    /** Insertion sorts the provided data. */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
            for (int last = 0; last < k; last++) {
                findPlace(array, last);
            }
        }

        private void findPlace(int[] array, int index) {
            while (index > 0 && array[index - 1] > array[index]) {
                swap(array, index, index - 1);
                index -= 1;
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
            for (int i = 0; i < k; i++) {
                swap(array, findMin(array, i, k), i);
            }
        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /** Your mergesort implementation. An iterative merge
      * method is easier to write than a recursive merge method.
      * Note: I'm only talking about the merge operation here,
      * not the entire algorithm, which is easier to do recursively.
      */
    public static class MergeSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
            if (array.length > 1) {
                int[] firstHalve = new int[(k + 1) / 2];
                int[] secondHalve = new int[k - firstHalve.length];
                System.arraycopy(array, 0, firstHalve,0, firstHalve.length);
                System.arraycopy(array, firstHalve.length, secondHalve, 0, secondHalve.length);
                sort(firstHalve, firstHalve.length);
                sort(secondHalve, secondHalve.length);
                System.arraycopy(merge(firstHalve,secondHalve), 0, array, 0, k);
            }
        }

        private int[] merge(int[] array1, int[] array2) {
            int[] result = new int[array1.length + array2.length];
            int i = 0; int j = 0; int k = 0;
            while (i < array1.length && j < array2.length) {
                if (array1[i] < array2[j]) {
                    result[k] = array1[i];
                    i++;
                } else {
                    result[k] = array2[j];
                    j++;
                }
                k++;
            }
            if (i == array1.length) {
                System.arraycopy(array2, j, result, k, result.length - k);
            } else {
                System.arraycopy(array1, i, result, k, result.length - k);
            }
            return result;
        }

        @Override
        public String toString() {
            return "Merge Sort";
        }
    }

    /**
     * Your Counting Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class CountingSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME: to be implemented
            int[] arrayToSort = new int[k];
            System.arraycopy(array, 0, arrayToSort, 0, k);

            int[] countingArray;
            countingArray = createCountingArray(arrayToSort);

            int[] indexArray;
            indexArray = createIndexArray(countingArray);

            int[] arrayAfterSort = new int[k];

            for (int j : arrayToSort) {
                int indexToInsert = indexArray[j];
                arrayAfterSort[indexToInsert] = j;
                indexArray[j] += 1;
            }

            System.arraycopy(arrayAfterSort, 0, array, 0, k);
        }

        private int[] createCountingArray(int[] array) {
            int[] result = new int[findMax(array) + 1];
            for (int j: array) {
                result[j] += 1;
            }
            return result;
        }

        private int[] createIndexArray(int[] countingArray) {
            int[] result = new int[countingArray.length];
            for (int i = 1; i < result.length; i++) {
                result[i] = result[i - 1] + countingArray[i];
            }
            return result;
        }

        @Override
        public String toString() {
            return "Counting Sort";
        }
    }

    /** Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
            int[] arrayToSort = new int[k];
            System.arraycopy(array, 0, arrayToSort, 0, k);
            heapify(arrayToSort);
            for (int i = 0; i < k; i++) {
                int temp;
                temp = removeMin(arrayToSort);
                array[i] = temp;
            }
        }

        private void heapify(int[] array) {
            int index = array.length / 2;
            while (index >= 0) {
                swapDown(array, index);
                index --;
            }
        }

        private void swapDown(int[] array, int index) {
            while (index * 2 + 1 < array.length) {
                int MinChildIndex;
                if (index * 2 + 2 >= array.length
                || array[index * 2 + 2] > array[index * 2 + 1]) {
                    MinChildIndex = index * 2 + 1;
                } else{
                    MinChildIndex = index * 2 + 2;
                }
                swap(array, index, MinChildIndex);
                index = MinChildIndex;
            }
        }


        private int removeMin(int[] array) {
            int result = array[0];
            swap(array, 0, array.length);
            if (array.length > 1) {
                int[] arrayAfterRemove = new int[array.length - 1];
                System.arraycopy(array, 0, arrayAfterRemove,0, array.length - 1);
                array = arrayAfterRemove;
            }
            return result;
        }

        @Override
        public String toString() {
            return "Heap Sort";
        }
    }

    /** Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Quicksort";
        }
    }

    /* For radix sorts, treat the integers as strings of x-bit numbers.  For
     * example, if you take x to be 2, then the least significant digit of
     * 25 (= 11001 in binary) would be 1 (01), the next least would be 2 (10)
     * and the third least would be 1.  The rest would be 0.  You can even take
     * x to be 1 and sort one bit at a time.  It might be interesting to see
     * how the times compare for various values of x. */

    /**
     * LSD Sort implementation.
     */
    public static class LSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
            int[] arrayToSort = new int[k];
            System.arraycopy(a, 0, arrayToSort, 0, k);
            int[] temp = new int[k];
            System.arraycopy(a, 0, temp, 0, k);
            int[] arrayAfterSort = new int[k];
            System.arraycopy(a, 0, arrayAfterSort, 0, k);
            int[] indexArray = new int[10];


            for (int i = 0; i < findLoopTime(arrayToSort); i++) {
                indexArray = createIndexArray(temp);
                int[] copyAfterSort = new int[arrayAfterSort.length];
                System.arraycopy(arrayAfterSort,0, copyAfterSort, 0, copyAfterSort.length);
                for (int j = 0; j < arrayToSort.length; j++) {
                    int indexToInsert = indexArray[temp[j] % 10];
                    arrayAfterSort[indexToInsert] = copyAfterSort[j];
                    indexArray[temp[j] % 10] += 1;
                }

                temp = arrayAfterSort;
                for(int j = 0; j < i + 1; j++) {
                    temp = allDivide10(temp);
                }
            }

            System.arraycopy(arrayAfterSort, 0, a, 0, k);
        }

        private int findLoopTime(int[] array) {
            int loopTimes = 0;
            int max = findMax(array);
            while(max != 0) {
                loopTimes++;
                max = max / 10;
            }
            return loopTimes;
        }

        private int[] allDivide10(int[] array) {
            int[] result = new int[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = array[i] / 10;
            }
            return result;
        }

        private int[] createIndexArray(int[] array) {
            int[] result = new int[10];
            int[] count = new int[10];

            for (int i = 0; i < array.length; i++) {
                count[array[i] % 10] += 1;
            }

            for (int i = 1; i < 10; i++) {
                result[i] = result[i - 1] + count[i - 1];
            }
            return result;
        }
        @Override
        public String toString() {
            return "LSD Sort";
        }
    }

    /**
     * MSD Sort implementation.
     */
    public static class MSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "MSD Sort";
        }
    }

    /** Exchange A[I] and A[J]. */
    private static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    private static int findMin(int[] array, int start, int end) {
        int minSoFar = start;
        for (int i = start + 1; i < end; i++) {
            if (array[minSoFar] > array[i]) {
                minSoFar = i;
            }
        }
        return minSoFar;
    }

    private static int findMax(int[] array) {
        int maxSoFar = array[0];
        for (int j : array) {
            if (maxSoFar < j) {
                maxSoFar = j;
            }
        }
        return maxSoFar;
    }
}
