package be.dieterdemeyer.metro.online.util;

import org.apache.commons.lang.StringUtils;

public class Strings {

    public static String stripNonNumeric(String input) {
        return input == null ? null : input.replaceAll("[^0-9]*", "");
    }

    public static String stripWhitespace(String input) {
        return input == null ? null : input.replaceAll("\\s", "");
    }

    public static String addLeadingZero(String input) {
        return StringUtils.leftPad(input, 2, '0');
    }

    private Strings() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate a static utility class");
    }

}
