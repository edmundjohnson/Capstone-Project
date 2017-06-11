package uk.jumpingmouse.moviecompanion.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.Award;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

/**
 * Superclass for helper classes for accessing the Firebase Realtime Database.
 * @author Edmund Johnson
 */
abstract class DatabaseHelperFirebaseBase implements DatabaseHelper {

    // The Firebase Realtime Database.
    private static FirebaseDatabase sFirebaseDatabase;
    // A database reference to the "/movies" node.
    private static DatabaseReference sDatabaseReferenceMovies;

    // A listener which listens for database events at the "/movies" node.
    private ChildEventListener mChildEventListenerMovies;

    //---------------------------------------------------------------------
    // Instance handling methods

    /** Default constructor. */
    DatabaseHelperFirebaseBase() {
    }

    //---------------------------------------------------------------------
    // Event-related methods

    /** Performs processing required when a user has signed in. */
    public void onSignedIn() {
        attachDatabaseEventListenerMovies();
    }

    /** Performs processing required when a user has signed out. */
    public void onSignedOut() {
        detachDatabaseEventListenerMovies();
    }

    //---------------------------------------------------------------------
    // Firebase database movie modification methods.
    // By default, not allowed.

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
        throw new UnsupportedOperationException("Insufficient privileges for add movie");
    }

    /**
     * Deletes a movie from the Firebase database.
     * @param context the context
     * @param id the id of the movie to be deleted
     * @return the number of rows deleted
     */
    @Override
    public int deleteMovie(@Nullable Context context, int id) {
        throw new UnsupportedOperationException("Insufficient privileges for delete movie");
    }

    //---------------------------------------------------------------------
    // Firebase database award modification methods.
    // By default, not allowed.

    /**
     * Adds a award's details to the Firebase database.
     * If the award does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param context the context
     * @param award the award to insert or update
     * @return the number of rows inserted or updated
     */
    @Override
    public int addAward(@Nullable final Context context, @NonNull final Award award) {
        throw new UnsupportedOperationException("Insufficient privileges for add award");
    }

    /**
     * Deletes an award from the Firebase database.
     * @param context the context
     * @param id the id of the award to be deleted
     * @return the number of rows deleted
     */
    @Override
    public int deleteAward(@Nullable Context context, @Nullable String id) {
        throw new UnsupportedOperationException("Insufficient privileges for delete award");
    }

    //---------------------------------------------------------------------
    // Database changes initiated on the local device

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
    int addNode(@Nullable final Context context, @NonNull final String targetNode,
                        @NonNull String nodeKey, @NonNull final Object nodeValue) {
        if (context == null) {
            return 0;
        }
        // Create the node to add to the database.
        Map<String, Object> mapValue = new HashMap<>(1);
        mapValue.put(nodeKey, nodeValue);

        // Add the new node to the database target node.
        getDatabaseReference(targetNode).updateChildren(mapValue,
                getDatabaseOperationCompletionListener(context, R.string.databaseOperationAddNode,
                        targetNode, nodeKey, true));

        // We don't know whether the add will succeed - assume it will
        return 1;
    }

    /**
     * Pushes an object to the Firebase database.
     * If the object does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * The object is added to the LOCAL database separately, by a listener attached
     * to the Firebase database.
     * @param context the context
     * @param targetNode the node to which the new node is to be added
     * @param nodeValue the value of the new node to insert or update
     * @return the number of rows inserted or updated
     */
    int pushNode(@Nullable final Context context, @NonNull final String targetNode,
                @NonNull final Object nodeValue) {
        if (context == null) {
            return 0;
        }
//        // Create the node to add to the database.
//        Map<String, Object> mapValue = new HashMap<>(1);
//        mapValue.put(nodeKey, nodeValue);

//        // Add the new node to the database target node.
//        getDatabaseReference(targetNode).updateChildren(mapValue,
//                getDatabaseOperationCompletionListener(context, R.string.databaseOperationAddNode,
//                        targetNode, nodeKey, true));

        // Push the new node to the database target node.
        // TODO: Improve "push_id" !
        getDatabaseReference(targetNode).push().setValue(nodeValue,
                getDatabaseOperationCompletionListener(context, R.string.databaseOperationAddNode,
                        targetNode, "push_id", true));

        // We don't know whether the add will succeed - assume it will
        return 1;
    }

    /**
     * Deletes a node from the database.
     * @param context the context
     * @param targetNode the node from which the node is to be deleted
     * @param nodeKey the key of the node to be deleted
     * @return the number of rows deleted
     */
    int deleteNode(@Nullable Context context, @NonNull final String targetNode,
                           @NonNull String nodeKey) {
        if (context == null) {
            return 0;
        }
        // Add the new node to the database target node.
        getDatabaseReference(targetNode).child(nodeKey).removeValue(
                getDatabaseOperationCompletionListener(context, R.string.databaseOperationDeleteNode,
                        targetNode, nodeKey, true));

        // We don't know whether the delete will succeed - assume it will
        return 1;
    }

    /**
     * Returns a listener which listens for the completion of database modification operations.
     * The listener reports on the success or failure of the operation.
     * It is assumed that only one child node is added or deleted at a time.
     * @param context the context
     * @param operationResId the String resource id of the name of the database operation,
     *                       e.g. a String resource for "Add node"
     * @param targetNode the name of the node where the child node was attempted to be added or deleted
     * @param childNode the name of the node which was attempted to be added or deleted
     * @param isAdminFunction whether the node is being added or deleted as part of an admin function
     * @return a listener which listens for the completion of database modification operations
     */
    private DatabaseReference.CompletionListener getDatabaseOperationCompletionListener(
            @NonNull final Context context, @StringRes final int operationResId,
            @NonNull final String targetNode, @NonNull final String childNode,
            @SuppressWarnings("SameParameterValue") final boolean isAdminFunction) {

        return new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    String message = context.getString(R.string.databaseOperationSuccess,
                            context.getString(operationResId), childNode, targetNode);
                    Timber.d(message);

                    // For admin functions, display a message indicating success
                    if (isAdminFunction) {
                        getViewUtils().displayInfoMessage(context, message);
                    }
                } else {
                    String message = context.getString(R.string.databaseOperationFailure,
                            context.getString(operationResId), childNode, targetNode, databaseError.getCode(),
                            databaseError.getDetails(), databaseError.getMessage());
                    Timber.e(message);

                    // For admin function failures, display a detailed error message.
                    // Display it as a long duration toast, as a dialog will not work from a background task
                    if (isAdminFunction) {
                        getViewUtils().displayInfoMessage(context, message, true);
                    }
                }
            }
        };
    }

    //---------------------------------------------------------------------
    // Query methods.
    // Queries are performed using the local database.

    /**
     * Returns the movie with a specified id.
     * @param id the id of the movie to be returned
     * @return the movie with the specified id
     */
    @Override
    @Nullable
    public Movie selectMovieById(int id) {
        return getLocalDatabase().selectMovieById(id);
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

    /**
     * Returns the award with a specified id.
     * @param id the id of the award to be returned
     * @return the award with the specified id
     */
    @Override
    @Nullable
    public Award selectAwardById(@Nullable String id) {
        return getLocalDatabase().selectAwardById(id);
    }

    //---------------------------------------------------------------------
    // Database event listeners

    /** Attach a ChildEventListener to the "/movies" node. */
    private void attachDatabaseEventListenerMovies() {
        if (mChildEventListenerMovies == null) {
            mChildEventListenerMovies = new ChildEventListener() {
                // This is called for each existing child when the listener is attached
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Movie movie = dataSnapshot.getValue(Movie.class);
                    getLocalDatabase().addMovie(movie);
                    //mMovieAdapter.add(movie);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Movie movie = dataSnapshot.getValue(Movie.class);
                    getLocalDatabase().addMovie(movie);
                    //mMovieAdapter.add(movie);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Movie movie = dataSnapshot.getValue(Movie.class);
                    getLocalDatabase().deleteMovie(movie.getId());
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    Timber.e("Unexpected operation detected at \"/movies\" node: onChildMoved(...)");
                }

                // Called if an error occurs while attempting a database operation
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Timber.e(String.format(Locale.getDefault(),
                            "Unexpected operation detected at node \"%s\": onCancelled(...)."
                            + "Error code: %d, details: %s, message: %s",
                            "/movies", databaseError.getCode(),
                            databaseError.getDetails(), databaseError.getMessage()));
                }
            };

            getDatabaseReferenceMovies().addChildEventListener(mChildEventListenerMovies);
        }
    }

    /** Detach the ChildEventListener from the "/movies" node. */
    private void detachDatabaseEventListenerMovies() {
        if (mChildEventListenerMovies != null) {
            getDatabaseReferenceMovies().removeEventListener(mChildEventListenerMovies);
            mChildEventListenerMovies = null;
        }
    }

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
    private static LocalDatabase getLocalDatabase() {
        return ObjectFactory.getLocalDatabase();
    }

    /**
     * Convenience method which returns a reference to a ViewUtils object.
     * @return a reference to a ViewUtils object
     */
    @NonNull
    private static ViewUtils getViewUtils() {
        return ObjectFactory.getViewUtils();
    }

}
