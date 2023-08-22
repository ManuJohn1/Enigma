package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a reflector in the enigma.
 *  @author Manu John
 */
class Reflector extends FixedRotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM. */
    Reflector(String name, Permutation perm) {
        super(name, perm);
        if (!perm.derangement()) {
            throw new EnigmaException("reflector's "
                    + "permutation needs to be a derangement");
        }
    }


    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("reflector has only one position");
        }
    }

    @Override
    void set(char cposn) {
        if (permutation().alphabet().toInt(cposn) != 0) {
            throw error("reflection has only one position");
        }
        return;
    }

    @Override
    boolean reflecting() {
        return true;
    }


}
