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
            // context is used - analyser confused by product flavours
            @SuppressWarnings("UnusedParameters") @Nullable final Context context,
            @NonNull final Movie movie);

    /**
     * Deletes a movie from the database.
     * @param context the context
     * @param id the id of the movie to be deleted
     * @return the number of rows deleted
     */
    int deleteMovie(
            // context is used, but lint gets confused by product flavours
            @SuppressWarnings("UnusedParameters") @Nullable Context context,
            int id);

    //---------------------------------------------------------------------
    // Movie query methods

//    /**
//     * Returns the movie with a specified id.
//     * @param id the id of the movie to be returned
//     * @return the movie with the specified id
//     */
//    @Nullable
//    Movie selectMovieById(int id);

//    /**
//     * Returns a list of movies in the database.
//     * @param projection The list of columns to put into the cursor.
//     *                   If this is {@code null} all columns are included.
//     * @param selection A selection criteria to apply when filtering rows.
//     *                  If this is {@code null} then all rows are included.
//     * @param selectionArgs Any ?s included in selection will be replaced by
//     *      the values from selectionArgs, in the order that they appear in the selection.
//     *      The values will be bound as Strings.
//     * @param sortOrder How the rows in the cursor should be sorted.
//     *      If this is {@code null}, the sort order is undefined.
//     * @return a list of movies in the database
//     */
//    @Nullable
//    List<Movie> selectMovies(
//            @Nullable final String[] projection, @Nullable final String selection,
//            @Nullable final String[] selectionArgs, @Nullable final String sortOrder);

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
            // context is used - analyser confused by product flavours
            @SuppressWarnings("UnusedParameters") @Nullable final Context context,
            @NonNull final Award award);

    /**
     * Deletes an award from the database.
     * @param context the context
     * @param id the id of the award to be deleted
     * @return the number of rows deleted
     */
    int deleteAward(
            // context is used, but lint gets confused by product flavours
            @SuppressWarnings("UnusedParameters") @Nullable Context context,
            @Nullable String id);

//    /**
//     * Returns the award with a specified id.
//     * @param id the id of the award to be returned
//     * @return the award with the specified id
//     */
//    @Nullable
//    Award selectAwardById(@Nullable String id);

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
    int addUserMovie(@Nullable final Context context, @NonNull final UserMovie userMovie);

    /**
     * Deletes a user movie from the Firebase database.
     * @param context the context
     * @param id the id of the user movie to be deleted
     * @return the number of rows deleted
     */
    int deleteUserMovie(@Nullable Context context, int id);

}
