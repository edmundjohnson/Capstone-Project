package uk.jumpingmouse.moviecompanion.activity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
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
import uk.jumpingmouse.moviecompanion.analytics.AnalyticsManager;
import uk.jumpingmouse.moviecompanion.data.UserMovie;
import uk.jumpingmouse.moviecompanion.data.ViewAward;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.model.LocalDatabase;
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

        // Set the transition name for the poster
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (getArgViewAwardUri() != null) {
                String viewAwardId = getArgViewAwardUri().getLastPathSegment();
                String transitionName = getString(R.string.transition_movie, viewAwardId);
                mImgPoster.setTransitionName(transitionName);
            }
        }

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

        loadData(getArgViewAwardUri());

        mLayoutImdbLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewAward != null) {

                    // log the event in analytics
                    int movieId = mViewAward.getMovieId();
                    String movieTitle = mViewAward.getTitle();
                    getAnalyticsManager().logImdbLink(movieId, movieTitle);

                    // display the IMDb web page for the movie
                    getNavUtils().displayWebLink((AppCompatActivity) getActivity(),
                            getString(R.string.imdb_link_address, mViewAward.getImdbId()));
                }
            }
        });

    }

    /**
     * Called to retrieve per-instance state from an activity before being killed so that the
     * state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     * @param outState the Bundle populated by this method
     */
    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(KEY_VIEW_AWARD, mViewAward);
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
                mViewAward.setOnWishlist(true);
                if (updateViewAwardFlags(context, mMenu, mViewAward)) {
                    getViewUtils().displayInfoMessage(context, R.string.added_to_wishlist);
                }
                return true;

            // remove from wishlist
            case R.id.menu_option_remove_from_wishlist:
                mViewAward.setOnWishlist(false);
                if (updateViewAwardFlags(context, mMenu, mViewAward)) {
                    getViewUtils().displayInfoMessage(context, R.string.removed_from_wishlist);
                }
                return true;

            // add to watched
            case R.id.menu_option_add_to_watched:
                mViewAward.setWatched(true);
                if (updateViewAwardFlags(context, mMenu, mViewAward)) {
                    getViewUtils().displayInfoMessage(context, R.string.added_to_watched);
                }
                return true;

            // remove from watched
            case R.id.menu_option_remove_from_watched:
                mViewAward.setWatched(false);
                if (updateViewAwardFlags(context, mMenu, mViewAward)) {
                    getViewUtils().displayInfoMessage(context, R.string.removed_from_watched);
                }
                return true;

            // add to favourites
            case R.id.menu_option_add_to_favourites:
                mViewAward.setFavourite(true);
                if (updateViewAwardFlags(context, mMenu, mViewAward)) {
                    getViewUtils().displayInfoMessage(context, R.string.added_to_favourites);
                }
                return true;

            // remove from favourites
            case R.id.menu_option_remove_from_favourites:
                mViewAward.setFavourite(false);
                if (updateViewAwardFlags(context, mMenu, mViewAward)) {
                    getViewUtils().displayInfoMessage(context, R.string.removed_from_favourites);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Perform updates based on the values of the boolean flag fields in a view award.
     * @param context the context
     * @param menu the menu
     * @param viewAward the view award
     * @return true if successful, false otherwise
     */
    private boolean updateViewAwardFlags(@Nullable Context context, @Nullable Menu menu,
                                         @Nullable ViewAward viewAward) {
        if (context != null && viewAward != null) {
            int rowsUpdated = updateUserMovie(context, viewAward);
            if (rowsUpdated > 0) {
                loadData(getArgViewAwardUri());
                return true;
            }
        }
        return false;
    }

    /**
     * Update the database values of a UserMovie, based on the values in a view award.
     * @param context the context
     * @param viewAward the view award
     * @return the number of database rows updated
     */
    private int updateUserMovie(@NonNull Context context, @NonNull ViewAward viewAward) {

        // Update the master database
        UserMovie userMovie = ModelUtils.newUserMovie(viewAward);
        int rowsUpdated = getMasterDatabase().addUserMovie(context, userMovie);
        if (rowsUpdated == 0) {
            Timber.e("updateUserMovie: 0 rows updated in master database");
            return 0;
        }

        // Update the local database as the user may be offline
        rowsUpdated = getLocalDatabase().addUserMovie(userMovie);
        if (rowsUpdated == 0) {
            Timber.e("updateUserMovie: 0 rows updated in local database");
            return 0;
        }
        return rowsUpdated;
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
    private void loadData(@Nullable Uri uri) {
        if (getActivity() != null && uri != null) {
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
        @DrawableRes int categoryRes = getViewUtils().getCategoryRes(categoryCode);
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

                    // Note: Setting the app bar background colour to match the movie info bar
                    // is a bad idea.  It's distracting, and the app bar goes green when the
                    // review is scrolled to the top.
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
        mImgCategory.setImageResource(categoryRes);
        mTxtCategory.setText(getViewUtils().getCategoryText(context, categoryCode));
        mTxtAwardDate.setText(awardDateText);
        mTxtReview.setText(viewAward.getReview());
    }

    //--------------------------------------------------------------
    // Getters and Setters

    @Nullable
    private Uri getArgViewAwardUri() {
        return mArgViewAwardUri;
    }
    public final void setArgViewAwardUri(@Nullable final Uri uri) {
        if (uri != null) {
            mArgViewAwardUri = uri;
            loadData(uri);
        }
    }

    /**
     * Convenience method which returns an AnalyticsManager.
     * @return an AnalyticsManager
     */
    @NonNull
    private static AnalyticsManager getAnalyticsManager() {
        return ObjectFactory.getAnalyticsManager();
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
     * Convenience method which returns a reference to a local database.
     * @return a reference to a local database
     */
    private static LocalDatabase getLocalDatabase() {
        return ObjectFactory.getLocalDatabase();
    }

    /**
     * Convenience method which returns a reference to a NavUtils object.
     * @return a reference to a NavUtils object
     */
    private NavUtils getNavUtils() {
        return ObjectFactory.getNavUtils();
    }

    /**
     * Convenience method which returns a reference to a NetUtils object.
     * @return a reference to a NetUtils object
     */
    private NetUtils getNetUtils() {
        return ObjectFactory.getNetUtils();
    }

    /**
     * Convenience method which returns a reference to a ViewUtils object.
     * @return a reference to a ViewUtils object
     */
    @NonNull
    private static ViewUtils getViewUtils() {
        return ObjectFactory.getViewUtils();
    }

}
