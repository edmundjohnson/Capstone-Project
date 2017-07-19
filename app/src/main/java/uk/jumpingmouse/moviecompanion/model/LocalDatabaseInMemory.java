package uk.jumpingmouse.moviecompanion.model;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import timber.log.Timber;

import uk.jumpingmouse.moviecompanion.data.Award;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.data.UserMovie;
import uk.jumpingmouse.moviecompanion.data.ViewAward;
import uk.jumpingmouse.moviecompanion.utils.JavaUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class giving access to a local copy of the database.
 * This class is used by all product flavours; it is only access to the master database
 * which is restricted.
 * @author Edmund Johnson
 */
public class LocalDatabaseInMemory implements LocalDatabase {
    /** The singleton instance of this class. */
    private static LocalDatabase sLocalDatabase = null;

    private static final int ARG_INDEX_FILTER_CATEGORY = 0;
    private static final int ARG_INDEX_FILTER_GENRE = ARG_INDEX_FILTER_CATEGORY + 1;
    private static final int ARG_INDEX_FILTER_WISHLIST = ARG_INDEX_FILTER_GENRE + 1;
    private static final int ARG_INDEX_FILTER_WATCHED = ARG_INDEX_FILTER_WISHLIST + 1;
    private static final int ARG_INDEX_FILTER_FAVOURITE = ARG_INDEX_FILTER_WATCHED + 1;
    private static final int ARG_INDEX_LIMIT = ARG_INDEX_FILTER_FAVOURITE + 1;

    /** The movies. */
    private final SparseArray<Movie> mMovies;
    /** The awards. */
    private final Map<String, Award> mAwards;
    /** The user movies. */
    private final SparseArray<UserMovie> mUserMovies;

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
        mMovies = new SparseArray<>();
        mAwards = new HashMap<>();
        mUserMovies = new SparseArray<>();
    }

    //---------------------------------------------------------------------
    // Movie modification methods

    /**
     * Adds a movie's details to the database.
     * If the movie does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param movie the movie to insert or update
     * @return the number of rows inserted or updated. This is currently always 1,
     *     but that could change if the local database is implemented in SQLite.
     */
    @Override
    public int addMovie(@NonNull Movie movie) {
        int id = movie.getId();
        // remove the movie if it already exists
        Movie existingMovie = selectMovieById(id);
        if (existingMovie != null) {
            mMovies.remove(id);
        }
        // add the new movie
        mMovies.put(id, movie);

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
            mMovies.remove(id);
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
        Movie movie = mMovies.get(id);
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
                comparator = Movie.MOVIE_COMPARATOR_IMDB_ID;
                break;
            case DataContract.MovieEntry.COLUMN_IMDB_ID:
                comparator = Movie.MOVIE_COMPARATOR_IMDB_ID;
                break;
            case DataContract.MovieEntry.COLUMN_TITLE:
                comparator = Movie.MOVIE_COMPARATOR_TITLE;
                break;
            // code coverage: default case cannot happen due to earlier checks
            default:
                comparator = Movie.MOVIE_COMPARATOR_TITLE;
                break;
        }
        if (!sortAscending) {
            comparator = Collections.reverseOrder(comparator);
        }
        return JavaUtils.asSortedList(mMovies, comparator);
    }

    //---------------------------------------------------------------------
    // Award modification methods

    /**
     * Adds an award's details to the database.
     * If the award does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param award the award to insert or update
     * @return the number of rows inserted or updated. This is currently always 1,
     *     but that could change if the local database is implemented in SQLite.
     */
    @Override
    public int addAward(@NonNull Award award) {
        String id = award.getId();
        // remove the award if it already exists
        Award existingAward = selectAwardById(id);
        if (existingAward != null) {
            mAwards.remove(id);
        }
        // add the new award
        mAwards.put(id, award);
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
            mAwards.remove(id);
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
        Award award = mAwards.get(id);
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
        List<Award> awardList = new ArrayList<>(mAwards.values());
        // always use the default order for now
        awardList = sortAwardList(awardList,
                getSortColumn(sortOrder, AWARD_SORT_COLUMN_DEFAULT),
                isSortAscending(sortOrder, AWARD_SORT_ASCENDING_DEFAULT));
        return awardList;
    }

    /**
     * Sorts a list of awards and returns the sorted award list.
     * @param sortColumn the column to sort by
     * @param sortAscending whether the sort is ascending
     * @return the award list sorted by the specified column and direction
     */
    @NonNull
    private List<Award> sortAwardList(@Nullable List<Award> awardList,
            @NonNull String sortColumn, boolean sortAscending) {
        Timber.d(String.format("selectAwards: sortColumn = %s, sortAscending = %b",
                sortColumn, sortAscending));
        if (awardList == null) {
            return Collections.emptyList();
        }
        Comparator<Award> comparator;
        // code coverage: sortColumn cannot be null
        switch (sortColumn) {
            // A bit of a fiddle, but it works for now
            case DataContract.AwardEntry.COLUMN_MOVIE_ID:
                comparator = Award.AWARD_COMPARATOR_MOVIE_ID;
                break;
            case DataContract.AwardEntry.COLUMN_AWARD_DATE:
                comparator = Award.AWARD_COMPARATOR_AWARD_DATE;
                break;
            // code coverage: default case cannot happen due to earlier checks
            default:
                comparator = Award.AWARD_COMPARATOR_AWARD_DATE;
                break;
        }
        if (!sortAscending) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(awardList, comparator);

        return awardList;
    }

    //---------------------------------------------------------------------
    // UserMovie modification methods

    /**
     * Adds a user movie's details to the database.
     * If the user movie does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param userMovie the user movie to insert or update
     * @return the number of rows inserted or updated. This is currently always 1,
     *     but that could change if the local database is implemented in SQLite.
     */
    @Override
    public int addUserMovie(@NonNull UserMovie userMovie) {
        int id = userMovie.getId();
        // remove the user movie if it already exists
        UserMovie existingUserMovie = selectUserMovieById(id);
        if (existingUserMovie != null) {
            mUserMovies.remove(id);
        }
        // add the new user movie
        mUserMovies.put(id, userMovie);

        return 1;
    }

    /**
     * Deletes all of the signed-in user's user movies from the database.
     * @return the number of rows deleted
     */
    @Override
    public int deleteUserMoviesAll() {
        int rowsDeleted = mUserMovies.size();
        mUserMovies.clear();
        return rowsDeleted;
    }

    /**
     * Deletes a user movie from the database.
     * @param id the id of the user movie to be deleted
     * @return the number of rows deleted
     */
    @Override
    public int deleteUserMovie(int id) {
        UserMovie existingUserMovie = selectUserMovieById(id);
        if (existingUserMovie == null) {
            Timber.w("deleteUserMovie: UserMovie not found with id: " + id);
            return 0;
        } else {
            mUserMovies.remove(id);
            return 1;
        }
    }

    //---------------------------------------------------------------------
    // UserMovie query methods

    /**
     * Returns the movie with a specified movie id.
     * @param id the movie's id
     * @return the user movie with the specified id, or null if there is no matching user movie
     */
    @Override
    @Nullable
    public UserMovie selectUserMovieById(int id) {
        //UserMovie userMovie = mUserMovies.get(id);
        // There may not yet be a UserMovie for this movie
        //if (userMovie == null) {
        //    Timber.d("selectUserMovieById: No user movies found with matching id: " + id);
        //}
        return mUserMovies.get(id);
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
        UserMovie userMovie = selectUserMovieById(award.getMovieId());
        // There may not yet be a userMovie, it is OK to pass it to ViewAward as null
        return new ViewAward(award, movie, userMovie);
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

        // Generate an unsorted, unfiltered ViewAward list from the list of awards.
//        // We use awardList rather than mAwards.values() in the call to generateViewAwardList(...)
//        // because using mAwards.values() can lead to a ConcurrentModificationException.
//        List<Award> awardList = new ArrayList<>(mAwards.values());
//        List<ViewAward> viewAwardList = generateViewAwardList(awardList);
        List<ViewAward> viewAwardList = generateViewAwardList(mAwards.values());

        // Filter the ViewAward list
        viewAwardList = applyFilterToViewAwardList(viewAwardList, selection, selectionArgs);

        // Sort the ViewAward list
        viewAwardList = applySortToViewAwardList(viewAwardList, sortOrder);

        // Apply limit to the ViewAward list
        viewAwardList = applyLimitToViewAwardList(viewAwardList, selection, selectionArgs);

        if (mCursorViewAwards != null) {
            mCursorViewAwards.close();
        }
        mCursorViewAwards = toCursorViewAwards(viewAwardList);
        return mCursorViewAwards;
    }

    /**
     * Generates and returns a ViewAward list corresponding to an Award list.
     * @param awardList the award list
     * @return a list of ViewAwards corresponding to awardList
     */
    private List<ViewAward> generateViewAwardList(Collection<Award> awardList) {
        List<ViewAward> viewAwardList = new ArrayList<>();

        for (Award award : awardList) {
            Movie movie = selectMovieById(award.getMovieId());
            if (movie != null) {
                UserMovie userMovie = selectUserMovieById(award.getMovieId());
                ViewAward viewAward = new ViewAward(award, movie, userMovie);
                viewAwardList.add(viewAward);
            }
        }
        return viewAwardList;
    }

    /**
     * Returns a filtered list of view awards.
     * @param viewAwardList a list of view awards to be filtered
     * @param selection The selection criteria to apply when filtering rows.
     *                  If this is {@code null} then all rows are included.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in the order that they appear in the selection.
     *      The values will be bound as Strings.
     * @return the list of ViewAwards, filtered according to the selection and selectionArgs
     */
    @NonNull
    private List<ViewAward> applyFilterToViewAwardList(@NonNull List<ViewAward> viewAwardList,
               @Nullable final String selection, @Nullable final String[] selectionArgs) {
        //Timber.d(String.format("applyFilterToViewAwardList: selection = %s, selectionArgs = %s",
        //        selection, selectionArgs.toString()));

        if (selection == null || selectionArgs == null) {
            return viewAwardList;
        }

        List<ViewAward> filteredViewAwardList = new ArrayList<>();
        for (ViewAward viewAward : viewAwardList) {
            if (isIncludedByFilter(viewAward, selectionArgs)) {
                filteredViewAwardList.add(viewAward);
            }
        }

        return filteredViewAwardList;
    }

    /**
     * Returns whether a view award is included after filtering
     * @param viewAward the view award
     * @param selectionArgs the selectionArgs,
     *             e.g. { "filter_wishlist_any", "filter_watched_show", "filter_favourite_hide" }
     * @return true if the view award is included after filtering, false otherwise
     */
    private boolean isIncludedByFilter(@NonNull ViewAward viewAward,
                                       @NonNull final String[] selectionArgs) {
        // category filter
        if (selectionArgs.length > ARG_INDEX_FILTER_CATEGORY) {
            if (!isIncludedByFilterCategory(viewAward, selectionArgs[ARG_INDEX_FILTER_CATEGORY])) {
                return false;
            }
        }
        // genre filter
        if (selectionArgs.length > ARG_INDEX_FILTER_GENRE) {
            if (!isIncludedByFilterGenre(viewAward, selectionArgs[ARG_INDEX_FILTER_GENRE])) {
                return false;
            }
        }
        // wishlist filter
        if (selectionArgs.length > ARG_INDEX_FILTER_WISHLIST) {
            if (!isIncludedByFilterWishlist(viewAward, selectionArgs[ARG_INDEX_FILTER_WISHLIST])) {
                return false;
            }
        }
        // watched filter
        if (selectionArgs.length > ARG_INDEX_FILTER_WATCHED) {
            if (!isIncludedByFilterWatched(viewAward, selectionArgs[ARG_INDEX_FILTER_WATCHED])) {
                return false;
            }
        }
        // favourite filter
        if (selectionArgs.length > ARG_INDEX_FILTER_FAVOURITE) {
            if (!isIncludedByFilterFavourite(viewAward, selectionArgs[ARG_INDEX_FILTER_FAVOURITE])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether a ViewAward is allowed through the category filter.
     * @param viewAward the ViewAward
     * @param filterValue the value of the category filter, e.g. "filter_category_movie"
     * @return true if the ViewAward is allowed through the filter, false otherwise
     */
    private boolean isIncludedByFilterCategory(
            @NonNull ViewAward viewAward, @NonNull String filterValue) {
        boolean isIncluded = true;
        switch (filterValue) {
            case DataContract.ViewAwardEntry.FILTER_CATEGORY_ANY:
                break;
            case DataContract.ViewAwardEntry.FILTER_CATEGORY_MOVIE:
                isIncluded = Award.CATEGORY_MOVIE.equals(viewAward.getCategory());
                break;
            case DataContract.ViewAwardEntry.FILTER_CATEGORY_DVD:
                isIncluded = Award.CATEGORY_DVD.equals(viewAward.getCategory());
                break;
            default:
                break;
        }
        return isIncluded;
    }

    /**
     * Returns whether a ViewAward is allowed through when filtered by genre.
     * @param viewAward the ViewAward
     * @param keyGenre the genre key being used as a filter, e.g. "filter_genre_comedy"
     * @return true if the ViewAward is allowed through the filter, false otherwise
     */
    private boolean isIncludedByFilterGenre(@NonNull ViewAward viewAward, @NonNull String keyGenre) {
        if (keyGenre.equals(DataContract.ViewAwardEntry.FILTER_GENRE_ALL)) {
            return true;
        }
        String filterGenreStored = DataContract.ViewAwardEntry.getGenreStoredForGenreKey(keyGenre);
        //noinspection SimplifiableIfStatement
        if (filterGenreStored == null) {
            return true;
        }
        return viewAward.getGenre() != null
                && viewAward.getGenre().contains(filterGenreStored);
    }

    /**
     * Returns whether a ViewAward is allowed through the wishlist filter.
     * @param viewAward the ViewAward
     * @param filterValue the value of the wishlist filter, e.g. "filter_wishlist_show"
     * @return true if the ViewAward is allowed through the filter, false otherwise
     */
    private boolean isIncludedByFilterWishlist(
            @NonNull ViewAward viewAward, @NonNull String filterValue) {
        boolean isIncluded = true;
        switch (filterValue) {
            case DataContract.ViewAwardEntry.FILTER_WISHLIST_ANY:
                break;
            case DataContract.ViewAwardEntry.FILTER_WISHLIST_SHOW:
                isIncluded = viewAward.isOnWishlist();
                break;
            case DataContract.ViewAwardEntry.FILTER_WISHLIST_HIDE:
                isIncluded = !viewAward.isOnWishlist();
                break;
            default:
                break;
        }
        return isIncluded;
    }

    /**
     * Returns whether a ViewAward is allowed through the watched filter.
     * @param viewAward the ViewAward
     * @param filterValue the value of the watched filter, e.g. "filter_watched_show"
     * @return true if the ViewAward is allowed through the filter, false otherwise
     */
    private boolean isIncludedByFilterWatched(
            @NonNull ViewAward viewAward, @NonNull String filterValue) {
        boolean isIncluded = true;
        switch (filterValue) {
            case DataContract.ViewAwardEntry.FILTER_WATCHED_ANY:
                break;
            case DataContract.ViewAwardEntry.FILTER_WATCHED_SHOW:
                isIncluded = viewAward.isWatched();
                break;
            case DataContract.ViewAwardEntry.FILTER_WATCHED_HIDE:
                isIncluded = !viewAward.isWatched();
                break;
            default:
                break;
        }
        return isIncluded;
    }

    /**
     * Returns whether a ViewAward is allowed through the favourite filter.
     * @param viewAward the ViewAward
     * @param filterValue the value of the favourite filter, e.g. "filter_favourite_show"
     * @return true if the ViewAward is allowed through the filter, false otherwise
     */
    private boolean isIncludedByFilterFavourite(
            @NonNull ViewAward viewAward, @NonNull String filterValue) {
        boolean isIncluded = true;
        switch (filterValue) {
            case DataContract.ViewAwardEntry.FILTER_FAVOURITE_ANY:
                break;
            case DataContract.ViewAwardEntry.FILTER_FAVOURITE_SHOW:
                isIncluded = viewAward.isFavourite();
                break;
            case DataContract.ViewAwardEntry.FILTER_FAVOURITE_HIDE:
                isIncluded = !viewAward.isFavourite();
                break;
            default:
                break;
        }
        return isIncluded;
    }

    /**
     * Returns a sorted list of view awards.
     * @param viewAwardList a list of view awards to be sorted
     * @param sortOrder How the rows in the cursor should be sorted, e.g. "awardDate DESC".
     *      If this is {@code null}, the sort order is undefined.
     * @return a cursor containing the list of ViewAwards ordered by the specified column
     */
    @NonNull
    private List<ViewAward> applySortToViewAwardList(@NonNull List<ViewAward> viewAwardList,
                                             @Nullable String sortOrder) {
        Timber.d(String.format("applySortToViewAwardList: sortOrder = %s", sortOrder));

        // If sortOrder is null, the default sort order is used,
        // rather than returning an unsorted list.

        String sortColumn = getSortColumn(sortOrder, VIEW_AWARD_SORT_COLUMN_DEFAULT);
        boolean sortAscending = isSortAscending(sortOrder, VIEW_AWARD_SORT_ASCENDING_DEFAULT);

        Comparator<ViewAward> comparator;
        // code coverage: sortColumn cannot be null
        switch (sortColumn) {
            case DataContract.ViewAwardEntry.COLUMN_AWARD_DATE:
                comparator = ViewAward.VIEW_AWARD_COMPARATOR_AWARD_DATE;
                break;
            case DataContract.ViewAwardEntry.COLUMN_TITLE:
                comparator = ViewAward.VIEW_AWARD_COMPARATOR_TITLE;
                break;
            case DataContract.ViewAwardEntry.COLUMN_RUNTIME:
                comparator = ViewAward.VIEW_AWARD_COMPARATOR_RUNTIME;
                break;
            // code coverage: default case cannot happen due to earlier checks
            default:
                comparator = ViewAward.VIEW_AWARD_COMPARATOR_AWARD_DATE;
                break;
        }
        if (!sortAscending) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(viewAwardList, comparator);

        return viewAwardList;
    }

    /**
     * Limits a list of view awards to a maximum number and returns the truncated list.
     * @param viewAwardList a list of view awards to be limited
     * @param selection The selection criteria for the query.
     *                  If this is {@code null} then all rows are included.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in the order that they appear in the selection.
     *      The values will be bound as Strings.
     * @return the list of ViewAwards limited to the specified number
     */
    @NonNull
    private List<ViewAward> applyLimitToViewAwardList(@NonNull List<ViewAward> viewAwardList,
                @Nullable final String selection, @Nullable final String[] selectionArgs) {
        //Timber.d(String.format("applyFilterToViewAwardList: selection = %s, selectionArgs = %s",
        //        selection, selectionArgs.toString()));

        // if there is no limit, return the original list
        if (selection != null
                && selectionArgs != null
                && selectionArgs.length > ARG_INDEX_LIMIT
                && selectionArgs[ARG_INDEX_LIMIT] != null) {
            int limit = 0;
            try {
                limit = Integer.parseInt(selectionArgs[ARG_INDEX_LIMIT]);
            } catch (Exception e) {
                Timber.e("Exception while parsing limit parameter: "
                        + selectionArgs[ARG_INDEX_LIMIT]);
            }
            if (limit > 0) {
                if (limit > viewAwardList.size()) {
                    limit = viewAwardList.size();
                }
                return viewAwardList.subList(0, limit);
            }
        }
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
                case DataContract.ViewAwardEntry.COLUMN_AWARD_DATE:
                    return DataContract.ViewAwardEntry.COLUMN_AWARD_DATE;
                case DataContract.MovieEntry.COLUMN_TITLE:
                    return DataContract.MovieEntry.COLUMN_TITLE;
                case DataContract.ViewAwardEntry.COLUMN_RUNTIME:
                    return DataContract.ViewAwardEntry.COLUMN_RUNTIME;
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

}
