package uk.jumpingmouse.moviecompanion.utils;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Class containing utility methods related to navigation.
 * This class contains the methods which are available only to the free product flavour.
 * @author Edmund Johnson
 */
public class NavUtilsFree extends NavUtils {

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
            sNavUtils = new NavUtilsFree();
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
        // there are no menu items specific to the free product flavour
        return false;
    }

    /**
     * Process selection of an item from the MovieFragment options menu which is specific to a
     * product flavour.
     * @param activity the activity on which the menu was displayed
     * @param item the menu item that was selected
     * @return false to allow normal menu processing to proceed,
     *         true if menu processing is consumed here.
     */
    @Override
    public boolean onFlavourSpecificItemSelectedMovieFragment(
            @NonNull AppCompatActivity activity, @NonNull MenuItem item) {
        // there are no menu items specific to the free product flavour
        return false;
    }

}
