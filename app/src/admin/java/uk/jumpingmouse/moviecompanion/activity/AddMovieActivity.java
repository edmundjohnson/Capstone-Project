package uk.jumpingmouse.moviecompanion.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.model.MasterDatabase;
import uk.jumpingmouse.moviecompanion.moviedb.MovieDbHandler;
import uk.jumpingmouse.moviecompanion.moviedb.MovieDbReceiver;
import uk.jumpingmouse.moviecompanion.utils.ModelUtils;
import uk.jumpingmouse.moviecompanion.utils.NavUtils;
import uk.jumpingmouse.moviecompanion.utils.NetUtils;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

/**
 * The add movie activity.
 * This is an admin activity, not a public-facing one, so the UI can be fairly basic.
 * @author Edmund Johnson
 */
public final class AddMovieActivity extends AppCompatActivity implements MovieDbReceiver {

    // Bundle keys, e.g. for use when saving and restoring state
    private static final String KEY_MOVIE = "KEY_MOVIE";

    // Screen fields
    private EditText mTxtImdbId;
    private TextView mLabelTitle;
    private TextView mTxtTitle;
    private TextView mLabelGenre;
    private TextView mTxtGenre;
    private Button mBtnCancel;
    private Button mBtnSave;

    // The movie being added, as fetched from the remote database
    private Movie mMovie;

    // A movie database handler for this instance of this activity
    private MovieDbHandler mMovieDbHandler;

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

        // Initialise the app bar
        getViewUtils().initialiseAppBar(this, R.id.tbAppBar,
                getString(R.string.title_add_movie), true, R.color.colorPrimary);

        mTxtImdbId = findViewById(R.id.txtImdbId);
        mLabelTitle = findViewById(R.id.labelTitle);
        mTxtTitle = findViewById(R.id.txtTitle);
        mLabelGenre = findViewById(R.id.labelGenre);
        mTxtGenre = findViewById(R.id.txtGenre);
        mBtnCancel = findViewById(R.id.btnCancel);
        mBtnSave = findViewById(R.id.btnSave);

        if (savedInstanceState != null) {
            mMovie = savedInstanceState.getParcelable(KEY_MOVIE);
            if (mMovie != null) {
                displayData(this, mMovie);
            }
        }

        mMovieDbHandler = newMovieDbHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Warn the user if there is no internet connection
        if (!getNetUtils().isNetworkAvailable(this)) {
            getViewUtils().displayInfoMessage(this, R.string.network_unavailable);
        }
    }

    /**
     * Called to retrieve per-instance state from an activity before being killed so that the
     * state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     * @param outState the Bundle populated by this method
     */
    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(KEY_MOVIE, mMovie);
    }

    //---------------------------------------------------------------------
    // Action methods

    /**
     * Handles the user clicking the "Cancel" button.
     * @param view the view that was clicked
     */
    public void onCancel(@Nullable View view) {
        if (view != null) {
            getViewUtils().displayInfoMessage(view.getContext(),
                    getString(R.string.movie_not_saved, mTxtTitle.getText()));
        }
        resetData();
    }

    /**
     * Handles the user clicking the "Save" button.
     * @param view the view that was clicked
     */
    public void onSave(@Nullable View view) {
        if (view == null) {
            return;
        }

        Context context = view.getContext();
        if (context != null) {
            int rowsAdded = getMasterDatabase().addMovie(context, mMovie);

            if (rowsAdded == 0) {
                getViewUtils().displayErrorMessage(this,
                        getString(R.string.movie_not_saved, mMovie.getImdbId()));
            } else {
                getViewUtils().displayInfoMessage(context,
                        getString(R.string.saving_movie, mMovie.getTitle()));
                resetData();
            }
        }

    }

    /**
     * Displays movie data on the screen.
     * @param context the context
     * @param movie the movie to display
     */
    private void displayData(@NonNull Context context, @NonNull Movie movie) {
        getViewUtils().dismissKeyboard(this);

        // disable data entry until Save or Cancel clicked
        mTxtImdbId.setEnabled(false);

        setValueFields(context, movie);
        showValueFields();
    }

    /**
     * Resets the screen, leaving it ready for input.
     */
    private void resetData() {
        mMovie = null;

        clearValueFields();
        hideValueFields();

        // Enable entry of a new movie
        mTxtImdbId.setEnabled(true);
    }

    /**
     * Sets the contents of the data value fields.
     * @param context the context
     * @param movie the movie whose data is to be displayed
     */
    private void setValueFields(@NonNull Context context, @NonNull Movie movie) {
        mTxtTitle.setText(movie.getTitle());
        mTxtGenre.setText(ModelUtils.toGenreNameCsv(context, movie.getGenre()));
    }

    /**
     * Shows the data value fields.
     */
    private void showValueFields() {
        // Display the movie details and Save/Cancel buttons
        showView(mLabelTitle);
        showView(mTxtTitle);
        showView(mLabelGenre);
        showView(mTxtGenre);
        showView(mBtnCancel);
        showView(mBtnSave);
    }

    /**
     * Clears the contents of the data value fields.
     */
    private void clearValueFields() {
        mTxtImdbId.setText(null);
        mTxtTitle.setText(null);
        mTxtGenre.setText(null);
    }

    /**
     * Hides the data value fields.
     */
    private void hideValueFields() {
        hideView(mLabelTitle);
        hideView(mTxtTitle);
        hideView(mLabelGenre);
        hideView(mTxtGenre);
        hideView(mBtnCancel);
        hideView(mBtnSave);
    }

    //---------------------------------------------------------------------
    // Implementation of the MovieDbReceiver interface

    /**
     * Handles the user clicking the "Fetch Movie Details" button.
     * @param view the view that was clicked, required because this method is set as
     *             an 'onClick' method in the layout xml
     */
    public void onFetchMovieDetails(@SuppressWarnings("UnusedParameters") @Nullable View view) {
        String imdbId = mTxtImdbId.getText().toString();
        if (imdbId.trim().isEmpty()) {
            return;
        }
        if (mMovieDbHandler != null) {
            int status = mMovieDbHandler.fetchMovieByImdbId(imdbId);
            if (status != MovieDbHandler.STATUS_SUCCESS) {
                getViewUtils().displayErrorMessage(
                        this, mMovieDbHandler.getErrorMessageForStatus(status));
            }
            // if status indicates success, onFetchMovieCompleted(...) will be called
        }
    }

    /**
     * Handles the completion of a movie being fetched from the movie database.
     * @param movie the movie that was fetched
     */
    @Override
    public void onFetchMovieCompleted(@Nullable Movie movie) {
        if (movie == null) {
            // Display a "Data found did not represent a Movie" error message
            getViewUtils().displayErrorMessage(this, R.string.movie_data_not_found);
        } else {
            // Save and display the fetched movie
            mMovie = movie;
            displayData(this, mMovie);
        }
    }

    /**
     * Return the receiver's context.
     * @return the receiver's context
     */
    @Override
    @NonNull
    public Context getReceiverContext() {
        return this;
    }

    //---------------------------------------------------------------
    // Utility methods

    /**
     * Shows a view.
     * @param view the view
     */
    private void showView(View view) {
        getViewUtils().showView(view);
    }

    /**
     * Hides a view.
     * @param view the view
     */
    private void hideView(View view) {
        getViewUtils().hideView(view);
    }

    //---------------------------------------------------------------------
    // Getters

    /**
     * Convenience method for returning a reference to the database helper.
     * @return a reference to the database helper
     */
    @NonNull
    private static MasterDatabase getMasterDatabase() {
        return ObjectFactory.getMasterDatabase();
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

    /**
     * Convenience method which returns a reference to a NetUtils object.
     * @return a reference to a NetUtils object
     */
    @NonNull
    private static NetUtils getNetUtils() {
        return ObjectFactory.getNetUtils();
    }

    /**
     * Convenience method which returns a reference to a new MovieDbHandler object.
     * @param movieDbReceiver the MovieDbReceiver to which the MovieDbHandler will pass any
     *                        data read from the remote database
     * @return a reference to a MovieDbHandler object
     */
    @NonNull
    private static MovieDbHandler newMovieDbHandler(@NonNull MovieDbReceiver movieDbReceiver) {
        return ObjectFactory.newMovieDbHandler(movieDbReceiver);
    }

}
