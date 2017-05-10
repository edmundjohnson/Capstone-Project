package uk.jumpingmouse.moviecompanion.security;

import android.support.v4.app.FragmentActivity;

/**
 * Interface for classes which implement security.
 * @author Edmund Johnson
 */
public interface SecurityManager {

    // Use this rather than, for example, the Firebase automatically-generated static import
    int RC_SIGN_IN = 1;

    /**
     * Perform security processing required when creating activity.
     * @param activity the activity being created
     */
    void onCreateActivity(FragmentActivity activity);

    /**
     * Perform security processing required when resuming activity.
     */
    void onResumeActivity();

    /**
     * Perform security processing required when pausing activity.
     */
    void onPauseActivity();

    /**
     * Sign the user out.
     * @param activity the current activity
     */
    void signOut(FragmentActivity activity);

}
