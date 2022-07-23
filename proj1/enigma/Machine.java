package enigma;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import static enigma.EnigmaException.error;


/** Class that represents a complete enigma machine.
 *  @author Ziyi Xu
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all theF
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        this._numRotors = numRotors;
        this._pawls = pawls;
        this._allRotors = new ArrayList<>();
        this._allRotors.addAll(allRotors);
        this._RotorsInSlots = new ArrayList<>();
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return this._numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return this._pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return this._RotorsInSlots.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    public Iterator<Rotor> getAlRotorsIterator() {
        return this._allRotors.iterator();
    }

    public Iterator<Rotor> getRotorsInSlotIterator() {
        return this._RotorsInSlots.iterator();
    }

    public ArrayList<Rotor> getRotorsInSlots() {
        return _RotorsInSlots;
    }

    /** Return true if there is a rotor named nameOfRotor in _allRotors.
     *  Return false if there isn't a rotor named
     *  @param nameOfRotor The name of rotor
     */
    public boolean containName(String nameOfRotor) {
        Iterator<Rotor> tempForContainName = this.getAlRotorsIterator();
        while (tempForContainName.hasNext()) {
            Rotor next = tempForContainName.next();
            if (Objects.equals(next.name(), nameOfRotor)) {
                return true;
            }
        }
        return false;
    }

    /** Return the rotor in _allRotors according to the name.
     *  Throw error if _allRotors doesn't contain nameOfRotor
     *  @param nameOfRotor The name of rotor
     */
    private Rotor getRotor(String nameOfRotor) {
        if (!this.containName(nameOfRotor)) {
            throw error(String.format("No rotor name"
                    + " %s in allRotors"), nameOfRotor);
        } else {
            Iterator<Rotor> tempForGetRotor = this.getAlRotorsIterator();
            while (tempForGetRotor.hasNext()) {
                Rotor next = tempForGetRotor.next();
                if (Objects.equals(next.name(), nameOfRotor)) {
                    return next;
                }
            }
        }
        throw error("Unexpected error when calling GetRotor in Machine");
    }

    /** Return all rotors notch in a String. */
    public String getAllSettings() {
        String result = "";
        for (int i = 0; i < this.getRotorsInSlots().size(); i++) {
            if (this.getRotor(i).rotates()) {
                result += this._alphabet.toChar(getRotor(i).setting());
            }
        }
        return result;
    }


    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (rotors.length > this._numRotors) {
            throw error("Too many rotors to insert");
        }
        this._RotorsInSlots = new ArrayList<>();
        for (int i = 0; i < rotors.length; i++) {
            if (!this.containName(rotors[i])) {
                throw error("%s is not in allRotors when implementing"
                        + " insertRotors in Machine", rotors[i]);
            } else {
                if (i == 0 && !getRotor(rotors[i]).reflecting()) {
                    throw new EnigmaException("The ROTORS[0] "
                            + "should be reflector");
                }
                this._RotorsInSlots.add(getRotor(rotors[i]));
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != this._numRotors - 1) {
            throw new EnigmaException("String "
                    + "length should equal to numRotors()-1");
        } else {
            Iterator<Rotor> tempForSetRotors = this.getRotorsInSlotIterator();
            tempForSetRotors.next();
            for (int i = 0; i < setting.length(); i++) {
                Rotor next = tempForSetRotors.next();
                next.set(setting.charAt(i));
            }
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % this._alphabet.size();
        if (r < 0) {
            r += this._alphabet.size();
        }
        return r;
    }

    /** Change the setting according to the ring.
     * Return the modified setting
     * @param ring The ring in input
     * @param setting The setting in input
     */
    String transition(String ring, String setting) {
        if (ring.length() != setting.length()) {
            throw new EnigmaException("Ring's length"
                    + " should equal to setting's length");
        }
        String result = "";
        for (int i = 0; i < ring.length(); i++) {
            int indexOfSetting = this._alphabet.toInt(setting.charAt(i));
            int indexOfRing = this._alphabet.toInt(ring.charAt(i));
            result += this._alphabet.toChar(wrap(indexOfSetting - indexOfRing));
        }
        return result;
    }

    /** Change the notch setting according to the ring.
     * @param ring The ring in the setting.*/
    void notchTransition(String ring) {
        Iterator<Rotor> notchIterator = this.getRotorsInSlotIterator();
        int j = 0;
        while (notchIterator.hasNext()) {
            Rotor next = notchIterator.next();
            if (!next.reflecting()) {
                next.setRing(_alphabet.toInt(ring.charAt(j)));
                j += 1;
            }
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return this._plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        this._plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    public void advanceRotors() {
        for (int i = 0; i < this._RotorsInSlots.size() - 1; i++) {
            if (this._RotorsInSlots.get(i).rotates()) {
                if (this._RotorsInSlots.get(i + 1).atNotch()) {
                    this._RotorsInSlots.get(i).advance();
                } else if (i >= 1
                        && this._RotorsInSlots.get(i - 1).rotates()
                        && this._RotorsInSlots.get(i).atNotch()) {
                    this._RotorsInSlots.get(i).advance();
                }
            }
        }
        this._RotorsInSlots.get(this._RotorsInSlots.size() - 1).advance();
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        for (int i = _RotorsInSlots.size() - 1; i >= 0; i--) {
            c = this._RotorsInSlots.get(i).convertForward(c);
        }
        for (int i = 1; i < _RotorsInSlots.size(); i++) {
            c = this._RotorsInSlots.get(i).convertBackward(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        char[] result = new char[msg.length()];
        for (int i = 0; i < msg.length(); i++) {
            if (!this._alphabet.contains(msg.charAt(i))) {
                throw new EnigmaException(String.format("%s in msg"
                        + " is not in alphabet", msg.charAt(i)));
            }
            result[i] = this._alphabet.
                    toChar(convert(this._alphabet.toInt(msg.charAt(i))));
        }
        return String.valueOf(result);
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors in machine. */
    private int _numRotors;

    /** Number of pawls in machine. */
    private int _pawls;

    /** Arraylist of all rotors. */
    private ArrayList<Rotor> _allRotors;

    /** Arraylist of all rotors in slots. */
    private ArrayList<Rotor> _RotorsInSlots;

    /** Plugboard of this machine. */
    private Permutation _plugboard;
}
