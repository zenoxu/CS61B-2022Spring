package image;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *  @author Ziyi Xu
 */

public class MatrixUtilsTest {
    /** FIXME
     */
    @Test
    public void accumulateVerticalTest(){
        double[][] input = new double[][]{{1000000,1000000,1000000,1000000},
                {1000000,75990,30003,1000000},{1000000,30002,103046,1000000},
                {1000000,29515,38273,1000000},{1000000,73403,35399,1000000},
                {1000000,1000000,1000000,1000000}};
        double[][] result = new double[][]{{1000000,1000000,1000000,1000000},
                {2000000,1075990,1030003,2000000},{2075990,1060005,1133049,2030003},
                {2060005,1089520,1098278,2133049},{2089520,1162923,1124919,2098278},
                {2162923,2124919,2124919,2124919}};
        //double[][] input1 = new double[][]{{10, 4, 5, 6}, {3, 10, 18, 6}, {8, 5, 19, 6}};
        //double[][] result1 = new double[][]{{10, 4, 5, 6}, {}}
        int i = 0;
        int j = 0;
        double[][] actural = MatrixUtils.accumulateVertical(input);
        while(i < input.length){
            j = 0;
            while(j < input[0].length){
                assertEquals("The element test", result[i][j], actural[i][j], 0.01);
                j = j + 1;
            }
            i = i + 1;
        }

    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(MatrixUtilsTest.class));
    }
}
