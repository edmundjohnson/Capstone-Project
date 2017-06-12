package uk.jumpingmouse.moviecompanion.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.data.Award;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.utils.ModelUtils;

/**
 * Content provider class which contains the content provider methods specific to the
 * 'admin' product flavour.
 */
public class DataProvider extends DataProviderBase {

    //---------------------------------------------------------------------
    // Database modification methods

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
    @Nullable
    @Override
    public Uri insert(@NonNull final Uri uri, @Nullable final ContentValues values) {
        Uri returnUri;
        Context context = getContext();

        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case MOVIE:
                Movie movie = insertMovie(context, values);
                if (movie == null) {
                    Timber.w("insert: Failed to insert movie using ContentValues: ", values);
                    return null;
                }
                returnUri = DataContract.MovieEntry.buildUriForRowById(movie.getId());
                break;
            case AWARD:
                String awardId = insertAward(context, values);
                if (awardId == null) {
                    Timber.w("insert: Failed to insert award using ContentValues: ", values);
                    return null;
                }
                returnUri = DataContract.AwardEntry.buildUriForRowById(awardId);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported URI for insert: " + uri);
        }
        notifyChange(context, uri, null);

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
                    rowsUpdated = updateMovie(context, movieId, values);
                }
                break;
            case AWARD_ID:
                String awardId = uri.getLastPathSegment();
                rowsUpdated = updateAward(context, awardId, values);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported URI for update: " + uri);
        }
        // Notify the listeners.
        if (rowsUpdated != 0) {
            notifyChange(context, uri, null);
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
                int id = ModelUtils.idToMovieId(uri.getLastPathSegment());
                if (id == Movie.ID_UNKNOWN) {
                    Timber.e("Could not obtain movie id from URI: " + uri);
                    rowsDeleted = 0;
                } else {
                    rowsDeleted = deleteMovie(context, id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unsupported URI for delete: " + uri);
        }

        // Notify the URI listeners (using the content resolver) if the rowsDeleted != 0.
        if (rowsDeleted != 0) {
            notifyChange(context, uri, null);
        }

        // return the number of rows deleted
        return rowsDeleted;
    }

    //---------------------------------------------------------------------
    // Movie modification methods

    /**
     * Inserts a movie into the database.
     * @param context the context
     * @param values the values to use for the new movie
     * @return the inserted movie
     */
    @Nullable
    private Movie insertMovie(@Nullable Context context, @Nullable final ContentValues values) {
        if (values == null) {
            return null;
        }
        Movie movie = ModelUtils.toMovie(values);
        if (movie == null) {
            return null;
        }
        int rowsAdded = getDatabaseHelper().addMovie(context, movie);
        if (rowsAdded == 0) {
            return null;
        } else {
            return movie;
        }
    }

    /**
     * Updates a movie in the database.
     * @param context the context
     * @param id the movie's id
     * @param values the new values to use for the movie
     * @return the number of rows updated
     */
    private int updateMovie(@Nullable Context context, final int id,
                            @Nullable final ContentValues values) {
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
        return getDatabaseHelper().addMovie(context, movie);
    }

    /**
     * Deletes a movie from the database.
     * @param context the context
     * @param id the movie's id
     * @return the number of rows deleted
     */
    private int deleteMovie(@Nullable Context context, final int id) {
        return getDatabaseHelper().deleteMovie(context, id);
    }

    /**
     * Inserts an award into the database.
     * @param context the context
     * @param values the values to use for the new award
     * @return the id of the inserted award
     */
    @Nullable
    private String insertAward(@Nullable Context context, @Nullable final ContentValues values) {
        if (values == null) {
            return null;
        }
        // Note: the id field in this award object is not set
        Award award = ModelUtils.toAward(values);
        if (award == null) {
            return null;
        }
        return getDatabaseHelper().addAward(context, award);
    }

    /**
     * Updates an award in the database.
     * @param context the context
     * @param id the award's id
     * @param values the new values to use for the award
     * @return the number of rows updated
     */
    private int updateAward(@Nullable Context context, @Nullable final String id,
                            @Nullable final ContentValues values) {
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
        String awardId = getDatabaseHelper().addAward(context, award);
        return awardId == null ? 0 : 1;
    }

    /**
     * Deletes an award from the database.
     * @param context the context
     * @param id the award's id
     * @return the number of rows deleted
     */
    private int deleteAward(@Nullable Context context, @Nullable final String id) {
        return getDatabaseHelper().deleteAward(context, id);
    }

    //---------------------------------------------------------------------
    // Getters

}
