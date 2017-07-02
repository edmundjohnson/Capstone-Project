package uk.jumpingmouse.moviecompanion.activity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.UserMovie;
import uk.jumpingmouse.moviecompanion.data.ViewAward;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.model.MasterDatabase;
import uk.jumpingmouse.moviecompanion.utils.ModelUtils;
import uk.jumpingmouse.moviecompanion.utils.NavUtils;
import uk.jumpingmouse.moviecompanion.utils.NetUtils;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

/**
 * The fragment class for displaying a movie (view award).
 * @author Edmund Johnson
 */
public class MovieFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /** The cursor loader id. */
    private static final int VIEW_AWARD_LOADER_ID = 1;

    // Bundle keys, e.g. for use when saving and restoring the fragment's state
    private static final String KEY_VIEW_AWARD = "KEY_VIEW_AWARD";
    private static final String KEY_VIEW_AWARD_URI = "KEY_VIEW_AWARD_URI";

    private static final int DARK_MUTED_COLOR_DEFAULT = 0xFF333333;
    // green_50
    private static final int LIGHT_MUTED_COLOR_DEFAULT = 0xFFE8F5E9;

    /** Value passed in: the URI of the view award to display. */
    private Uri mArgViewAwardUri;

    /** The currently displayed ViewAward. */
    private ViewAward mViewAward;

    /** The cursor loader for view award. */
    private CursorLoader mCursorLoader;

    /** The menu. */
    private Menu mMenu;

    private View mRootView;
    private int mDarkMutedColor = DARK_MUTED_COLOR_DEFAULT;
    private int mLightMutedColor = LIGHT_MUTED_COLOR_DEFAULT;

    // Screen fields
    private ImageView mImgPoster;
    private TextView mTxtTitle;
    private TextView mTxtRuntime;
    private TextView mTxtGenre;
    private LinearLayout mLayoutImdbLink;
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
        mRootView = inflater.inflate(R.layout.movie_fragment, container, false);

        // Initialise the display element references
        mImgPoster = (ImageView) mRootView.findViewById(R.id.imgPoster);
        mTxtTitle = (TextView) mRootView.findViewById(R.id.txtTitle);
        mTxtRuntime = (TextView) mRootView.findViewById(R.id.txtRuntime);
        mTxtGenre = (TextView) mRootView.findViewById(R.id.txtGenre);
        mLayoutImdbLink = (LinearLayout) mRootView.findViewById(R.id.layoutImdbLink);
        mImgCategory = (ImageView) mRootView.findViewById(R.id.imgCategory);
        mTxtCategory = (TextView) mRootView.findViewById(R.id.txtCategory);
        mTxtAwardDate = (TextView) mRootView.findViewById(R.id.txtAwardDate);
        mTxtReview = (TextView) mRootView.findViewById(R.id.txtReview);

        Context context = getActivity();
        if (savedInstanceState != null) {
            // Retrieve and display the saved ViewAward
            mViewAward = savedInstanceState.getParcelable(KEY_VIEW_AWARD);
            displayViewAward(context, mViewAward);
        }

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle bundle) {
        super.onActivityCreated(bundle);

        if (getArgViewAwardUri() != null) {
            loadData(getArgViewAwardUri());
        }

        mLayoutImdbLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewAward != null) {
                    getNavUtils().displayWebLink(getActivity(),
                            getString(R.string.imdb_link_address, mViewAward.getImdbId()));
                }
            }
        });

    }

//    /**
//     * Performs processing required when the fragment is resumed.
//     * i.e. display the required movie.
//     */
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        // If there is no saved ViewAward (from onSaveInstanceState), display the latest
//        // movie
//        if (mDisplayedViewAward == null) {
//            Context context = getActivity();
//            ViewAward viewAward = getLocalDatabase().getViewAwardLatest(context);
//            displayViewAward(context, viewAward);
//        }
//    }

//    @Override
//    public void onPause() {
//        super.onPause();
//    }

//    /**
//     * Called to retrieve per-instance state from an activity before being killed so that the
//     * state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
//     * @param outState the Bundle populated by this method
//     */
//    @Override
//    public final void onSaveInstanceState(final Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        outState.putParcelable(KEY_VIEW_AWARD, mDisplayedViewAward);
//    }

//    /**
//     * Called when all saved state has been restored into the view hierarchy
//     * of the fragment.  This can be used to do initialization based on saved
//     * state that you are letting the view hierarchy track itself, such as
//     * whether check box widgets are currently checked.  This is called
//     * after {@link #onActivityCreated(Bundle)} and before
//     * {@link #onStart()}.
//     *
//     * @param savedInstanceState If the fragment is being re-created from
//     *     a previous saved state, this is the state.
//     */
//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            displaySavedViewAward(getActivity(), savedInstanceState);
//        }
//
//        super.onViewStateRestored(savedInstanceState);
//    }

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

    //---------------------------------------------------------------------
    // Menu actions

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        // Inflate the menu; this adds items to the app bar if it is present.
        inflater.inflate(R.menu.menu_movie_fragment, menu);
        setMenuItemVisibility(menu, mViewAward);
        mMenu = menu;
    }

    /**
     * Set the correct visibility for the ViewAward-related menu items.
     * @param menu the menu
     * @param viewAward the view award
     */
    private void setMenuItemVisibility(@Nullable Menu menu, @Nullable ViewAward viewAward) {
        if (menu != null && viewAward != null) {
            boolean isOnWishlist = mViewAward != null && mViewAward.isOnWishlist();
            boolean isWatched = mViewAward != null && mViewAward.isWatched();
            boolean isFavourite = mViewAward != null && mViewAward.isFavourite();

            menu.findItem(R.id.menu_option_add_to_wishlist).setVisible(!isOnWishlist);
            menu.findItem(R.id.menu_option_remove_from_wishlist).setVisible(isOnWishlist);
            menu.findItem(R.id.menu_option_add_to_watched).setVisible(!isWatched);
            menu.findItem(R.id.menu_option_remove_from_watched).setVisible(isWatched);
            menu.findItem(R.id.menu_option_add_to_favourites).setVisible(!isFavourite);
            menu.findItem(R.id.menu_option_remove_from_favourites).setVisible(isFavourite);
        }
    }

    /**
     * Process selection of an item in the options menu.
     * @param item the menu item that was selected
     * @return false to allow normal menu processing to proceed,
     *         true if menu processing is consumed here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Context context = getActivity();
        switch (item.getItemId()) {
            // add to wishlist
            case R.id.menu_option_add_to_wishlist:
                addToWishlist(context);
                return true;

            // remove from wishlist
            case R.id.menu_option_remove_from_wishlist:
                removeFromWishlist(context);
                return true;

            // add to watched
            case R.id.menu_option_add_to_watched:
                addToWatched(context);
                return true;

            // remove from watched
            case R.id.menu_option_remove_from_watched:
                removeFromWatched(context);
                return true;

            // add to favourites
            case R.id.menu_option_add_to_favourites:
                addToFavourites(context);
                return true;

            // remove from favourites
            case R.id.menu_option_remove_from_favourites:
                removeFromFavourites(context);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addToWishlist(@Nullable Context context) {
        mViewAward.setOnWishlist(true);
        toggleUserMovieField(context,
                R.id.menu_option_add_to_wishlist,
                R.id.menu_option_remove_from_wishlist,
                R.string.added_to_wishlist);
    }

    private void removeFromWishlist(@Nullable Context context) {
        mViewAward.setOnWishlist(false);
        toggleUserMovieField(context,
                R.id.menu_option_remove_from_wishlist,
                R.id.menu_option_add_to_wishlist,
                R.string.removed_from_wishlist);
    }

    private void addToWatched(@Nullable Context context) {
        mViewAward.setWatched(true);
        toggleUserMovieField(context,
                R.id.menu_option_add_to_watched,
                R.id.menu_option_remove_from_watched,
                R.string.added_to_watched);
    }

    private void removeFromWatched(@Nullable Context context) {
        mViewAward.setWatched(false);
        toggleUserMovieField(context,
                R.id.menu_option_remove_from_watched,
                R.id.menu_option_add_to_watched,
                R.string.removed_from_watched);
    }

    private void addToFavourites(@Nullable Context context) {
        mViewAward.setFavourite(true);
        toggleUserMovieField(context,
                R.id.menu_option_add_to_favourites,
                R.id.menu_option_remove_from_favourites,
                R.string.added_to_favourites);
    }

    private void removeFromFavourites(@Nullable Context context) {
        mViewAward.setFavourite(false);
        toggleUserMovieField(context,
                R.id.menu_option_remove_from_favourites,
                R.id.menu_option_add_to_favourites,
                R.string.removed_from_favourites);
    }

    /**
     * Change the value of a boolean field in a UserMovie.
     * @param context the context
     * @param menuItemClickedResId the menu item that was clicked to change the field
     * @param menuItemToggleResId the menu item which changes the value back
     * @param messageResId a message to display when the operation has been performed
     */
    private void toggleUserMovieField(@Nullable Context context,
                                      @IdRes int menuItemClickedResId,
                                      @IdRes int menuItemToggleResId,
                                      @StringRes int messageResId) {
        if (context == null || mViewAward == null) {
            return;
        }

        if (!getNetUtils().isNetworkAvailable(context)) {
            getViewUtils().displayInfoMessage(context, R.string.error_internet_connection_required);
            return;
        }

        updateUserMovieInDatabase(context, mViewAward);

        if (mMenu != null) {
            mMenu.findItem(menuItemClickedResId).setVisible(false);
            mMenu.findItem(menuItemToggleResId).setVisible(true);
        }

        getViewUtils().displayInfoMessage(context, messageResId);
    }

    /**
     * Update a UserMovie in the database to reflect the values of a view award.
     * @param context the context
     * @param viewAward the view award
     */
    private void updateUserMovieInDatabase(@NonNull Context context, @NonNull ViewAward viewAward) {
        // Generate a UserMovie based on the currently displayed values
        UserMovie userMovie = UserMovie.builder()
                .id(viewAward.getMovieId())
                .onWishlist(viewAward.isOnWishlist())
                .watched(viewAward.isWatched())
                .favourite(viewAward.isFavourite())
                .build();

        // Update the Firebase database
        int rowsUpdated = getMasterDatabase().addUserMovie(context, userMovie);
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
        // Construct and display the ViewAward
        if (cursor != null && cursor.moveToFirst()) {
            mViewAward = ModelUtils.newViewAward(cursor);
            displayViewAward(getActivity(), mViewAward);
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
    // UI methods

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
        if (context == null || viewAward == null) {
            return;
        }
        // Update the menu to reflect the latest values
        setMenuItemVisibility(mMenu, mViewAward);

        String runtimeText = getViewUtils().getRuntimeText(context, viewAward.getRuntime());
        String categoryCode = viewAward.getCategory();
        Drawable categoryDrawable = getViewUtils().getCategoryDrawable(context, categoryCode);
        String awardDateText = getViewUtils().getAwardDateDisplayable(viewAward.getAwardDate());

        Picasso.with(context).load(viewAward.getPoster()).into(mImgPoster, new Callback() {
            @Override
            public void onSuccess() {
                if (mRootView == null || mImgPoster.getDrawable() == null) {
                    return;
                }
                Bitmap bitmap = ((BitmapDrawable) mImgPoster.getDrawable()).getBitmap();
                if (bitmap != null) {
                    Palette.Builder paletteBuilder = new Palette.Builder(bitmap);
                    Palette palette = paletteBuilder.generate();
                    mDarkMutedColor = palette.getDarkMutedColor(DARK_MUTED_COLOR_DEFAULT);
                    mLightMutedColor = getViewUtils().lightenColor(palette.getLightMutedColor(LIGHT_MUTED_COLOR_DEFAULT));

                    mRootView.findViewById(R.id.layoutMovieInfo).setBackgroundColor(mDarkMutedColor);
                    mRootView.findViewById(R.id.layoutMovieFragment).setBackgroundColor(mLightMutedColor);
                    mRootView.findViewById(R.id.layoutAwardInfo).setBackgroundColor(mLightMutedColor);
                    mRootView.findViewById(R.id.txtReview).setBackgroundColor(mLightMutedColor);

                    //// Set the status bar background colour to match the meta bar
                    //// Nice idea, but it's quite distracting, and the appbar goes green
                    //// when the review is scrolled to the top.
                    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //    if (getActivity() != null && getActivity().getWindow() != null) {
                    //        getActivity().getWindow().setStatusBarColor(mDarkMutedColor);
                    //    }
                    //}
                    //AppCompatActivity activity = (AppCompatActivity) getActivity();
                    //// Set appbar to movie_info colour - no, this doesn't work, it sets the colour
                    //// immediately, we want it set when the review is scrolled up.
                    //if (getActivity() != null) {
                    //    Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.tbAppBar);
                    //    toolbar.setBackground(new ColorDrawable(mDarkMutedColor));
                    //}
                }
            }

            @Override
            public void onError() {
                Timber.w("Error loading poster into ImageView");
            }
        });

        mTxtTitle.setText(viewAward.getTitle().trim());
        mTxtRuntime.setText(runtimeText);
        mTxtGenre.setText(viewAward.getGenre());
        mImgCategory.setImageDrawable(categoryDrawable);
        mTxtCategory.setText(getViewUtils().getCategoryText(context, categoryCode));
        mTxtAwardDate.setText(awardDateText);
        mTxtReview.setText(viewAward.getReview());
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
     * Convenience method for returning a reference to the database helper.
     * @return a reference to the database helper
     */
    @NonNull
    private static MasterDatabase getMasterDatabase() {
        return ObjectFactory.getMasterDatabase();
    }

    /**
     * Returns a NetUtils object.
     * @return a NetUtils object
     */
    private NetUtils getNetUtils() {
        return NetUtils.getInstance();
    }

    /**
     * Returns a NavUtils object.
     * @return a NavUtils object
     */
    private NavUtils getNavUtils() {
        return ObjectFactory.getNavUtils();
    }

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
