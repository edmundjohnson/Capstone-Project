package uk.jumpingmouse.moviecompanion.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uk.jumpingmouse.moviecompanion.data.Movie;

/**
 * Database helper class for accessing the Firebase Realtime Database.
 * This class contains methods available only to the admin product flavour.
 * @author Edmund Johnson
 */
public class DatabaseHelperFirebase extends DatabaseHelperFirebaseBase {

    // The singleton instance of this class.
    private static DatabaseHelper sDatabaseHelper = null;

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
    // Firebase database movie modification methods

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
        return addNode(context, DataContract.MovieEntry.ROOT_NODE, movie.getImdbId(), movie);
    }

    /**
     * Deletes a movie from the database.
     * @param context the context
     * @param imdbId the imdbId of the movie to be deleted
     * @return the number of rows deleted
     */
    @Override
    public int deleteMovie(@Nullable Context context, @NonNull String imdbId) {
        return deleteNode(context, DataContract.MovieEntry.ROOT_NODE, imdbId);
    }

    //---------------------------------------------------------------------
    // Query methods
    // Use the defaults.

    //---------------------------------------------------------------------
    // Getters

}
