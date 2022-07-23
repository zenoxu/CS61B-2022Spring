package lists;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *
 *  @author Ziyi Xu
 */

public class ListsTest {

    @Test
    public void basicRunsTest() {
        IntList input = IntList.list(1, 2, 3, 1, 2);
        IntList run1 = IntList.list(1, 2, 3);
        IntList run2 = IntList.list(1, 2);
        IntListList result = IntListList.list(run1, run2);
        IntList input1 = IntList.list();
        IntList result1 = IntList.list();
        //FIXME: Add some assertion to make this a real test.
        assertEquals("naturalrun in empty list",Lists.naturalRuns(input1),result1);
        assertEquals("natural run in normal list", result, Lists.naturalRuns(input));
        /* re-declare those variables to check*/
        input = IntList.list(1, 2, 3, 1, 2);
        run1 = IntList.list(1, 2, 3);
        run2 = IntList.list(1, 2);
        result = IntListList.list(run1, run2);
        input1 = IntList.list();
        result1 = IntList.list();
        assertEquals("naturalrunRecursively in empty list",Lists.naturalRunsRecursive(input1),result1);
        assertEquals("naturalrunRecursively in normal list", result, Lists.naturalRunsRecursive(input));
    }

    //FIXME: Add more tests!

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}
