package uk.jumpingmouse.moviecompanion.activity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.utils.NavUtils;
import uk.jumpingmouse.moviecompanion.utils.OmdbUtils;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

/**
 * The add movie activity.
 * Note that this is an admin activity, not a public-facing one.
 * @author Edmund Johnson
 */
public class AddMovieActivity extends AppCompatActivity {

    // Screen fields
    private EditText mTxtImdbId;
    private TextView mLabelTitle;
    private TextView mTxtTitle;
    private TextView mLabelGenre;
    private TextView mTxtGenre;
    private Button mBtnCancel;
    private Button mBtnSave;

    // Movie fetched from OMDb
    private Movie mMovie;

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

        mTxtImdbId = (EditText) findViewById(R.id.txtImdbId);
        mLabelTitle = (TextView) findViewById(R.id.labelTitle);
        mTxtTitle = (TextView) findViewById(R.id.txtTitle);
        mLabelGenre = (TextView) findViewById(R.id.labelGenre);
        mTxtGenre = (TextView) findViewById(R.id.txtGenre);
        mBtnCancel = (Button) findViewById(R.id.btnCancel);
        mBtnSave = (Button) findViewById(R.id.btnSave);
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
    // Action methods

    /**
     * Handles the user clicking the "Fetch Details" button.
     * @param view the view that was clicked
     */
    public void onFetchDetails(@Nullable  View view) {
        String imdbId = mTxtImdbId.getText().toString();
        if (imdbId.trim().isEmpty()) {
            return;
        }

        new FetchMovieTask(this).execute(imdbId, null, null);
    }

    /**
     * Handles the completion of a movie being fetched from the server.
     * @param movie the movie that was fetched
     */
    private void onFetchDetailsCompleted(@Nullable  Movie movie) {
        if (movie == null) {
            // Display a "Movie not found" error message
            getViewUtils().displayErrorMessage(this, R.string.movie_not_found);
        } else {
            // Display the fetched movie
            mMovie = movie;
            displayMovie(mMovie);
        }
    }

    /**
     * Handles the user clicking the "Cancel" button.
     * @param view the view that was clicked
     */
    public void onCancel(@Nullable View view) {
        clearMovie();
        if (view != null) {
            getViewUtils().displayInfoMessage(view.getContext(),
                    getString(R.string.movie_not_saved, mTxtTitle.getText()));
        }
    }

    /**
     * Handles the user clicking the "Save" button.
     * @param view the view that was clicked
     */
    public void onSave(@Nullable View view) {
        if (view == null) {
            return;
        }

        Uri uriInserted = getContentResolver().insert(
                DataContract.MovieEntry.CONTENT_URI, mMovie.toContentValues());

        if (uriInserted == null) {
            getViewUtils().displayErrorMessage(view.getContext(),
                    getString(R.string.movie_not_saved, mMovie.getImdbId()));
        } else {
            getViewUtils().displayInfoMessage(view.getContext(),
                    getString(R.string.movie_saved, mMovie.getTitle()));
            clearMovie();
        }

    }

    /**
     * Displays a movie on the screen.
     * @param movie the movie to display
     */
    private void displayMovie(@NonNull Movie movie) {
        getViewUtils().dismissKeyboard(this);

        mTxtTitle.setText(movie.getTitle());
        mTxtGenre.setText(movie.getGenre());

        showValueFields();
    }

    /**
     * Clears the displayed movie from the screen.
     */
    private void clearMovie() {
        clearValueFields();
        hideValueFields();
    }

    /**
     * Clears the contents of the movie fields.
     */
    private void clearValueFields() {
        mTxtImdbId.setText(null);
        mTxtTitle.setText(null);
        mTxtGenre.setText(null);
    }

    /**
     * Hides the movie value fields.
     */
    private void hideValueFields() {
        hideView(mLabelTitle);
        hideView(mTxtTitle);
        hideView(mLabelGenre);
        hideView(mTxtGenre);
        hideView(mBtnCancel);
        hideView(mBtnSave);
    }

    /**
     * Shows the movie value fields.
     */
    private void showValueFields() {
        showView(mLabelTitle);
        showView(mTxtTitle);
        showView(mLabelGenre);
        showView(mTxtGenre);
        showView(mBtnCancel);
        showView(mBtnSave);
    }

    /**
     * Hides a view.
     * @param view the view
     */
    private void hideView(View view) {
        getViewUtils().hideView(view);
    }

    /**
     * Shows a view.
     * @param view the view
     */
    private void showView(View view) {
        getViewUtils().showView(view);
    }

    //---------------------------------------------------------------------
    // Getters

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

    /**
     * Convenience method which returns a reference to a OmdbUtils object.
     * @return a reference to a OmdbUtils object
     */
    @NonNull
    private static OmdbUtils getOmdbUtils() {
        return ObjectFactory.getOmdbUtils();
    }

    //---------------------------------------------------------------------
    // FetchMovieTask

    /**
     * A task which fetches a movie from the OMDb and displays it on the screen.
     */
    private static class FetchMovieTask extends AsyncTask<String, Integer, Movie> {
        private final WeakReference<AddMovieActivity> mActivity;

        /**
         * Constructor.
         * @param activity the activity which initiated this task
         */
        FetchMovieTask(AddMovieActivity activity) {
            super();
            mActivity = new WeakReference<>(activity);
        }

        /**
         * Fetches the movie from the OMDb and returns it.
         * This is run in the background thread.
         * @param args the arguments passed to the background task, the first is the IMDb id
         * @return the movie with the specified IMDb id, or null if movie not found
         */
        @Override
        @WorkerThread
        @Nullable
        protected Movie doInBackground(@Nullable final String... args) {
            if (args != null && args.length > 0) {
                String imdbId = args[0];
                if (imdbId != null && !imdbId.isEmpty()) {
                    // fetch and return the Movie
                    return getOmdbUtils().fetchMovie(imdbId);
                }
            }
            return null;
        }

        /**
         * This is run in the UI thread when doInBackground() has completed.
         * @param movie the movie that was fetched in the background
         */
        @Override
        @UiThread
        protected void onPostExecute(final Movie movie) {
            if (mActivity != null && mActivity.get() != null) {
                AddMovieActivity activity = mActivity.get();

                activity.onFetchDetailsCompleted(movie);
            }
        }

    }

}
