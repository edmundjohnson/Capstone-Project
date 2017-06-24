package uk.jumpingmouse.moviecompanion.activity;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

/**
 * The activity class for the movie screen.
 * @author Edmund Johnson
 */
public class MovieActivity extends AppCompatActivity {

    /** Tag for movie fragment. */
    private static final String TAG_MOVIE_FROM_LIST = "TAG_MOVIE_FROM_LIST";

    //---------------------------------------------------------------------------
    // Activity Lifecycle Methods

    /**
     * Perform initialisation on activity creation.
     * @param savedInstanceState a Bundle containing saved state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        // Initialise the app bar
        getViewUtils().initialiseAppBar(this, R.id.tbAppBar, "", true, 0);

        MovieFragment movieFragment = getMovieFragment();

        // Display the movie selected from the list.
        if (getIntent() != null) {
            Uri uri = getIntent().getData();

            // Note: We are re-using an already-existing instance of MovieFragment to display the
            // selected movie.  Therefore, we cannot call movieFragment.setArguments(), as that can
            // only be done on new fragments.  Instead, we use custom setArg...() methods.
            movieFragment.setArgViewAwardUri(uri);
        }

        //        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            // Get the movie from the passed-in URI
//            Uri movieUri = extras.getParcelable("VIEW_MOVIE_URI");
//
//            // Note: We are re-using an already-existing instance of MovieFragment to display the
//            // selected movie.  Therefore, we cannot call movieFragment.setArguments(), as that can
//            // only be done on new fragments.  Instead, we use custom setArg...() methods.
//            movieFragment.setArgViewAwardUri(movieUri);
//        }

        addFragmentToContainer(movieFragment);
    }

    /**
     * Perform UI processing for resumption of the activity.
     */
    @Override
    public void onResume() {
        super.onResume();

        this.supportInvalidateOptionsMenu();
    }

    //--------------------------------------------------------------
    // Utility methods

    /**
     * Return this activity's fragment.
     * @return this activity's fragment
     */
    @NonNull
    private MovieFragment getMovieFragment() {
        MovieFragment movieFragment = (MovieFragment)
                getSupportFragmentManager().findFragmentByTag(TAG_MOVIE_FROM_LIST);
        if (movieFragment == null) {
            movieFragment = MovieFragment.newInstance();
        }
        return movieFragment;
    }

    /**
     * Add a movie fragment to the movie fragment container.
     * @param movieFragment the movie fragment
     */
    private void addFragmentToContainer(@NonNull MovieFragment movieFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_container, movieFragment, TAG_MOVIE_FROM_LIST)
                .commit();
    }

    //--------------------------------------------------------------
    // Getters

    /**
     * Convenience method which returns a reference to a ViewUtils object.
     * @return a reference to a ViewUtils object
     */
    @NonNull
    private static ViewUtils getViewUtils() {
        return ObjectFactory.getViewUtils();
    }

}
