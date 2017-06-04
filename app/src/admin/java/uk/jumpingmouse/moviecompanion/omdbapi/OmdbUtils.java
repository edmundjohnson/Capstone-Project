package uk.jumpingmouse.moviecompanion.omdbapi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * Class containing utility methods related to the open movie database API.
 * @author Edmund Johnson
 */
class OmdbUtils {
    /** The date format used by the OMDb API, e.g. "01 Jun 2016". */
    private static final String DATE_FORMAT_OMDB_RELEASED = "dd MMM yyyy";

    /** The format for converting between OMDb-formatted date Strings and Dates. */
    private static SimpleDateFormat sDateFormatOmdbReleased = null;

//    /** The singleton instance of this class. */
//    private static OmdbUtils sOmdbUtils = null;

    //---------------------------------------------------------------------
    // Instance handling methods

//    /**
//     * Returns an instance of this class.
//     * @return an instance of this class
//     */
//    @NonNull
//    public static OmdbUtils getInstance() {
//        if (sOmdbUtils == null) {
//            sOmdbUtils = new OmdbUtils();
//        }
//        return sOmdbUtils;
//    }

//    /** Private default constructor, to prevent instantiation from outside this class. */
//    private OmdbUtils() {
//    }

    //---------------------------------------------------------------------
    // Movie runtime field

    /**
     * Returns an OMDb runtime as an int, e.g. returns "144 min" as 144
     * @param omdbRuntime the OMDb runtime, e.g. "144 min"
     * @return the runtime as an int, e.g. 144,
     *         or OmdbMovie.RUNTIME_UNKNOWN if omdbRuntime could not be converted to an int
     */
    static int toIntOmdbRuntime(@Nullable String omdbRuntime) {
        if (omdbRuntime != null) {
            String[] split = omdbRuntime.split(" ", 2);
            // split.length is always at least 1
            try {
                return Integer.decode(split[0]);
            } catch (NumberFormatException e) {
                Timber.w(String.format(
                        "NumberFormatException while attempting to decode OMDb runtime to int: \"%s\"",
                        omdbRuntime));
            }
        }
        return OmdbMovie.RUNTIME_UNKNOWN;
    }

//    /**
//     * Returns an OMDb-formatted runtime string representing a number of minutes.
//     * @param runtime the runtime in minutes
//     * @return an OMDb-formatted runtime string corresponding to the runtime,
//     *         or an empty String if runtime indicates an unknown runtime
//     */
//    @NonNull
//    String toStringOmdbRuntime(final int runtime) {
//        if (runtime == OmdbMovie.RUNTIME_UNKNOWN) {
//            return "";
//        }
//        return runtime + " min";
//    }

    //---------------------------------------------------------------------
    // Movie released field

//    /**
//     * Returns an OMDb-formatted released date string representing a number of milliseconds.
//     * @param released a release date as a number of milliseconds
//     * @return an OMDb-formatted released date string corresponding to released,
//     *         or an empty String if released indicates an unknown release date
//     */
//    @NonNull
//    String toStringOmdbReleased(final long released) {
//        if (released == OmdbMovie.RELEASED_UNKNOWN || released < 0) {
//            return "";
//        }
//        Date dateReleased = new Date(released);
//        return toStringOmdbReleased(dateReleased);
//    }

//    /**
//     * Returns a Date as a String in the OMDb released date format, e.g. "12 Jun 2017".
//     * @param date the date
//     * @return the date in the OMDb released date string format
//     */
//    @NonNull
//    private String toStringOmdbReleased(@NonNull final Date date) {
//        return toString(getDateFormatOmdbReleased(), date);
//    }

    /**
     * Returns a long representing an OMDb-formatted released date as a number of milliseconds.
     * @param omdbReleased an OMDb released date, formatted as "dd MMM yyyy"
     * @return a long object representing omdbReleased as a number of milliseconds,
     *         or OmdbMovie.RELEASED_UNKNOWN if omdbReleased could not be converted to a long
     */
    static long toLongOmdbReleased(@Nullable final String omdbReleased) {
        Date dateReleased = toDateOmdbReleased(omdbReleased);
        if (dateReleased == null) {
            return OmdbMovie.RELEASED_UNKNOWN;
        } else {
            return dateReleased.getTime();
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

    //---------------------------------------------------------------------
    // Date utilities

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

//    /**
//     * Returns a Date as a String.
//     * @param format the SimpleDateFormat used for returned String, e.g. "dd MMM yyyy"
//     * @param date the Date
//     * @return the Date as a String
//     */
//    @NonNull
//    private static String toString(@NonNull SimpleDateFormat format, @NonNull final Date date) {
//        return format.format(date);
//    }

}
