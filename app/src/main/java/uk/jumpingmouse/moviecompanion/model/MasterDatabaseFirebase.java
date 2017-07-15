package uk.jumpingmouse.moviecompanion.model;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.Award;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.data.UserMovie;
import uk.jumpingmouse.moviecompanion.security.SecurityManager;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;
import uk.jumpingmouse.moviecompanion.widget.WidgetProviderBase;

/**
 * Superclass for helper classes for accessing the Firebase Realtime Database.
 * @author Edmund Johnson
 */
abstract class MasterDatabaseFirebase implements MasterDatabase {

    /** Firebase database nodes. */
    static final String NODE_MOVIES = "movies";
    static final String NODE_AWARDS = "awards";
    private static final String NODE_USERS = "users";
    private static final String NODE_USER_MOVIES = "userMovies";

    // The Firebase Realtime Database.
    private static FirebaseDatabase sFirebaseDatabase;
    // A database reference to the "/movies" node.
    private static DatabaseReference sDatabaseReferenceMovies;
    // A database reference to the "/awards" node.
    private static DatabaseReference sDatabaseReferenceAwards;
    // A database reference to the "/users/[uid]/userMovies" node.
    private static DatabaseReference sDatabaseReferenceUserMovies;

    // A listener which listens for database events at the "/movies" node.
    private ChildEventListener mChildEventListenerMovies;
    // A listener which listens for database events at the "/awards" node.
    private ChildEventListener mChildEventListenerAwards;
    // A listener which listens for database events at the "/users/[uid]/userMovies" node.
    private ChildEventListener mChildEventListenerUserMovies;

    //---------------------------------------------------------------------
    // Event-related methods

    /**
     * Performs processing required when a user has signed in.
     * @param context the context
     */
    public void onSignedIn(@Nullable Context context) {
        attachDatabaseEventListenerMovies(context);
        attachDatabaseEventListenerAwards(context);
        attachDatabaseEventListenerUserMovies(context);
    }

    /** Performs processing required when a user has signed out. */
    public void onSignedOut() {
        detachDatabaseEventListenerMovies();
        detachDatabaseEventListenerAwards();
        detachDatabaseEventListenerUserMovies();
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
    public abstract int addMovie(@Nullable final Context context, @NonNull final Movie movie);

    /**
     * Deletes a movie from the Firebase database.
     * @param context the context
     * @param id the id of the movie to be deleted
     * @return the number of rows deleted
     */
    @Override
    public abstract int deleteMovie(@Nullable Context context, int id);

    //---------------------------------------------------------------------
    // Firebase database award modification methods.
    // By default, not allowed.

    /**
     * Adds an award's details to the Firebase database.
     * If the award does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param context the context
     * @param award the award to insert or update
     * @return the number of rows inserted or updated
     */
    @Override
    public abstract int addAward(@Nullable final Context context, @NonNull final Award award);

    /**
     * Deletes an award from the Firebase database.
     * @param context the context
     * @param id the id of the award to be deleted
     * @return the number of rows deleted
     */
    @Override
    public abstract int deleteAward(@Nullable Context context, @Nullable String id);

    //---------------------------------------------------------------------
    // Firebase database UserMovie modification methods.
    // Available to all users.

    /**
     * Adds a user movie's details to the Firebase database.
     * If the user movie does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param context the context
     * @param userMovie the user movie to insert or update
     * @return the number of rows inserted or updated
     */
    @Override
    public int addUserMovie(@Nullable final Context context,  @NonNull final UserMovie userMovie) {
        if (context == null) {
            return 0;
        }
        String userMoviesNode = getUserMoviesNode(getSecurityManager().getUid());
        if (userMoviesNode == null) {
            return 0;
        }
        return setNode(context, userMoviesNode, Integer.toString(userMovie.getId()),
                userMovie, false);
    }

    /**
     * Deletes a user movie from the Firebase database.
     * @param context the context
     * @param id the id of the user movie to be deleted
     * @return the number of rows deleted
     */
    @Override
    public int deleteUserMovie(@Nullable Context context, int id) {
        if (context == null || id == Movie.ID_UNKNOWN) {
            return 0;
        }
        String userMoviesNode = getUserMoviesNode(getSecurityManager().getUid());
        if (userMoviesNode == null) {
            return 0;
        }
        return deleteNode(context, userMoviesNode, Integer.toString(id), false);
    }

    /**
     * Returns the user movies node for a user, i.e. "/users/[uid]/userMovies".
     * @param uid the user's uid
     * @return the user movies node for a user
     */
    @Nullable
    private String getUserMoviesNode(@Nullable String uid) {
        if (uid == null) {
            return null;
        }
        return NODE_USERS + "/" + uid + "/" + NODE_USER_MOVIES;
    }

    //---------------------------------------------------------------------
    // Database changes initiated on the local device

    /**
     * Sets the value of a node in the Firebase database.
     * If the node does not exist in the database, it is created.
     * If it already exists in the database, it is updated.
     * The object is added to the LOCAL database separately, by a listener attached
     * to the Firebase database.
     * @param context the context
     * @param targetNode the node to which the new node is to be added
     * @param nodeKey the key of the new node to insert or update
     * @param nodeValue the value of the new node to insert or update
     * @param isAdminFunction whether this operation is part of an admin function
     * @return the number of rows inserted or updated
     */
    int setNode(@Nullable final Context context, @NonNull final String targetNode,
                @NonNull String nodeKey, @NonNull final Object nodeValue,
                boolean isAdminFunction) {
        if (context == null) {
            return 0;
        }
        // Create the node to add to the database.
        Map<String, Object> mapValue = new HashMap<>(1);
        mapValue.put(nodeKey, nodeValue);

        // Add or update the new node at the database target node.
        getDatabaseReference(targetNode).updateChildren(mapValue,
                getDatabaseOperationCompletionListener(context, R.string.databaseOperationAddNode,
                        targetNode, nodeKey, isAdminFunction));

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
     * @param isAdminFunction whether this operation is part of an admin function
     * @return the key of the created node, or null if no node was created
     */
    @Nullable
    private String pushNode(@Nullable final Context context, @NonNull final String targetNode,
                @NonNull final Object nodeValue, boolean isAdminFunction) {
        if (context == null) {
            return null;
        }
        // Push the new node to the database target node.
        DatabaseReference newNode = getDatabaseReference(targetNode).push();
        String newNodeKey = newNode.getKey();
        newNode.setValue(nodeValue,
                getDatabaseOperationCompletionListener(context, R.string.databaseOperationAddNode,
                        targetNode, newNodeKey, isAdminFunction));
        return newNodeKey;
    }

    /**
     * Deletes a node from the database.
     * @param context the context
     * @param targetNode the node from which the node is to be deleted
     * @param nodeKey the key of the node to be deleted
     * @param isAdminFunction whether this operation is part of an admin function
     * @return the number of rows deleted
     */
    int deleteNode(@Nullable Context context, @NonNull final String targetNode,
                           @NonNull String nodeKey, boolean isAdminFunction) {
        if (context == null) {
            return 0;
        }
        // Delete the node from the target node.
        getDatabaseReference(targetNode).child(nodeKey).removeValue(
                getDatabaseOperationCompletionListener(context, R.string.databaseOperationDeleteNode,
                        targetNode, nodeKey, isAdminFunction));

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
    // Database event listeners

    /**
     * Attach a ChildEventListener to the "/movies" node.
     * @param context the context
     */
    private void attachDatabaseEventListenerMovies(@Nullable final Context context) {
        if (mChildEventListenerMovies == null) {
            mChildEventListenerMovies = new ChildEventListener() {
                // This is called for each existing child when the listener is attached
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Movie movie = dataSnapshot.getValue(Movie.class);
                    if (movie != null && context != null) {
                        Uri uriInserted = context.getContentResolver().insert(
                                DataContract.MovieEntry.CONTENT_URI, movie.toContentValues());
                        updateWidgets(context);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Movie movie = dataSnapshot.getValue(Movie.class);
                    if (movie != null && context != null) {
                        int rowsUpdated = context.getContentResolver().update(
                                DataContract.MovieEntry.buildUriForRowById(movie.getId()),
                                movie.toContentValues(), null, null);
                        updateWidgets(context);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Movie movie = dataSnapshot.getValue(Movie.class);
                    if (movie != null && context != null) {
                        int rowsDeleted = context.getContentResolver().delete(
                                DataContract.MovieEntry.buildUriForRowById(movie.getId()),
                                null, null);
                        updateWidgets(context);
                    }
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

    /**
     * Attach a ChildEventListener to the "/awards" node.
     * @param context the context
     */
    private void attachDatabaseEventListenerAwards(@Nullable final Context context) {
        if (mChildEventListenerAwards == null) {
            mChildEventListenerAwards = new ChildEventListener() {
                // This is called for each existing child when the listener is attached
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Award award = dataSnapshot.getValue(Award.class);
                    if (award != null && context != null) {
                        Uri uriInserted = context.getContentResolver().insert(
                                DataContract.AwardEntry.CONTENT_URI, award.toContentValues());
                        updateWidgets(context);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Award award = dataSnapshot.getValue(Award.class);
                    if (award != null && context != null) {
                        int rowsUpdated = context.getContentResolver().update(
                                DataContract.AwardEntry.buildUriForRowById(award.getId()),
                                award.toContentValues(), null, null);
                        updateWidgets(context);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Award award = dataSnapshot.getValue(Award.class);
                    if (award != null && context != null) {
                        int rowsDeleted = context.getContentResolver().delete(
                                DataContract.AwardEntry.buildUriForRowById(award.getId()),
                                null, null);
                        updateWidgets(context);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    Timber.e("Unexpected operation detected at \"/awards\" node: onChildMoved(...)");
                }

                // Called if an error occurs while attempting a database operation
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Timber.e(String.format(Locale.getDefault(),
                            "Unexpected operation detected at node \"%s\": onCancelled(...)."
                                    + "Error code: %d, details: %s, message: %s",
                            "/awards", databaseError.getCode(),
                            databaseError.getDetails(), databaseError.getMessage()));
                }
            };

            getDatabaseReferenceAwards().addChildEventListener(mChildEventListenerAwards);
        }
    }

    /**
     * Attach a ChildEventListener to the "/users/[uid]/userMovies" node.
     * @param context the context
     */
    private void attachDatabaseEventListenerUserMovies(@Nullable final Context context) {
        if (mChildEventListenerUserMovies == null) {
            mChildEventListenerUserMovies = new ChildEventListener() {
                // This is called for each existing child when the listener is attached
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    UserMovie userMovie = dataSnapshot.getValue(UserMovie.class);
                    if (userMovie != null && context != null) {
                        Uri uriInserted = context.getContentResolver().insert(
                                DataContract.UserMovieEntry.CONTENT_URI, userMovie.toContentValues());
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    UserMovie userMovie = dataSnapshot.getValue(UserMovie.class);
                    if (userMovie != null && context != null) {
                        int rowsUpdated = context.getContentResolver().update(
                                DataContract.UserMovieEntry.buildUriForRowById(userMovie.getId()),
                                userMovie.toContentValues(), null, null);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    UserMovie userMovie = dataSnapshot.getValue(UserMovie.class);
                    if (userMovie != null && context != null) {
                        String uid = getSecurityManager().getUid();
                        Uri uri = DataContract.UserMovieEntry.buildUriForRowById(userMovie.getId());
                        int rowsDeleted = context.getContentResolver().delete(uri, null, null);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    Timber.e("Unexpected operation detected at \"/users/%s/userMovies\" node: onChildMoved(...)",
                            getSecurityManager().getUid());
                }

                // Called if an error occurs while attempting a database operation
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Timber.e(String.format(Locale.getDefault(),
                            "Unexpected operation detected at node \"/users/%s/userMovies\": onCancelled(...)."
                                    + "Error code: %d, details: %s, message: %s",
                            getSecurityManager().getUid(), databaseError.getCode(),
                            databaseError.getDetails(), databaseError.getMessage()));
                }
            };

            String uid = getSecurityManager().getUid();
            if (uid != null) {
                getDatabaseReferenceUserMovies(uid).addChildEventListener(mChildEventListenerUserMovies);
            }
        }
    }

    /** Detach the ChildEventListener from the "/movies" node. */
    private void detachDatabaseEventListenerMovies() {
        if (mChildEventListenerMovies != null) {
            getDatabaseReferenceMovies().removeEventListener(mChildEventListenerMovies);
            mChildEventListenerMovies = null;
        }
    }

    /** Detach the ChildEventListener from the "/awards" node. */
    private void detachDatabaseEventListenerAwards() {
        if (mChildEventListenerAwards != null) {
            getDatabaseReferenceAwards().removeEventListener(mChildEventListenerAwards);
            mChildEventListenerAwards = null;
        }
    }

    /** Detach the ChildEventListener from the "/users/[uid]/userMovies" node. */
    private void detachDatabaseEventListenerUserMovies() {
        if (mChildEventListenerUserMovies != null) {
            String uid = getSecurityManager().getUid();
            if (uid != null) {
                getDatabaseReferenceUserMovies(uid).removeEventListener(mChildEventListenerUserMovies);
            }
            mChildEventListenerUserMovies = null;
        }
    }

    /**
     * Updates the content displayed by the widgets.
     * @param context the context
     */
    private void updateWidgets(@NonNull Context context) {
        WidgetProviderBase.updateWidgets(context);
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
            sFirebaseDatabase.setPersistenceEnabled(true);
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
            sDatabaseReferenceMovies = getDatabaseReference(NODE_MOVIES);
        }
        return sDatabaseReferenceMovies;
    }

    /**
     * Returns a reference to the "awards" part of the database.
     * @return a reference to the "awards" part of the database
     */
    @NonNull
    private DatabaseReference getDatabaseReferenceAwards() {
        if (sDatabaseReferenceAwards == null) {
            sDatabaseReferenceAwards = getDatabaseReference(NODE_AWARDS);
        }
        return sDatabaseReferenceAwards;
    }

    /**
     * Returns a reference to the "/users/[uid]/userMovies" part of the database.
     * @return a reference to the "/users/[uid]/userMovies" part of the database
     */
    @NonNull
    private DatabaseReference getDatabaseReferenceUserMovies(@NonNull String uid) {
        if (sDatabaseReferenceUserMovies == null) {
            sDatabaseReferenceUserMovies = getDatabaseReference(getUserMoviesNode(uid));
        }
        return sDatabaseReferenceUserMovies;
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
     * Convenience method which returns a SecurityManager.
     * @return a SecurityManager
     */
    @NonNull
    private static SecurityManager getSecurityManager() {
        return ObjectFactory.getSecurityManager();
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
