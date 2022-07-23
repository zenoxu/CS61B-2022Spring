package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/**
 * The suite of all JUnit tests for the Permutation class. For the purposes of
 * this lab (in order to test) this is an abstract class, but in proj1, it will
 * be a concrete class. If you want to copy your tests for proj1, you can make
 * this class concrete by removing the 4 abstract keywords and implementing the
 * 3 abstract methods.
 *
 *  @author
 */
public abstract class PermutationTest {

    /**
     * For this lab, you must use this to get a new Permutation,
     * the equivalent to:
     * new Permutation(cycles, alphabet)
     * @return a Permutation with cycles as its cycles and alphabet as
     * its alphabet
     * @see Permutation for description of the Permutation conctructor
     */
    abstract Permutation getNewPermutation(String cycles, Alphabet alphabet);

    /**
     * For this lab, you must use this to get a new Alphabet,
     * the equivalent to:
     * new Alphabet(chars)
     * @return an Alphabet with chars as its characters
     * @see Alphabet for description of the Alphabet constructor
     */
    abstract Alphabet getNewAlphabet(String chars);

    /**
     * For this lab, you must use this to get a new Alphabet,
     * the equivalent to:
     * new Alphabet()
     * @return a default Alphabet with characters ABCD...Z
     * @see Alphabet for description of the Alphabet constructor
     */
    abstract Alphabet getNewAlphabet();

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /** Check that PERM has an ALPHABET whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha,
                           Permutation perm, Alphabet alpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.toInt(c), ei = alpha.toInt(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */
    @Test
    public void checkSize() {
        Alphabet alpha = getNewAlphabet();
        Permutation perm = getNewPermutation("", alpha);
        assertEquals("UPPER_STRING size should equal to 26",perm.size(), 26);
        Alphabet alpha1 = getNewAlphabet("12345");
        Permutation perm1 = getNewPermutation("", alpha1);
        assertEquals("12345's size should equal to 5",perm1.size(), 5);
        Alphabet alpha2 = getNewAlphabet("");
        Permutation perm2 = getNewPermutation("", alpha2);
        assertEquals("EMPTY STRING's size should equal to 0",perm2.size(), 0);
    }

    @Test
    public void checkPermute() {
        Alphabet alpha = getNewAlphabet();
        Permutation perm = getNewPermutation("", alpha);
        checkPerm("CheckPermute When cycles is empty", UPPER_STRING, UPPER_STRING, perm, alpha);
        Permutation perm1 = getNewPermutation(NAVALZ.get("I"), alpha);
        checkPerm("CheckPermute When cycles == NAVALZ (I)",
                UPPER_STRING, NAVALZ_MAP.get("I"), perm1, alpha);
    }

    @Test
    public void checkIdTransform() {
        Alphabet alpha = getNewAlphabet();
        Permutation perm = getNewPermutation("", alpha);
        checkPerm("identity", UPPER_STRING, UPPER_STRING, perm, alpha);
    }
    @Test
    public void checkAlphabet () {
        Alphabet alpha = getNewAlphabet();
        Permutation perm = getNewPermutation("", alpha);
        assertEquals("Default upperstring alhapbet", alpha, perm.alphabet());
        Alphabet alpha1 = getNewAlphabet("12345");
        Permutation perm1 = getNewPermutation("", alpha1);
        assertEquals("when alphabet == 12345", alpha1, perm1.alphabet());
    }
    @Test
    public void checkInvert() {
        Alphabet alpha = getNewAlphabet();
        Permutation perm = getNewPermutation("", alpha);
        assertEquals("Invert B should equal to B when Identity" +
                ", check invert(char) specifically", perm.invert('B'), 'B');
        assertEquals("Invert B should equal to B when Identity" +
                ", check invert(int) specifically", perm.invert(3) , 3);
        Permutation perm_Navalb1 = getNewPermutation(NAVALB.get("I"), alpha);
        assertEquals("Invert D should equal to Z when cycles = Navalb1" +
                ", check invert(char) specifically", perm_Navalb1.invert('D') , 'Z');
        assertEquals("Invert D should equal to Z when cycles = Navalb1" +
                ", check invert(int) specifically", 25, perm_Navalb1.invert(3));
    }
    @Test
    public void checkDerangment() {
        Alphabet alpha = getNewAlphabet();
        Permutation perm = getNewPermutation("", alpha);
        assertFalse("Identity all map to itself", perm.derangement());
        Permutation perm_NavalbGamma = getNewPermutation(NAVALB.get("Gamma"), alpha);
        assertTrue("No value in Navalb.Gamma map to itself ", perm_NavalbGamma.derangement());
        Permutation perm_Navalb3 = getNewPermutation(NAVALB.get("III"), alpha);
        assertFalse("M in Navalb3 map to itself ", perm_Navalb3.derangement());
    }
    @Test
    public void checkSizeInAlphabet() {
        Alphabet alpha = getNewAlphabet();
        assertEquals("Default alphabetsize should be 26", alpha.size(), 26);
        Alphabet alpha1 = getNewAlphabet("12345");
        assertEquals("12345's size should equal to 5", alpha1.size(), 5);
    }
    @Test
    public void checkContainsInAlphabet() {
        Alphabet alpha = getNewAlphabet();
        assertTrue("A is contained in UPPER_STRING", alpha.contains('A'));
        Alphabet alpha1 = getNewAlphabet("12345");
        assertTrue("1 is contained in 12345", alpha1.contains('1'));
    }
    @Test
    public void checkToChar() {
        Alphabet alpha = getNewAlphabet();
        assertEquals(alpha.toChar(1),'B');
    }
    // FIXME: Add tests here that pass on a correct Permutation and fail on buggy Permutations.
}
