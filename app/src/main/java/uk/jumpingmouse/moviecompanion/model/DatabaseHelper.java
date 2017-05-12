package uk.jumpingmouse.moviecompanion.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uk.jumpingmouse.moviecompanion.data.Movie;

import java.util.List;

/**
 * Interface for database helper classes.
 * @author Edmund Johnson
 */
public interface DatabaseHelper {

    String MOVIE_SORT_COLUMN_DEFAULT = DataContract.MovieEntry.COLUMN_TITLE;
    String MOVIE_SORT_ORDER_DEFAULT = DataContract.ORDER_ASC;

    //---------------------------------------------------------------------
    // Movie methods

    /**
     * Inserts a movie into the database.
     * @param movie the movie to insert
     */
    void insertMovie(@NonNull Movie movie);

    /**
     * Updates a movie in the database.
     * @param movie a Movie object, where the imdbId is used to determine which movie is to be
     *              updated, and the remaining attributes are the new values to be set.
     * @return the number of rows updated
     */
    int updateMovie(@NonNull Movie movie);

    /**
     * Deletes a movie from the database.
     * @param imdbId the imdbId of the movie to be deleted
     * @return the number of rows deleted
     */
    int deleteMovie(@NonNull String imdbId);

    /**
     * Returns the movie with a specified IMDb id.
     * @param imdbId the imdbId of the movie to be returned
     * @return the movie with the specified IMDb id
     */
    @Nullable
    Movie selectMovieByImdbId(@NonNull String imdbId);

    /**
     * Returns a list of all the movies in the database in the default order.
     * @return a list of all the movies in the database in the default order
     */
    @Nullable
    List<Movie> selectMovies();

    /**
     * Returns a list of all the movies in the database ordered by a specified column.
     * @param sortColumn the column to sort by
     * @param sortOrder the sort order, "ASC" or "DESC"
     * @return a list of all the movies in the database ordered by a specified column
     */
    @Nullable
    List<Movie> selectMovies(@NonNull String sortColumn, @NonNull String sortOrder);

}
