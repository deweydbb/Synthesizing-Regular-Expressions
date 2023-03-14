package utils;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CharSetTest {

    @Test
    public void test1() {
        CharSet c = new CharSet();
        c.addChar('a');
        assertTrue(c.contains('a'));
        assertEquals("[a]", c.toString());
    }

    @Test
    public void addAll() {
        CharSet set = new CharSet();

        for (char c = 0; c < 128; c++) {
            set.addChar(c);
        }

        for (char c = 0; c < 128; c++) {
            assertTrue(set.contains(c));
        }
    }

    @Test
    public void addAllRemoveAll() {
        CharSet set = new CharSet();

        for (char c = 0; c < 128; c++) {
            set.addChar(c);
        }

        for (char c = 0; c < 128; c++) {
            assertTrue(set.contains(c));
        }

        for (char c = 0; c < 128; c++) {
            set.removeChar(c);
        }

        for (char c = 0; c < 128; c++) {
            assertFalse(set.contains(c));
        }
    }

    @Test
    public void testEquality() {
        CharSet set1 = new CharSet();
        CharSet set2 = new CharSet();

        for (char c = 0; c < 128; c++) {
            set1.addChar(c);
            set2.addChar(c);
        }

        for (char c = 0; c < 128; c++) {
            assertTrue(set1.contains(c));
            assertTrue(set2.contains(c));
        }

        assertEquals(set1, set2);

    }

    @Test
    public void testCopy() {
        CharSet c = new CharSet();
        c.addChar('a');

        CharSet c2 = c.copy();

        assertEquals(c, c2);
    }

}