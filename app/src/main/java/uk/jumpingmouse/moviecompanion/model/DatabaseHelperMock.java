package uk.jumpingmouse.moviecompanion.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;

import uk.jumpingmouse.moviecompanion.data.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for the Firebase Realtime Database.
 * @author Edmund Johnson
 */
public class DatabaseHelperMock implements DatabaseHelper {

    /** The singleton instance of this class. */
    private static DatabaseHelperMock sDatabaseHelper = null;

    private static final MockDatabase mockDatabase = new MockDatabase();

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    public static DatabaseHelperMock getInstance() {
        if (sDatabaseHelper == null) {
            sDatabaseHelper = new DatabaseHelperMock();
        }
        return sDatabaseHelper;
    }

    /** Private default constructor to prevent instantiation from outside this class. */
    private DatabaseHelperMock() {
    }

    //---------------------------------------------------------------------
    // Movie methods

    /**
     * Inserts a movie into the database.
     * @param movie the movie to insert
     */
    public void insertMovie(@NonNull Movie movie) {
        mockDatabase.insertMovie(movie);
    }

    /**
     * Updates a movie in the database.
     * @param movie a Movie object, where the imdbId is used to determine which movie is to be
     *              updated, and the remaining attributes are the new values to be set.
     * @return the number of rows updated
     */
    public int updateMovie(@NonNull Movie movie) {
        return mockDatabase.updateMovie(movie);
    }

    /**
     * Deletes a movie from the database.
     * @param imdbId the imdbId of the movie to be deleted
     * @return the number of rows deleted
     */
    public int deleteMovie(@NonNull String imdbId) {
        return mockDatabase.deleteMovie(imdbId);
    }

    /**
     * Returns the movie with a specified IMDb id.
     * @param imdbId the imdbId of the movie to be returned
     * @return the movie with the specified IMDb id
     */
    @Override
    @Nullable
    public Movie selectMovieByImdbId(@NonNull String imdbId) {
        return mockDatabase.selectMovieByImdbId(imdbId);
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
        return mockDatabase.selectMovies(sortColumn, sortOrder);
    }

    /**
     * A class representing a mock database.
     */
    private static class MockDatabase {
        List<Movie> movieList;

        MockDatabase() {
            movieList = new ArrayList<>();
        }

        void insertMovie(@Nullable Movie movie) {
            if (movie == null) {
                Timber.w("insertMovie: method called with null movie");
                return;
            }
            movieList.add(movie);
        }

        int updateMovie(@Nullable Movie movie) {
            if (movie == null) {
                Timber.w("updateMovie: method called with null movie");
                return 0;
            }
            Movie existingMovie = selectMovieByImdbId(movie.imdbId());
            if (existingMovie == null) {
                Timber.w("updateMovie: Movie not found with imdbId: " + movie.imdbId());
                return 0;
            }
            // Movie is immutable, so delete the existing one and add the new one
            movieList.remove(existingMovie);
            movieList.add(movie);
            return 1;
        }

        int deleteMovie(@Nullable String imdbId) {
            if (imdbId == null) {
                Timber.w("deleteMovie: method called with null imdbId");
                return 0;
            }
            Movie existingMovie = selectMovieByImdbId(imdbId);
            if (existingMovie == null) {
                Timber.w("deleteMovie: Movie not found with imdbId: " + imdbId);
                return 0;
            }
            movieList.remove(existingMovie);
            return 1;
        }

        /**
         * Returns the movie with a specified imdbId.
         * @param imdbId the imdbId
         * @return the movie with the specified imdbId, or null if there are no matching movies
         */
        @Nullable
        Movie selectMovieByImdbId(@Nullable String imdbId) {
            if (imdbId == null) {
                Timber.w("selectMovieByImdbId: method called with null imdbId");
                return null;
            }
            for (Movie movie : movieList) {
                if (imdbId.equals(movie.imdbId())) {
                    return movie;
                }
            }
            Timber.d("selectMovieByImdbId: No movies found with matching imdbId");
            return null;
        }

        /**
         * Returns a list of all the movies in the mock database ordered by a specified column.
         * @param sortColumn the column to sort by
         * @param sortOrder the sort order, "ASC" or "DESC"
         * @return a list of all the movies in the mock database ordered by a specified column
         */
        @Nullable
        List<Movie> selectMovies(@NonNull String sortColumn, @NonNull String sortOrder) {
            Timber.d(String.format("selectMovies: sortColumn = %s, sortOrder = %s", sortColumn, sortOrder));
            // TODO implement sorting
            return movieList;
        }

    }

}
