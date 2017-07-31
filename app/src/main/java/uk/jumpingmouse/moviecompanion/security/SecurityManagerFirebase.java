package uk.jumpingmouse.moviecompanion.security;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.analytics.AnalyticsManager;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.model.MasterDatabase;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

/**
 * A Firebase implementation of SecurityManager.
 * @author Edmund Johnson
 */
public final class SecurityManagerFirebase implements SecurityManager {

    /** The singleton instance of this class. */
    private static SecurityManagerFirebase sSecurityManager;

    // Firebase authentication variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Return a Firebase implementation of SecurityManager.
     * @return a Firebase implementation of SecurityManager
     */
    public static SecurityManager getInstance() {
        if (sSecurityManager == null) {
            sSecurityManager = new SecurityManagerFirebase();
        }
        return sSecurityManager;
    }

    /** Private default constructor to prevent instantiation from outside this class. */
    private SecurityManagerFirebase() {
    }

    //---------------------------------------------------------------------
    // Implementation of interface

    /**
     * Perform security processing required when creating an activity.
     * @param activity the activity being created
     */
    @Override
    public void onCreateActivity(@NonNull final AppCompatActivity activity) {

        // Create listener for change in signed-in state
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (isUserSignedIn()) {
                    // user has just signed in
                    onSignedInInitialise(activity);

                } else {
                    // user has just signed out

                    postSignedOutCleanup();

                    // display login screen
                    activity.startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(getIdProviders())
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    /**
     * Perform security processing required when resuming activity.
     */
    @Override
    public void onResumeActivity() {
        getFirebaseAuth().addAuthStateListener(mAuthStateListener);
    }

    /**
     * Perform security processing required when pausing activity.
     */
    @Override
    public void onPauseActivity() {
        if (mAuthStateListener != null) {
            getFirebaseAuth().removeAuthStateListener(mAuthStateListener);
        }
    }

    /**
     * Perform processing required when an activity is being returned to via the back button being
     * pressed.
     * If the user has signed out, then clicked the back button from the sign-in screen,
     * exit the activity rather than displaying the activity they signed out from again.
     * Note that this method is called before onResume(), which displays the sign-in screen
     * if the user is not signed in.
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the returned data
     */
    @Override
    public void onActivityResult(@NonNull AppCompatActivity activity, int requestCode,
                                 int resultCode, @Nullable Intent data) {

        // Are we returning from the sign-in screen?
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != Activity.RESULT_OK) {
                IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
                if (idpResponse == null) {
                    // user pressed the back button
                    // finish the activity
                    activity.finish();
                } else if (idpResponse.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    getViewUtils().displayInfoMessage(activity.getApplicationContext(),
                            activity.getString(R.string.connection_required_for_sign_in,
                                    activity.getString(R.string.app_name)), true);
                    activity.finish();
                } else if (idpResponse.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    getViewUtils().displayInfoMessage(activity.getApplicationContext(),
                            R.string.unknown_error_on_sign_in, true);
                }
            }
        }
    }

    /**
     * Signs out the currently signed-in user.
     * @param activity the current activity
     */
    @Override
    public void signOut(@Nullable AppCompatActivity activity) {
        if (activity != null) {
            // detach the listeners from the master database
            getMasterDatabase().onSignedOut();

            // delete all of the signed-in user's data from the local database
            Uri uri = DataContract.UserMovieEntry.buildUriForAllRows();
            activity.getContentResolver().delete(uri, null, null);

            AuthUI.getInstance().signOut(activity);
        }
    }

    /**
     * Returns whether the user is signed in.
     * @return true if the user is signed in, false otherwise
     */
    @Override
    public boolean isUserSignedIn() {
        FirebaseUser user = getFirebaseAuth().getCurrentUser();
        return (user != null);
    }

    /**
     * Returns the unique user id of the user who is signed in.
     * @return the unique user id of the user who is signed in, or null if no user is signed in
     */
    @Override
    @Nullable
    public String getUid() {
        FirebaseUser user = getFirebaseAuth().getCurrentUser();
        return user == null ? null : user.getUid();
    }

    //---------------------------------------------------------------------
    // Local methods

    @NonNull
    private FirebaseAuth getFirebaseAuth() {
        if (mFirebaseAuth == null) {
            mFirebaseAuth = FirebaseAuth.getInstance();
        }
        return mFirebaseAuth;
    }

    /**
     * Creates and returns the list of allowed id providers (IDPs).
     * @return the list of allowed {@link AuthUI.IdpConfig}s, where each {@link AuthUI.IdpConfig}
     *         contains the configuration parameters for an IDP
     */
    private List<AuthUI.IdpConfig> getIdProviders() {
        return Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
        //        , new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
        );
    }

    /**
     * Performs processing required on user sign in.
     * @param context the context
     */
    private void onSignedInInitialise(@NonNull Context context) {
        Timber.d("onSignedInInitialise");

        // attach the listeners to the master database
        getMasterDatabase().onSignedIn(context);

        // log the event in analytics
        if (mFirebaseAuth != null && mFirebaseAuth.getCurrentUser() != null) {
            FirebaseUser user = mFirebaseAuth.getCurrentUser();
            getAnalyticsManager().logUserSignIn(user.getUid(), user.getProviderId());
        }
    }

    /**
     * Performs processing required after user sign out.
     */
    private void postSignedOutCleanup() {
        Timber.d("postSignedOutCleanup");

        //getMasterDatabase().preSignOut();
    }

    //---------------------------------------------------------------------
    // Getters

    /**
     * Convenience method for returning a reference to the master database.
     * @return a reference to the master database
     */
    @NonNull
    private static MasterDatabase getMasterDatabase() {
        return ObjectFactory.getMasterDatabase();
    }

    /**
     * Convenience method which returns an AnalyticsManager.
     * @return an AnalyticsManager
     */
    @NonNull
    private static AnalyticsManager getAnalyticsManager() {
        return ObjectFactory.getAnalyticsManager();
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
