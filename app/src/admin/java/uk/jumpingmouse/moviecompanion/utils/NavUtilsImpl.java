package uk.jumpingmouse.moviecompanion.utils;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.activity.AddAwardActivity;
import uk.jumpingmouse.moviecompanion.activity.AddMovieActivity;
import uk.jumpingmouse.moviecompanion.security.SecurityManager;

/**
 * Class containing utility methods related to navigation.
 * This class contains the methods which are available only to the admin product flavour.
 * @author Edmund Johnson
 */
public class NavUtilsImpl extends NavUtils {

    /** The singleton instance of this class. */
    private static NavUtils sNavUtils = null;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    public static NavUtils getInstance() {
        if (sNavUtils == null) {
            sNavUtils = new NavUtilsImpl();
        }
        return sNavUtils;
    }

    //---------------------------------------------------------------------
    // Utility methods

    /**
     * Processes the selection of an item in the options menu.
     * @param activity the activity for which the menu is being displayed
     * @param item the selected menu item
     * @return false to allow normal menu processing to proceed,
     *         true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@Nullable FragmentActivity activity,
                                         @NonNull MenuItem item) {
        switch (item.getItemId()) {
            // add movie
            case R.id.menu_option_add_movie:
                displayAddMovie(activity);
                return true;

            // add award
            case R.id.menu_option_add_award:
                displayAddAward(activity);
                return true;

            // sign out
            case R.id.menu_option_sign_out:
                getSecurityManager().signOut(activity);
                return true;

            default:
                // The caller must call super.onOptionsItemSelected(item) if this method
                // returns false
                return false;
        }
    }

    /**
     * Displays the add movie screen.
     * @param activity the activity invoking the activity to be displayed
     */
    private void displayAddMovie(@Nullable FragmentActivity activity) {
        displayActivity(activity, AddMovieActivity.class, SecurityManager.RC_ADD_MOVIE);
    }

    /**
     * Displays the add award screen.
     * @param activity the activity invoking the activity to be displayed
     */
    private void displayAddAward(@Nullable FragmentActivity activity) {
        displayActivity(activity, AddAwardActivity.class, SecurityManager.RC_ADD_AWARD);
    }

    /**
     * Displays an activity.
     * @param activity the activity invoking the activity to be displayed
     * @param activityClass the class of the activity, e.g. AddMovieActivity.class
     */
    private void displayActivity(@Nullable FragmentActivity activity, @NonNull Class activityClass,
                                 int requestCode) {
        if (activity != null) {
            Intent intent = new Intent(activity, activityClass);
            // We always do a startActivityForResult(...) because we could sign out
            // on any activity, and then all activities on the stack must be able
            // to detect this and finish (in onActivityResult(...)).
            activity.startActivityForResult(intent, requestCode);
        }
    }

}
