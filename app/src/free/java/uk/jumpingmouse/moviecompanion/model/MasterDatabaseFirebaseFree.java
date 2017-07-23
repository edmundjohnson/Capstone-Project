package uk.jumpingmouse.moviecompanion.model;

import android.content.Context;
import android.support.annotation.NonNull;

import uk.jumpingmouse.moviecompanion.data.Award;
import uk.jumpingmouse.moviecompanion.data.Movie;

/**
 * Class for accessing the Firebase Realtime Database.
 * This class contains methods available only to the free product flavour.
 * @author Edmund Johnson
 */
public class MasterDatabaseFirebaseFree extends MasterDatabaseFirebase {

    // The singleton instance of this class.
    private static MasterDatabaseFirebaseFree sMasterDatabase = null;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    public static MasterDatabase getInstance() {
        if (sMasterDatabase == null) {
            sMasterDatabase = new MasterDatabaseFirebaseFree();
        }
        return sMasterDatabase;
    }

    //---------------------------------------------------------------------
    // Firebase database movie modification methods.
    // Not supported.

    /**
     * Adds a movie's details to the Firebase database.
     * If the movie does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param context the context
     * @param movie the movie to insert or update
     * @return the number of rows inserted or updated
     */
    @Override
    public int addMovie(@NonNull final Context context, @NonNull final Movie movie) {
        throw new UnsupportedOperationException("Insufficient privileges for add movie");
    }

    /**
     * Deletes a movie from the Firebase database.
     * @param context the context
     * @param id the id of the movie to be deleted
     * @return the number of rows deleted
     */
    @Override
    public int deleteMovie(@NonNull Context context, @NonNull String id) {
        throw new UnsupportedOperationException("Insufficient privileges for delete movie");
    }

    /**
     * Adds an award's details to the Firebase database.
     * If the award does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param context the context
     * @param award the award to insert or update
     * @return the number of rows inserted or updated
     */
    @Override
    public int addAward(@NonNull final Context context, @NonNull final Award award) {
        throw new UnsupportedOperationException("Insufficient privileges for add award");
    }

    /**
     * Deletes an award from the Firebase database.
     * @param context the context
     * @param id the id of the award to be deleted
     * @return the number of rows deleted
     */
    @Override
    public int deleteAward(@NonNull Context context, @NonNull String id) {
        throw new UnsupportedOperationException("Insufficient privileges for delete award");
    }

    //---------------------------------------------------------------------
    // Query methods
    // Use the defaults.

    //---------------------------------------------------------------------
    // Getters

}
