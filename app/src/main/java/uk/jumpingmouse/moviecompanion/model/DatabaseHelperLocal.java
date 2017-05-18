package uk.jumpingmouse.moviecompanion.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.data.Movie;

import java.util.List;

/**
 * Database helper class for accessing a local database.
 * @author Edmund Johnson
 */
class DatabaseHelperLocal implements DatabaseHelper {

    /** The singleton instance of this class. */
    private static DatabaseHelperLocal sDatabaseHelper = null;

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

    /** Private default constructor to prevent instantiation from outside this class. */
    private DatabaseHelperLocal() {
    }

    //---------------------------------------------------------------------
    // Movie methods

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
     * @param imdbId the getImdbId of the movie to be deleted
     * @return the number of rows deleted
     */
    public int deleteMovie(@Nullable Context context, @NonNull String imdbId) {
        return getLocalDatabase().deleteMovie(imdbId);
    }

    /**
     * Returns the movie with a specified IMDb id.
     * @param imdbId the getImdbId of the movie to be returned
     * @return the movie with the specified IMDb id
     */
    @Override
    @Nullable
    public Movie selectMovieByImdbId(@NonNull String imdbId) {
        return getLocalDatabase().selectMovieByImdbId(imdbId);
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

    /**
     * Convenience method which returns a reference to a local database.
     * @return a reference to a local database
     */
    private LocalDatabase getLocalDatabase() {
        return ObjectFactory.getLocalDatabase();
    }

}
