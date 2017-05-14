package uk.jumpingmouse.moviecompanion.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;

import uk.jumpingmouse.moviecompanion.data.Movie;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class for date/time utilities.
 * @author Edmund Johnson
 */
public final class DateUtils {
    /** The date format used by the OMDb API, e.g. "01 Jun 2016". */
    private static final String DATE_FORMAT_OMDB_RELEASED = "dd MMM yyyy";

//    /**
//     * A date format which allows dates to be compared chronologically.
//     * This is not private as it is used in a test class.
//     */
//    static final String DATE_FORMAT_COMPARABLE = "yyyy-MM-dd HH:mm:ss";

    /** The format for converting between OMDb-formatted date Strings and Dates. */
    private static SimpleDateFormat sDateFormatOmdbReleased = null;

//    /** The day/month format for displaying dates. */
//    private static SimpleDateFormat sDateFormatDisplay;

    /** Private default constructor to prevent instantiation. */
    private DateUtils() {
    }

    /**
     * Returns a SimpleDateFormat object for OMDb released dates.
     * @return a SimpleDateFormat object for OMDb released dates
     */
    @NonNull
    private static SimpleDateFormat getDateFormatOmdbReleased() {
        if (sDateFormatOmdbReleased == null) {
            sDateFormatOmdbReleased = new SimpleDateFormat(DATE_FORMAT_OMDB_RELEASED, Locale.US);
        }
        return sDateFormatOmdbReleased;
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
    private static Date toDate(@NonNull SimpleDateFormat format, @Nullable final String strDate) {
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
     * Returns a Date object representing an OMDb-formatted date string, e.g. "11 Jun 2016".
     * @param strDate an OMDb-formatted date string, e.g. "11 Jun 2016"
     * @return a Date object representing the supplied String,
     *         or null if the String could not be parsed as a date
     */
    @Nullable
    private static Date toDateOmdbReleased(@Nullable final String strDate) {
        return toDate(getDateFormatOmdbReleased(), strDate);
    }

    /**
     * Returns a long representing an OMDb-formatted released date as a number of milliseconds.
     * @param omdbReleased an OMDb released date, formatted as "dd MMM yyyy"
     * @return a long object representing omdbReleased as a number of milliseconds,
     *         or RELEASED_UNKNOWN if omdbReleased could not be converted to a long
     */
    public static long toLongOmdbReleased(@Nullable final String omdbReleased) {
        Date dateReleased = DateUtils.toDateOmdbReleased(omdbReleased);
        if (dateReleased == null) {
            return Movie.RELEASED_UNKNOWN;
        } else {
            return dateReleased.getTime();
        }
    }

    /**
     * Returns a Date as a String.
     * @param format the SimpleDateFormat used for returned String, e.g. "dd MMM yyyy"
     * @param date the Date
     * @return the Date as a String
     */
    @NonNull
    private static String toString(@NonNull SimpleDateFormat format, @NonNull final Date date) {
        return format.format(date);
    }

    /**
     * Returns a Date as a String in the OMDb released date format, e.g. "12 Jun 2017".
     * @param date the date
     * @return the date in the OMDb released date string format
     */
    @NonNull
    public static String toStringOmdbReleased(@NonNull final Date date) {
        return toString(getDateFormatOmdbReleased(), date);
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
