package be.dieterdemeyer.metro.online.util;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class Logger {

    private static final String LOGGER_CLASS_NAME = Logger.class.getName();

    public static Logger forClass(Class<?> clazz) {
        return forName(clazz.getName());
    }

    public static Logger forDeclaringClass() {
        final int declaringClassOffset = 2;

        return forName(getClassNameFromStackTrace(declaringClassOffset));
    }

    public static Logger forName(String name) {
        return new Logger(name);
    }

    private static String getClassNameFromStackTrace(int offset) {
        String className = LOGGER_CLASS_NAME;

        try {
            final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

            for (int i = 0, end = stackTrace.length - offset; i < end; i++) {
                if (LOGGER_CLASS_NAME.equals(stackTrace[i].getClassName())) {
                    className = stackTrace[i + offset].getClassName();
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        return className;
    }

    private final java.util.logging.Logger jdkLogger;

    private Logger(String name) {
        jdkLogger = java.util.logging.Logger.getLogger(name);
    }

    public boolean isLoggable(Level level) {
        return jdkLogger.isLoggable(level);
    }

    public void log(Level level, Object object) {
        if (isLoggable(level)) log(level, object.toString());
    }

    public void log(Level level, String message) {
        if (isLoggable(level)) {
            LogRecord lr = new LogRecord(level, message);
            lr.setLoggerName(jdkLogger.getName());
            lr.setSourceClassName(jdkLogger.getName());
            jdkLogger.log(lr);
        }
    }

    public void log(Level level, Throwable throwable) {
        if (isLoggable(level)) log(level, throwable, throwable.getMessage());
    }

    public void log(Level level, Throwable throwable, String message) {
        if (isLoggable(level)) {
            LogRecord lr = new LogRecord(level, message);
            lr.setLoggerName(jdkLogger.getName());
            lr.setThrown(throwable);
            lr.setSourceClassName(jdkLogger.getName());
            jdkLogger.log(lr);
        }

    }

    public void log(Level level, Throwable throwable, String messagePattern, Object... messageArguments) {
        if (isLoggable(level)) log(level, throwable, Messages.format(messagePattern, messageArguments));
    }

    public void log(Level level, String messagePattern, Object... messageArguments) {
        if (isLoggable(level)) log(level, Messages.format(messagePattern, messageArguments));
    }

    /**
     * DO NOT REMOVE THIS METHOD The JDK logger has a log method with Throwable as last argument, but this mixes up with {@link #log(java.util.logging.Level, String, Object...)}.
     * To avoid possible problems this method is defined and redirects the call to the correct method: {@link #log(java.util.logging.Level, Throwable, String)}
     */
    @Deprecated
    public void log(Level level, String message, Throwable throwable) {
        log(level, throwable, message);
    }

    final java.util.logging.Logger getJdkLogger() {
        return jdkLogger;
    }
}
