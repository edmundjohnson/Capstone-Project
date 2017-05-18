package uk.jumpingmouse.moviecompanion.model;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Database helper class for accessing the Firebase Realtime Database.
 * @author Edmund Johnson
 */
public class DatabaseHelperFirebase implements DatabaseHelper {

    /** The singleton instance of this class. */
    private static DatabaseHelperFirebase sDatabaseHelper = null;


    private static FirebaseDatabase sFirebaseDatabase;
    private static DatabaseReference sDatabaseReferenceMovies;

//    /**
//     * A local database which stores the Firebase Realtime database data
//     * which is relevant to the current user.
//     * The local database is to allow faster queries to be executed.
//     */
//    private static LocalDatabase mLocalDatabase;


    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    public static DatabaseHelper getInstance() {
        if (sDatabaseHelper == null) {
            sDatabaseHelper = new DatabaseHelperFirebase();
        }
        return sDatabaseHelper;
    }

    /** Private default constructor to prevent instantiation from outside this class. */
    private DatabaseHelperFirebase() {
    }

    //---------------------------------------------------------------------
    // Database changes initiated on the local device

    /**
     * Adds a movie's details to the Firebase database.
     * If the movie does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param context the context
     * @param movie the movie to insert or update
     * @return the number of rows inserted or updated
     */
    @Override
    public int addMovie(@Nullable final Context context, @NonNull final Movie movie) {
        return addNode(context, DataContract.MovieEntry.ROOT_NODE, movie.getImdbId(), movie);
    }

    /**
     * Adds an object to the Firebase database.
     * If the object does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * The object is added to the LOCAL database separately, by a listener attached
     * to the Firebase database.
     * @param context the context
     * @param targetNode the node to which the new node is to be added
     * @param nodeKey the key of the new node to insert or update
     * @param nodeValue the value of the new node to insert or update
     * @return the number of rows inserted or updated
     */
    private int addNode(@Nullable final Context context, @NonNull final String targetNode,
                        @NonNull String nodeKey, @NonNull final Object nodeValue) {
        // Create the node to add to the database.
        Map<String, Object> mapValue = new HashMap<>(1);
        mapValue.put(nodeKey, nodeValue);

        // Add the new node to the database target node.
        getDatabaseReference(targetNode).updateChildren(mapValue,
                getUpdateChildrenCompletionListener(context, targetNode, nodeKey, true));

        // We don't know whether the add will succeed - assume it will
        return 1;
    }

    /**
     * Returns a listener which listens for the completion of "updateChildren" database events.
     * The listener reports on the success or failure of the operation.
     * It is assumed that only one child node is added at a time.
     * @param context the context
     * @param targetNode the name of the node where the children were attempted to be added
     * @param newNode the name of the node which was attempted to be added
     * @param isAdminFunction whether the node is being added as part of an admin function
     * @return a listener which listens for the completion of "updateChildren" database events
     */
    private DatabaseReference.CompletionListener getUpdateChildrenCompletionListener(
            @Nullable final Context context,
            @NonNull final String targetNode, @NonNull final String newNode,
            @SuppressWarnings("SameParameterValue") final boolean isAdminFunction) {

        return new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Timber.d(String.format(Locale.getDefault(),
                            "Node \"%s\" added successfully to node \"/%s\"", newNode, targetNode));

                    // For admin functions, display a message indicating success
                    if (context != null && isAdminFunction) {
                        String message = context.getString(R.string.nodeAddedSuccess, newNode, targetNode);
                        getViewUtils().displayInfoMessage(context, message);
                    }
                } else {
                    Timber.e(String.format(Locale.getDefault(),
                            "Failed to add node \"%s\" to node /\"%s\","
                                    + " error code: %d, details: %s, message: %s",
                            newNode, targetNode, databaseError.getCode(),
                            databaseError.getDetails(), databaseError.getMessage()));

                    // For admin function failures, display detailed error message
                    if (context != null && isAdminFunction) {
                        String message = context.getString(R.string.nodeAddedFailure,
                                newNode, targetNode, databaseError.getCode(),
                                databaseError.getDetails(), databaseError.getMessage());
                        getViewUtils().displayErrorMessage(context, message);
                    }
                }
            }
        };
    }

    /**
     * Deletes a movie from the database.
     * @param context the context
     * @param imdbId the getImdbId of the movie to be deleted
     * @return the number of rows deleted
     */
    @Override
    public int deleteMovie(@Nullable Context context, @NonNull String imdbId) {
        // TODO
        return 0;
    }

    //---------------------------------------------------------------------
    // Query methods.
    // Queries are performed using the local database.

    /**
     * Returns the movie with a specified IMDb id.
     * @param imdbId the imdbId of the movie to be returned
     * @return the movie with the specified IMDb id
     */
    @Override
    @Nullable
    public Movie selectMovieByImdbId(@NonNull String imdbId) {
        return getLocalDatabase().selectMovieByImdbId(imdbId);
    }

    /**
     * Returns a list of movies in the database, filtered and sorted according
     * to specified criteria.
     * @param projection The list of columns to put into the cursor.
     *                   If this is {@code null} all columns are included.
     * @param selection A selection criteria to apply when filtering rows.
     *                  If this is {@code null} then all rows are included.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in the order that they appear in the selection.
     *      The values will be bound as Strings.
     * @param sortOrder How the rows in the cursor should be sorted.
     *      If this is {@code null}, the sort order is undefined.
     * @return a list of movies in the database
     */
    @Override
    @Nullable
    public List<Movie> selectMovies(
            @Nullable final String[] projection, @Nullable final String selection,
            @Nullable final String[] selectionArgs, @Nullable final String sortOrder) {
        return getLocalDatabase().selectMovies(projection, selection, selectionArgs, sortOrder);
    }

    //---------------------------------------------------------------------
    // Database listeners

//    private void attachDatabaseReadListener() {
//        if (mChildEventListener == null) {
//            mChildEventListener = new ChildEventListener() {
//                // This is called for each existing child when the listener is attached
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    Movie movie = dataSnapshot.getValue(Movie.class);
//                    getLocalDatabase().addMovie(movie);
//                    //mMovieAdapter.add(movie);
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                    Movie movie = dataSnapshot.getValue(Movie.class);
//                    getLocalDatabase().addMovie(movie);
//                    //mMovieAdapter.add(movie);
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                }
//
//                // Called if an error occurs while attempting a database operation
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                }
//            };
//
//            getDatabaseReferenceMovies().addChildEventListener(mChildEventListener);
//        }
//    }

    //---------------------------------------------------------------------
    // Getters

    /**
     * Returns a reference to the Firebase database.
     * @return a reference to the Firebase database
     */
    @NonNull
    private FirebaseDatabase getFirebaseDatabase() {
        if (sFirebaseDatabase == null) {
            sFirebaseDatabase = FirebaseDatabase.getInstance();
        }
        return sFirebaseDatabase;
    }

    /**
     * Returns a reference to the "movies" part of the database.
     * @return a reference to the "movies" part of the database
     */
    @NonNull
    private DatabaseReference getDatabaseReferenceMovies() {
        if (sDatabaseReferenceMovies == null) {
            sDatabaseReferenceMovies = getDatabaseReference(DataContract.MovieEntry.ROOT_NODE);
        }
        return sDatabaseReferenceMovies;
    }

    /**
     * Returns a reference to a Firebase database node.
     * @param nodePath the path to the node, relative to the root node
     * @return a reference to the Firebase database node
     */
    @NonNull
    private DatabaseReference getDatabaseReference(@NonNull String nodePath) {
        // This returns a non-null value, even if the node does not exist.
        return getFirebaseDatabase().getReference(nodePath);
    }

    /**
     * Convenience method which returns a reference to the local database,
     * which is used to store a copy of the subset of Firebase realtime database
     * data which is relevant to the current user.
     * The local database is used to allow faster queries to be executed.
     * @return a reference to the local database
     */
    @NonNull
    private LocalDatabase getLocalDatabase() {
        return ObjectFactory.getLocalDatabase();
    }

    /**
     * Convenience method which returns a reference to a ViewUtils object.
     * @return a reference to a ViewUtils object
     */
    @NonNull
    private ViewUtils getViewUtils() {
        return ObjectFactory.getViewUtils();
    }

}
