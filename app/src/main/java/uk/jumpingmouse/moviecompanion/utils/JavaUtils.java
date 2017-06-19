package uk.jumpingmouse.moviecompanion.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

/**
 * Class for Java utilities.
 * @author Edmund Johnson
 */
public final class JavaUtils {

    //---------------------------------------------------------------------
    // Date utilities

//    /**
//     * A date format which allows dates to be compared chronologically.
//     * This is not private as it is used in a test class.
//     */
//    static final String DATE_FORMAT_COMPARABLE = "yyyy-MM-dd HH:mm:ss";

    /** Private default constructor to prevent instantiation. */
    private JavaUtils() {
    }

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

    //---------------------------------------------------------------------

    /**
     * Converts a String to an int and returns the int.
     * @param str the String to convert
     * @param defaultValue the value to return if the String cannot be parsed as an int
     * @return the String as an int, or defaultValue if the String could not be parsed as an int
     */
    public static int toInt(@Nullable String str, int defaultValue) {
        if (str != null && !str.trim().isEmpty()) {
            try {
                return Integer.parseInt(str);
            } catch (Exception e) {
                Timber.w("Exception while converting String to int", e);
                return defaultValue;
            }
        }
        return defaultValue;
    }

}
