package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Ziyi Xu
 */
class Alphabet {

    /**The list to contain all alphabets.**/
    private char[] _Alphabet;

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        this._Alphabet = new char[chars.length()];
        for (int i = 0; i < chars.length(); i++) {
            this._Alphabet[i] = chars.charAt(i);
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return this._Alphabet.length;
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (int i = 0; i < this._Alphabet.length; i++) {
            if (this._Alphabet[i] == ch) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return this._Alphabet[index];
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar().
     *  Returns -1 if ch is not in the alphabet*/
    int toInt(char ch) {
        if (!this.contains(ch)) {
            throw new EnigmaException(String.format("%s is not in alphabet"
                    + ", can not convert to Int", ch));
        }
        for (int i = 0; i < this._Alphabet.length; i++) {
            if (ch == this._Alphabet[i]) {
                return i;
            }
        }
        throw new EnigmaException("Unexpected"
                + " error when conduct toInt in Alphabet");
    }

}
