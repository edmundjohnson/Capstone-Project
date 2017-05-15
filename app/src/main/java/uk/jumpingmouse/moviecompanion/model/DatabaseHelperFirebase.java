package uk.jumpingmouse.moviecompanion.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;

import uk.jumpingmouse.moviecompanion.data.Movie;

import java.util.List;

/**
 * Database helper class for accessing the Firebase Realtime Database.
 * @author Edmund Johnson
 */
class DatabaseHelperFirebase implements DatabaseHelper {

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
        // TODO
        return null;
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

}
