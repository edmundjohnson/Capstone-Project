package uk.jumpingmouse.moviecompanion.utils;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import uk.jumpingmouse.moviecompanion.data.ViewAward;

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
     * Process selection of an item from the MainActivity options menu which is specific to a
     * product flavour.
     * @param activity the activity on which the menu was displayed
     * @param item the menu item that was selected
     * @return false to allow normal menu processing to proceed,
     *         true if menu processing is consumed here.
     */
    public abstract boolean onFlavourSpecificItemSelectedMainActivity(
            @NonNull AppCompatActivity activity, @NonNull MenuItem item);


    /**
     * Process selection of an item from the MovieFragment options menu which is specific to a
     * product flavour.
     * @param activity the activity on which the menu was displayed
     * @param item the menu item that was selected
     * @param viewAward the displayed ViewAward
     * @return false to allow normal menu processing to proceed,
     *         true if menu processing is consumed here.
     */
    public abstract boolean onFlavourSpecificItemSelectedMovieFragment(
            @NonNull AppCompatActivity activity, @NonNull MenuItem item,
            @NonNull ViewAward viewAward);


    /**
     * Displays a web link in an external browser.
     * @param activity the activity invoking the activity to be displayed
     * @param url the web address to link to
     */
    public void displayWebLink(@NonNull AppCompatActivity activity, @NonNull String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    //---------------------------------------------------------------------
    // Getters

}
