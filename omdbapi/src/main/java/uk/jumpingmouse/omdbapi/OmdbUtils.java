package uk.jumpingmouse.omdbapi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class containing utility methods related to the open movie database API.
 * @author Edmund Johnson
 */
final class OmdbUtils {
    private static final String TAG = OmdbUtils.class.getSimpleName();

    /** The date format used by the OMDb API, e.g. "01 Jun 2016". */
    private static final String DATE_FORMAT_OMDB_RELEASED = "dd MMM yyyy";

    /** The format for converting between OMDb-formatted date Strings and Dates. */
    private static SimpleDateFormat sDateFormatOmdbReleased = null;

    //---------------------------------------------------------------------
    // 'released' field

    /**
     * Returns a Date object representing an OMDb-formatted date string, e.g. "11 Jun 2016".
     * @param strDate an OMDb-formatted date string, e.g. "11 Jun 2016"
     * @return a Date object representing the supplied String,
     *         or null if the String could not be parsed as a date
     */
    @Nullable
    static Date toDateOmdbReleased(@Nullable final String strDate) {
        return toDate(getDateFormatOmdbReleased(), strDate);
    }

    //---------------------------------------------------------------------
    // Date utilities

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
            Log.e(TAG, "toDate: Cannot parse String \"" + strDate + "\" to a Date using format: \""
                    + format + "\"", e);
            return null;
        }
    }

}
