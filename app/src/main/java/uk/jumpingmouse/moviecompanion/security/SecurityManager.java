package uk.jumpingmouse.moviecompanion.security;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Interface for classes which implement security.
 * @author Edmund Johnson
 */
public interface SecurityManager {

    // Request codes
    // Use RC_SIGN_IN rather than the Firebase automatically-generated static import
    int RC_SIGN_IN = 1;
    int RC_ADD_MOVIE = 2;
    int RC_ADD_AWARD = 3;

    /**
     * Perform security processing required when creating activity.
     * @param activity the activity being created
     */
    void onCreateActivity(@NonNull AppCompatActivity activity);

    /**
     * Perform security processing required when resuming activity.
     */
    void onResumeActivity();

    /**
     * Perform security processing required when pausing activity.
     */
    void onPauseActivity();

    /**
     * Check whether the user has clicked the back button from the sign-in screen.
     * If so, exit the app rather than displaying the sign-in screen again.
     * Note that this method is called before onResume(), which displays the sign-in screen
     * if the user is not signed in.
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the returned data
     */
    void onActivityResult(@NonNull AppCompatActivity activity, int requestCode,
                          int resultCode, @Nullable Intent data);

    /**
     * Returns whether the user is signed in.
     * @return true if the user is signed in, false otherwise
     */
    boolean isUserSignedIn();

    /**
     * Sign the user out.
     * @param activity the current activity
     */
    void signOut(@Nullable AppCompatActivity activity);

    /**
     * Returns the unique user id of the user who is signed in.
     * @return the unique user id of the user who is signed in, or null if no user is signed in
     */
    @Nullable
    String getUid();

}
