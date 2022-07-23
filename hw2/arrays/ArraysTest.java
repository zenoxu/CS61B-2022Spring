package arrays;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *  @author Ziyi Xu
 */

public class ArraysTest {
    /** FIXME
     */
    @Test
    public void catenateTest(){
        int[] A = new int[]{1, 2, 3};
        int[] B = new int[]{4, 5};
        int[] empty = new int[]{};
        int[] result = new int[]{1, 2, 3, 4, 5};
        assertArrayEquals("catenate{1,2,3}with{4,5}", result, Arrays.catenate(A, B));
        assertArrayEquals("catenate{1,2,3} with empty array",Arrays.catenate(A, empty), A);
        assertArrayEquals("catenate{4,5} with empty array(empty in the front)",Arrays.catenate(empty,B), B);
    }

    @Test
    public void removeTest(){
        int[] A = new int[]{1, 2, 3};
        int[] empty = new int[]{};
        int[] result = new int[]{1};
        int[] result1 = new int[]{1, 2};
        assertArrayEquals("remove 2 item start from 1 in {1,2,3}", result, Arrays.remove(A,1,2));
        assertArrayEquals("len = 0",Arrays.remove(A, 2, 0 ), A);
        assertArrayEquals("Remove the last element in {1,2,3}",Arrays.remove(A,2,1),result1);
        assertArrayEquals("Out of bound {1,2,3} start = 1, len = 3",Arrays.remove(A,1,3),null);

    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}
