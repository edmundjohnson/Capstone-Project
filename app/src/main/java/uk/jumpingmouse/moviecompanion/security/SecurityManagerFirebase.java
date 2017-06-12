package uk.jumpingmouse.moviecompanion.security;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.model.DatabaseHelper;

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
        // Initialise Firebase variables
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Authentication

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
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
                } else {
                    // user has just signed in
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
                }
            }
        };

    }

    /**
     * Perform security processing required when resuming activity.
     */
    @Override
    public void onResumeActivity() {
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    /**
     * Perform security processing required when pausing activity.
     */
    @Override
    public void onPauseActivity() {
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void onSignedInInitialise() {
        Timber.d("onSignedInInitialise");

//        mUsername = username;

        getDatabaseHelper().onSignedIn();
    }

    private void onSignedOutCleanup() {
        Timber.d("onSignedOutCleanup");

//        mUsername = ANONYMOUS;

        getDatabaseHelper().onSignedOut();

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

    /**
     * Convenience method for returning a reference to the database helper.
     * @return a reference to the database helper
     */
    @NonNull
    private static DatabaseHelper getDatabaseHelper() {
        return ObjectFactory.getDatabaseHelper();
    }

}
