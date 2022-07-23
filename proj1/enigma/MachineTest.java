package enigma;

import java.util.HashMap;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Machine class.
 *  @author Ziyi Xu
 */
public class MachineTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTS ***** */

    private static final Alphabet AZ = new Alphabet(TestUtils.UPPER_STRING);

    private static final HashMap<String, Rotor> ROTORS = new HashMap<>();

    static {
        HashMap<String, String> nav = TestUtils.NAVALA;
        ROTORS.put("B", new Reflector("B", new Permutation(nav.get("B"), AZ)));
        ROTORS.put("Beta",
                new FixedRotor("Beta",
                        new Permutation(nav.get("Beta"), AZ)));
        ROTORS.put("III",
                new MovingRotor("III",
                        new Permutation(nav.get("III"), AZ), "V"));
        ROTORS.put("IV",
                new MovingRotor("IV", new Permutation(nav.get("IV"), AZ),
                        "J"));
        ROTORS.put("I",
                new MovingRotor("I", new Permutation(nav.get("I"), AZ),
                        "Q"));
    }

    private static final String[] ROTORS1 = { "B", "Beta", "III", "IV", "I" };
    private static final String SETTING1 = "AXLE";

    private Machine mach1() {
        Machine mach = new Machine(AZ, 5, 3, ROTORS.values());
        mach.insertRotors(ROTORS1);
        mach.setRotors(SETTING1);
        return mach;
    }

    private static final HashMap<String, Rotor>
            ROTORSFORADVANCE = new HashMap<>();
    static {
        HashMap<String, String> nav = TestUtils.NAVALA;
        ROTORSFORADVANCE.put("B", new
                Reflector("B", new Permutation(nav.get("B"), AZ)));
        ROTORSFORADVANCE.put("III",
                new MovingRotor("III",
                        new Permutation(nav.get("III"), AZ), "M"));
        ROTORSFORADVANCE.put("IV",
                new MovingRotor("IV", new Permutation(nav.get("IV"), AZ),
                        "B"));
        ROTORSFORADVANCE.put("I",
                new MovingRotor("I", new Permutation(nav.get("I"), AZ),
                        "A"));
    }
    private static final String[] ROTORSFORADVANCE1 = {"B", "III", "IV", "I" };
    private static final String SETTING2 = "MAA";

    private Machine machForAdvance() {
        Machine mach = new Machine(AZ, 4, 3, ROTORSFORADVANCE.values());
        mach.insertRotors(ROTORSFORADVANCE1);
        mach.setRotors(SETTING2);
        return mach;
    }

    @Test
    public void testInsertRotors() {
        Machine mach = new Machine(AZ, 5, 3, ROTORS.values());
        mach.insertRotors(ROTORS1);
        assertEquals(5, mach.numRotors());
        assertEquals(3, mach.numPawls());
        assertEquals(AZ, mach.alphabet());
        assertEquals(ROTORS.get("B"), mach.getRotor(0));
        assertEquals(ROTORS.get("Beta"), mach.getRotor(1));
        assertEquals(ROTORS.get("III"), mach.getRotor(2));
        assertEquals(ROTORS.get("IV"), mach.getRotor(3));
        assertEquals(ROTORS.get("I"), mach.getRotor(4));
    }

    @Test
    public void setRotors() {
        Machine mach = mach1();
        Rotor temp;
        for (int i = 1; i < 5; i++) {
            temp = mach.getRotor(i);
            assertEquals(String.format("Wrong Setting for rotor %s", i),
                    mach.alphabet().toInt(
                            SETTING1.charAt(i - 1)), temp.setting());
        }
    }

    @Test
    public void testGetAllSettings() {
        Machine mach = machForAdvance();
        assertEquals(mach.getAllSettings(), "MAA");
    }

    @Test
    public void testAdvance() {
        Machine mach = machForAdvance();
        mach.advanceRotors();
        assertEquals("Wrong advance in 1st rotate",
                "MBB", mach.getAllSettings());
        mach.advanceRotors();
        assertEquals("Wrong advance in 2nd rotate",
                "NCC", mach.getAllSettings());
        mach.advanceRotors();
        assertEquals("Wrong advance in 3rd rotate",
                "NCD", mach.getAllSettings());
    }

    @Test
    public void testConvertChar() {
        Machine mach = mach1();
        mach.setPlugboard(new Permutation("(YF) (HZ)", AZ));
        assertEquals(25, mach.convert(24));
    }

    @Test
    public void testConvertMsg() {
        Machine mach = mach1();
        mach.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", AZ));
        assertEquals("QVPQSOKOILPUBKJZPISFXDW",
                mach.convert("FROMHISSHOULDERHIAWATHA"));
    }
}
