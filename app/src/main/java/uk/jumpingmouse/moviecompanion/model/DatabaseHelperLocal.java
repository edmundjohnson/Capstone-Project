package uk.jumpingmouse.moviecompanion.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.data.Movie;

/**
 * Helper class for accessing a local database.
 * This class is used by all product flavours; it is only access to the master database
 * which is restricted.
 * @author Edmund Johnson
 */
class DatabaseHelperLocal implements DatabaseHelper {

    /** The singleton instance of this class. */
    private static DatabaseHelper sDatabaseHelper = null;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    static DatabaseHelper getInstance() {
        if (sDatabaseHelper == null) {
            sDatabaseHelper = new DatabaseHelperLocal();
        }
        return sDatabaseHelper;
    }

    /** Private constructor to prevent instantiation from outside this class. */
    private DatabaseHelperLocal() {
    }

    //---------------------------------------------------------------------
    // Event-related methods

    /** Performs processing required when a user has signed in. */
    public void onSignedIn() {
        // no action required
    }

    /** Performs processing required when a user has signed out. */
    public void onSignedOut() {
        // no action required
    }

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
    public int addMovie(@Nullable final Context context, @NonNull final Movie movie) {
        return getLocalDatabase().addMovie(movie);
    }

    /**
     * Deletes a movie from the database.
     * @param context the context
     * @param id the id of the movie to be deleted
     * @return the number of rows deleted
     */
    public int deleteMovie(@Nullable Context context, @NonNull String id) {
        return getLocalDatabase().deleteMovie(id);
    }

    //---------------------------------------------------------------------
    // Movie query methods

    /**
     * Returns the movie with a specified id.
     * @param id the id of the movie to be returned
     * @return the movie with the specified id
     */
    @Override
    @Nullable
    public Movie selectMovieById(@NonNull String id) {
        return getLocalDatabase().selectMovieById(id);
    }

    /**
     * Returns a list of all the movies in the database.
     * @param projection The list of columns to put into the cursor.
     *                   If this is {@code null} all columns are included.
     * @param selection A selection criteria to apply when filtering rows.
     *                  If this is {@code null} then all rows are included.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in the order that they appear in the selection.
     *      The values will be bound as Strings.
     * @param sortOrder How the rows in the cursor should be sorted.
     *      If this is {@code null}, the sort order is undefined.
     * @return a list of all the movies in the database in the default order
     */
    @Override
    @Nullable
    public List<Movie> selectMovies(
            @Nullable final String[] projection, @Nullable final String selection,
            @Nullable final String[] selectionArgs, @Nullable final String sortOrder) {
        return getLocalDatabase().selectMovies(projection, selection, selectionArgs, sortOrder);
    }

    //---------------------------------------------------------------------
    // Getters

    /**
     * Convenience method which returns a reference to a local database.
     * @return a reference to a local database
     */
    private static LocalDatabase getLocalDatabase() {
        return ObjectFactory.getLocalDatabase();
    }

}
