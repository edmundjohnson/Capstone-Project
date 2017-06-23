package uk.jumpingmouse.moviecompanion.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.ViewAward;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.model.LocalDatabase;
import uk.jumpingmouse.moviecompanion.utils.ModelUtils;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

/**
 * The fragment class for displaying a movie.
 * @author Edmund Johnson
 */
public class MovieFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
            SharedPreferences.OnSharedPreferenceChangeListener {

    /** The cursor loader id. */
    private static  final int VIEW_AWARD_LOADER_ID = 1;

    // Bundle keys, e.g. for use when saving and restoring the fragment's state
    private static final String KEY_VIEW_AWARD = "KEY_VIEW_AWARD";
    private static final String KEY_VIEW_AWARD_URI = "KEY_VIEW_AWARD_URI";

    /** Value passed in: the URI of the view award to display. */
    private Uri mArgViewAwardUri;

    /** The currently displayed ViewAward. */
    private ViewAward mDisplayedViewAward;

    /** The cursor loader for view award. */
    private CursorLoader mCursorLoader;

    // Screen fields
    private ImageView mImgPoster;
    private TextView mTxtTitle;
    private TextView mTxtRuntime;
    private TextView mTxtGenre;
    private ImageView mImgCategory;
    private TextView mTxtCategory;
    private TextView mTxtAwardDate;
    private TextView mTxtReview;

    //--------------------------------------------------------------
    // Lifecycle methods

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add the next line if the fragment needs to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Initialise the display element references
        View rootView = inflater.inflate(R.layout.movie_fragment, container, false);

        // Initialise the display element references
        mImgPoster = (ImageView) rootView.findViewById(R.id.imgPoster);
        mTxtTitle = (TextView) rootView.findViewById(R.id.txtTitle);
        mTxtRuntime = (TextView) rootView.findViewById(R.id.txtRuntime);
        mTxtGenre = (TextView) rootView.findViewById(R.id.txtGenre);
        mImgCategory = (ImageView) rootView.findViewById(R.id.imgCategory);
        mTxtCategory = (TextView) rootView.findViewById(R.id.txtCategory);
        mTxtAwardDate = (TextView) rootView.findViewById(R.id.txtAwardDate);
        mTxtReview = (TextView) rootView.findViewById(R.id.txtReview);

        Context context = getActivity();
        if (savedInstanceState != null) {
            displaySavedViewAward(context, savedInstanceState);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle bundle) {
        super.onActivityCreated(bundle);

        if (getArgViewAwardUri() != null) {
            loadData(getArgViewAwardUri());
        }
    }

    /**
     * Performs processing required when the fragment is resumed.
     * i.e. register to be notified of changes to SharedPreferences and
     * display the required movie.
     */
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);

//        // If there is no saved ViewAward (from onSaveInstanceState), display the latest
//        // movie
//        if (mDisplayedViewAward == null) {
//            Context context = getActivity();
//            ViewAward viewAward = getLocalDatabase().getViewAwardLatest(context);
//            displayViewAward(context, viewAward);
//        }
    }

    @Override
    public void onPause() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    /**
     * Called to retrieve per-instance state from an activity before being killed so that the
     * state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     * @param outState the Bundle populated by this method
     */
    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

//        outState.putParcelable(KEY_VIEW_AWARD, mDisplayedViewAward);
    }

    /**
     * Called when all saved state has been restored into the view hierarchy
     * of the fragment.  This can be used to do initialization based on saved
     * state that you are letting the view hierarchy track itself, such as
     * whether check box widgets are currently checked.  This is called
     * after {@link #onActivityCreated(Bundle)} and before
     * {@link #onStart()}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *     a previous saved state, this is the state.
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            displaySavedViewAward(getActivity(), savedInstanceState);
//        }

        super.onViewStateRestored(savedInstanceState);
    }

    //--------------------------------------------------------------
    // Construction

    /**
     * Returns a new instance of this class.
     * @return  a new instance of this class
     */
    @NonNull
    public static MovieFragment newInstance() {
        return new MovieFragment();
    }

    //------------------------------------------------------------------------------
    // Loader callbacks

    /**
     * Instantiates and returns a new loader for the given loader ID.
     * @param id the ID whose loader is to be created.
     * @param args any arguments supplied by the caller.
     * @return a new loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(final int id, @Nullable final Bundle args) {
        // This fragment only uses one loader, so we don't care about checking the id.

        Uri uri = null;
        if (args != null) {
            uri = args.getParcelable(KEY_VIEW_AWARD_URI);
        }
//        if (uri == null) {
//            // By default, display the most recent ViewAward
//            uri = DataContract.ViewAwardEntry.buildUriForRowById(1);
//        }

        mCursorLoader = new CursorLoader(getActivity(),
                uri,
                DataContract.ViewAwardEntry.getAllColumns(),
                null,
                null,
                null);

        return mCursorLoader;
    }

    /**
     * <p>
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.
     * See {@link FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     * </p>
     * <p>
     * This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * </p>
     * <ul>
     * <li>
     * The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the {@link CursorAdapter#CursorAdapter(Context,
     * Cursor, int)} constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * </li>
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </li>
     * </ul>
     *
     * @param loader the loader that has finished.
     * @param cursor the data generated by the loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor != null && cursor.moveToFirst()) {
            ViewAward viewAward = ModelUtils.newViewAward(cursor);

            // display the viewAward
            if (viewAward != null) {
                displayDetailFields(getActivity(), viewAward);
            }
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the loader's data.
     *
     * @param loader The loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // no action required
    }

    /**
     * Loads data from the database into the cursor loader, either by creating the
     * cursor loader if it does not exist, or by reloading the cursor loader.
     * @param uri the URI of the data to be loaded
     */
    private void loadData(@NonNull Uri uri) {
        if (getActivity() != null) {
            if (mCursorLoader == null) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(KEY_VIEW_AWARD_URI, uri);
                getLoaderManager().initLoader(VIEW_AWARD_LOADER_ID, bundle, this);
            } else {
                // The URI may have changed, so restart the loader rather than just doing a forceLoad.
                Bundle bundle = new Bundle();
                bundle.putParcelable(KEY_VIEW_AWARD_URI, uri);
                getLoaderManager().restartLoader(VIEW_AWARD_LOADER_ID, bundle, this);
            }
        }
    }

    //--------------------------------------------------------------
    // SharedPreference listener

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * This callback will be run on your main thread.
     *
     * @param sharedPreferences the {@link SharedPreferences} that received the change.
     * @param key               the key of the preference that was changed
     */
    @Override
    @MainThread
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (getString(R.string.pref_feed_status_key).equals(key)
//                && mDisplayedViewAward == null) {
//            Context context = getActivity();
//            updateEmptyView(context);
//        }
    }

    /**
     * Update the view appropriately when there is no data, based on the
     * network status and feed status.
     * @param context the context
     */
    private void updateEmptyView(@Nullable final Context context) {

//        // Get the appropriate error message to display
//        boolean isNetworkAvailable = getNetUtils().isNetworkAvailable(context);
//        @PrefUtils.FeedStatus int feedStatus = getPrefUtils().getFeedStatus(context);
//        int message = getPrefUtils().getNoDataMessage(isNetworkAvailable, feedStatus);

//        // Update the screen fields to display the error message
//        mTxtDate.setText(null);
//        mTxtTitle.setText(null);
//        mTxtText.setText(message);
    }

    //--------------------------------------------------------------
    // UI methods

    /**
     * Displays a ViewAward which has been saved in a bundle.
     * @param context the context
     * @param savedInstanceState the bundle containing the saved state
     */
    private void displaySavedViewAward(@Nullable final Context context,
                                       @NonNull final Bundle savedInstanceState) {
        // Retrieve and display the saved ViewAward
        ViewAward viewAward = savedInstanceState.getParcelable(KEY_VIEW_AWARD);
        displayViewAward(context, viewAward);
    }

//    /**
//     * Displays the requested ViewAward, i.e. one which was not saved.
//     * @param context the context
//     * @return the requested ViewAward, or null if no ViewAward was requested
//     */
//    private ViewAward getRequestedViewAward(@Nullable final Context context) {
//        // display the ViewAward whose URI was passed in
//        Uri uri = getArgViewAwardUri();
//        if (uri != null) {
//            String id = uri.getLastPathSegment();
//            ViewAward viewAward = getLocalDatabase().selectViewAwardById(id);
//            setArgViewAwardUri(null);
//            return viewAward;
//        }
//        return null;
//    }

    /**
     * Displays a ViewAward.
     * @param context the context
     * @param viewAward the ViewAward to display
     */
    private void displayViewAward(@Nullable final Context context, @Nullable final ViewAward viewAward) {
//        Timber.d("displayViewAward() called with: " + " viewAward = [" + viewAward + "]");

        // record the ViewAward, so it can be saved and restored if necessary
        mDisplayedViewAward = viewAward;

        if (viewAward == null) {
            // Note: we wish mDisplayedViewAward to be null when there is no data
            updateEmptyView(context);
        } else {
            displayDetailFields(context, viewAward);
        }
    }

    /**
     * Displays a set of ViewAward field values.
     * @param context the context
     * @param viewAward the ViewAward to display
     */
    private void displayDetailFields(@Nullable final Context context, @NonNull final ViewAward viewAward) {
        String runtimeText = getViewUtils().getRuntimeText(context, viewAward.getRuntime());
        String categoryCode = viewAward.getCategory();
        Drawable categoryDrawable = getViewUtils().getCategoryDrawable(context, categoryCode);
        String awardDateText = getViewUtils().getAwardDateDisplayable(viewAward.getAwardDate());

        Picasso.with(context).load(viewAward.getPoster()).into(mImgPoster);
        mTxtTitle.setText(viewAward.getTitle().trim());
        mTxtRuntime.setText(runtimeText);
        mTxtGenre.setText(viewAward.getGenre());
        mImgCategory.setImageDrawable(categoryDrawable);
        mTxtCategory.setText(getViewUtils().getCategoryText(context, categoryCode));
        mTxtAwardDate.setText(awardDateText);
        mTxtReview.setText(viewAward.getReview());
    }

    //--------------------------------------------------------------
    // Navigation methods

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        // Inflate the menu; this adds items to the app bar if it is present.
        inflater.inflate(R.menu.main_menu, menu);
    }

    /**
     * Handler method for selection of an app bar icon or menu option.
     * @param item the Menu item which was clicked
     * @return true if the item has been consumed, false otherwise
     */
    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.mnuShare:
//                shareText();
//                break;
            default:
                return super.onOptionsItemSelected(item);
        }
//        return true;
    }

    //--------------------------------------------------------------
    // Utility methods

//    /**
//     * Displays the ViewAward (if any) which has been set as an argument.
//     * @param context the context
//     */
//    public final void refreshUserInterface(@Nullable final Context context) {
//        Uri uri = getArgViewAwardUri();
//        if (uri != null) {
//            ViewAward viewAward = getResolver().getContentProvider().etc(context, uri);
//            displayViewAward(context, viewAward);
//            setArgViewAwardUri(null);
//        }
//    }

    //--------------------------------------------------------------
    // Getters and Setters

    @Nullable
    private Uri getArgViewAwardUri() {
        return mArgViewAwardUri;
    }
    public final void setArgViewAwardUri(@Nullable final Uri uri) {
        if (uri != null) {
            mArgViewAwardUri = uri;
            if (getActivity() != null) {
                loadData(uri);
            }
        }
    }

    /**
     * Convenience method which returns a reference to a local database.
     * @return a reference to a local database
     */
    private static LocalDatabase getLocalDatabase() {
        return ObjectFactory.getLocalDatabase();
    }

//    /**
//     * Returns a NetUtils object.
//     * @return a NetUtils object
//     */
//    private NetUtils getNetUtils() {
//        return NetUtils.getInstance();
//    }

//    /**
//     * Returns a PrefUtils object.
//     * @return a PrefUtils object
//     */
//    private PrefUtils getPrefUtils() {
//        return PrefUtils.getInstance();
//    }

    /**
     * Convenience method which returns a reference to a ViewUtils object.
     * @return a reference to a ViewUtils object
     */
    @NonNull
    private static ViewUtils getViewUtils() {
        return ObjectFactory.getViewUtils();
    }

//    //------------------------------------------------------------------------------
//    // Callbacks
//
//    /**
//     * A callback interface that all activities containing this fragment must implement.
//     */
//    public interface MovieFragmentContainer {
//        /**
//         * Return whether a ViewAward is currently being displayed.
//         * @return true if a ViewAward is currently being displayed, false otherwise
//         */
//        boolean isViewAwardDisplayed();
//    }

}
