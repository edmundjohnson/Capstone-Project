package uk.jumpingmouse.moviecompanion.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.utils.NavUtils;

/**
 * The add movie activity.
 * @author Edmund Johnson
 */
public class AddMovieActivity extends AppCompatActivity {

    //---------------------------------------------------------------------
    // Lifecycle methods

    /**
     * Perform activity initialisation.
     * @param savedInstanceState If the activity is being re-initialised after previously
     *     being shut down then this Bundle contains the data it most recently supplied
     *     in {@link #onSaveInstanceState}, otherwise it is null.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);
    }

//    /**
//     * Perform processing required when the activity becomes able to interact with the user.
//     */
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }

//    /**
//     * Perform processing required when the activity becomes unable to interact with the user.
//     */
//    @Override
//    protected void onPause() {
//
//
//        super.onPause();
//    }

    //---------------------------------------------------------------------
    // Navigation methods

    /**
     * Initialise the contents of the activity's options menu.
     * @param menu the options menu in which items are placed
     * @return true, which results in the menu being displayed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Process selection of an item in the options menu.
     * @param item the menu item that was selected
     * @return false to allow normal menu processing to proceed,
     *         true if menu processing is consumed here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean consumed = getNavUtils().onOptionsItemSelected(this, item);
        //noinspection SimplifiableConditionalExpression
        return consumed ? true : super.onOptionsItemSelected(item);
    }

    //---------------------------------------------------------------------
    // Getters

//    /**
//     * Convenience method which returns a reference to a ViewUtils object.
//     * @return a reference to a ViewUtils object
//     */
//    @NonNull
//    private static ViewUtils getViewUtils() {
//        return ObjectFactory.getViewUtils();
//    }

    /**
     * Convenience method which returns a reference to a NavUtils object.
     * @return a reference to a NavUtils object
     */
    @NonNull
    private static NavUtils getNavUtils() {
        return ObjectFactory.getNavUtils();
    }

}
