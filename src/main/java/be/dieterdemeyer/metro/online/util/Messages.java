package be.dieterdemeyer.metro.online.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.LITERAL;

public final class Messages {

    private static final Pattern ONE_QUOTE_PATTERN = Pattern.compile("'", LITERAL);
    private static final String TWO_QUOTES_REPLACEMENT = "''";
    private static final Logger LOGGER = Logger.forDeclaringClass();

    public static final String dump(String pattern, Object... arguments) {
        final StringBuilder builder = new StringBuilder(pattern).append(':');

        for (Object argument : arguments) {
            builder.append(' ').append(argument).append(';');
        }

        return builder.toString();
    }

    public static final String format(String pattern, Object... arguments) {
        String result = null;

        try {
            result = MessageFormat.format(escapeSingleQuotes(pattern), arguments);

        } catch (IllegalArgumentException ex) {
            result = dump(pattern, arguments);
        }

        return result;
    }

    public static String translate(Locale locale, String bundleName, ClassLoader classLoader, String resourceKey, Object... parameters) {
        String result = null;

        try {
            String translation = ResourceBundle.getBundle(bundleName, locale, classLoader).getString(resourceKey);
            result = format(translation, parameters);

        } catch (MissingResourceException e) {
            LOGGER.log(Level.WARNING, format("Bundle {1} not found or {0} not found in bundle", resourceKey, bundleName));
            result = resourceKey;
        }

        return result;
    }

    public static String translate(Locale locale, String bundleName, String resourceKey, Object... parameters) {
        return translate(locale, bundleName, Messages.class.getClassLoader(), resourceKey, parameters);
    }

    public static String translate(String bundleName, String resourceKey, Object... parameters) {
        return translate(Locale.getDefault(), bundleName, resourceKey, parameters);
    }

    private static final String escapeSingleQuotes(String pattern) {
        return ONE_QUOTE_PATTERN.matcher(pattern).replaceAll(TWO_QUOTES_REPLACEMENT);
    }

    private Messages() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate a static utility class");
    }

}