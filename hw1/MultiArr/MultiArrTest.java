import static org.junit.Assert.*;
import org.junit.Test;

public class MultiArrTest {

    @Test
    public void testMaxValue() {
        //TODO: Your code here!
        int[][] testcase1 = {{1,3,4},{1},{5,6,7,8},{7,9}};
        assertEquals(MultiArr.maxValue(testcase1),9);
    }

    @Test
    public void testAllRowSums() {
        //TODO: Your code here!
        int[][] testcase = new int[][]{{1,3,4},{1},{5,6,7,8},{7,9}};
        int[] actual_result = new int[]{8,1,26,16};
        assertArrayEquals(MultiArr.allRowSums(testcase),actual_result);
    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(MultiArrTest.class));
    }
}
