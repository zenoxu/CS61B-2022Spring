import com.google.common.base.Strings;


/** Multidimensional array
 *  @author Zoe Plaxco
 */

public class MultiArr {

    /**
    {{"hello","you","world"} ,{"how","are","you"}} prints:
    Rows: 2
    Columns: 3

    {{1,3,4},{1},{5,6,7,8},{7,9}} prints:
    Rows: 4
    Columns: 4
    */
    public static void printRowAndCol(int[][] arr) {
        //TODO: Your code here!
        System.out.println("Rows:"+arr.length);
        System.out.println("Columns:"+ arr[0].length);
    }

    /**
    @param arr: 2d array
    @return maximal value present anywhere in the 2d array
    */
    public static int maxValue(int[][] arr) {
        //TODO: Your code here!
        int max = arr[0][0];
        int i = 0, j = 0;
        while(i< arr.length){
            while(j<arr[i].length){
                if(arr[i][j]>max){
                    max = arr[i][j];
                }
                j = j + 1;
            }
            i = i + 1;
            j = 0;
        }
        return max;
    }

    /**Return an array where each element is the sum of the
    corresponding row of the 2d array*/
    public static int[] allRowSums(int[][] arr) {
        //TODO: Your code here!!
        int[] result = new int[arr.length];
        int Sum = 0, i = 0, j = 0;
        while(i < arr.length){
            while(j<arr[i].length){
                Sum = Sum +arr[i][j];
                j = j + 1;
            }
            result[i] = Sum;
            Sum = 0;
            i = i + 1;
            j = 0;
        }
        return result;
    }

}
