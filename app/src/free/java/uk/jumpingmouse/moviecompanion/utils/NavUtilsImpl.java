package uk.jumpingmouse.moviecompanion.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import uk.jumpingmouse.moviecompanion.R;

/**
 * Class containing utility methods related to navigation.
 * This class contains the methods which are available only to the free product flavour.
 * @author Edmund Johnson
 */
public class NavUtilsImpl extends NavUtils {

    /** The singleton instance of this class. */
    private static NavUtilsImpl sNavUtils = null;

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
     * Process selection of an item in the options menu.
     * @param activity the activity for which the menu is displayed
     * @param item the menu item that was selected
     * @return false to allow normal menu processing to proceed,
     *         true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(
            @Nullable FragmentActivity activity, @NonNull MenuItem item) {
        switch (item.getItemId()) {
            // sign out
            case R.id.menu_option_sign_out:
                getSecurityManager().signOut(activity);
                return true;

            default:
                return false;
        }
    }

}
