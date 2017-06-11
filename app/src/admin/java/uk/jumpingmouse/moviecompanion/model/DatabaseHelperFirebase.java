package uk.jumpingmouse.moviecompanion.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uk.jumpingmouse.moviecompanion.data.Award;
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
        return addNode(context, DataContract.MovieEntry.ROOT_NODE, Integer.toString(movie.getId()), movie);
    }

    /**
     * Deletes a movie from the database.
     * @param context the context
     * @param id the id of the movie to be deleted
     * @return the number of rows deleted
     */
    @Override
    public int deleteMovie(@Nullable Context context, int id) {
        return deleteNode(context, DataContract.MovieEntry.ROOT_NODE, Integer.toString(id));
    }

    //---------------------------------------------------------------------
    // Firebase database award modification methods

    /**
     * Adds a movie's details to the Firebase database.
     * If the movie does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param context the context
     * @param award the award to insert or update
     * @return the number of rows inserted or updated
     */
    @Override
    public int addAward(@Nullable final Context context, @NonNull final Award award) {
        return pushNode(context,
                DataContract.AwardEntry.ROOT_NODE + "/" + award.getMovieId(),
                award);
    }

    /**
     * Deletes an award from the database.
     * @param context the context
     * @param id the id of the award to be deleted
     * @return the number of rows deleted
     */
    @Override
    public int deleteAward(@Nullable Context context, @Nullable String id) {
        if (id == null) {
            return 0;
        }
        return deleteNode(context, DataContract.AwardEntry.ROOT_NODE, id);
    }

    //---------------------------------------------------------------------
    // Query methods
    // Use the defaults.

    //---------------------------------------------------------------------
    // Getters

}
