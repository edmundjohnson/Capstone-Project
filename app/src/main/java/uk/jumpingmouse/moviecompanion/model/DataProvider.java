package uk.jumpingmouse.moviecompanion.model;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.utils.ModelUtils;

import java.util.List;

/**
 * Content provider for this app.
 */
public class DataProvider extends ContentProvider {

    /** The URI Matcher used by this content provider. */
    private static final UriMatcher URI_MATCHER = buildUriMatcher();

//    /** The sort order for ordering movies in ascending order of title. */
//    private static final String MOVIE_SORT_TITLE_ASC =
//            DataContract.MovieEntry.COLUMN_TITLE + " " + DataContract.ORDER_ASC;

    // Constants representing URL formats
    private static final int MOVIE = 100;
    private static final int MOVIE_IMDB_ID = 101;
    private static final int MOVIE_ALL = 102;

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
        // PostContract to help define the types to the UriMatcher.
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY,
                DataContract.PATH_MOVIE,
                MOVIE);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY,
                DataContract.PATH_MOVIE + "/" + DataContract.PARAM_ALL,
                MOVIE_ALL);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY,
                DataContract.PATH_MOVIE + "/*",
                MOVIE_IMDB_ID);

        // 3) Return the new matcher!
        return uriMatcher;
    }

    //---------------------------------------------------------------------
    // Mandatory methods, abstract in superclass

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
            case MOVIE_IMDB_ID:
                return DataContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_ALL:
                return DataContract.MovieEntry.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

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
            case MOVIE_IMDB_ID:
                String imdbId = uri.getLastPathSegment();
                // imdbId can never be null because the MOVIE case would be executed
                if (imdbId == null) {
                    cursor = null;
                } else {
                    cursor = selectMovieByImdbId(imdbId);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI for query: " + uri);
        }
        if (cursor != null && getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    /**
     * Handle a request to insert a new row.
     * As a courtesy, after inserting call:
     * {@link ContentResolver#notifyChange(Uri ,ContentObserver) notifyChange()}
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * @param uri The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The URI for the newly inserted item.
     */
    @Override
    public Uri insert(@NonNull final Uri uri, @Nullable final ContentValues values) {
        Uri returnUri;

        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case MOVIE:
                Movie movie = insertMovie(values);
                if (movie == null) {
                    Timber.w("Failed to insert movie using ContentValues: ", values);
                    return null;
                }
                returnUri = DataContract.MovieEntry.buildUriMovieId(movie.imdbId());
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI for insert: " + uri);
        }
        notifyChange(uri, null);

        return returnUri;
    }

    /**
     * Handle a request to update one or more rows.
     * Update all rows matching the selection, setting the columns according to the provided
     * values map.
     * As a courtesy, after updating call:
     * {@link ContentResolver#notifyChange(Uri, ContentObserver) notifyChange()}
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

        // Use the uriMatcher to get the id of the URI being handled.
        // If there is no match, throw an UnsupportedOperationException.
        final int match = URI_MATCHER.match(uri);

        switch (match) {
//            case MOVIE:
//                // A null id updates all rows
//                rowsUpdated = updateAllMovies(values);
//                break;
            case MOVIE_IMDB_ID:
                String imdbId = uri.getLastPathSegment();
                rowsUpdated = updateMovie(imdbId, values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI for update: " + uri);
        }
        // Notify the listeners.
        if (rowsUpdated != 0) {
            notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Handle a request to delete a number of rows.
     * The selection clause is applied when performing deletion, allowing the operation to affect
     * multiple rows in a directory.
     * As a courtesy, after deleting call:
     * {@link ContentResolver#notifyChange(Uri ,ContentObserver) notifyChange()}
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
//                rowsDeleted = deleteAllMovies(values);
//                break;
            case MOVIE_IMDB_ID:
                String imdbId = uri.getLastPathSegment();
                rowsDeleted = deleteMovie(imdbId);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI for delete: " + uri);
        }

        // Notify the URI listeners (using the content resolver) if the rowsDeleted != 0.
        if (rowsDeleted != 0) {
            notifyChange(uri, null);
        }

        // return the number of rows deleted
        return rowsDeleted;
    }

    // Use the default implementation of bulkInsert, as there are no plans to call it.

    //---------------------------------------------------------------------
    // Utility methods

    /**
     * Notify observers registered with the content resolver that content was updated.
     * @param uri The URI of the content that was updated.
     * @param observer The observer that originated the change, may be <code>null</code>.
     */
    private void notifyChange(@NonNull final Uri uri,
                @SuppressWarnings("SameParameterValue") @Nullable final ContentObserver observer) {
        if (getContext() != null && getContext().getContentResolver() != null) {
            getContext().getContentResolver().notifyChange(uri, observer);
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
    // Movie methods

    /**
     * Inserts a movie into the database.
     * @param values the values to use for the new movie
     * @return the inserted movie
     */
    @Nullable
    private Movie insertMovie(@Nullable final ContentValues values) {
        if (values == null) {
            return null;
        }
        Movie movie = ModelUtils.toMovie(values);
        if (movie == null) {
            return null;
        }
        getDatabaseHelper().insertMovie(movie);

        return movie;
    }

    /**
     * Updates a movie in the database.
     * @param imdbId the movie's imdbId
     * @param values the new values to use for the movie
     * @return the number of rows updated
     */
    private int updateMovie(@NonNull final String imdbId, @Nullable final ContentValues values) {
        if (values == null) {
            return 0;
        }
        Movie movie = ModelUtils.toMovie(values);
        if (movie == null) {
            return 0;
        } else if (!imdbId.equals(movie.imdbId())) {
            throw new UnsupportedOperationException("ImdbId mismatch between URL and body of update request");
        }
        return getDatabaseHelper().updateMovie(movie);
    }

    /**
     * Deletes a movie from the database.
     * @param imdbId the movie's imdbId
     * @return the number of rows deleted
     */
    private int deleteMovie(@NonNull final String imdbId) {
        return getDatabaseHelper().deleteMovie(imdbId);
    }

    /**
     * Return a cursor whose first row is the movie with a specified IMDb id.
     * @param imdbId the IMDb id of the required row
     * @return a cursor whose first row is the row with the specified IMDb id
     */
    @Nullable
    private Cursor selectMovieByImdbId(@NonNull final String imdbId) {
        Movie movie = getDatabaseHelper().selectMovieByImdbId(imdbId);
        if (movie == null) {
            Timber.w("", "Movie not found with IMDb id: " + imdbId);
            return null;
        }
        return toCursor(movie);
    }

    /**
     * Return a cursor which contains all of the movies in the database, in the default order.
     * @param projection The list of columns to put into the cursor.
     *                   If this is {@code null} all columns are included.
     * @param selection A selection criteria to apply when filtering rows.
     *                  If this is {@code null} then all rows are included.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in the order that they appear in the selection.
     *      The values will be bound as Strings.
     * @return a cursor which contains all of the movies in the database in the default order
     */
    @Nullable
    private Cursor selectMovies(
            @Nullable final String[] projection, @Nullable final String selection,
            @Nullable final String[] selectionArgs, @Nullable final String sortOrder) {
        return toCursor(getDatabaseHelper().selectMovies());
    }

    /**
     * Returns a one-row cursor containing a movie.
     * @param movie the movie
     * @return a one-row cursor containing the movie
     */
    @Nullable
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
    private Cursor toCursor(@Nullable List<Movie> movies) {
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
                // This must match the order of columns in DataContract.MovieEntry.COLUMNS_ALL
                // for now we are using imdbId as the _id, so it is repeated
                movie.imdbId(),
                movie.imdbId(),
                movie.title(),
                movie.genre(),
                movie.runtime(),
                movie.posterUrl(),
                movie.year(),
                movie.released()
        };
    }

    //---------------------------------------------------------------------
    // Getters

    /**
     * Convenience method for returning a database helper.
     * @return a database helper
     */
    @NonNull
    private DatabaseHelper getDatabaseHelper() {
        // For using the Firebase Realtime Database
        //return DatabaseHelperFirebase.getInstance();
        // For using a mock database, e.g. when unit testing the ContentProvider class
        return DatabaseHelperMock.getInstance();
    }

}
