package uk.jumpingmouse.moviecompanion.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;

import uk.jumpingmouse.moviecompanion.data.Movie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Database helper class for accessing a mock database.
 * @author Edmund Johnson
 */
class DatabaseHelperMock implements DatabaseHelper {

    /** The singleton instance of this class. */
    private static DatabaseHelperMock sDatabaseHelper = null;

    private final MockDatabase sMockDatabase = new MockDatabase();

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    static DatabaseHelperMock getInstance() {
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
        sMockDatabase.insertMovie(movie);
    }

    /**
     * Updates a movie in the database.
     * @param movie a Movie object, where the imdbId is used to determine which movie is to be
     *              updated, and the remaining attributes are the new values to be set.
     * @return the number of rows updated
     */
    public int updateMovie(@NonNull Movie movie) {
        return sMockDatabase.updateMovie(movie);
    }

    /**
     * Deletes a movie from the database.
     * @param imdbId the imdbId of the movie to be deleted
     * @return the number of rows deleted
     */
    public int deleteMovie(@NonNull String imdbId) {
        return sMockDatabase.deleteMovie(imdbId);
    }

    /**
     * Returns the movie with a specified IMDb id.
     * @param imdbId the imdbId of the movie to be returned
     * @return the movie with the specified IMDb id
     */
    @Override
    @Nullable
    public Movie selectMovieByImdbId(@NonNull String imdbId) {
        return sMockDatabase.selectMovieByImdbId(imdbId);
    }

    /**
     * Returns a list of all the movies in the database in the default order.
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
        if (projection != null) {
            Timber.d("selectMovies: projection is currently not supported");
        }
        if (selection != null) {
            Timber.d("selectMovies: selection is currently not supported");
        }
        if (selectionArgs != null) {
            Timber.d("selectMovies: selectionArgs is currently not supported");
        }
        return selectMovies(getSortColumn(sortOrder), isSortAscending(sortOrder));
    }

    /**
     * Returns a list of all the movies in the database ordered by a specified column.
     * @param sortColumn the column to sort by
     * @param sortAscending whether the sort is ascending
     * @return a list of all the movies in the database ordered by a specified column
     */
    @Nullable
    private List<Movie> selectMovies(@NonNull String sortColumn, boolean sortAscending) {
        return sMockDatabase.selectMovies(sortColumn, sortAscending);
    }

    /**
     * Returns the sort column from a sort order.
     * @param sortOrder the sort order, e.g. "title ASC"
     * @return the sort column, e.g. "title"
     */
    private static String getSortColumn(@Nullable String sortOrder) {
        if (sortOrder != null) {
            String[] split = sortOrder.split(" ");
            if (split.length > 0) {
                switch (split[0]) {
                    case DataContract.MovieEntry.COLUMN_IMDB_ID:
                        return DataContract.MovieEntry.COLUMN_IMDB_ID;
                    case DataContract.MovieEntry.COLUMN_TITLE:
                        return DataContract.MovieEntry.COLUMN_TITLE;
                    default:
                        return MOVIE_SORT_COLUMN_DEFAULT;
                }
            }
        }
        return MOVIE_SORT_COLUMN_DEFAULT;
    }

    /**
     * Returns whether the sort column is ascending from a sort order.
     * @param sortOrder the sort order, e.g. "title ASC"
     * @return whether the sort column is ascending
     */
    private static boolean isSortAscending(@Nullable String sortOrder) {
        if (sortOrder != null) {
            String[] split = sortOrder.split(" ");
            if (split.length > 1) {
                switch (split[1]) {
                    case DataContract.SORT_DIRECTION_ASC:
                        return true;
                    case DataContract.SORT_DIRECTION_DESC:
                        return false;
                    default:
                        return MOVIE_SORT_ASCENDING_DEFAULT;
                }
            }
        }
        return MOVIE_SORT_ASCENDING_DEFAULT;
    }

    /**
     * A class representing a mock database.
     */
    private class MockDatabase {
        final List<Movie> mMovieList;

        MockDatabase() {
            mMovieList = new ArrayList<>();
        }

        void insertMovie(@NonNull Movie movie) {
            mMovieList.add(movie);
        }

        int updateMovie(@NonNull Movie movie) {
            String imdbId = movie.imdbId();
            // This cannot happen due to previous checks, but the compiler requires it
            if (imdbId == null) {
                Timber.w("updateMovie: Cannot update movie whose imdbId is null");
                return 0;
            }
            Movie existingMovie = selectMovieByImdbId(imdbId);
            if (existingMovie == null) {
                Timber.w("updateMovie: Movie not found with imdbId: " + imdbId);
                return 0;
            }
            // Movie is immutable, so delete the existing one and add the new one
            mMovieList.remove(existingMovie);
            mMovieList.add(movie);
            return 1;
        }

        int deleteMovie(@NonNull String imdbId) {
            Movie existingMovie = selectMovieByImdbId(imdbId);
            if (existingMovie == null) {
                Timber.w("deleteMovie: Movie not found with imdbId: " + imdbId);
                return 0;
            }
            mMovieList.remove(existingMovie);
            return 1;
        }

        /**
         * Returns the movie with a specified imdbId.
         * @param imdbId the imdbId
         * @return the movie with the specified imdbId, or null if there are no matching movies
         */
        @Nullable
        Movie selectMovieByImdbId(@NonNull String imdbId) {
            for (Movie movie : mMovieList) {
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
         * @param sortAscending whether the sort is ascending
         * @return a list of all the movies in the mock database ordered by the specified column
         */
        @Nullable
        List<Movie> selectMovies(@NonNull String sortColumn, boolean sortAscending) {
            Timber.d(String.format("selectMovies: sortColumn = %s, sortAscending = %b",
                    sortColumn, sortAscending));

            Comparator<Movie> comparator;
            switch (sortColumn) {
                case DataContract.MovieEntry.COLUMN_IMDB_ID:
                    comparator = Movie.MovieComparatorImdbId;
                    break;
                case DataContract.MovieEntry.COLUMN_TITLE:
                    comparator = Movie.MovieComparatorTitle;
                    break;
                // default case should never happen
                default:
                    comparator = Movie.MovieComparatorTitle;
                    break;
            }
            if (!sortAscending) {
                comparator = Collections.reverseOrder(comparator);
            }
            Collections.sort(mMovieList, comparator);
            return mMovieList;
        }

    }

}
