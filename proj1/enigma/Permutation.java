package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Ziyi Xu
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
        _isDeragement = true;
        boolean inCycles = false;
        for (int i = 0; i < this._alphabet.size(); i++) {
            for (int j = 0; j < this._cycles.length(); j++) {
                if (this._alphabet.toChar(i) == this._cycles.charAt(j)) {
                    inCycles = true;
                    if (this._cycles.charAt(j - 1) == '('
                            && this._cycles.charAt(j + 1) == ')') {
                        this._isDeragement = false;
                    }
                    break;
                }
            }
            if (!inCycles) {
                addCycle(String.valueOf(this._alphabet.toChar(i)));
                this._isDeragement = false;
            }
            inCycles = false;
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        String toBeAdded = "(" + cycle + ")";
        this._cycles += toBeAdded;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char toPermute = this._alphabet.toChar(wrap(p));
        int indexOfToPermute = this._cycles.indexOf(toPermute);
        char result;
        if (this._cycles.charAt(indexOfToPermute + 1) != ')') {
            result = this._cycles.charAt(indexOfToPermute + 1);
        } else {
            int i = 1;
            while (this._cycles.charAt(indexOfToPermute - i) != '(') {
                i++;
            }
            result = this._cycles.charAt(indexOfToPermute - i + 1);
        }
        return this._alphabet.toInt(result);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char toinvert = this._alphabet.toChar(wrap(c));
        int indexOfToInvert = this._cycles.indexOf(toinvert);
        char result;
        if (this._cycles.charAt(indexOfToInvert - 1) != '(') {
            result = this._cycles.charAt(indexOfToInvert - 1);
        } else {
            int i = 1;
            while (this._cycles.charAt(indexOfToInvert + i) != ')') {
                i++;
            }
            result = this._cycles.charAt(indexOfToInvert + i - 1);
        }
        return this._alphabet.toInt(result);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (!this._alphabet.contains(p)) {
            throw error(String.format("When conducting"
                    + " permute(char p), %s is not in alphabet", p));
        }
        return _alphabet.toChar(permute(_alphabet.toInt(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (!this._alphabet.contains(c)) {
            throw error(String.format("When conducting"
                    + " invert(char c), %s is not in alphabet", c));
        }
        return _alphabet.toChar(invert(_alphabet.toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return this._isDeragement;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles for initializing permutation. */
    private String _cycles;

    /** Indicate whether add cycle in constructor.
     * facilitate to write the derangement function
     */
    private boolean _isDeragement;
}
