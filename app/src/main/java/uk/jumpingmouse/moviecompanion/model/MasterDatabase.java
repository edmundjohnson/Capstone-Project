package uk.jumpingmouse.moviecompanion.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uk.jumpingmouse.moviecompanion.data.Award;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.data.UserMovie;

/**
 * Interface for implementations of the master database.
 * The master database is the one which is updated and may be remote,
 * whereas queries are done against a LocalDatabase.
 * @author Edmund Johnson
 */
public interface MasterDatabase {

    String MOVIE_SORT_COLUMN_DEFAULT = DataContract.MovieEntry.COLUMN_TITLE;
    boolean MOVIE_SORT_ASCENDING_DEFAULT = true;

    //---------------------------------------------------------------------
    // Event-related methods

    /**
     * Performs processing required when a user has signed in.
     * @param context the context
     */
    void onSignedIn(@Nullable Context context);

    /** Performs processing required when a user has signed out. */
    void onSignedOut();

    //---------------------------------------------------------------------
    // Movie modification methods

    /**
     * Adds a movie's details to the database.
     * If the movie does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param context the context
     * @param movie the movie to insert or update
     * @return the number of rows inserted or updated
     */
    int addMovie(
            // parameters are used, but lint gets confused by product flavours
            @SuppressWarnings("UnusedParameters") @Nullable Context context,
            @SuppressWarnings("UnusedParameters") @NonNull Movie movie);

    /**
     * Deletes a movie from the database.
     * @param context the context
     * @param id the id of the movie to be deleted
     * @return the number of rows deleted
     */
    int deleteMovie(
            // parameters are used, but lint gets confused by product flavours
            @SuppressWarnings("UnusedParameters") @Nullable Context context,
            @SuppressWarnings("UnusedParameters") @Nullable String id);

    //---------------------------------------------------------------------
    // Award modification methods

    /**
     * Adds an award's details to the database.
     * If the award does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param context the context
     * @param award the award to insert or update
     * @return the number of rows inserted or updated
     */
    int addAward(
            // parameters are used, but lint gets confused by product flavours
            @SuppressWarnings("UnusedParameters") @Nullable Context context,
            @SuppressWarnings("UnusedParameters") @NonNull Award award);

    /**
     * Deletes an award from the database.
     * @param context the context
     * @param id the id of the award to be deleted
     * @return the number of rows deleted
     */
    int deleteAward(
            // parameters are used, but lint gets confused by product flavours
            @SuppressWarnings("UnusedParameters") @Nullable Context context,
            @SuppressWarnings("UnusedParameters") @Nullable String id);

    //---------------------------------------------------------------------
    // UserMovie modification methods.

    /**
     * Adds a user movie's details to the Firebase database.
     * If the user movie does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param context the context
     * @param userMovie the user movie to insert or update
     * @return the number of rows inserted or updated
     */
    int addUserMovie(@Nullable Context context, @NonNull UserMovie userMovie);

    /**
     * Deletes a user movie from the Firebase database.
     * @param context the context
     * @param id the id of the user movie to be deleted
     * @return the number of rows deleted
     */
    int deleteUserMovie(@Nullable Context context, @Nullable String id);

}
