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
 * A class containing a local copy of the database.
 * @author Edmund Johnson
 */
public class LocalDatabaseInMemory implements LocalDatabase {
    /** The singleton instance of this class. */
    private static LocalDatabase sLocalDatabase = null;

    /** The list of movies. */
    private final List<Movie> mMovieList;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    public static LocalDatabase getInstance() {
        if (sLocalDatabase == null) {
            sLocalDatabase = new LocalDatabaseInMemory();
        }
        return sLocalDatabase;
    }

    /** Private default constructor to prevent instantiation from outside this class. */
    private LocalDatabaseInMemory() {
        mMovieList = new ArrayList<>();
    }

    //---------------------------------------------------------------------
    // Movie handling methods

    /**
     * Adds a movie's details to the database.
     * If the movie does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param movie the movie to insert or update
     * @return the number of rows inserted or updated
     */
    @Override
    public int addMovie(@NonNull Movie movie) {
        String imdbId = movie.getImdbId();
        // This cannot happen due to previous checks, but the compiler requires it
        if (imdbId == null) {
            Timber.w("addMovie: Cannot add a movie whose getImdbId is null");
            return 0;
        }
        // remove the movie if it already exists
        Movie existingMovie = selectMovieByImdbId(imdbId);
        if (existingMovie != null) {
            mMovieList.remove(existingMovie);
        }
        // add the new movie
        mMovieList.add(movie);
        return 1;
    }

    @Override
    public int deleteMovie(@NonNull String imdbId) {
        Movie existingMovie = selectMovieByImdbId(imdbId);
        if (existingMovie == null) {
            Timber.w("deleteMovie: Movie not found with getImdbId: " + imdbId);
            return 0;
        }
        mMovieList.remove(existingMovie);
        return 1;
    }

    /**
     * Returns the movie with a specified getImdbId.
     * @param imdbId the getImdbId
     * @return the movie with the specified getImdbId, or null if there are no matching movies
     */
    @Override
    @Nullable
    public Movie selectMovieByImdbId(@NonNull String imdbId) {
        for (Movie movie : mMovieList) {
            if (imdbId.equals(movie.getImdbId())) {
                return movie;
            }
        }
        Timber.d("selectMovieByImdbId: No movies found with matching getImdbId");
        return null;
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
     * Returns a list of all the movies in the mock database ordered by a specified column.
     * @param sortColumn the column to sort by
     * @param sortAscending whether the sort is ascending
     * @return a list of all the movies in the mock database ordered by the specified column
     */
    @Nullable
    private List<Movie> selectMovies(@NonNull String sortColumn, boolean sortAscending) {
        Timber.d(String.format("selectMovies: sortColumn = %s, sortAscending = %b",
                sortColumn, sortAscending));

        Comparator<Movie> comparator;
        // code coverage: sortColumn cannot be null
        switch (sortColumn) {
            case DataContract.MovieEntry.COLUMN_IMDB_ID:
                comparator = Movie.MovieComparatorImdbId;
                break;
            case DataContract.MovieEntry.COLUMN_TITLE:
                comparator = Movie.MovieComparatorTitle;
                break;
            // code coverage: default case cannot happen due to earlier checks
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

    /**
     * Returns the sort column from a sort order.
     * @param sortOrder the sort order, e.g. "getTitle ASC"
     * @return the sort column, e.g. "getTitle"
     */
    private static String getSortColumn(@Nullable String sortOrder) {
        if (sortOrder != null) {
            String[] split = sortOrder.split(" ");
            // split.length is always at least 1
            // code coverage: split[0] cannot be null
            switch (split[0]) {
                case DataContract.MovieEntry.COLUMN_IMDB_ID:
                    return DataContract.MovieEntry.COLUMN_IMDB_ID;
                case DataContract.MovieEntry.COLUMN_TITLE:
                    return DataContract.MovieEntry.COLUMN_TITLE;
                default:
                    return MOVIE_SORT_COLUMN_DEFAULT;
            }
        }
        return MOVIE_SORT_COLUMN_DEFAULT;
    }

    /**
     * Returns whether the sort column is ascending from a sort order.
     * @param sortOrder the sort order, e.g. "getTitle ASC"
     * @return whether the sort column is ascending
     */
    private static boolean isSortAscending(@Nullable String sortOrder) {
        if (sortOrder != null) {
            String[] split = sortOrder.split(" ");
            if (split.length > 1) {
                // code coverage: split[1] cannot be null
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

}
