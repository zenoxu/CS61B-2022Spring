/** Solutions to the HW0 Java101 exercises.
 *  @author Ziyi Xu
 */
public class Solutions {

    /** Returns whether or not the input x is even.
     */
    public static boolean isEven(int x) {
        // TODO: Your code here. Replace the following return statement.
        if (x%2==0){
            return true;
        }else {
            return false;
        }
    }
    public static int max(int[] arr){
        int max;
        if(arr.length==0) {
            System.out.println("The array is empty");
            return 0;
        }
        else{
            max = arr[0];
            int i=1;
            while(i<arr.length){
                if(arr[i]>max){
                    max = arr[i];
                }
                i++;
            }
        }
        return max;
    }
    // TODO: Fill in the method signatures for the other exercises
    // Your methods should be static for this HW. DO NOT worry about what this means.
    // Note that "static" is not necessarily a default, it just happens to be what
    // we want for THIS homework. In the future, do not assume all methods should be
    // static.

}
