package uk.jumpingmouse.moviecompanion.model;

import android.support.annotation.NonNull;

/**
 * Database helper class for accessing the Firebase Realtime Database.
 * This class contains methods available only to the free product flavour.
 * @author Edmund Johnson
 */
public class DatabaseHelperFirebaseFree extends DatabaseHelperFirebaseBase {

    // The singleton instance of this class.
    private static DatabaseHelperFirebaseFree sDatabaseHelper = null;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    public static DatabaseHelper getInstance() {
        if (sDatabaseHelper == null) {
            sDatabaseHelper = new DatabaseHelperFirebaseFree();
        }
        return sDatabaseHelper;
    }

    //---------------------------------------------------------------------
    // Event-related methods
    // Use defaults.

    //---------------------------------------------------------------------
    // Firebase database movie modification methods.
    // Use the defaults.

    /**
     * Adds a movie's details to the Firebase database.
     * If the movie does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param context the context
     * @param movie the movie to insert or update
     * @return the number of rows inserted or updated
     */
    @Override
    public int addMovie(@Nullable final Context context, @NonNull final Movie movie) {
        throw new UnsupportedOperationException("Insufficient privileges for add movie");
    }

    /**
     * Deletes a movie from the Firebase database.
     * @param context the context
     * @param id the id of the movie to be deleted
     * @return the number of rows deleted
     */
    @Override
    public int deleteMovie(@Nullable Context context, int id) {
        throw new UnsupportedOperationException("Insufficient privileges for delete movie");
    }

    /**
     * Adds an award's details to the Firebase database.
     * If the award does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param context the context
     * @param award the award to insert or update; the id field may not be set
     * @return the id of the award, or null if the award was not added
     */
    @Nullable
    @Override
    public String addAward(@Nullable final Context context, @NonNull final Award award) {
        throw new UnsupportedOperationException("Insufficient privileges for add award");
    }

    /**
     * Deletes an award from the Firebase database.
     * @param context the context
     * @param id the id of the award to be deleted
     * @return the number of rows deleted
     */
    @Override
    public int deleteAward(@Nullable Context context, @Nullable String id) {
        throw new UnsupportedOperationException("Insufficient privileges for delete award");
    }

    //---------------------------------------------------------------------
    // Query methods
    // Use the defaults.

    //---------------------------------------------------------------------
    // Getters

}
