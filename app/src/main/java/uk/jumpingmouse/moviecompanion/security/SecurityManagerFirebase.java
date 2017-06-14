package uk.jumpingmouse.moviecompanion.security;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.model.MasterDatabase;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

/**
 * A Firebase implementation of SecurityManager.
 * @author Edmund Johnson
 */
public class SecurityManagerFirebase implements SecurityManager {

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
    public void onCreateActivity(@NonNull final FragmentActivity activity) {

        // Create listener for change in signed-in state
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (isUserSignedIn()) {
                    // user has just signed in
//                    FirebaseUser user = firebaseAuth.getCurrentUser();
//                    String displayName = user.getDisplayName();
//
//                    // If the displayName was null, iterate the provider-specific data
//                    // and set with the first non-null value
//                    for (UserInfo userInfo : user.getProviderData()) {
//
//                        // Id of the provider (ex: google.com)
//                        String providerId = userInfo.getProviderId();
//
//                        // UID specific to the provider
//                        String uid = userInfo.getUid();
//
//                        // Name and email address
//                        String name = userInfo.getDisplayName();
//                        String email = userInfo.getEmail();
//
//                        if (displayName == null && userInfo.getDisplayName() != null) {
//                            displayName = userInfo.getDisplayName();
//                        }
//                    }
                    onSignedInInitialise();

                } else {
                    // user has just signed out
                    onSignedOutCleanup();

                    // display login screen
                    activity.startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
//                                    // Firebase UI 0.6.0
//                                    AuthUI.EMAIL_PROVIDER,
//                                    AuthUI.GOOGLE_PROVIDER)
                                            // Firebase UI 1.0.1
                                            Arrays.asList(
                                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
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
    public void onActivityResult(@NonNull FragmentActivity activity, int requestCode,
                                 int resultCode, @Nullable Intent data) {

        // Are we returning from the sign-in screen?
        if (requestCode == SecurityManager.RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                // The user signed in successfully
                //IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
                getViewUtils().displayInfoMessage(activity, R.string.sign_in_ok);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user cancelled the sign-in, e.g. they hit the back button
                //getViewUtils().displayInfoMessage(this, R.string.sign_in_cancelled);
                // finish the activity
                activity.finish();
            }
        // Are we are returning from an admin screen (perhaps via back arrow on sign-in screen)?
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
     * Returns whether the user is signed in.
     * @return true if the user is signed in, false otherwise
     */
    @Override
    public boolean isUserSignedIn() {
        FirebaseUser user = getFirebaseAuth().getCurrentUser();
        return (user != null);
    }

    /**
     * Performs processing required on user sign in.
     */
    private void onSignedInInitialise() {
        Timber.d("onSignedInInitialise");

        getMasterDatabase().onSignedIn();
    }

    /**
     * Performs processing required on user sign out.
     */
    private void onSignedOutCleanup() {
        Timber.d("onSignedOutCleanup");

        getMasterDatabase().onSignedOut();

        // TODO: Implement adapter sign out processing - pass adapters in?
//        mMovieAdapter.clear();
//        mAwardAdapter.clear();
    }


    //---------------------------------------------------------------------
    // Security methods

    @Override
    public void signOut(@Nullable FragmentActivity activity) {
        if (activity != null) {
            AuthUI.getInstance().signOut(activity);
        }
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
