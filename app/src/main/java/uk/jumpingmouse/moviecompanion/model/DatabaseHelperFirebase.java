package uk.jumpingmouse.moviecompanion.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uk.jumpingmouse.moviecompanion.data.Movie;

import java.util.List;

/**
 * Helper class for the Firebase Realtime Database.
 * @author Edmund Johnson
 */
public class DatabaseHelperFirebase implements DatabaseHelper {

    /** The singleton instance of this class. */
    private static DatabaseHelperFirebase sDatabaseHelper = null;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    public static DatabaseHelperFirebase getInstance() {
        if (sDatabaseHelper == null) {
            sDatabaseHelper = new DatabaseHelperFirebase();
        }
        return sDatabaseHelper;
    }

    /** Private default constructor to prevent instantiation from outside this class. */
    private DatabaseHelperFirebase() {
    }

    //---------------------------------------------------------------------
    // Movie methods

    /**
     * Inserts a movie into the database.
     * @param movie the movie to insert
     */
    public void insertMovie(@NonNull Movie movie) {
        // TODO
    }

    /**
     * Updates a movie in the database.
     * @param movie a Movie object, where the imdbId is used to determine which movie is to be
     *              updated, and the remaining attributes are the new values to be set.
     * @return the number of rows updated
     */
    public int updateMovie(@NonNull Movie movie) {
        // TODO
        return 0;
    }

    /**
     * Deletes a movie from the database.
     * @param imdbId the imdbId of the movie to be deleted
     * @return the number of rows deleted
     */
    public int deleteMovie(@NonNull String imdbId) {
        // TODO
        return 0;
    }

    /**
     * Returns the movie with a specified IMDb id.
     * @param imdbId the imdbId of the movie to be returned
     * @return the movie with the specified IMDb id
     */
    @Override
    @Nullable
    public Movie selectMovieByImdbId(@NonNull String imdbId) {
        // TODO
        return null;
    }

    /**
     * Returns a list of all the movies in the database in the default order.
     * @return a list of all the movies in the database in the default order
     */
    @Override
    @Nullable
    public List<Movie> selectMovies() {
        return selectMovies(MOVIE_SORT_COLUMN_DEFAULT, MOVIE_SORT_ORDER_DEFAULT);
    }

    /**
     * Returns a list of all the movies in the database ordered by a specified column.
     * @param sortColumn the column to sort by
     * @param sortOrder the sort order, "ASC" or "DESC"
     * @return a list of all the movies in the database ordered by a specified column
     */
    @Override
    @Nullable
    public List<Movie> selectMovies(@NonNull String sortColumn, @NonNull String sortOrder) {
        // TODO
        return null;
    }

}
