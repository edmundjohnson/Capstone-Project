package uk.jumpingmouse.moviecompanion.utils;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.activity.AddAwardActivity;
import uk.jumpingmouse.moviecompanion.activity.AddMovieActivity;
import uk.jumpingmouse.moviecompanion.activity.EditAwardActivity;
import uk.jumpingmouse.moviecompanion.data.ViewAward;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.security.SecurityManager;

/**
 * Class containing utility methods related to navigation.
 * This class contains the methods which are available only to the admin product flavour.
 * @author Edmund Johnson
 */
public class NavUtilsAdmin extends NavUtils {

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
            sNavUtils = new NavUtilsAdmin();
        }
        return sNavUtils;
    }

    //---------------------------------------------------------------------
    // Utility methods

    /**
     * Process selection of an item from the MainActivity options menu which is specific to a
     * product flavour.
     * @param activity the activity on which the menu was displayed
     * @param item the menu item that was selected
     * @return false to allow normal menu processing to proceed,
     *         true if menu processing is consumed here.
     */
    @Override
    public boolean onFlavourSpecificItemSelectedMainActivity(
            @NonNull AppCompatActivity activity, @NonNull MenuItem item) {
        switch (item.getItemId()) {
            // add movie
            case R.id.menu_option_add_movie:
                displayAddMovie(activity);
                return true;

            // add award
            case R.id.menu_option_add_award:
                displayAddAward(activity);
                return true;

            default:
                return false;
        }
    }

    /**
     * Process selection of an item from the MovieFragment options menu which is specific to a
     * product flavour.
     * @param activity the activity on which the menu was displayed
     * @param item the menu item that was selected
     * @param viewAward the displayed ViewAward
     * @return false to allow normal menu processing to proceed,
     *         true if menu processing is consumed here.
     */
    @Override
    public boolean onFlavourSpecificItemSelectedMovieFragment(@NonNull AppCompatActivity activity,
                                          @NonNull MenuItem item, @NonNull ViewAward viewAward) {
        switch (item.getItemId()) {
            // edit award
            case R.id.menu_option_edit_award:
                displayEditAward(activity, viewAward.getId());
                return true;

            default:
                return false;
        }
    }

    /**
     * Displays the add movie screen.
     * @param activity the activity invoking the activity to be displayed
     */
    private void displayAddMovie(@NonNull AppCompatActivity activity) {
        displayActivity(activity, AddMovieActivity.class, SecurityManager.RC_ADD_MOVIE);
    }

    /**
     * Displays the add award screen.
     * @param activity the activity invoking the activity to be displayed
     */
    private void displayAddAward(@NonNull AppCompatActivity activity) {
        displayActivity(activity, AddAwardActivity.class, SecurityManager.RC_ADD_AWARD);
    }

    /**
     * Displays the edit award screen.
     * @param activity the activity invoking the activity to be displayed
     * @param awardId the id of the award to be edited
     */
    private void displayEditAward(@NonNull AppCompatActivity activity, @NonNull String awardId) {
        // Build an intent for launching the edit award activity
        Uri uri = DataContract.AwardEntry.buildUriForRowById(awardId);
        Intent intent = new Intent(activity, EditAwardActivity.class).setData(uri);
        activity.startActivity(intent);
    }

    /**
     * Displays an activity.
     * @param activity the activity invoking the activity to be displayed
     * @param activityClass the class of the activity to be displayed, e.g. AddMovieActivity.class
     * @param requestCode the request code to pass to the activity to be displayed
     */
    private void displayActivity(@NonNull AppCompatActivity activity, @NonNull Class activityClass,
                                 int requestCode) {
        Intent intent = new Intent(activity, activityClass);
        // We always do a startActivityForResult(...) because we could sign out
        // on any activity, and then all activities on the stack must be able
        // to detect this and finish (in onActivityResult(...)).
        activity.startActivityForResult(intent, requestCode);
    }

}
