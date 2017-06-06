package uk.jumpingmouse.moviecompanion.activity;

import android.content.ContentValues;
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
import android.widget.RadioGroup;
import android.widget.TextView;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.Award;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.utils.ModelUtils;
import uk.jumpingmouse.moviecompanion.utils.NavUtils;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;
import uk.jumpingmouse.omdbapi.OmdbApi;

/**
 * The add award activity.
 * Note that this is an admin activity, not a public-facing one.
 * @author Edmund Johnson
 */
public class AddAwardActivity extends AppCompatActivity {

    // Screen fields
    private EditText mTxtImdbId;
    private TextView mLabelTitle;
    private TextView mTxtTitle;
    private TextView mLabelAwardDate;
    private EditText mTxtAwardDate;
    private TextView mLabelAwardCategory;
    private RadioGroup mRadioAwardCategory;
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

        mTxtImdbId = (EditText) findViewById(R.id.txtImdbId);
        mLabelTitle = (TextView) findViewById(R.id.labelTitle);
        mTxtTitle = (TextView) findViewById(R.id.txtTitle);
        mLabelAwardDate = (TextView) findViewById(R.id.labelAwardDate);
        mTxtAwardDate = (EditText) findViewById(R.id.txtAwardDate);
        mLabelAwardCategory = (TextView) findViewById(R.id.labelCategory);
        mRadioAwardCategory = (RadioGroup) findViewById(R.id.radioCategory);
        mLabelReview = (TextView) findViewById(R.id.labelReview);
        mTxtReview = (EditText) findViewById(R.id.txtReview);
        mLabelAwardDisplayOrder = (TextView) findViewById(R.id.labelDisplayOrder);
        mTxtAwardDisplayOrder = (EditText) findViewById(R.id.txtDisplayOrder);
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
            Movie movie = ModelUtils.toMovie(cursor);
            if (movie == null) {
                // Display a "Data found did not represent a Movie" error message
                getViewUtils().displayErrorMessage(this, R.string.movie_data_not_found);
            } else {
                displayMovie(movie);
            }
            cursor.close();
        }
    }

    /**
     * Handles the user clicking one of the "Award Category" radio buttons (Movie/DVD of the week).
     * @param view the view that was clicked
     */
    public void onCategoryClicked(View view) {
        // TODO - probably remove this and handle with the Save button
    }

    /**
     * Handles the user clicking the "Cancel" button.
     * @param view the view that was clicked
     */
    public void onCancel(@Nullable View view) {
        clearAward();
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

        Uri uriInserted = getContentResolver().insert(
                DataContract.AwardEntry.CONTENT_URI, toContentValues(mAward));

        if (uriInserted == null) {
            getViewUtils().displayErrorMessage(view.getContext(),
                    getString(R.string.award_not_saved, mMovie.getTitle()));
        } else {
            getViewUtils().displayInfoMessage(view.getContext(),
                     getString(R.string.saving_movie, mMovie.getTitle()));
            clearAward();
        }

    }

    /**
     * Displays a movie on the screen.
     * @param movie the movie to display
     */
    private void displayMovie(@NonNull Movie movie) {
        getViewUtils().dismissKeyboard(this);
        mTxtTitle.setText(movie.getTitle());
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
        mTxtAwardDate.setText(null);
        mRadioAwardCategory.clearCheck();
        mTxtAwardDisplayOrder.setText(null);
    }

    /**
     * Hides the movie value fields.
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

    /**
     * Shows the movie value fields.
     */
    private void showValueFields() {
        showView(mLabelTitle);
        showView(mTxtTitle);
        showView(mLabelAwardDate);
        showView(mTxtAwardDate);
        showView(mLabelAwardCategory);
        showView(mRadioAwardCategory);
        showView(mLabelAwardDisplayOrder);
        showView(mTxtAwardDisplayOrder);
        showView(mLabelReview);
        showView(mTxtReview);
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
     * Returns a set of ContentValues corresponding to an award.
     * @param award the award
     * @return the set of ContentValues corresponding to the award
     */
    @NonNull
    private ContentValues toContentValues(@NonNull Award award) {
        ContentValues values = new ContentValues();

        values.put(DataContract.AwardEntry.COLUMN_ID, award.getId());
        values.put(DataContract.AwardEntry.COLUMN_MOVIE_ID, award.getMovieId());
        values.put(DataContract.AwardEntry.COLUMN_AWARD_DATE, award.getAwardDate());
        values.put(DataContract.AwardEntry.COLUMN_CATEGORY, award.getCategory());
        values.put(DataContract.AwardEntry.COLUMN_REVIEW, award.getReview());
        values.put(DataContract.AwardEntry.COLUMN_DISPLAY_ORDER, award.getDisplayOrder());

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
