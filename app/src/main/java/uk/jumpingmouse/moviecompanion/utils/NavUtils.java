package uk.jumpingmouse.moviecompanion.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.security.SecurityManager;

/**
 * Superclass for classes containing utility methods related to navigation.
 * This class contains the methods which are available to all product flavours.
 * @author Edmund Johnson
 */
public abstract class NavUtils {

    //---------------------------------------------------------------------
    // Instance handling methods

    /** Default constructor. */
    NavUtils() {
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
    public abstract boolean onOptionsItemSelected(
            @Nullable FragmentActivity activity, @NonNull MenuItem item);

    //---------------------------------------------------------------------
    // Getters

    /**
     * Convenience method which returns a SecurityManager.
     * @return a SecurityManager
     */
    @NonNull
    static SecurityManager getSecurityManager() {
        return ObjectFactory.getSecurityManager();
    }

}
