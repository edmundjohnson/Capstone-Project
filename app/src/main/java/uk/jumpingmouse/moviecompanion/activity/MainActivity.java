package uk.jumpingmouse.moviecompanion.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.BuildConfig;
import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.security.SecurityManager;
import uk.jumpingmouse.moviecompanion.utils.NavUtils;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

/**
 * The main activity, i.e. the movie list.
 * @author Edmund Johnson
 */
public class MainActivity extends AppCompatActivity
        implements AwardListFragment.ListFragmentContainer {

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
        setContentView(R.layout.activity_main);

        // Initialise Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        //} else {
        //    // Read the Timber documentation for this
        //    Timber.plant(new CrashReportingTree());
        }

        // Initialise the app bar
        getViewUtils().initialiseAppBar(this, R.id.tbAppBar, getString(R.string.app_name), false, 0);

        // Initialise the security manager
        getSecurityManager().onCreateActivity(this);
    }

    /**
     * Perform processing required when the activity becomes able to interact with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();

        getSecurityManager().onResumeActivity();
    }

    /**
     * Perform processing required when the activity becomes unable to interact with the user.
     */
    @Override
    protected void onPause() {
        getSecurityManager().onPauseActivity();

//        mFilmAdapter.clear();
//        mAwardAdapter.clear();

//        detachDatabaseReadListener(mDatabaseReferenceFilms, mChildEventListenerFilms);
//        detachDatabaseReadListener(mDatabaseReferenceAwards, mChildEventListenerAwards);

        super.onPause();
    }

    /**
     * Check whether the user has clicked the back button from the sign-in screen.
     * If so, exit the app rather than displaying the sign-in screen again.
     * Note that this method is called before onResume(), which displays the sign-in screen
     * if the user is not signed in.
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the returned data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getSecurityManager().onActivityResult(this, requestCode, resultCode, data);
    }

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
        inflater.inflate(R.menu.menu_main, menu);
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

    /**
     * Callback for when an item has been selected.
     * @param context the context
     * @param uri     the URI of the selected list item
     */
    @Override
    public void onItemSelected(Context context, Uri uri) {
        displayViewMovie(context, uri);
    }

    /**
     * Display the movie activity.
     * @param context the context
     * @param uri     the URI of the selected list item
     */
    private void displayViewMovie(Context context, Uri uri) {
        // Launch movie activity
        Intent intent = new Intent(this, MovieActivity.class).setData(uri);
        startActivity(intent);

// Use the following code for shared element transitions
//        String transitionName = getString(R.string.detail_icon_transition_name);
//
//        @SuppressWarnings("unchecked")
//        ActivityOptionsCompat activityOptions =
//                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
//                        new Pair<View, String>(viewHolder.mIconView, transitionName));
//        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
    }

    //---------------------------------------------------------------------
    // Getters

    /**
     * Convenience method which returns a SecurityManager.
     * @return a SecurityManager
     */
    @NonNull
    private static SecurityManager getSecurityManager() {
        return ObjectFactory.getSecurityManager();
    }

    /**
     * Convenience method which returns a reference to a ViewUtils object.
     * @return a reference to a ViewUtils object
     */
    @NonNull
    private static ViewUtils getViewUtils() {
        return ObjectFactory.getViewUtils();
    }

    /**
     * Convenience method which returns a reference to a NavUtils object.
     * @return a reference to a NavUtils object
     */
    @NonNull
    private static NavUtils getNavUtils() {
        return ObjectFactory.getNavUtils();
    }

}
