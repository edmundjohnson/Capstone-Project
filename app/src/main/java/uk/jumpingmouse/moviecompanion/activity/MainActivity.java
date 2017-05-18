package uk.jumpingmouse.moviecompanion.activity;

import android.content.Intent;
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
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

/**
 * The main activity, i.e. the movie list.
 * @author Edmund Johnson
 */
public class MainActivity extends AppCompatActivity {

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
//        mCriticAdapter.clear();

//        detachDatabaseReadListener(mDatabaseReferenceFilms, mChildEventListenerFilms);
//        detachDatabaseReadListener(mDatabaseReferenceAwards, mChildEventListenerAwards);
//        detachDatabaseReadListener(mDatabaseReferenceCritics, mChildEventListenerCritics);

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

        // Are we returning from the sign-in screen?
        if (requestCode == SecurityManager.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // The user signed in successfully
                //IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
                getViewUtils().displayInfoMessage(this, R.string.sign_in_ok);
            } else if (resultCode == RESULT_CANCELED) {
                // The user cancelled the sign-in, e.g. they hit the back button
                getViewUtils().displayInfoMessage(this, R.string.sign_in_cancelled);
                // finish the activity
                finish();
            }
        }
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
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Process selection of an item in the options menu.
     * @param item the menu item that was selected
     * @return false to allow normal menu processing to proceed,
     *         true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // sign out
            case R.id.sign_out_menu:
                getSecurityManager().signOut(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //---------------------------------------------------------------------
    // Getters

    /**
     * Convenience method which returns a SecurityManager.
     * @return a SecurityManager
     */
    @NonNull
    private SecurityManager getSecurityManager() {
        return ObjectFactory.getSecurityManager();
    }

    /**
     * Convenience method which returns a reference to a ViewUtils object.
     * @return a reference to a ViewUtils object
     */
    @NonNull
    private ViewUtils getViewUtils() {
        return ObjectFactory.getViewUtils();
    }
}
