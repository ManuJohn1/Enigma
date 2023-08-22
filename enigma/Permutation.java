package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Manu John
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _input = alphabet.getSeq();
        _output = alphabet.getSeq();

        String subseq = "";
        boolean cond = false;

        for (int i = 0; i < cycles.length(); i++) {
            if (cycles.charAt(i) == ' ') {
                continue;
            } else if (cycles.charAt(i) == ')') {
                cond = false;
                addCycle(subseq);
                subseq = "";
            } else if (cycles.charAt(i) == '(') {
                cond = true;
            } else if (cond) {
                subseq = subseq + cycles.charAt(i);
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        for (int i = 0; i < cycle.length(); i++) {
            for (int j = 0; j < _input.length(); j++) {
                if (cycle.charAt(i) == _input.charAt(j)) {
                    if (i < cycle.length() - 1) {
                        _output = _output.substring(0, j)
                                + cycle.charAt(wrap(i + 1))
                                + _output.substring(j + 1);
                    } else {
                        _output = _output.substring(0, j)
                                + cycle.charAt(0)
                                + _output.substring(j + 1);
                    }
                }
            }
        }
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
        return _input.indexOf(_output.charAt(wrap(p)));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        return _output.indexOf(_input.charAt(wrap(c)));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (!_alphabet.contains(p)) {
            throw new EnigmaException("Error: this char is not in alphabet");
        }
        return _output.charAt(permute(_output.indexOf(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (!_alphabet.contains(c)) {
            throw new EnigmaException("Error: this char is not in alphabet");
        }
        return _input.charAt(invert(_input.indexOf(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _input.length(); i++) {
            if (_input.charAt(i) == _output.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** input. */
    private String _input;

    /** output. */
    private String _output;

}
