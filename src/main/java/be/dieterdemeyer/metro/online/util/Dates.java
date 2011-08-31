package be.dieterdemeyer.metro.online.util;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class Dates {

    public static final String DEFAULT_XML_DATE_PATTERN = "yyyy-MM-dd";

    public static Date copyOf(Date date) {
        return (date == null) ? null : new Date(date.getTime());
    }

    public static Date atMidnight(Date date) {
        return (date == null) ? null : new DateMidnight(date.getTime()).toDate();
    }

    public static Date now() {
        return new DateTime().toDate();
    }

    public static Date today() {
        return new DateMidnight().toDate();
    }

    public static Date tomorrow() {
        return new DateMidnight().plusDays(1).toDate();
    }

    public static Date yesterday() {
        return new DateMidnight().minusDays(1).toDate();
    }

    public static Date fromYMD(int year, int month, int days) {
        return new DateMidnight(year, month, days).toDate();
    }

    public static Date addMonths(Date date, int months) {
        return new DateTime(date).plusMonths(months).toDate();
    }

    public static Date subtractMonths(Date date, int months) {
        return new DateTime(date).minusMonths(months).toDate();
    }

    public static Date subtractWeeks(Date date, int weeks) {
        return new DateTime(date).minusWeeks(weeks).toDate();
    }

    public static Date subtractDays(Date date, int days) {
        return new DateTime(date).minusDays(days).toDate();
    }

    public static Date lastDayOfMonth(Date date) {
        return new DateMidnight(date).dayOfMonth().withMaximumValue().toDate();
    }

    public static Date firstDayOfMonth(Date date) {
        return new DateMidnight(date).dayOfMonth().withMinimumValue().toDate();
    }

    public static int currentYear() {
        return new DateMidnight().year().get();
    }

    public static int currentMonth() {
        return new DateMidnight().monthOfYear().get();
    }

    public static boolean before(Date firstDate, Date secondDate) {
        return firstDate.before(secondDate);
    }

    public static boolean after(Date firstDate, Date secondDate) {
        return firstDate.after(secondDate);
    }

    public static boolean afterOrOn(Date firstDate, Date secondDate) {
        return firstDate.after(secondDate) || firstDate.equals(secondDate);
    }

    public static boolean beforeOrOn(Date firstDate, Date secondDate) {
        return firstDate.before(secondDate) || firstDate.equals(secondDate);
    }

    public static Calendar toCalendar(Date date) {
        return new DateTime(date).toCalendar(Locale.getDefault());
    }

    public static String format(Date date, String format) {
        String dateString = null;

        if (date != null) {
            DateFormat dateFormat = instantiateDateFormat(format);
            dateString = dateFormat.format(date);
        }

        return dateString;
    }

    public static long currentMillis() {
        return DateTimeUtils.currentTimeMillis();
    }

    private static final DateFormat instantiateDateFormat(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(false);
        return dateFormat;
    }

    public static Date from(Long milliseconds) {
        return new DateTime(milliseconds).toDate();
    }

    private Dates() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate a static utility class");
    }

}
