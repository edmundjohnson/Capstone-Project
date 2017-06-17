package uk.jumpingmouse.moviecompanion;

import android.content.ContentResolver;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uk.jumpingmouse.moviecompanion.model.DataContract;

/**
 * Utilities for the android test classes.
 * Created by edmund on 17/06/2017.
 */

public class AndroidTestUtils {
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


}
