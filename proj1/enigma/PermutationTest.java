package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Ziyi Xu
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha,
                           Permutation permutation, Alphabet alphabet) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, permutation.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                    e, permutation.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                    c, permutation.invert(e));
            int ci = alphabet.toInt(c), ei = alphabet.toInt(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                    ei, permutation.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                    ci, permutation.invert(ei));
        }
    }

    /* ***** TESTS ***** */
    @Test
    public void checkSize() {
        Alphabet alphabet = new Alphabet();
        Permutation permutation = new Permutation("", alphabet);
        assertEquals("UPPER_STRING size should"
                + " equal to 26", permutation.size(), 26);
        Alphabet alpha1 = new Alphabet("12345");
        Permutation perm1 = new Permutation("", alpha1);
        assertEquals("12345's size should equal to 5", perm1.size(), 5);
        Alphabet alpha2 = new Alphabet("");
        Permutation perm2 = new Permutation("", alpha2);
        assertEquals("EMPTY STRING's size should equal to 0", perm2.size(), 0);
    }

    @Test
    public void checkPermute() {
        Alphabet alphabet = new Alphabet();
        Permutation permutation = new Permutation("", alphabet);
        checkPerm("CheckPermute When cycles is empty",
                UPPER_STRING, UPPER_STRING, permutation, alphabet);
        Permutation perm1 = new Permutation(NAVALZ.get("I"), alphabet);
        checkPerm("CheckPermute When cycles == NAVALZ (I)",
                UPPER_STRING, NAVALZ_MAP.get("I"), perm1, alphabet);
    }

    @Test
    public void checkIdTransform() {
        Alphabet alphabet = new Alphabet();
        Permutation permutation = new Permutation("", alphabet);
        checkPerm("identity", UPPER_STRING,
                UPPER_STRING, permutation, alphabet);
    }
    @Test
    public void checkAlphabet() {
        Alphabet alphabet = new Alphabet();
        Permutation permutation = new Permutation("", alphabet);
        assertEquals("Default upperstring alhapbet",
                alphabet, permutation.alphabet());
        Alphabet alpha1 = new Alphabet("12345");
        Permutation perm1 = new Permutation("", alpha1);
        assertEquals("when alphabet == 12345",
                alpha1, perm1.alphabet());
    }
    @Test
    public void checkInvert() {
        Alphabet alphabet = new Alphabet();
        Permutation permutation = new Permutation("", alphabet);
        assertEquals("Invert B should equal to B when Identity"
                + ", check invert(char) specifically",
                permutation.invert('B'), 'B');
        assertEquals("Invert B should equal to B when Identity"
                + ", check invert(int) specifically",
                permutation.invert(3), 3);
        Permutation permNavalb1 = new Permutation(NAVALB.get("I"), alphabet);
        assertEquals("Invert D should equal to Z when cycles = Navalb1"
                + ", check invert(char) specifically",
                permNavalb1.invert('D'), 'Z');
        assertEquals("Invert D should equal to Z when cycles = Navalb1"
                + ", check invert(int) specifically",
                25, permNavalb1.invert(3));
    }
    @Test
    public void checkDerangment() {
        Alphabet alphabet = new Alphabet();
        Permutation permutation = new Permutation("", alphabet);
        assertFalse("Identity all map to itself", permutation.derangement());
        Permutation permNavalbGamma = new
                Permutation(NAVALB.get("Gamma"), alphabet);
        assertTrue("No value in Navalb.Gamma map to itself ",
                permNavalbGamma.derangement());
        Permutation permNavalb3 = new Permutation(NAVALB.get("III"), alphabet);
        assertFalse("M in Navalb3 map to itself ", permNavalb3.derangement());
    }
    @Test
    public void checkSizeInAlphabet() {
        Alphabet alphabet = new Alphabet();
        assertEquals("Default alphabetsize should be 26", alphabet.size(), 26);
        Alphabet alpha1 = new Alphabet("12345");
        assertEquals("12345's size should equal to 5", alpha1.size(), 5);
    }
    @Test
    public void checkContainsInAlphabet() {
        Alphabet alphabet = new Alphabet();
        assertTrue("A is contained in UPPER_STRING", alphabet.contains('A'));
        Alphabet alpha1 = new Alphabet("12345");
        assertTrue("1 is contained in 12345", alpha1.contains('1'));
    }
    @Test
    public void checkToChar() {
        Alphabet alphabet = new Alphabet();
        assertEquals(alphabet.toChar(1), 'B');
    }

}
