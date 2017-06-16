package uk.jumpingmouse.moviecompanion.model;

import android.content.ContentProvider;
import android.content.ContentValues;
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
 * The content provider, through which the local database is accessed.
 */
public class DataProvider extends ContentProvider {

    /** The URI Matcher used by this content provider. */
    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    // Constants representing URL formats
    private static final int MOVIE = 100;
    private static final int MOVIE_ID = 101;
    private static final int MOVIE_ALL = 102;
    private static final int AWARD = 200;
    private static final int AWARD_ID = 201;
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

    /**
     * Handle a request to insert a new row.
     * As a courtesy, after inserting call:
     * { @link ContentResolver#notifyChange(Uri, ContentObserver) notifyChange()}
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * @param uri The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The URI for the newly inserted item.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull final Uri uri, @Nullable final ContentValues values) {
        Uri returnUri;
        Context context = getContext();

        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case MOVIE:
                Movie movie = insertMovie(values);
                if (movie == null) {
                    Timber.w("insert: Failed to insert movie using ContentValues: ", values);
                    return null;
                }
                returnUri = DataContract.MovieEntry.buildUriForRowById(movie.getId());
                break;
            case AWARD:
                Award award = insertAward(values);
                if (award == null) {
                    Timber.w("insert: Failed to insert award using ContentValues: ", values);
                    return null;
                }
                returnUri = DataContract.AwardEntry.buildUriForRowById(award.getId());
                break;
            default:
                throw new UnsupportedOperationException("Unsupported URI for insert: " + uri);
        }
        // Notify any observers on the movie/award URI
        notifyChange(context, uri, null);
        // Notify any observers on the viewAward URI, as ViewAwards are affected by
        // changes to movies and awards.
        notifyChange(context, DataContract.ViewAwardEntry.buildUriForAllRows(), null);

        return returnUri;
    }

    /**
     * Handle a request to update one or more rows.
     * Update all rows matching the selection, setting the columns according to the provided
     * values map.
     * As a courtesy, after updating call:
     * { @link ContentResolver#notifyChange(Uri, ContentObserver) notifyChange()}
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri The URI of the content to update. This can potentially contain a record ID
     *            if this is an update request for a specific record.
     * @param values A set of column_name/value pairs to update in the database.
     *               This must not be {@code null}.
     * @param selection An optional filter to match the rows to update.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in order that they appear in the selection.
     *      The values will be bound as Strings.
     * @return the number of rows affected.
     */
    @Override
    public final int update(@NonNull final Uri uri, @Nullable final ContentValues values,
                            @Nullable final String selection, @Nullable final String[] selectionArgs) {
        int rowsUpdated;
        Context context = getContext();

        // Use the uriMatcher to get the id of the URI being handled.
        // If there is no match, throw an UnsupportedOperationException.
        final int match = URI_MATCHER.match(uri);

        switch (match) {
//            case MOVIE:
//                // A null id updates all rows
//                rowsUpdated = updateAllMovies(context, values);
//                break;
            case MOVIE_ID:
                int movieId = ModelUtils.idToMovieId(uri.getLastPathSegment());
                if (movieId == Movie.ID_UNKNOWN) {
                    Timber.w("Could not obtain movie id from URI: " + uri);
                    rowsUpdated = 0;
                } else {
                    rowsUpdated = updateMovie(movieId, values);
                }
                break;
            case AWARD_ID:
                String awardId = uri.getLastPathSegment();
                rowsUpdated = updateAward(awardId, values);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported URI for update: " + uri);
        }
        // Notify the listeners
        if (rowsUpdated != 0) {
            // Notify any observers on the movie/award URI
            notifyChange(context, uri, null);
            // Notify any observers on the viewAward URI, as ViewAwards are affected by
            // changes to movies and awards
            notifyChange(context, DataContract.ViewAwardEntry.buildUriForAllRows(), null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Handle a request to delete a number of rows.
     * The selection clause is applied when performing deletion, allowing the operation to affect
     * multiple rows in a directory.
     * As a courtesy, after deleting call:
     * { @link ContentResolver#notifyChange(Uri ,ContentObserver) notifyChange()}
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri The full URI of the content to delete, including a row ID if a specific record
     *            is requested.
     * @param selection An optional filter to match the rows to delete.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in order that they appear in the selection.
     *      The values will be bound as Strings.
     * @return The number of rows affected.
     */
    @Override
    public final int delete(@NonNull final Uri uri, @Nullable String selection,
                            @Nullable final String[] selectionArgs) {
        int rowsDeleted;
        Context context = getContext();

//        // A null selection deletes all rows and returns 0.
//        // A selection of "1" deletes all rows and returns the number of rows deleted.
//        if (selection == null) {
//            selection = "1";
//        }

        // Use the uriMatcher to get the id of the URI being handled.
        // If there is no match, throw an UnsupportedOperationException.
        final int match = URI_MATCHER.match(uri);

        switch (match) {
//            case MOVIE:
//                rowsDeleted = deleteAllMovies(context, values);
//                break;
            case MOVIE_ID:
                int movieId = ModelUtils.idToMovieId(uri.getLastPathSegment());
                if (movieId == Movie.ID_UNKNOWN) {
                    Timber.e("Could not obtain movie id from URI: " + uri);
                    rowsDeleted = 0;
                } else {
                    rowsDeleted = deleteMovie(context, movieId);
                }
                break;
            case AWARD_ID:
                String awardId = uri.getLastPathSegment();
                if (awardId == null) {
                    Timber.e("Could not obtain award id from URI: " + uri);
                    rowsDeleted = 0;
                } else {
                    rowsDeleted = deleteAward(awardId);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unsupported URI for delete: " + uri);
        }

        // Notify the URI listeners (using the content resolver) if the rowsDeleted != 0.
        if (rowsDeleted != 0) {
            // Notify any observers on the movie/award URI
            notifyChange(context, uri, null);
            // Notify any observers on the viewAward URI, as ViewAwards are affected by
            // changes to movies and awards
            notifyChange(context, DataContract.ViewAwardEntry.buildUriForAllRows(), null);
        }

        // return the number of rows deleted
        return rowsDeleted;
    }

    // Use the default implementation of bulkInsert, as there are no plans for it to be used.

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
            // "award"
            case AWARD:
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
            // "viewAward"
            case VIEW_AWARD:
            // "viewAward/all"
            case VIEW_AWARD_ALL:
                cursor = selectViewAwards(projection, selection, selectionArgs, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported URI for query: " + uri);
        }
        Context context = getContext();
        if (cursor != null && context != null) {
            // register an observer on the URI in the content resolver, through the cursor
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return cursor;
    }

    //---------------------------------------------------------------------
    // Movie modification methods

    /**
     * Inserts a movie into the database.
     * @param values the values to use for the new movie
     * @return the inserted movie
     */
    @Nullable
    private Movie insertMovie(@Nullable final ContentValues values) {
        if (values == null) {
            Timber.e("insertMovie: values are null");
            return null;
        }
        Movie movie = ModelUtils.toMovie(values);
        if (movie == null) {
            Timber.e("insertMovie: unable to create Movie from values: " + values);
            return null;
        }
        int rowsAdded = getLocalDatabase().addMovie(movie);
        if (rowsAdded == 0) {
            Timber.e("insertMovie: unable to add movie to database: " + movie);
            return null;
        } else {
            return movie;
        }
    }

    /**
     * Updates a movie in the database.
     * @param id the movie's id
     * @param values the new values to use for the movie
     * @return the number of rows updated
     */
    private int updateMovie(final int id, @Nullable final ContentValues values) {
        if (values == null) {
            return 0;
        }
        Movie movie = ModelUtils.toMovie(values);
        if (movie == null) {
            return 0;
        } else if (id != movie.getId()) {
            throw new UnsupportedOperationException(
                    "Id mismatch between URL and body of update movie request");
        }
        return getLocalDatabase().addMovie(movie);
    }

    /**
     * Deletes a movie from the database.
     * @param id the movie's id
     * @return the number of rows deleted
     */
    private int deleteMovie(@Nullable Context context, final int id) {
        return getLocalDatabase().deleteMovie(id);
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
        matrixCursor.addRow(movie.toObjectArray());

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
            matrixCursor.addRow(movie.toObjectArray());
        }

        return matrixCursor;
    }

    //---------------------------------------------------------------------
    // Award modification methods

    /**
     * Inserts an award into the database.
     * @param values the values to use for the new award
     * @return the inserted award, or null if the award could not be inserted
     */
    @Nullable
    private Award insertAward(@Nullable final ContentValues values) {
        if (values == null) {
            Timber.e("insertAward: values are null");
            return null;
        }
        Award award = ModelUtils.toAward(values);
        if (award == null) {
            Timber.e("insertAward: unable to create Award from values: " + values);
            return null;
        }
        int rowsAdded = getLocalDatabase().addAward(award);
        if (rowsAdded == 0) {
            Timber.e("insertAward: unable to add award to database: " + award);
            return null;
        } else {
            return award;
        }
    }

    /**
     * Updates an award in the database.
     * @param id the award's id
     * @param values the new values to use for the award
     * @return the number of rows updated
     */
    private int updateAward(@Nullable final String id, @Nullable final ContentValues values) {
        if (id == null || values == null) {
            return 0;
        }
        Award award = ModelUtils.toAward(values);
        if (award == null) {
            return 0;
        } else if (!id.equals(award.getId())) {
            throw new UnsupportedOperationException(
                    "Id mismatch between URL and body of update award request");
        }
        return getLocalDatabase().addAward(award);
    }

    /**
     * Deletes an award from the database.
     * @param id the award's id
     * @return the number of rows deleted
     */
    private int deleteAward(@Nullable final String id) {
        return getLocalDatabase().deleteAward(id);
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
        matrixCursor.addRow(award.toObjectArray());

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
            matrixCursor.addRow(award.toObjectArray());
        }

        return matrixCursor;
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
        return getLocalDatabase().selectViewAwards(
                projection, selection, selectionArgs, sortOrder);
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
        matrixCursor.addRow(viewAward.toObjectArray());

        return matrixCursor;
    }

    //---------------------------------------------------------------------
    // Utility methods

    /**
     * Notify observers registered with the content resolver that content was updated.
     * @param context the context
     * @param uri The URI of the content that was updated.
     * @param observer The observer that originated the change, may be <code>null</code>.
     */
    private void notifyChange(@Nullable Context context, @NonNull final Uri uri,
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

}
