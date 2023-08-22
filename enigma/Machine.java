package enigma;

import java.util.Collection;
import java.util.Iterator;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Manu John
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {

        if (numRotors <= 1 || pawls < 0 || pawls >= numRotors) {
            throw new EnigmaException("incorrect "
                    + "number of rotors and/or pawls");
        }

        this._alphabet = alpha;
        this._numRotors = numRotors;
        this._pawls = pawls;
        this._allRotors = allRotors;
        this._plugboard = null;
        this.rotorArr = new Rotor[_numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        if (k < 0 || k >= _numRotors) {
            throw new EnigmaException("Error: k is not valid size");
        }
        return rotorArr[k];
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length; i++) {
            Iterator<Rotor> c = _allRotors.iterator();
            while (c.hasNext()) {
                Rotor r = c.next();
                if (r.name().equals(rotors[i])) {
                    rotorArr[i] = r;
                }
            }

            if (rotorArr[i] == null) {
                throw new EnigmaException("the "
                       + "rotor name is not in collection");
            }
        }
        verifyRotorPos();
        cond = true;
    }

    /** get cond.
     * @return  */
    boolean getCond() {
        return cond;
    }

    /** Verify rotor positions. */
    void verifyRotorPos() {
        for (int i = 0; i < rotorArr.length; i++) {
            if (i == 0 && !(rotorArr[i] instanceof Reflector)) {
                throw new EnigmaException("error: first rotor "
                        + "needs to be a reflector");
            } else if (i >= 1 && (i < (numRotors() - numPawls()))
                    && (rotorArr[i] instanceof Reflector
                    || !(rotorArr[i] instanceof FixedRotor))) {
                throw new EnigmaException("group S-P "
                        + "should be non-moving");
            } else if ((i >= (numRotors() - numPawls()))
                    && i < numRotors()
                    && !(rotorArr[i] instanceof MovingRotor)) {
                throw new EnigmaException("error: "
                        + "rotors at S+P should be moving");
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw new EnigmaException("size of setting is incorrect");
        }

        for (int i = 0; i < setting.length(); i++) {
            rotorArr[i + 1].set(setting.charAt(i));
        }
    }

    /** Set the ring with SETTING. */
    void setRing(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw new EnigmaException("size of setting is incorrect");
        }

        for (int i = 0; i < setting.length(); i++) {
            rotorArr[i + 1].setRinger(setting.charAt(i));
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
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
    private void advanceRotors() {
        boolean[] canAdv = new boolean[rotorArr.length];
        canAdv[rotorArr.length - 1] = true;
        for (int i = canAdv.length - 2; i >= 0; i--) {
            if (rotorArr[i].rotates() && rotorArr[i + 1].atNotch()) {
                canAdv[i] = true;
                canAdv[i + 1] = true;
            } else {
                canAdv[i] = false;
            }
        }

        int i = 0;
        while (i < canAdv.length) {
            if (canAdv[i]) {
                rotorArr[i].advance();
            }
            i++;
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        for (int i = rotorArr.length - 1; i >= 0; i--) {
            c = rotorArr[i].convertForward(c);
        }

        for (int i = 1; i < rotorArr.length; i++) {
            c = rotorArr[i].convertBackward(c);
        }

        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String s = "";
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == ' ') {
                continue;
            } else {
                s += _alphabet.toChar(convert(_alphabet.toInt(msg.charAt(i))));
            }
        }
        return s;
    }


    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** number of Rotors. */
    private int _numRotors;
    /** number of pawls. */
    private int _pawls;
    /** collection of all rotors. */
    private Collection<Rotor> _allRotors;
    /** */
    private Rotor[] rotorArr;
    /** plugboard. */
    private Permutation _plugboard;
    /** boolean condition. */
    private boolean cond = false;
}
