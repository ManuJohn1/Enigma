package enigma;

import org.junit.Test;

import static org.junit.Assert.*;

public class AlphabetTest {
    /* **** TESTS **** */

    @Test
    public void test1() {
        Alphabet m = new Alphabet("AETCJ");
        assertEquals(5, m.size());
        assertTrue(m.contains('T'));
        assertFalse(m.contains('t'));
        assertFalse(m.contains('L'));
        assertEquals(3, m.toInt('C'));
        assertEquals('E', m.toChar(1));
    }

    @Test
    public void test2() {
        String e = "jk#@0{}[";
        Alphabet v = new Alphabet(e);
        assertEquals(8, v.size());
        for (int i = 0; i < e.length(); i++) {
            char r = e.charAt(i);
            assertTrue(v.contains((r)));
            assertEquals(r, v.toChar(i));
            assertEquals(i, v.toInt(r));
        }
        v.contains('K');
        v.contains('?');
    }

}
