package uk.jumpingmouse.moviecompanion.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.data.Award;
import uk.jumpingmouse.moviecompanion.data.Movie;

/**
 * Class giving access to a local copy of the database.
 * This class is used by all product flavours; it is only access to the master database
 * which is restricted.
 * @author Edmund Johnson
 */
public class LocalDatabaseInMemory implements LocalDatabase {
    /** The singleton instance of this class. */
    private static LocalDatabase sLocalDatabase = null;

    /** The list of movies. */
    private final List<Movie> mMovieList;
    /** The list of awards. */
    private final List<Award> mAwardList;

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
        mAwardList = new ArrayList<>();
    }

    //---------------------------------------------------------------------
    // Movie modification methods

    /**
     * Adds a movie's details to the database.
     * If the movie does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param movie the movie to insert or update
     */
    @Override
    public void addMovie(@NonNull Movie movie) {
        int id = movie.getId();
        // remove the movie if it already exists
        Movie existingMovie = selectMovieById(id);
        if (existingMovie != null) {
            mMovieList.remove(existingMovie);
        }
        // add the new movie
        mMovieList.add(movie);
    }

    /**
     * Deletes a movie from the database.
     * @param id the id of the movie to be deleted
     */
    @Override
    public void deleteMovie(int id) {
        Movie existingMovie = selectMovieById(id);
        if (existingMovie == null) {
            Timber.w("deleteMovie: Movie not found with id: " + id);
        } else {
            mMovieList.remove(existingMovie);
        }
    }

    //---------------------------------------------------------------------
    // Movie query methods

    /**
     * Returns the movie with a specified id.
     * @param id the movie's id
     * @return the movie with the specified id, or null if there is no matching movie
     */
    @Override
    @Nullable
    public Movie selectMovieById(int id) {
        for (Movie movie : mMovieList) {
            if (id == movie.getId()) {
                return movie;
            }
        }
        Timber.d("selectMovieById: No movies found with matching id: " + id);
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
    @NonNull
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
    @NonNull
    private List<Movie> selectMovies(@NonNull String sortColumn, boolean sortAscending) {
        Timber.d(String.format("selectMovies: sortColumn = %s, sortAscending = %b",
                sortColumn, sortAscending));

        Comparator<Movie> comparator;
        // code coverage: sortColumn cannot be null
        switch (sortColumn) {
            // A bit of a fiddle, but it works for now
            case DataContract.MovieEntry.COLUMN_ID:
                comparator = Movie.MovieComparatorImdbId;
                break;
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

    //---------------------------------------------------------------------
    // Award modification methods

    /**
     * Adds an award's details to the database.
     * If the award does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param award the award to insert or update
     */
    @Override
    public void addAward(@NonNull Award award) {
        String id = award.getId();
        if (id != null) {
            // remove the award if it already exists
            Award existingAward = selectAwardById(id);
            if (existingAward != null) {
                mAwardList.remove(existingAward);
            }
            // add the new award
            mAwardList.add(award);
        }
    }

    /**
     * Deletes an award from the database.
     * @param id the id of the award to be deleted
     */
    @Override
    public void deleteAward(@Nullable String id) {
        Award existingAward = selectAwardById(id);
        if (existingAward == null) {
            Timber.w("deleteAward: Award not found with id: " + id);
        } else {
            mAwardList.remove(existingAward);
        }
    }

    //---------------------------------------------------------------------
    // Award query methods

    /**
     * Returns the award with a specified id.
     * @param id the award's id
     * @return the award with the specified id, or null if there is no matching award
     */
    @Override
    @Nullable
    public Award selectAwardById(@Nullable String id) {
        if (id == null) {
            return null;
        }
        for (Award award : mAwardList) {
            if (id.equals(award.getId())) {
                return award;
            }
        }
        Timber.d("selectAwardById: No awards found with matching id: " + id);
        return null;
    }

    //---------------------------------------------------------------------
    // Utility methods

    /**
     * Returns the sort column from a sort order.
     * @param sortOrder the sort order, e.g. "title ASC"
     * @return the sort column, e.g. "title"
     */
    private static String getSortColumn(@Nullable String sortOrder) {
        if (sortOrder != null) {
            String[] split = sortOrder.split(" ");
            // split.length is always at least 1
            // code coverage: split[0] cannot be null
            switch (split[0]) {
                case DataContract.MovieEntry.COLUMN_ID:
                    return DataContract.MovieEntry.COLUMN_ID;
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
     * @param sortOrder the sort order, e.g. "title ASC"
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
