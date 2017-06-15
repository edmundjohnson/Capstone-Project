package uk.jumpingmouse.moviecompanion.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

/**
 * Class for date/time utilities.
 * @author Edmund Johnson
 */
final class DateUtils {

//    /**
//     * A date format which allows dates to be compared chronologically.
//     * This is not private as it is used in a test class.
//     */
//    static final String DATE_FORMAT_COMPARABLE = "yyyy-MM-dd HH:mm:ss";

//    /** The day/month format for displaying dates. */
//    private static SimpleDateFormat sDateFormatDisplay;

    /** Private default constructor to prevent instantiation. */
    private DateUtils() {
    }

//    /**
//     * Returns a dateFormatDayMonth object.
//     * @return a dateFormatDayMonth object
//     */
//    @NonNull
//    private static SimpleDateFormat getDateFormatForDisplay() {
//        if (sDateFormatDayMonth == null) {
//            sDateFormatDayMonth = new SimpleDateFormat(DATE_FORMAT_FOR_DISPLAY, Locale.US);
//        }
//        return sDateFormatDayMonth;
//    }

    /**
     * Returns a Date object representing a supplied String.
     * @param format the SimpleDateFormat used for strDate, e.g. "dd MMM yyyy"
     * @param strDate a String representing a date
     * @return a Date object representing the supplied String,
     *         or null if the String could not be parsed as a date
     */
    @Nullable
    public static Date toDate(@NonNull SimpleDateFormat format, @Nullable final String strDate) {
        if (strDate == null) {
            return null;
        }
        try {
            return format.parse(strDate);
        } catch (ParseException e) {
            Timber.e("toDate: Cannot parse String \"" + strDate + "\" to a Date using format: \""
                    + format + "\"", e);
            return null;
        }
    }

    /**
     * Returns a Date as a String.
     * @param format the SimpleDateFormat used for returned String, e.g. "dd MMM yyyy"
     * @param date the Date
     * @return the Date as a String
     */
    @NonNull
    public static String toString(@NonNull SimpleDateFormat format, @NonNull final Date date) {
        return format.format(date);
    }

//    /**
//     * Returns a Date in a String format which is suitable for comparison.
//     * @param date the Date
//     * @return the Date in a String format which is suitable for comparison
//     */
//    @Nullable
//    public static String toStringComparable(@Nullable final Date date) {
//        return toString(getDateFormatComparable(), date);
//    }

//    /**
//     * Returns a String version of a Date in a format which is suitable for display.
//     * @param date the Date
//     * @return the String version of the Date in a format which is suitable for display
//     */
//    @Nullable
//    public static String toStringForDisplay(@Nullable final Date date) {
//        return toString(getDateFormatForDisplay(), date);
//    }

}
