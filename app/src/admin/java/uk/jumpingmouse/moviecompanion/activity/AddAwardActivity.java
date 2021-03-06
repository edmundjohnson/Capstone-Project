package uk.jumpingmouse.moviecompanion.activity;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import uk.jumpingmouse.moviecompanion.utils.JavaUtils;
import uk.jumpingmouse.moviecompanion.utils.ModelUtils;
import uk.jumpingmouse.moviecompanion.utils.NavUtils;
import uk.jumpingmouse.moviecompanion.utils.NetUtils;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

/**
 * The add award activity.
 * This is an admin activity, not a public-facing one, so the UI can be fairly basic.
 * @author Edmund Johnson
 */
public final class AddAwardActivity extends AppCompatActivity {

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

    // The movie to which an award is being added
    private Movie mMovie;
    // The award being added
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
        getViewUtils().initialiseAppBar(this, R.id.tbAppBar,
                getString(R.string.title_add_award), true, R.color.colorPrimary);

        mTxtImdbId = findViewById(R.id.txtImdbId);
        mLabelTitle = findViewById(R.id.labelTitle);
        mTxtTitle = findViewById(R.id.txtTitle);
        mLabelAwardDate = findViewById(R.id.labelAwardDate);
        mTxtAwardDate = findViewById(R.id.txtAwardDate);
        mLabelAwardCategory = findViewById(R.id.labelCategory);
        mRadioAwardCategory = findViewById(R.id.radioCategory);
        mRadioAwardCategoryMovie = findViewById(R.id.radioCategoryMovie);
        mRadioAwardCategoryDvd = findViewById(R.id.radioCategoryDvd);
        mLabelReview = findViewById(R.id.labelReview);
        mTxtReview = findViewById(R.id.txtReview);
        mLabelAwardDisplayOrder = findViewById(R.id.labelDisplayOrder);
        mTxtAwardDisplayOrder = findViewById(R.id.txtDisplayOrder);
        mBtnCancel = findViewById(R.id.btnCancel);
        mBtnSave = findViewById(R.id.btnSave);

        if (savedInstanceState != null) {
            mMovie = savedInstanceState.getParcelable(KEY_MOVIE);
            mAward = savedInstanceState.getParcelable(KEY_AWARD);
            if (mMovie != null) {
                displayData(mMovie);
            }
        }
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
        outState.putParcelable(KEY_AWARD, mAward);
    }

    //---------------------------------------------------------------------
    // Action methods

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
        String id = ModelUtils.imdbIdToMovieId(imdbId);
        if (id == null) {
            // Display an "Invalid IMDb" error message
            getViewUtils().displayErrorMessage(this, R.string.invalid_imdb);
            return;
        }

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

    /**
     * Handles the user clicking the "Cancel" button.
     * @param view the view that was clicked
     */
    public void onCancel(@Nullable View view) {
        if (view != null) {
            getViewUtils().displayInfoMessage(view.getContext(),
                    getString(R.string.award_not_saved, mTxtTitle.getText()));
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

        String awardDate = mTxtAwardDate.getText().toString();
        String category =
                mRadioAwardCategory.getCheckedRadioButtonId() == R.id.radioCategoryMovie
                        ? Award.CATEGORY_MOVIE : Award.CATEGORY_DVD;
        int displayOrder = JavaUtils.toInt(mTxtAwardDisplayOrder.getText().toString(), 1);
        String review = mTxtReview.getText().toString();

        if (awardDate.isEmpty() || category.isEmpty() || displayOrder <= 0 || review.isEmpty()) {
            getViewUtils().displayErrorMessage(this, R.string.error_mandatory_field_missing);
            return;
        }

        // Generate the unique award id, e.g. "3666024_170522_M"
        String movieId = mMovie.getId();
        String id = movieId + "_" + awardDate + "_" + category;

        mAward = Award.builder()
                .id(id)
                .movieId(movieId)
                .awardDate(awardDate)
                .category(category)
                .review(review)
                .displayOrder(displayOrder)
                .build();

        Context context = view.getContext();
        if (context != null) {
            int rowsAdded = getMasterDatabase().addAward(context, mAward);
            if (rowsAdded == 0) {
                getViewUtils().displayErrorMessage(this,
                        getString(R.string.award_not_saved, mMovie.getTitle()));
            } else {
                getViewUtils().displayInfoMessage(context,
                        getString(R.string.saving_award, mMovie.getTitle()));
                resetData();
            }
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
    // Utility methods

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

}
