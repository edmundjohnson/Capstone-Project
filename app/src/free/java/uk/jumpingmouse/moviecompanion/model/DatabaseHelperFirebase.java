package uk.jumpingmouse.moviecompanion.model;

import android.support.annotation.NonNull;

/**
 * Database helper class for accessing the Firebase Realtime Database.
 * This class contains methods available only to the free product flavour.
 * @author Edmund Johnson
 */
public class DatabaseHelperFirebase extends DatabaseHelperFirebaseBase {

    // The singleton instance of this class.
    private static DatabaseHelperFirebase sDatabaseHelper = null;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    public static DatabaseHelper getInstance() {
        if (sDatabaseHelper == null) {
            sDatabaseHelper = new DatabaseHelperFirebase();
        }
        return sDatabaseHelper;
    }

    //---------------------------------------------------------------------
    // Event-related methods
    // Use defaults.

    //---------------------------------------------------------------------
    // Firebase database movie modification methods.
    // Use the defaults.

    //---------------------------------------------------------------------
    // Query methods
    // Use the defaults.

    //---------------------------------------------------------------------
    // Getters

}
