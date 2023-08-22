package enigma;

import java.util.HashMap;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Machine class.
 *  @author
 */
public class MachineTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTS ***** */

    private static final Alphabet AZ = new Alphabet(TestUtils.UPPER_STRING);

    private static final HashMap<String, Rotor> ROTORS = new HashMap<>();
    private static final HashMap<String, Rotor> ROTORS2 = new HashMap<>();

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

    @Test
    public void advanceRotorTest() {
        ROTORS2.put("B", new Reflector("B",
                new Permutation("(AE) (BN) (CK) (DQ) (FU) "
                        + "(GY) (HW) (IJ) (LO) (MP) (RX) (SZ) (TV)", AZ)));
        ROTORS2.put("C", new Reflector("C", new Permutation("(AR) "
                + "(BD) (CO) (EJ) (FN) (GT) (HK) (IV) "
                + "(LM) (PW) (QZ) (SX) (UY)", AZ)));
        ROTORS2.put("Beta",
                new FixedRotor("Beta",
                        new Permutation(
                                "(ALBEVFCYODJWUGNMQTZSKPR) (HIX)", AZ)));
        ROTORS2.put("Gamma",
                new FixedRotor("Gamma",
                        new Permutation(
                                "(AFNIRLBSQWVXGUZDKMTPCOYJHE)", AZ)));
        ROTORS2.put("I",
                new MovingRotor("I",
                        new Permutation(
                                "(AELTPHQXRU) (BKNW) (CMOY) "
                                        + "(DFG) (IV) (JZ) (S)", AZ), "Q"));
        ROTORS2.put("II",
                new MovingRotor("II",
                        new Permutation(
                                "(FIXVYOMW) (CDKLHUP) (ESZ) "
                                       + "(BJ) (GR) (NT) (A) (Q)", AZ), "E"));
        ROTORS2.put("III",
                new MovingRotor("III",
                        new Permutation(""
                                + "(ABDHPEJT) "
                                + "(CFLVMZOYQIRWUKXSG) (N)", AZ), "V"));
        ROTORS2.put("IV",
                new MovingRotor("IV",
                        new Permutation(""
                                + "(AEPLIYWCOXMRFZBSTGJQNH) "
                                + "(DV) (KU)", AZ), "J"));
        ROTORS2.put("V",
                new MovingRotor("V",
                        new Permutation(""
                                + "(AVOLDRWFIUQ)(BZKSMNHYC)"
                                + " (EGTJPX)", AZ), "Z"));
        ROTORS2.put("VI",
                new MovingRotor("VI",
                        new Permutation(""
                                + "(AJQDVLEOZWIYTS) "
                                + "(CGMNHFUX) (BPRK)", AZ), "ZM"));
        ROTORS2.put("VII",
                new MovingRotor("VII",
                        new Permutation(""
                                + "(ANOUPFRIMBZTLWKSVEGCJYDHXQ)", AZ), "ZM"));
        ROTORS2.put("VIII",
                new MovingRotor("VIII",
                        new Permutation(""
                               + "(AFLSETWUNDHOZVICQ) "
                               + "(BKJ) (GXY) (MPR)", AZ), "ZM"));

    }

}
