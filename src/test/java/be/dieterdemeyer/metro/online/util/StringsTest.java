package be.dieterdemeyer.metro.online.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringsTest {

    @Test
    public void inputWithNumericValuesAndCharactersReturnsOnlyNumericValues() {
        String input = "000n0125cr642";

        assertEquals("0000125642", Strings.stripNonNumeric(input));
    }

    @Test
    public void inputWithNumericValuesAndSpacesReturnsOnlyNumericValues() {
        String input = "000 01256 42";

        assertEquals("0000125642", Strings.stripNonNumeric(input));
    }

    @Test
    public void inputWithWhitespaceReturnsStrippedInput() {
        String input = "000 ab 25 ...\t .. \n";

        assertEquals("000ab25.....", Strings.stripWhitespace(input));
    }
}
