import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

/**
 * Test of a BST-based String Set.
 * @author Ziyi Xu
 */
public class BSTStringSetTest  {
    // FIXME: Add your own tests for your BST StringSet
    String[] words = new String[] {"Cat", "Boo", "Cob", "Art", "Bye", "Zoo"};
    BSTStringSet Empty = new BSTStringSet();

    @Test
    public void StringSetTest() {
        // FIXME: Delete this function and add your own tests
        for (int i = 0; i < words.length; i++) {
            Empty.put(words[i]);
        }
        assertEquals("Cat", Empty.get_root().getValue());
        assertEquals("Boo", Empty.get_root().getLeft().getValue());
        assertTrue(Empty.contains("Bye"));
        assertFalse(Empty.contains("City"));

    }
}
