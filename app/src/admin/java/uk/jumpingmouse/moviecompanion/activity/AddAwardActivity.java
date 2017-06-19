package uk.jumpingmouse.moviecompanion.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.Award;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.model.MasterDatabase;
import uk.jumpingmouse.moviecompanion.security.SecurityManager;
import uk.jumpingmouse.moviecompanion.utils.JavaUtils;
import uk.jumpingmouse.moviecompanion.utils.ModelUtils;
import uk.jumpingmouse.moviecompanion.utils.NavUtils;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;
import uk.jumpingmouse.omdbapi.OmdbApi;

/**
 * The add award activity.
 * This is an admin activity, not a public-facing one, so the UI can be fairly basic.
 * @author Edmund Johnson
 */
public class AddAwardActivity extends AppCompatActivity {

    // Bundle keys, e.g. for use when saving and restoring state
    private static final String KEY_MOVIE = "KEY_MOVIE";
    private static final String KEY_AWARD = "KEY_AWARD";

    // Screen fields
    private EditText mTxtImdbId;
    private TextView mLabelTitle;
    private TextView mTxtTitle;
    private TextView mLabelAwardDate;
    private EditText mTxtAwardDate;
    private TextView mLabelAwardCategory;
    private RadioGroup mRadioAwardCategory;
    private RadioButton mRadioAwardCategoryMovie;
    private RadioButton mRadioAwardCategoryDvd;
    private TextView mLabelReview;
    private EditText mTxtReview;
    private TextView mLabelAwardDisplayOrder;
    private EditText mTxtAwardDisplayOrder;
    private Button mBtnCancel;
    private Button mBtnSave;

    // Movie fetched from OMDb
    private Movie mMovie;
    // Award
    private Award mAward;

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

        // Initialise the app bar
        getViewUtils().initialiseAppBar(this, R.id.tbAppBar, getString(R.string.app_name), false);

        mTxtImdbId = (EditText) findViewById(R.id.txtImdbId);
        mLabelTitle = (TextView) findViewById(R.id.labelTitle);
        mTxtTitle = (TextView) findViewById(R.id.txtTitle);
        mLabelAwardDate = (TextView) findViewById(R.id.labelAwardDate);
        mTxtAwardDate = (EditText) findViewById(R.id.txtAwardDate);
        mLabelAwardCategory = (TextView) findViewById(R.id.labelCategory);
        mRadioAwardCategory = (RadioGroup) findViewById(R.id.radioCategory);
        mRadioAwardCategoryMovie = (RadioButton) findViewById(R.id.radioCategoryMovie);
        mRadioAwardCategoryDvd = (RadioButton) findViewById(R.id.radioCategoryDvd);
        mLabelReview = (TextView) findViewById(R.id.labelReview);
        mTxtReview = (EditText) findViewById(R.id.txtReview);
        mLabelAwardDisplayOrder = (TextView) findViewById(R.id.labelDisplayOrder);
        mTxtAwardDisplayOrder = (EditText) findViewById(R.id.txtDisplayOrder);
        mBtnCancel = (Button) findViewById(R.id.btnCancel);
        mBtnSave = (Button) findViewById(R.id.btnSave);

        if (savedInstanceState != null) {
            mMovie = savedInstanceState.getParcelable(KEY_MOVIE);
            mAward = savedInstanceState.getParcelable(KEY_AWARD);
            if (mMovie != null) {
                displayData(mMovie);
            }
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

    /**
     * Called to retrieve per-instance state from an activity before being killed so that the
     * state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     * @param outState the Bundle populated by this method
     */
    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(KEY_MOVIE, mMovie);
        outState.putParcelable(KEY_AWARD, mAward);
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
     * Handles the user clicking the "Fetch Movie Details" button.
     * @param view the view that was clicked
     */
    public void onFetchMovieDetails(@Nullable View view) {
        String imdbId = mTxtImdbId.getText().toString();
        if (imdbId.trim().isEmpty()) {
            return;
        }
        int id = ModelUtils.imdbIdToMovieId(imdbId);
        Uri uri = DataContract.MovieEntry.buildUriForRowById(id);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            // Display a "Movie not found" error message
            getViewUtils().displayErrorMessage(this, R.string.movie_not_found);
        } else if (cursor.getCount() > 1) {
            // Display a "Multiple movies found" error message
            getViewUtils().displayErrorMessage(this, R.string.multiple_matching_movies);
        } else {
            cursor.moveToFirst();
            Movie movie = ModelUtils.newMovie(cursor);
            if (movie == null) {
                // Display a "Data found did not represent a Movie" error message
                getViewUtils().displayErrorMessage(this, R.string.movie_data_not_found);
            } else {
                mMovie = movie;
                displayData(mMovie);
            }
            cursor.close();
        }
    }

//    /**
//     * Handles the user clicking one of the "Award Category" radio buttons (Movie/DVD of the week).
//     * @param view the view that was clicked
//     */
//    public void onCategoryClicked(View view) {
//    }

    /**
     * Handles the user clicking the "Cancel" button.
     * @param view the view that was clicked
     */
    public void onCancel(@Nullable View view) {
        resetData();
        if (view != null) {
            getViewUtils().displayInfoMessage(view.getContext(),
                    getString(R.string.award_not_saved, mTxtTitle.getText()));
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

        int movieId = mMovie.getId();
        String awardDate = mTxtAwardDate.getText().toString();
        String category = mRadioAwardCategory.getCheckedRadioButtonId() ==
                R.id.radioCategoryMovie ? Award.CATEGORY_MOVIE : Award.CATEGORY_DVD;
        int displayOrder = JavaUtils.toInt(mTxtAwardDisplayOrder.getText().toString(), 1);
        // Generate the unique award id, e.g. "3666024_170522_M"
        String id = movieId + "_" + awardDate + "_" + category;

        mAward = Award.builder()
                .id(id)
                .movieId(movieId)
                .awardDate(awardDate)
                .category(category)
                .review(mTxtReview.getText().toString())
                .displayOrder(displayOrder)
                .build();

        Context context = view.getContext();
        int rowsAdded = getMasterDatabase().addAward(context, mAward);
        if (rowsAdded == 0) {
            getViewUtils().displayErrorMessage(context,
                    getString(R.string.award_not_saved, mMovie.getTitle()));
        } else {
            getViewUtils().displayInfoMessage(context,
                     getString(R.string.saving_award, mMovie.getTitle()));
            resetData();
        }

    }

    /**
     * Displays movie data on the screen.
     * @param movie the movie to display
     */
    private void displayData(@NonNull Movie movie) {
        getViewUtils().dismissKeyboard(this);

        // disable data entry until Save or Cancel clicked
        mTxtImdbId.setEnabled(false);

        setValueFields(movie);
        showValueFields();
    }

    /**
     * Resets the screen, leaving it ready for input.
     */
    private void resetData() {
        mMovie = null;
        mAward = null;

        clearValueFields();
        hideValueFields();

        // Enable entry of a new movie
        mTxtImdbId.setEnabled(true);
    }

    /**
     * Sets the contents of the data value fields.
     * @param movie the movie whose data is to be displayed
     */
    private void setValueFields(@NonNull Movie movie) {
        mTxtTitle.setText(movie.getTitle());
    }

    /**
     * Shows the data value fields.
     */
    private void showValueFields() {
        showView(mLabelTitle);
        showView(mTxtTitle);
        showView(mLabelAwardDate);
        showView(mTxtAwardDate);
        showView(mLabelAwardCategory);
        showView(mRadioAwardCategory);
        mRadioAwardCategoryMovie.setChecked(true);
        mRadioAwardCategoryDvd.setChecked(false);
        showView(mLabelReview);
        showView(mTxtReview);
        showView(mLabelAwardDisplayOrder);
        showView(mTxtAwardDisplayOrder);
        mTxtAwardDisplayOrder.setText(R.string.display_order_default);

        showView(mBtnCancel);
        showView(mBtnSave);
    }

    /**
     * Clears the contents of the data value fields.
     */
    private void clearValueFields() {
        // movie fields
        mTxtImdbId.setText(null);

        // award fields
        mTxtTitle.setText(null);
        mTxtAwardDate.setText(null);
        mRadioAwardCategoryMovie.setChecked(false);
        mRadioAwardCategoryDvd.setChecked(false);
        mTxtReview.setText(null);
        mTxtAwardDisplayOrder.setText(null);
    }

    /**
     * Hides the data value fields.
     */
    private void hideValueFields() {
        hideView(mLabelTitle);
        hideView(mTxtTitle);
        hideView(mLabelAwardDate);
        hideView(mTxtAwardDate);
        hideView(mLabelAwardCategory);
        hideView(mRadioAwardCategory);
        hideView(mLabelReview);
        hideView(mTxtReview);
        hideView(mLabelAwardDisplayOrder);
        hideView(mTxtAwardDisplayOrder);
        hideView(mBtnCancel);
        hideView(mBtnSave);
    }

    //---------------------------------------------------------------
    // Util methods

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
     * Convenience method for returning a reference to the database helper.
     * @return a reference to the database helper
     */
    @NonNull
    private static MasterDatabase getMasterDatabase() {
        return ObjectFactory.getMasterDatabase();
    }

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

    /**
     * Convenience method which returns a reference to an OmdbApi object.
     * @return a reference to an OmdbApi object
     */
    @NonNull
    private static OmdbApi getOmdbApi() {
        return OmdbApi.getInstance();
    }

}
