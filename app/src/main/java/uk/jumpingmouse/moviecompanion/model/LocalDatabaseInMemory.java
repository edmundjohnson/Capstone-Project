package uk.jumpingmouse.moviecompanion.model;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.data.Award;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.data.ViewAward;
import uk.jumpingmouse.moviecompanion.data.ViewMovie;

/**
 * Class giving access to a local copy of the database.
 * This class is used by all product flavours; it is only access to the master database
 * which is restricted.
 * @author Edmund Johnson
 */
public class LocalDatabaseInMemory implements LocalDatabase {
    /** The singleton instance of this class. */
    private static LocalDatabase sLocalDatabase = null;

    /** The map of movies. */
    private final Map<Integer, Movie> mMovieMap;
    /** The map of awards. */
    private final Map<String, Award> mAwardMap;

    /** The cursor for the view award list. It is assumed there is only ever one. */
    private Cursor mCursorViewAwards;

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
        mMovieMap = new HashMap<>();
        mAwardMap = new HashMap<>();
    }

    //---------------------------------------------------------------------
    // Movie modification methods

    /**
     * Adds a movie's details to the database.
     * If the movie does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param movie the movie to insert or update
     * @return the number of rows inserted or updated. This is currently always 1,
     * but that could change if the local database is implemented in SQLite.
     */
    @Override
    public int addMovie(@NonNull Movie movie) {
        int id = movie.getId();
        // remove the movie if it already exists
        Movie existingMovie = selectMovieById(id);
        if (existingMovie != null) {
            mMovieMap.remove(id);
        }
        // add the new movie
        mMovieMap.put(id, movie);

        return 1;
    }

    /**
     * Deletes a movie from the database.
     * @param id the id of the movie to be deleted
     * @return the number of rows deleted
     */
    @Override
    public int deleteMovie(int id) {
        Movie existingMovie = selectMovieById(id);
        if (existingMovie == null) {
            Timber.w("deleteMovie: Movie not found with id: " + id);
            return 0;
        } else {
            mMovieMap.remove(id);
            return 1;
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
        Movie movie = mMovieMap.get(id);
        if (movie == null) {
            Timber.d("selectMovieById: No movies found with matching id: " + id);
        }
        return movie;
    }

    /**
     * Returns a list of movies from the database.
     * @param projection The list of columns to put into the cursor.
     *                   If this is {@code null} all columns are included.
     * @param selection A selection criteria to apply when filtering rows.
     *                  If this is {@code null} then all rows are included.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in the order that they appear in the selection.
     *      The values will be bound as Strings.
     * @param sortOrder How the rows in the cursor should be sorted.
     *      If this is {@code null}, the sort order is undefined.
     * @return a list of movies from the database
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
        return selectMovies(getSortColumn(sortOrder, MOVIE_SORT_COLUMN_DEFAULT),
                isSortAscending(sortOrder, MOVIE_SORT_ASCENDING_DEFAULT));
    }

    /**
     * Returns a list of all the movies in the local database ordered by a specified column.
     * @param sortColumn the column to sort by
     * @param sortAscending whether the sort is ascending
     * @return a list of all the movies in the local database ordered by the specified column
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
        List<Movie> movieList = new ArrayList<>(mMovieMap.values());
        Collections.sort(movieList, comparator);

        return movieList;
    }

    //---------------------------------------------------------------------
    // Award modification methods

    /**
     * Adds an award's details to the database.
     * If the award does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param award the award to insert or update
     * @return the number of rows inserted or updated. This is currently always 1,
     * but that could change if the local database is implemented in SQLite.
     */
    @Override
    public int addAward(@NonNull Award award) {
        String id = award.getId();
        // remove the award if it already exists
        Award existingAward = selectAwardById(id);
        if (existingAward != null) {
            mAwardMap.remove(id);
        }
        // add the new award
        mAwardMap.put(id, award);
        return 1;
    }

    /**
     * Deletes an award from the database.
     * @param id the id of the award to be deleted
     * @return the number of rows deleted
     */
    @Override
    public int deleteAward(@Nullable String id) {
        Award existingAward = selectAwardById(id);
        if (existingAward == null) {
            Timber.w("deleteAward: Award not found with id: " + id);
            return 0;
        } else {
            mAwardMap.remove(id);
            return 1;
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
        Award award = mAwardMap.get(id);
        if (award == null) {
            Timber.d("selectAwardById: No awards found with matching id: " + id);
        }
        return award;
    }

    /**
     * Returns a list of awards from the database.
     * @param projection The list of columns to put into the cursor.
     *                   If this is {@code null} all columns are included.
     * @param selection A selection criteria to apply when filtering rows.
     *                  If this is {@code null} then all rows are included.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in the order that they appear in the selection.
     *      The values will be bound as Strings.
     * @param sortOrder How the rows in the cursor should be sorted.
     *      If this is {@code null}, the sort order is undefined.
     * @return a list of awards from the database
     */
    @Override
    @NonNull
    public List<Award> selectAwards(
            @Nullable final String[] projection, @Nullable final String selection,
            @Nullable final String[] selectionArgs, @Nullable final String sortOrder) {
        if (projection != null) {
            Timber.d("selectAwards: projection is currently not supported");
        }
        if (selection != null) {
            Timber.d("selectAwards: selection is currently not supported");
        }
        if (selectionArgs != null) {
            Timber.d("selectAwards: selectionArgs is currently not supported");
        }
        return selectAwards(getSortColumn(sortOrder, AWARD_SORT_COLUMN_DEFAULT),
                isSortAscending(sortOrder, AWARD_SORT_ASCENDING_DEFAULT));
    }

    /**
     * Returns a list of all the awards in the local database ordered by a specified column.
     * @param sortColumn the column to sort by
     * @param sortAscending whether the sort is ascending
     * @return a list of all the movies in the local database ordered by the specified column
     */
    @NonNull
    private List<Award> selectAwards(@NonNull String sortColumn, boolean sortAscending) {
        Timber.d(String.format("selectAwards: sortColumn = %s, sortAscending = %b",
                sortColumn, sortAscending));

        Comparator<Award> comparator;
        // code coverage: sortColumn cannot be null
        switch (sortColumn) {
            // A bit of a fiddle, but it works for now
            case DataContract.AwardEntry.COLUMN_MOVIE_ID:
                comparator = Award.AwardComparatorMovieId;
                break;
            case DataContract.AwardEntry.COLUMN_AWARD_DATE:
                comparator = Award.AwardComparatorAwardDate;
                break;
            // code coverage: default case cannot happen due to earlier checks
            default:
                comparator = Award.AwardComparatorAwardDate;
                break;
        }
        if (!sortAscending) {
            comparator = Collections.reverseOrder(comparator);
        }
        List<Award> awardList = new ArrayList<>(mAwardMap.values());
        Collections.sort(awardList, comparator);

        return awardList;
    }

    //---------------------------------------------------------------------
    // ViewMovie query methods

    /**
     * Returns the view movie with a specified id.
     * @param id the view movie's id
     * @return the view movie with the specified id, or null if there is no matching view movie
     */
    @Override
    @Nullable
    public ViewMovie selectViewMovieById(int id) {
        if (id == Movie.ID_UNKNOWN) {
            return null;
        }
        Movie movie = selectMovieById(id);
        if (movie == null) {
            Timber.d("selectViewMovieById: Movie not found with id: " + id);
            return null;
        }
        return new ViewMovie(movie);
    }

    //---------------------------------------------------------------------
    // ViewAward query methods

    /**
     * Returns the view award with a specified award id.
     * @param id the view award's id
     * @return the view award with the specified id, or null if there is no matching view award
     */
    @Override
    @Nullable
    public ViewAward selectViewAwardById(@Nullable String id) {
        if (id == null) {
            return null;
        }
        Award award = selectAwardById(id);
        if (award == null) {
            Timber.d("selectViewAwardById: Award not found with id: " + id);
            return null;
        }
        Movie movie = selectMovieById(award.getMovieId());
        if (movie == null) {
            Timber.d("selectViewAwardById: Movie not found with id: " + award.getMovieId());
            return null;
        }
        return new ViewAward(award, movie);
    }

    /**
     * Returns a list of view awards from the database.
     * @param projection The list of columns to put into the cursor.
     *                   If this is {@code null} all columns are included.
     * @param selection A selection criteria to apply when filtering rows.
     *                  If this is {@code null} then all rows are included.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in the order that they appear in the selection.
     *      The values will be bound as Strings.
     * @param sortOrder How the rows in the cursor should be sorted.
     *      If this is {@code null}, the sort order is undefined.
     * @return a cursor containing a list of view awards from the database
     */
    @Override
    @NonNull
    public Cursor selectViewAwards(
            @Nullable final String[] projection, @Nullable final String selection,
            @Nullable final String[] selectionArgs, @Nullable final String sortOrder) {
        if (projection != null) {
            Timber.d("selectViewAwards: projection is currently not supported");
        }
        if (selection != null) {
            Timber.d("selectViewAwards: selection is currently not supported");
        }
        if (selectionArgs != null) {
            Timber.d("selectViewAwards: selectionArgs is currently not supported");
        }

        String sortColumn = getSortColumn(sortOrder, VIEW_AWARD_SORT_COLUMN_DEFAULT);
        boolean sortAscending = isSortAscending(sortOrder, VIEW_AWARD_SORT_ASCENDING_DEFAULT);

        if (mCursorViewAwards != null) {
            mCursorViewAwards.close();
        }
        List<ViewAward> viewAwardList = selectViewAwards(sortColumn, sortAscending);
        mCursorViewAwards = toCursorViewAwards(viewAwardList);

        return mCursorViewAwards;
    }

    /**
     * Returns a list of all the view awards in the local database ordered by a specified column.
     * @param sortColumn the column to sort by
     * @param sortAscending whether the sort is ascending
     * @return a cursor containing a list of all the ViewAwards in the local database
     *         ordered by the specified column
     */
    @NonNull
    private List<ViewAward> selectViewAwards(@NonNull String sortColumn, boolean sortAscending) {
        Timber.d(String.format("selectViewAwards: sortColumn = %s, sortAscending = %b",
                sortColumn, sortAscending));

        List<ViewAward> viewAwardList = new ArrayList<>();

        for (Award award : mAwardMap.values()) {
            Movie movie = selectMovieById(award.getMovieId());
            if (movie != null) {
                ViewAward viewAward = new ViewAward(award, movie);
                viewAwardList.add(viewAward);
            }
        }

        Comparator<ViewAward> comparator;
        // code coverage: sortColumn cannot be null
        switch (sortColumn) {
            // A bit of a fiddle, but it works for now
            case DataContract.ViewAwardEntry.COLUMN_MOVIE_ID:
                comparator = ViewAward.ViewAwardComparatorMovieId;
                break;
            case DataContract.ViewAwardEntry.COLUMN_AWARD_DATE:
                comparator = ViewAward.ViewAwardComparatorAwardDate;
                break;
            // code coverage: default case cannot happen due to earlier checks
            default:
                comparator = ViewAward.ViewAwardComparatorAwardDate;
                break;
        }
        if (!sortAscending) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(viewAwardList, comparator);

        return viewAwardList;
    }

    /**
     * Returns a multi-row cursor containing a list of view awards.
     * @param viewAwards the list of view awards, correctly ordered for the cursor
     * @return a multi-row cursor containing the list of view awards
     */
    @NonNull
    private Cursor toCursorViewAwards(@NonNull List<ViewAward> viewAwards) {

        // Create a cursor containing the view award columns
        String[] columns = DataContract.ViewAwardEntry.getAllColumns();
        MatrixCursor cursor = new MatrixCursor(columns);

        // populate the cursor with the view awards
        for (ViewAward viewAward : viewAwards) {
            cursor.addRow(viewAward.toObjectArray());
        }

        return cursor;
    }

    //---------------------------------------------------------------------
    // Utility methods

    /**
     * Returns the sort column from a sort order.
     * @param sortOrder the sort order, e.g. "title ASC"
     * @param sortColumnDefault the sort column to use if one cannot be determined
     *                          from the sortOrder
     * @return the sort column, e.g. "title"
     */
    private static String getSortColumn(@Nullable String sortOrder,
                                        @NonNull String sortColumnDefault) {
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
                case DataContract.ViewAwardEntry.COLUMN_AWARD_DATE:
                    return DataContract.ViewAwardEntry.COLUMN_AWARD_DATE;
                default:
                    return sortColumnDefault;
            }
        }
        return sortColumnDefault;
    }

    /**
     * Returns whether the sort column is ascending from a sort order.
     * @param sortOrder the sort order, e.g. "title ASC"
     * @param sortAscendingDefault the sort ascending value to use if one cannot be determined
     *                             from sortOrder
     * @return whether the sort column is ascending
     */
    private static boolean isSortAscending(@Nullable String sortOrder,
                                           boolean sortAscendingDefault) {
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
                        return sortAscendingDefault;
                }
            }
        }
        return sortAscendingDefault;
    }

    //---------------------------------------------------------------------
    // Getters and setters

}
