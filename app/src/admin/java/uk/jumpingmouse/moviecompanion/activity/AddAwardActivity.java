package uk.jumpingmouse.moviecompanion.activity;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.utils.NavUtils;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;
import uk.jumpingmouse.omdbapi.OmdbApi;
import uk.jumpingmouse.omdbapi.OmdbHandler;
import uk.jumpingmouse.omdbapi.OmdbMovie;

/**
 * The add award activity.
 * Note that this is an admin activity, not a public-facing one.
 * @author Edmund Johnson
 */
public class AddAwardActivity extends AppCompatActivity implements OmdbHandler {

    // Screen fields
    private EditText mTxtImdbId;
    private TextView mLabelTitle;
    private TextView mTxtTitle;
    private TextView mLabelGenre;
    private TextView mTxtGenre;
    private Button mBtnCancel;
    private Button mBtnSave;

    // Movie fetched from OMDb
    //private Movie mMovie;
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
        setContentView(R.layout.activity_add_award);

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
        // Get the OMDb API key from the local.properties file
        String omdbApiKey = getString(R.string.omdbapi_key);
        if (omdbApiKey.trim().isEmpty()) {
            getViewUtils().displayErrorMessage(this, R.string.omdbapi_key_missing);
            return;
        }
        getOmdbApi().fetchMovie(omdbApiKey, imdbId, this);
    }

    /**
     * Handles the completion of a movie being fetched from the server.
     * @param omdbMovie the movie that was fetched
     */
    @Override
    public void onFetchMovieCompleted(@Nullable OmdbMovie omdbMovie) {
        Movie movie = toMovie(omdbMovie);
        if (movie == null) {
            // Display a "Movie not found" error message
            getViewUtils().displayErrorMessage(this, R.string.movie_not_found);
        } else {
            // Save and display the fetched movie
            mMovie = movie;
            displayMovie(mMovie);
        }
    }

    /**
     * Handles the user clicking the "Cancel" button.
     * @param view the view that was clicked
     */
    public void onCancel(@Nullable View view) {
        clearAward();
//        if (view != null) {
//            getViewUtils().displayInfoMessage(view.getContext(),
//                    getString(R.string.movie_not_saved, mTxtTitle.getText()));
//        }
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
                DataContract.MovieEntry.CONTENT_URI, toContentValues(mMovie));

        if (uriInserted == null) {
            getViewUtils().displayErrorMessage(view.getContext(),
                    getString(R.string.movie_not_saved, mMovie.getImdbId()));
        } else {
            getViewUtils().displayInfoMessage(view.getContext(),
                     getString(R.string.saving_movie, mMovie.getTitle()));
            clearAward();
        }

    }

    /**
     * Converts an OmdbMovie to a Movie and returns it.
     * @param omdbMovie the OmdbMovie
     * @return a Movie corresponding to omdbMovie
     */
    private Movie toMovie(OmdbMovie omdbMovie) {
        if (omdbMovie == null) {
            Timber.w("toMovie: omdbMovie is null");
            return null;
        } else if (omdbMovie.getImdbID() == null) {
            Timber.w("toMovie: omdbMovie.imdbId is null");
            return null;
        } else if (omdbMovie.getTitle() == null) {
            Timber.w("toMovie: omdbMovie.title is null");
            return null;
        }

        // Build and return the movie
        int runtime = OmdbApi.toIntOmdbRuntime(omdbMovie.getRuntime());
        long released = OmdbApi.toLongOmdbReleased(omdbMovie.getReleased());
        return Movie.builder()
                .id(omdbMovie.getImdbID())
                .imdbId(omdbMovie.getImdbID())
                .title(omdbMovie.getTitle())
                .genre(omdbMovie.getGenre())
                .runtime(runtime)
                .poster(omdbMovie.getPoster())
                .year(omdbMovie.getYear())
                .released(released)
                .build();
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
    private void clearAward() {
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

    //---------------------------------------------------------------
    // Util methods

    /**
     * Returns a set of ContentValues corresponding to the movie.
     * @return the set of ContentValues corresponding to the movie
     */
    @NonNull
    private ContentValues toContentValues(@NonNull Movie movie) {
        ContentValues values = new ContentValues();

        values.put(DataContract.MovieEntry.COLUMN_ID, movie.getId());
        values.put(DataContract.MovieEntry.COLUMN_IMDB_ID, movie.getImdbId());
        values.put(DataContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(DataContract.MovieEntry.COLUMN_YEAR, movie.getYear());
        values.put(DataContract.MovieEntry.COLUMN_RELEASED, movie.getReleased());
        values.put(DataContract.MovieEntry.COLUMN_RUNTIME, movie.getRuntime());
        values.put(DataContract.MovieEntry.COLUMN_GENRE, movie.getGenre());
        values.put(DataContract.MovieEntry.COLUMN_POSTER, movie.getPoster());

        return values;
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
     * Convenience method which returns a reference to an OmdbApi object.
     * @return a reference to an OmdbApi object
     */
    @NonNull
    private static OmdbApi getOmdbApi() {
        return OmdbApi.getInstance();
    }

}
