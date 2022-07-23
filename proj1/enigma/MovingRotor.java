package enigma;


/** Class that represents a rotating rotor in the enigma machine.
 *  @author Ziyi Xu
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        this._notches = notches;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    void advance() {
        int next = this.permutation().wrap(this.setting() + 1);
        super.set(next);
    }

    @Override
    String notches() {
        return this._notches;
    }


    /** The notches of this rotor.*/
    private String _notches;

}
