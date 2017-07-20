package uk.jumpingmouse.moviecompanion;

import android.content.ContentResolver;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.utils.JavaUtils;

/**
 * Utilities for the android test classes.
 * Created by edmund on 17/06/2017.
 */

public class AndroidTestUtils {
    /** The date format used by the OMDb API, e.g. "01 Jun 2016". */
    private static final String DATE_FORMAT_OMDB_RELEASED = "dd MMM yyyy";

    /** The singleton instance of this class. */
    private static AndroidTestUtils sAndroidTestUtils = null;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    public static AndroidTestUtils getInstance() {
        if (sAndroidTestUtils == null) {
            sAndroidTestUtils = new AndroidTestUtils();
        }
        return sAndroidTestUtils;
    }

    /** Private default constructor to prevent instantiation from outside this class. */
    private AndroidTestUtils() {
    }

    //---------------------------------------------------------------------
    // Utility methods

    /**
     * Returns the numbers of Movies in the local database.
     * @param contentResolver the content resolver
     * @return the numbers of Movies in the local database
     */
    public int getMovieCount(@NonNull ContentResolver contentResolver) {
        int count = 0;
        Cursor cursor = contentResolver.query(DataContract.MovieEntry.buildUriForAllRows(),
                null, null, null, null);
        if (cursor != null) {
            count = cursor.getCount();
            closeCursor(cursor);
        }
        return count;
    }

    /**
     * Silently closes a cursor.
     * @param cursor the cursor
     */
    public void closeCursor(@Nullable Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    /**
     * Returns a long representing an OMDb-formatted released date as a number of milliseconds.
     * @param omdbReleased an OMDb released date, formatted as "dd MMM yyyy"
     * @return a long object representing omdbReleased as a number of milliseconds,
     *         or Movie.RELEASED_UNKNOWN if omdbReleased could not be converted to a long
     */
    public static long toLongOmdbReleased(@Nullable final String omdbReleased) {
        Date dateReleased = toDateOmdbReleased(omdbReleased);
        if (dateReleased == null) {
            return Movie.RELEASED_UNKNOWN;
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
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_OMDB_RELEASED, Locale.US);
        return JavaUtils.toDate(sdf, strDate);
    }


}
