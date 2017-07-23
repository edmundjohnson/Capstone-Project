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

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
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
    // Lifecycle methods

    /**
     * Perform security processing required when creating activity.
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

                    // delete all of the signed-in user's data from the local database
                    Uri uri = DataContract.UserMovieEntry.buildUriForAllRows();
                    activity.getContentResolver().delete(uri, null, null);

                    onSignedOutCleanup();

                    AuthUI.IdpConfig idpConfigGoogle
                            = new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build();
                    AuthUI.IdpConfig idpConfigEmail
                            = new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build();

                    // display login screen
                    activity.startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    //.setTheme(R.style.AppThemeWithActionBar)
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(idpConfigGoogle, idpConfigEmail))
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
     * Check whether the user has clicked the back button from the sign-in screen.
     * If so, exit the activity rather than displaying the sign-in screen again.
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
//            getViewUtils().displayInfoMessage(activity, R.string.sign_in_ok);

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
        // Are we are returning from an admin screen (perhaps via back arrow on sign-in screen)?
        // (No longer possible)
        } else if (requestCode == SecurityManager.RC_ADD_MOVIE
                || requestCode == SecurityManager.RC_ADD_AWARD) {
            // if the user has clicked back arrow after signing out on a different activity,
            // we end up here
            if (!isUserSignedIn()) {
                activity.finish();
            }
        }
    }

    /**
     * Performs processing required on user sign in.
     * @param context the context
     */
    private void onSignedInInitialise(@NonNull Context context) {
        Timber.d("onSignedInInitialise");

        getMasterDatabase().onSignedIn(context);
    }

    /**
     * Performs processing required on user sign out.
     */
    private void onSignedOutCleanup() {
        Timber.d("onSignedOutCleanup");

        getMasterDatabase().onSignedOut();

//        mViewAwardAdapter.clear();
    }


    //---------------------------------------------------------------------
    // Security methods

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
     * Signs out the currently signed-in user.
     * @param activity the current activity
     */
    @Override
    public void signOut(@Nullable AppCompatActivity activity) {
        if (activity != null) {
            AuthUI.getInstance().signOut(activity);
        }
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
    // Getters

    @NonNull
    private FirebaseAuth getFirebaseAuth() {
        if (mFirebaseAuth == null) {
            mFirebaseAuth = FirebaseAuth.getInstance();
        }
        return mFirebaseAuth;
    }

    /**
     * Convenience method which returns a reference to a ViewUtils object.
     * @return a reference to a ViewUtils object
     */
    @NonNull
    private static ViewUtils getViewUtils() {
        return ObjectFactory.getViewUtils();
    }

    /**
     * Convenience method for returning a reference to the master database.
     * @return a reference to the master database
     */
    @NonNull
    private static MasterDatabase getMasterDatabase() {
        return ObjectFactory.getMasterDatabase();
    }

}
