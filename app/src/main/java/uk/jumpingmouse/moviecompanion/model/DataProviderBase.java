package uk.jumpingmouse.moviecompanion.model;

import android.content.ContentProvider;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.data.Award;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.data.ViewAward;
import uk.jumpingmouse.moviecompanion.utils.ModelUtils;

/**
 * The base class for the content provider, containing the content provider elements which
 * are common to all product flavours (i.e. requiring no special privileges).
 */
public abstract class DataProviderBase extends ContentProvider {

    /** The URI Matcher used by this content provider. */
    static final UriMatcher URI_MATCHER = buildUriMatcher();

    // Constants representing URL formats
    static final int MOVIE = 100;
    static final int MOVIE_ID = 101;
    private static final int MOVIE_ALL = 102;
    static final int AWARD = 200;
    static final int AWARD_ID = 201;
    private static final int AWARD_ALL = 202;
    private static final int VIEW_AWARD = 300;
    private static final int VIEW_AWARD_ID = 301;
    private static final int VIEW_AWARD_ALL = 302;

    //---------------------------------------------------------------------
    // URI matcher

    /**
     * Create and return the URI matcher.
     * @return the URI matcher
     */
    @NonNull
    private static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // DataContract to help define the types to the UriMatcher.

        // movie
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY,
                DataContract.URI_PATH_MOVIE,
                MOVIE);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY,
                DataContract.URI_PATH_MOVIE + "/" + DataContract.PARAM_ALL,
                MOVIE_ALL);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY,
                DataContract.URI_PATH_MOVIE + "/*",
                MOVIE_ID);

        // award
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY,
                DataContract.URI_PATH_AWARD,
                AWARD);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY,
                DataContract.URI_PATH_AWARD + "/" + DataContract.PARAM_ALL,
                AWARD_ALL);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY,
                DataContract.URI_PATH_AWARD + "/*",
                AWARD_ID);

        // view award
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY,
                DataContract.URI_PATH_VIEW_AWARD,
                VIEW_AWARD);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY,
                DataContract.URI_PATH_VIEW_AWARD + "/" + DataContract.PARAM_ALL,
                VIEW_AWARD_ALL);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY,
                DataContract.URI_PATH_VIEW_AWARD + "/*",
                VIEW_AWARD_ID);

        // 3) Return the new matcher!
        return uriMatcher;
    }

    //---------------------------------------------------------------------
    // Mandatory methods common to all product flavours, and implemented here.

    /**
     * Initialise the content provider on startup.
     * This method is called for all registered content providers on the
     * application main thread at application launch time.  It must not perform
     * lengthy operations, or application startup will be delayed.
     *
     * <p>You should defer nontrivial initialization (such as opening,
     * upgrading, and scanning databases) until the content provider is used
     * (via {@link #query}, {@link #insert}, etc).
     * </p>
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        return true;
    }

    /**
     * Returns the type of a URI using the UriMatcher.
     * @param uri the URI
     * @return the type of the URI
     */
    @Override
    public final String getType(@NonNull final Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = URI_MATCHER.match(uri);

        switch (match) {
            case MOVIE:
                return DataContract.MovieEntry.CONTENT_DIR_TYPE;
            case MOVIE_ID:
                return DataContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_ALL:
                return DataContract.MovieEntry.CONTENT_DIR_TYPE;
            case AWARD:
                return DataContract.AwardEntry.CONTENT_DIR_TYPE;
            case AWARD_ID:
                return DataContract.AwardEntry.CONTENT_ITEM_TYPE;
            case AWARD_ALL:
                return DataContract.AwardEntry.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Unsupported URI for getType: " + uri);
        }
    }

    //----------------------------------------------------------------------------
    // Database modification methods

    // insert(...) must be implemented in the subclass for the specific product flavour.
    // update(...) must be implemented in the subclass for the specific product flavour.
    // delete(...) must be implemented in the subclass for the specific product flavour.

    // Use the default implementation of bulkInsert, as there are no plans to call it.

    //---------------------------------------------------------------------
    // Data query methods
    // These are implemented here as they are available to all product flavours.

    /**
     * Handle a query request.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * @param uri The URI to query. This is the full URI sent by the client.
     *      If the client is requesting a specific record, the URI will end in a record number
     *      that the implementation should parse and add to a WHERE or HAVING clause, specifying
     *      that _id value.
     * @param projection The list of columns to put into the cursor.
     *                   If this is {@code null} all columns are included.
     * @param selection A selection criteria to apply when filtering rows.
     *                  If this is {@code null} then all rows are included.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in order that they appear in the selection.
     *      The values will be bound as Strings.
     * @param sortOrder How the rows in the cursor should be sorted.
     *      If this is {@code null}, the sort order is undefined.
     * @return a Cursor or {@code null}.
     */
    @Override
    public final Cursor query(@NonNull final Uri uri, @Nullable final String[] projection,
                              @Nullable final String selection, @Nullable final String[] selectionArgs,
                              @Nullable final String sortOrder) {
        // From the URI, determine what kind of request it is and query the database accordingly.
        Cursor cursor;
        switch (URI_MATCHER.match(uri)) {
            // "movie"
            case MOVIE:
            // "movie/all"
            case MOVIE_ALL:
                cursor = selectMovies(projection, selection, selectionArgs, sortOrder);
                break;
            // "movie/*"
            case MOVIE_ID:
                int movieId = ModelUtils.idToMovieId(uri.getLastPathSegment());
                if (movieId == Movie.ID_UNKNOWN) {
                    Timber.w("Could not obtain movie id from URI" + uri);
                    cursor = null;
                } else {
                    cursor = selectMovieById(movieId);
                }
                break;
            // "award/all"
            case AWARD_ALL:
                cursor = selectAwards(projection, selection, selectionArgs, sortOrder);
                break;
            // "award/*"
            case AWARD_ID:
                String awardId = uri.getLastPathSegment();
                if (awardId == null) {
                    Timber.w("Could not obtain movie id from URI" + uri);
                    cursor = null;
                } else {
                    cursor = selectAwardById(awardId);
                }
                break;
            // "viewAward/all"
            case VIEW_AWARD_ALL:
                cursor = selectViewAwards(projection, selection, selectionArgs, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported URI for query: " + uri);
        }
        if (cursor != null && getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    //---------------------------------------------------------------------
    // Movie query methods

    /**
     * Return a cursor whose first row is the movie with a specified id.
     * @param id the id of the required row
     * @return a cursor whose first row is the row with the specified id
     */
    @Nullable
    private Cursor selectMovieById(final int id) {
        Movie movie = getLocalDatabase().selectMovieById(id);
        if (movie == null) {
            Timber.w("", "Movie not found with id: " + id);
            return null;
        }
        return toCursor(movie);
    }

    /**
     * Return a cursor which contains selected movies from the database.
     * @param projection The list of columns to put into the cursor.
     *                   If this is {@code null} all columns are included.
     * @param selection A selection criteria to apply when filtering rows.
     *                  If this is {@code null} then all rows are included.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in the order that they appear in the selection.
     *      The values will be bound as Strings.
     * @param sortOrder How the rows in the cursor should be sorted.
     *      If this is {@code null}, the sort order is undefined.
     * @return a cursor which contains selected movies from the database
     */
    @Nullable
    private Cursor selectMovies(
            @Nullable final String[] projection, @Nullable final String selection,
            @Nullable final String[] selectionArgs, @Nullable final String sortOrder) {
        return toCursorMovies(getLocalDatabase().selectMovies(projection, selection, selectionArgs, sortOrder));
    }

    /**
     * Returns a one-row cursor containing a movie.
     * @param movie the movie
     * @return a one-row cursor containing the movie
     */
    @NonNull
    private Cursor toCursor(@NonNull Movie movie) {
        // Create a cursor containing the movie columns
        String[] columns = DataContract.MovieEntry.getAllColumns();
        MatrixCursor matrixCursor = new MatrixCursor(columns);

        // populate the cursor with the movie
        matrixCursor.addRow(toObjectArray(movie));

        return matrixCursor;
    }

    /**
     * Returns a multi-row cursor containing a list of movies.
     * @param movies the list of movies
     * @return a multi-row cursor containing the list of movies
     */
    @Nullable
    private Cursor toCursorMovies(@Nullable List<Movie> movies) {
        if (movies == null) {
            // This path can only be executed if no movies match a query, i.e. it requires
            // an empty database or query filters which currently do not exist.
            // Hence code coverage tests may not include this path.
            return null;
        }
        // Create a cursor containing the movie columns
        String[] columns = DataContract.MovieEntry.getAllColumns();
        MatrixCursor matrixCursor = new MatrixCursor(columns);

        // populate the cursor with the movies
        for (Movie movie : movies) {
            matrixCursor.addRow(toObjectArray(movie));
        }

        return matrixCursor;
    }

    /**
     * Returns a movie as an object array, one element per field value.
     * @param movie the movie
     * @return the movie as an Object array
     */
    private Object[] toObjectArray(Movie movie) {
        return new Object[] {
                // This must match the order of columns in DataContract.MovieEntry.getAllColumns().
                movie.getId(),
                movie.getImdbId(),
                movie.getTitle(),
                movie.getYear(),
                movie.getReleased(),
                movie.getRuntime(),
                movie.getGenre(),
                movie.getPoster()
        };
    }

    //---------------------------------------------------------------------
    // Award query methods

    /**
     * Return a cursor whose first row is the award with a specified id.
     * @param id the id of the required row
     * @return a cursor whose first row is the row with the specified id
     */
    @Nullable
    private Cursor selectAwardById(final @Nullable String id) {
        Award award = getLocalDatabase().selectAwardById(id);
        if (award == null) {
            Timber.w("", "Award not found with id: " + id);
            return null;
        }
        return toCursor(award);
    }

    /**
     * Return a cursor which contains selected awards from the database.
     * @param projection The list of columns to put into the cursor.
     *                   If this is {@code null} all columns are included.
     * @param selection A selection criteria to apply when filtering rows.
     *                  If this is {@code null} then all rows are included.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in the order that they appear in the selection.
     *      The values will be bound as Strings.
     * @param sortOrder How the rows in the cursor should be sorted.
     *      If this is {@code null}, the sort order is undefined.
     * @return a cursor which contains selected awards from the database
     */
    @Nullable
    private Cursor selectAwards(
            @Nullable final String[] projection, @Nullable final String selection,
            @Nullable final String[] selectionArgs, @Nullable final String sortOrder) {
        return toCursorAwards(getLocalDatabase().selectAwards(projection, selection, selectionArgs, sortOrder));
    }

    /**
     * Returns a one-row cursor containing an award.
     * @param award the award
     * @return a one-row cursor containing the award
     */
    @NonNull
    private Cursor toCursor(@NonNull Award award) {
        // Create a cursor containing the award columns
        String[] columns = DataContract.AwardEntry.getAllColumns();
        MatrixCursor matrixCursor = new MatrixCursor(columns);

        // populate the cursor with the award
        matrixCursor.addRow(toObjectArray(award));

        return matrixCursor;
    }

    /**
     * Returns a multi-row cursor containing a list of awards.
     * @param awards the list of awards
     * @return a multi-row cursor containing the list of awards
     */
    @Nullable
    private Cursor toCursorAwards(@Nullable List<Award> awards) {
        if (awards == null) {
            // This path can only be executed if no awards match a query, i.e. it requires
            // an empty database or query filters which currently do not exist.
            // Hence code coverage tests may not include this path.
            return null;
        }
        // Create a cursor containing the award columns
        String[] columns = DataContract.AwardEntry.getAllColumns();
        MatrixCursor matrixCursor = new MatrixCursor(columns);

        // populate the cursor with the awards
        for (Award award : awards) {
            matrixCursor.addRow(toObjectArray(award));
        }

        return matrixCursor;
    }

    /**
     * Returns a award as an object array, one element per field value.
     * @param award the award
     * @return the award as an Object array
     */
    private Object[] toObjectArray(Award award) {
        return new Object[] {
                // This must match the order of columns in DataContract.AwardEntry.getAllColumns().
                award.getId(),
                award.getMovieId(),
                award.getAwardDate(),
                award.getCategory(),
                award.getReview(),
                award.getDisplayOrder()
        };
    }

    //---------------------------------------------------------------------
    // View Award query methods

    /**
     * Return a cursor whose first row is the view award with a specified id.
     * @param id the id of the required row
     * @return a cursor whose first row is the row with the specified id
     */
    @Nullable
    private Cursor selectViewAwardById(final @Nullable String id) {
        ViewAward viewAward = getLocalDatabase().selectViewAwardById(id);
        if (viewAward == null) {
            Timber.w("", "ViewAward not found with id: " + id);
            return null;
        }
        return toCursor(viewAward);
    }

    /**
     * Return a cursor which contains selected view awards from the database.
     * @param projection The list of columns to put into the cursor.
     *                   If this is {@code null} all columns are included.
     * @param selection A selection criteria to apply when filtering rows.
     *                  If this is {@code null} then all rows are included.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in the order that they appear in the selection.
     *      The values will be bound as Strings.
     * @param sortOrder How the rows in the cursor should be sorted.
     *      If this is {@code null}, the sort order is undefined.
     * @return a cursor which contains selected view awards from the database
     */
    @Nullable
    private Cursor selectViewAwards(
            @Nullable final String[] projection, @Nullable final String selection,
            @Nullable final String[] selectionArgs, @Nullable final String sortOrder) {
        return toCursorViewAwards(getLocalDatabase().selectViewAwards(projection, selection, selectionArgs, sortOrder));
    }

    /**
     * Returns a one-row cursor containing a view award.
     * @param viewAward the view award
     * @return a one-row cursor containing the view award
     */
    @NonNull
    private Cursor toCursor(@NonNull ViewAward viewAward) {
        // Create a cursor containing the award columns
        String[] columns = DataContract.AwardEntry.getAllColumns();
        MatrixCursor matrixCursor = new MatrixCursor(columns);

        // populate the cursor with the view award
        matrixCursor.addRow(toObjectArray(viewAward));

        return matrixCursor;
    }

    /**
     * Returns a multi-row cursor containing a list of view awards.
     * @param viewAwards the list of view awards
     * @return a multi-row cursor containing the list of view awards
     */
    @Nullable
    private Cursor toCursorViewAwards(@Nullable List<ViewAward> viewAwards) {
        if (viewAwards == null) {
            // This path can only be executed if no view awards match a query, i.e. it requires
            // an empty database or query filters which currently do not exist.
            // Hence code coverage tests may not include this path.
            return null;
        }
        // Create a cursor containing the view award columns
        String[] columns = DataContract.ViewAwardEntry.getAllColumns();
        MatrixCursor matrixCursor = new MatrixCursor(columns);

        // populate the cursor with the view awards
        for (ViewAward viewAward : viewAwards) {
            matrixCursor.addRow(toObjectArray(viewAward));
        }

        return matrixCursor;
    }

    /**
     * Returns a view award as an object array, one element per field value.
     * @param viewAward the view award
     * @return the view award as an Object array
     */
    private Object[] toObjectArray(ViewAward viewAward) {
        return new Object[] {
                // This must match the order of columns in DataContract.ViewAwardEntry.getAllColumns().
                viewAward.getId(),
                viewAward.getMovieId(),
                viewAward.getAwardDate(),
                viewAward.getCategory(),
                viewAward.getDisplayOrder(),
                viewAward.getTitle()
        };
    }

    //---------------------------------------------------------------------
    // Utility methods

    /**
     * Notify observers registered with the content resolver that content was updated.
     * @param context the context
     * @param uri The URI of the content that was updated.
     * @param observer The observer that originated the change, may be <code>null</code>.
     */
    void notifyChange(@Nullable Context context, @NonNull final Uri uri,
                @SuppressWarnings("SameParameterValue") @Nullable final ContentObserver observer) {
        if (context != null && context.getContentResolver() != null) {
            context.getContentResolver().notifyChange(uri, observer);
        }
    }

//    /**
//     * Shut down the ContentProvider instance.
//     * This method can be invoked in unit tests.
//     * You do not need to call this method. This is a method specifically to assist the testing
//     * framework in running smoothly. You can read more at:
//     * http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
//     *
//     * <p>
//     * Android normally handles ContentProvider startup and shutdown
//     * automatically. You do not need to start up or shut down a
//     * ContentProvider. When you invoke a test method on a ContentProvider,
//     * however, a ContentProvider instance is started and keeps running after
//     * the test finishes, even if a succeeding test instantiates another
//     * ContentProvider. A conflict develops because the two instances are
//     * usually running against the same underlying data source (for example, a
//     * SQLite database).
//     * </p>
//     * <p>
//     * Implementing shutDown() avoids this conflict by providing a way to
//     * terminate the ContentProvider. This method can also prevent memory leaks
//     * from multiple instantiations of the ContentProvider, and it can ensure
//     * unit test isolation by allowing you to completely clean up the test
//     * fixture before moving on to the next test.
//     * </p>
//     */
//    @Override
//    public final void shutdown() {
//        super.shutdown();
//    }

    //---------------------------------------------------------------------
    // Getters

    /**
     * Convenience method which returns a reference to a local database.
     * @return a reference to a local database
     */
    private static LocalDatabase getLocalDatabase() {
        return ObjectFactory.getLocalDatabase();
    }

    /**
     * Convenience method for returning a reference to the database helper.
     * @return a reference to the database helper
     */
    @NonNull
    static MasterDatabase getMasterDatabase() {
        return ObjectFactory.getMasterDatabase();
    }

}
