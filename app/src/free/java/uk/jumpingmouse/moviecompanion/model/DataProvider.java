package uk.jumpingmouse.moviecompanion.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Content provider class which contains the content provider methods specific to the
 * 'free' product flavour (i.e. operations not requiring admin privileges).
 */
public class DataProvider extends DataProviderBase {

    //---------------------------------------------------------------------
    // Database modification methods.
    // Database modification operations are only allowed in the free product flavour where they
    // modify the user's own data (e.g. adding a movie to a wishlist).

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
        //Uri returnUri;
        //Context context = getContext();

        // Use the uriMatcher to get the id of the URI being handled.
        // If there is no match, throw an UnsupportedOperationException.
        final int match = URI_MATCHER.match(uri);

        // Perform the insert.  Hard-coded strings are used for errors because a) they are
        // system errors, and b) context may be null
        switch (match) {
            // inserting a movie or award requires admin privileges
            case MOVIE:
            case AWARD:
                throw new UnsupportedOperationException("Insufficient privileges for insert: " + uri);

            default:
                throw new UnsupportedOperationException("Unsupported URI for insert: " + uri);
        }
        //notifyChange(context, uri, null);

        //return returnUri;
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
        //int rowsUpdated;
        //Context context = getContext();

        // Use the uriMatcher to get the id of the URI being handled.
        // If there is no match, throw an UnsupportedOperationException.
        final int match = URI_MATCHER.match(uri);

        // Perform the update.  Hard-coded strings are used for errors because a) they are
        // system errors, and b) context may be null
        switch (match) {
            // updating a movie or award requires admin privileges
            case MOVIE_ID:
            case AWARD_ID:
                throw new UnsupportedOperationException("Insufficient privileges for update: " + uri);
            default:
                throw new UnsupportedOperationException("Unsupported URI for update: " + uri);
        }
        //// Notify the listeners.
        //if (rowsUpdated != 0) {
        //    notifyChange(context, uri, null);
        //}

        // Return the number of rows updated
        //return rowsUpdated;
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
        //int rowsDeleted;
        //Context context = getContext();

//        // A null selection deletes all rows and returns 0.
//        // A selection of "1" deletes all rows and returns the number of rows deleted.
//        if (selection == null) {
//            selection = "1";
//        }

        // Use the uriMatcher to get the id of the URI being handled.
        // If there is no match, throw an UnsupportedOperationException.
        final int match = URI_MATCHER.match(uri);

        // Perform the delete.  Hard-coded strings are used for errors because a) they are
        // system errors, and b) context may be null
        switch (match) {
//            case MOVIE:
//                rowsDeleted = deleteAllMovies(context, values);
//                break;

            // deleting a movie or award requires admin privileges
            case MOVIE_ID:
            case AWARD_ID:
                throw new UnsupportedOperationException("Insufficient privileges for delete: " + uri);
            default:
                throw new UnsupportedOperationException("Unsupported URI for delete: " + uri);
        }

        // Notify the URI listeners (using the content resolver) if the rowsDeleted != 0.
        //if (rowsDeleted != 0) {
        //    notifyChange(context, uri, null);
        //}

        // return the number of rows deleted
        //return rowsDeleted;
    }

    //---------------------------------------------------------------------
    // Data query methods
    // Use the defaults.

    //---------------------------------------------------------------------
    // Getters

}
