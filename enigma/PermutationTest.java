package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = fromAlpha.indexOf(c), ei = fromAlpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
        perm = new Permutation("(BCF)", new Alphabet("ABCDEFG"));
        checkPerm("reg1", "ABCDEFG", "ACFDEBG");
        perm = new Permutation("(BCF) (AE)", new Alphabet("ABCDEFG"));
        checkPerm("reg2", "ABCDEFG", "ECFDABG");
        perm = new Permutation("(ABCDEFG)", new Alphabet("ABCDEFG"));
        checkPerm("reversal", "ABCDEFG", "BCDEFGA");
        perm = new Permutation("(A) (B)(C) (D)(E) (F) (G)",
                new Alphabet("ABCDEFG"));
        checkPerm("same", "ABCDEFG", "ABCDEFG");
        perm = new Permutation("(AD)", new Alphabet("ABCD"));
        checkPerm("special characters", "ABCD", "DBCA");

    }


    @Test
    public void permuteAndInvertTest() {
        perm = new Permutation("(ABCDEFGH)", new Alphabet("ABCDEFGH"));
        assertEquals(true, perm.derangement());
        assertEquals(3, perm.permute(10));
        assertEquals(3, perm.permute(2));
        assertEquals('D', perm.permute('C'));
        assertEquals(2, perm.invert(3));
        assertEquals('C', perm.invert('D'));
        assertEquals(0, perm.permute(7));
        assertEquals(0, perm.permute(15));
        assertEquals(7, perm.invert(0));
    }


}
