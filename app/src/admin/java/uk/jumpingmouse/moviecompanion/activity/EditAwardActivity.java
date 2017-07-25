package uk.jumpingmouse.moviecompanion.activity;

import android.content.Context;
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

import java.util.Locale;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.Award;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.model.LocalDatabase;
import uk.jumpingmouse.moviecompanion.model.MasterDatabase;
import uk.jumpingmouse.moviecompanion.utils.JavaUtils;
import uk.jumpingmouse.moviecompanion.utils.NavUtils;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;
import uk.jumpingmouse.omdbapi.OmdbApi;

/**
 * The edit award activity.
 * This is an admin activity, not a public-facing one, so the UI is fairly basic.
 * @author Edmund Johnson
 */
public class EditAwardActivity extends AppCompatActivity {

    // Bundle keys, e.g. for use when saving and restoring state
    private static final String KEY_MOVIE = "KEY_MOVIE";
    private static final String KEY_AWARD = "KEY_AWARD";

    // Screen fields - movie
    private TextView mTxtMovieId;
    private TextView mTxtImdbId;
    private TextView mTxtTitle;

    // Screen fields - award
    private TextView mTxtAwardId;
    private EditText mTxtAwardDate;
    private RadioGroup mRadioAwardCategory;
    private RadioButton mRadioAwardCategoryMovie;
    private RadioButton mRadioAwardCategoryDvd;
    private EditText mTxtReview;
    private EditText mTxtAwardDisplayOrder;

    // Data
    private Movie mMovie;
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
        setContentView(R.layout.activity_edit_award);

        // Initialise the app bar
        getViewUtils().initialiseAppBar(this, R.id.tbAppBar,
                getString(R.string.title_edit_award), true, R.color.colorPrimary);

        // Movie
        mTxtMovieId = findViewById(R.id.txtMovieId);
        mTxtImdbId = findViewById(R.id.txtImdbId);
        mTxtTitle = findViewById(R.id.txtTitle);

        // Award
        mTxtAwardId = findViewById(R.id.txtAwardId);
        mTxtAwardDate = findViewById(R.id.txtAwardDate);
        mRadioAwardCategory = findViewById(R.id.radioCategory);
        mRadioAwardCategoryMovie = findViewById(R.id.radioCategoryMovie);
        mRadioAwardCategoryDvd = findViewById(R.id.radioCategoryDvd);
        mTxtReview = findViewById(R.id.txtReview);
        mTxtAwardDisplayOrder = findViewById(R.id.txtDisplayOrder);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnSave = findViewById(R.id.btnSave);

        if (savedInstanceState == null) {
            // Store the movie and award indicated by the URL passed in
            loadReceivedData();
        } else {
            // Store the movie and award from saved state, e.g. on device rotation
            mMovie = savedInstanceState.getParcelable(KEY_MOVIE);
            mAward = savedInstanceState.getParcelable(KEY_AWARD);
        }
        displayData(mMovie, mAward);
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
    // Action methods

    /**
     * Handles the user clicking the "Cancel" button.
     * @param view the view that was clicked
     */
    public void onCancel(@Nullable View view) {
        if (view != null) {
            getViewUtils().displayInfoMessage(view.getContext(),
                    getString(R.string.award_not_saved, mTxtTitle.getText()));
        }
        loadReceivedData();
        displayData(mMovie, mAward);
    }

    /**
     * Handles the user clicking the "Save" button.
     * @param view the view that was clicked
     */
    public void onSave(@Nullable View view) {
        if (view == null || mAward == null) {
            return;
        }

        String id = mAward.getId();
        String movieId = mAward.getMovieId();
        String awardDate = mTxtAwardDate.getText().toString();
        String category =
                mRadioAwardCategory.getCheckedRadioButtonId() == R.id.radioCategoryMovie
                        ? Award.CATEGORY_MOVIE : Award.CATEGORY_DVD;
        String review = mTxtReview.getText().toString();
        int displayOrder = JavaUtils.toInt(mTxtAwardDisplayOrder.getText().toString(), 1);

        if (awardDate.isEmpty() || category.isEmpty() || review.isEmpty() || displayOrder <= 0) {
            getViewUtils().displayErrorMessage(this, R.string.error_mandatory_field_missing);
            return;
        }

        mAward = Award.builder()
                .id(id)
                .movieId(movieId)
                .awardDate(awardDate)
                .category(category)
                .review(review)
                .displayOrder(displayOrder)
                .build();

        Context context = view.getContext();
        int rowsUpdated = getMasterDatabase().addAward(context, mAward);
        if (rowsUpdated == 0) {
            getViewUtils().displayErrorMessage(this,
                    getString(R.string.award_not_saved, mMovie.getTitle()));
        } else {
            getViewUtils().displayInfoMessage(context,
                     getString(R.string.saving_award, mMovie.getTitle()));
        }

    }

    /**
     * Sets the movie and award to the values passed into the activity.
     */
    private void loadReceivedData() {
        // Display the movie and award indicated by the URL passed in
        if (getIntent() != null && getIntent().getData() != null) {
            String awardId = getIntent().getData().getLastPathSegment();
            mAward = getLocalDatabase().selectAwardById(awardId);
            if (mAward != null) {
                mMovie = getLocalDatabase().selectMovieById(mAward.getMovieId());
            }
        }
    }

    /**
     * Displays movie and award data on the screen.
     * @param movie the movie to display
     * @param award the award to display
     */
    private void displayData(@Nullable Movie movie, @Nullable Award award) {
        if (movie != null) {
            displayMovie(movie);
        }
        if (award != null) {
            displayAward(award);
        }
    }

    /**
     * Displays movie data on the screen.
     * @param movie the movie to display
     */
    private void displayMovie(@NonNull Movie movie) {
        mTxtMovieId.setText(movie.getId());
        mTxtImdbId.setText(movie.getImdbId());
        mTxtTitle.setText(movie.getTitle());
    }

    /**
     * Displays award data on the screen.
     * @param award the award to display
     */
    private void displayAward(@NonNull Award award) {
        mTxtAwardId.setText(award.getId());
        mTxtAwardDate.setText(award.getAwardDate());
        mRadioAwardCategoryMovie.setChecked(award.getCategory().equals(Award.CATEGORY_MOVIE));
        mRadioAwardCategoryDvd.setChecked(award.getCategory().equals(Award.CATEGORY_DVD));
        mTxtReview.setText(award.getReview());
        mTxtAwardDisplayOrder.setText(toDisplayableText(award.getDisplayOrder()));
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

    /**
     * Returns an integer value as a String to display in a TextView.
     * @param value the integer value
     * @return a String corresponding to value
     */
    private String toDisplayableText(int value) {
        return String.format(Locale.getDefault(), "%d", value);
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
     * Convenience method which returns a reference to a local database.
     * @return a reference to a local database
     */
    private static LocalDatabase getLocalDatabase() {
        return ObjectFactory.getLocalDatabase();
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
