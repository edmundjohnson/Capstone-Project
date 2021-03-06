package uk.jumpingmouse.moviecompanion.model;

import android.content.Context;
import android.support.annotation.NonNull;

import uk.jumpingmouse.moviecompanion.data.Award;
import uk.jumpingmouse.moviecompanion.data.Movie;

/**
 * Database helper class for accessing the Firebase Realtime Database.
 * This class contains methods available only to the admin product flavour.
 * @author Edmund Johnson
 */
public class MasterDatabaseFirebaseAdmin extends MasterDatabaseFirebase {

    // The singleton instance of this class.
    private static MasterDatabase sMasterDatabase = null;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    public static MasterDatabase getInstance() {
        if (sMasterDatabase == null) {
            sMasterDatabase = new MasterDatabaseFirebaseAdmin();
        }
        return sMasterDatabase;
    }

    //---------------------------------------------------------------------
    // Movie modification methods

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
        return setNode(context, NODE_MOVIES, movie.getId(),
                movie, true);
    }

    /**
     * Deletes a movie from the database.
     * @param context the context
     * @param id the id of the movie to be deleted
     * @return the number of rows deleted
     */
    @Override
    public int deleteMovie(@NonNull Context context, @NonNull String id) {
        return deleteNode(context, NODE_MOVIES, id, true);
    }

    //---------------------------------------------------------------------
    // Award modification methods

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
        return setNode(context, NODE_AWARDS, award.getId(), award, true);
    }

    /**
     * Deletes an award from the database.
     * @param context the context
     * @param id the id of the award to be deleted
     * @return the number of rows deleted
     */
    @Override
    public int deleteAward(@NonNull Context context, @NonNull String id) {
        return deleteNode(context, NODE_AWARDS, id, true);
    }

    //---------------------------------------------------------------------
    // Query methods
    // Use the defaults.

}
