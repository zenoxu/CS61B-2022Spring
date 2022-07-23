import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** HW #7, Two-sum problem.
 * @author
 */
public class Sum {

    /** Returns true iff A[i]+B[j] = M for some i and j. */
    public static boolean sumsTo(int[] A, int[] B, int m) {
        Arrays.sort(A, 0, A.length);
        Arrays.sort(B, 0, B.length);
        if (A.length == 0 || B.length == 0) {
            return false;
        } else {
            for (int i = 0; i < B.length; i++) {
                if (B[i] + A[A.length - 1] >= m) {
                    if (binarySearch(A, m - B[i])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean binarySearch(int[] array, int m) {
        if (array.length == 1) {
            return array[0] == m;
        }
        int index = array.length / 2;
        if (array[index] == m) {
            return true;
        } else if (array[index] > m) {
            int[] temp = new int[array.length / 2];
            System.arraycopy(array, 0, temp, 0, array.length / 2);
            return binarySearch(temp,m);
        } else {
            int[] temp = new int[array.length - array.length/2];
            System.arraycopy(array, array.length / 2, temp, 0, temp.length);
            return binarySearch(temp, m);
        }
    }



    static final int[] A = new int[]{1,3,5,7};
    static final int[] B = new int[]{2,4,9,8};
    static final boolean CORRECT = true;
    @Test
    public void BasicTest() {
        assertTrue(sumsTo(A, B, 12));
        assertFalse(sumsTo(A, B, 20));
    }
}
