package uk.jumpingmouse.moviecompanion.utils;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
            @Nullable AppCompatActivity activity, @NonNull MenuItem item);

    /**
     * Displays a web link in an external browser.
     * @param activity the activity invoking the activity to be displayed
     * @param url the web address to link to
     */
    public void displayWebLink(@Nullable AppCompatActivity activity, @NonNull String url) {
        if (activity != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            activity.startActivity(intent);
        }
    }

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
