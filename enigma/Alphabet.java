package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Manu John
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        if (chars.contains("*") || chars.contains("(") || chars.contains(")")) {
            throw new EnigmaException("cannot contain *, (, )");
        }
        for (int i = 0; i < chars.length() - 1; i++) {
            for (int j = i + 1; j < chars.length(); j++) {
                if (chars.charAt(i) == chars.charAt(j)) {
                    throw new EnigmaException("cannot contain duplicate");
                }
            }
        }
        this.seq = chars;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return seq.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (int i = 0; i < seq.length(); i++) {
            if (seq.charAt(i) == ch) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return seq.charAt(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        for (int i = 0; i < seq.length(); i++) {
            if (seq.charAt(i) == ch) {
                return i;
            }
        }
        return -1;
    }

    /** Returns String. */
    public String getSeq() {
        return seq;
    }

    /** seq. */
    private String seq;

}
