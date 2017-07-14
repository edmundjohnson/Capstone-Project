package uk.jumpingmouse.moviecompanion.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.adapter.ViewAwardAdapter;
import uk.jumpingmouse.moviecompanion.data.ViewAwardQueryParameters;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.model.DataProvider;
import uk.jumpingmouse.moviecompanion.model.LocalDatabase;
import uk.jumpingmouse.moviecompanion.utils.NetUtils;
import uk.jumpingmouse.moviecompanion.utils.PrefUtils;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

/**
 * The fragment class for the list of posts.
 * @author Edmund Johnson
 */
public class AwardListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    /** The cursor loader id. */
    private static  final int AWARD_LIST_LOADER_ID = 1;

    // Attributes for the bundle passed in
    private static final String KEY_VIEW_AWARD_URI = "KEY_VIEW_AWARD_URI";

    // Attributes for saving and restoring the fragment's state
    private static final String KEY_LIST_LAYOUT = "KEY_LIST_LAYOUT";
    private static final String KEY_POSITION = "KEY_POSITION";

    /** The menu. */
    private Menu mMenu;

    /** The content observer for view awards. */
    private ViewAwardContentObserver mViewAwardContentObserver;

    /** The data adapter. */
    private ViewAwardAdapter mViewAwardAdapter;

    /** The RecyclerView containing the list of awards. */
    private RecyclerView mRecyclerView;
    /** The position in the list of the currently selected item. */
    private int mSelectedPosition = RecyclerView.NO_POSITION;

//    static {
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
//    }

    //--------------------------------------------------------------
    // Lifecycle Methods

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add the next line if the fragment needs to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.award_list_fragment, container, false);
        View emptyListView = rootView.findViewById(R.id.viewAwardListEmpty);

        // Create a click handler for the list items
        ViewAwardAdapter.AdapterOnClickHandler clickHandler =
                new ViewAwardAdapter.AdapterOnClickHandler() {
                    @Override
                    public void onClick(final String id, final int selectedPosition) {

                        if (mViewAwardAdapter != null && getActivity() != null) {
                            mSelectedPosition = selectedPosition;
                            mViewAwardAdapter.handleItemClick(id, selectedPosition,
                                    (AwardListFragment.ListFragmentContainer) getActivity());
                        }
                    }
                };

        Context context = getActivity();

        // The adapter will take data and populate the RecyclerView.
        // We cannot use FLAG_AUTO_REQUERY since it is deprecated, so we would end
        // up with an empty list the first time we run.
        mViewAwardAdapter = ViewAwardAdapter.newInstance(context, clickHandler, emptyListView);

        // Restore any saved state
        if (savedInstanceState != null) {
            mViewAwardAdapter.setListLayout(savedInstanceState.getInt(KEY_LIST_LAYOUT));
            mSelectedPosition = savedInstanceState.getInt(KEY_POSITION);
        }

        // Get a reference to the RecyclerView, and attach the adapter to it.
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.viewPostList);
        mRecyclerView.setAdapter(mViewAwardAdapter);

        // This setting improves performance as long as changes in content do not change
        // the layout size of the RecyclerView.
        mRecyclerView.setHasFixedSize(true);

        // Display the list layout as either list view or grid view
        setListLayout(context, mViewAwardAdapter.isListLayoutList());

        return rootView;
    }

    /**
     * Save the fragment's state.
     * @param outState the bundle in which to save the fragment's state
     */
    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putInt(KEY_LIST_LAYOUT, mViewAwardAdapter.getListLayout());
        outState.putInt(KEY_POSITION, mSelectedPosition);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(final Bundle bundle) {
        super.onActivityCreated(bundle);
        getLoaderManager().initLoader(AWARD_LIST_LOADER_ID, bundle, this);
    }

    /**
     * Perform processing required when the fragment is resumed.
     */
    @Override
    public void onResume() {
        super.onResume();

        // Register the shared preference change listener.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.registerOnSharedPreferenceChangeListener(this);

        // Register a content observer on the ViewAwards URI.
        // The observer will be notified whenever the view award data changes.
        getContext().getContentResolver().registerContentObserver(
                DataContract.ViewAwardEntry.buildUriForAllRows(), true,
                getViewAwardContentObserver());
    }

    @Override
    public void onPause() {
        // Unregister the view awards content observer.
        getContext().getContentResolver().unregisterContentObserver(
                getViewAwardContentObserver());

        // Unregister the shared preference change listener.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.unregisterOnSharedPreferenceChangeListener(this);

        super.onPause();
    }

    //---------------------------------------------------------------------
    // Menu actions

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        // Inflate the menu; this adds items to the app bar if it is present.
        inflater.inflate(R.menu.menu_award_list_fragment, menu);

        boolean isListLayoutGrid = mViewAwardAdapter != null && mViewAwardAdapter.isListLayoutGrid();
        menu.findItem(R.id.menu_option_list_view).setVisible(isListLayoutGrid);
        menu.findItem(R.id.menu_option_grid_view).setVisible(!isListLayoutGrid);

        Context context = getActivity();
        displayMenuOptionForFilter(context, menu);

        mMenu = menu;
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
        if (context == null) {
            return super.onOptionsItemSelected(item);
        }

        switch (item.getItemId()) {
            // list view
            case R.id.menu_option_list_view:
                setListLayout(context, true);
                return true;

            // grid view
            case R.id.menu_option_grid_view:
                setListLayout(context, false);
                return true;

            // sort
            case R.id.menu_option_sort:
                displayAwardListSortOptions();
                return true;

            // filter and filter (active)
            case R.id.menu_option_filter:
            case R.id.menu_option_filter_active:
                displayAwardListFilter();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sets the list layout to list view or grid view.
     * @param context the context
     * @param isListLayoutList if true, set the list to list view, otherwise set the layout to grid view
     */
    private void setListLayout(@NonNull Context context, boolean isListLayoutList) {
        if (mRecyclerView != null && mViewAwardAdapter != null) {
            if (isListLayoutList) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                mRecyclerView.addItemDecoration(
                        new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
                mViewAwardAdapter.setListLayoutList();
            } else {
                int columnCount = getResources().getInteger(R.integer.list_grid_column_count);
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
                mViewAwardAdapter.setListLayoutGrid();
            }
            // The grid icon is shown in list view and vice versa
            if (mMenu != null) {
                mMenu.findItem(R.id.menu_option_list_view).setVisible(!isListLayoutList);
                mMenu.findItem(R.id.menu_option_grid_view).setVisible(isListLayoutList);
            }
        }
    }

    /**
     * Display the sort options dialog.
     */
    private void displayAwardListSortOptions() {
        // clear any saved position, which is now meaningless
        mSelectedPosition = RecyclerView.NO_POSITION;

        // display the sort dialog
        FragmentManager fm = getFragmentManager();
        AwardListSortFragment awardListSortFragment = new AwardListSortFragment();
        awardListSortFragment.show(fm, "TAG_AWARD_LIST_SORT_FRAGMENT");
    }

    /**
     * Display the appropriate filter icon, based on whether there are any active filters.
     * @param context the context
     * @param menu the menu in which to display the filter icon
     */
    private void displayMenuOptionForFilter(@Nullable Context context, @Nullable Menu menu) {
        if (menu == null) {
            return;
        }
        boolean isFilterActive = PrefUtils.isFilterActive(context);
        menu.findItem(R.id.menu_option_filter).setVisible(!isFilterActive);
        menu.findItem(R.id.menu_option_filter_active).setVisible(isFilterActive);
    }

    /**
     * Display the filter dialog.
     */
    private void displayAwardListFilter() {
        // clear any saved position, which is now meaningless
        mSelectedPosition = RecyclerView.NO_POSITION;

        // display the filter dialog
        FragmentManager fm = getFragmentManager();
        AwardListFilterFragment awardListFilterFragment = new AwardListFilterFragment();
        awardListFilterFragment.show(fm, "TAG_AWARD_LIST_FILTER_FRAGMENT");
    }

    //--------------------------------------------------------------
    // Loader Callbacks

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        Context context = getActivity();
        if (context == null) {
            return null;
        }

        Uri uri = null;
        String sortOrder = null;
        String filterCategory = null;
        String filterGenre = null;
        String filterWishlist = null;
        String filterWatched = null;
        String filterFavourite = null;

        // URI, sort order and filters are fiddly!
        if (args != null) {
            uri = args.getParcelable(KEY_VIEW_AWARD_URI);
            if (uri != null) {
                sortOrder = uri.getQueryParameter(DataContract.PARAM_SORT_ORDER);
                filterCategory = uri.getQueryParameter(DataContract.PARAM_FILTER_CATEGORY);
                filterGenre = uri.getQueryParameter(DataContract.PARAM_FILTER_GENRE);
                filterWishlist = uri.getQueryParameter(DataContract.PARAM_FILTER_WISHLIST);
                filterWatched = uri.getQueryParameter(DataContract.PARAM_FILTER_WATCHED);
                filterFavourite = uri.getQueryParameter(DataContract.PARAM_FILTER_FAVOURITE);
            }
        }
        if (sortOrder == null) {
            sortOrder = PrefUtils.getAwardListSortOrder(context);
        }
        if (filterCategory == null) {
            filterCategory = PrefUtils.getAwardListFilterCategory(context);
        }
        if (filterGenre == null) {
            filterGenre = PrefUtils.getAwardListFilterGenre(context);
        }
        if (filterWishlist == null) {
            filterWishlist = PrefUtils.getAwardListFilterWishlist(context);
        }
        if (filterWatched == null) {
            filterWatched = PrefUtils.getAwardListFilterWatched(context);
        }
        if (filterFavourite == null) {
            filterFavourite = PrefUtils.getAwardListFilterFavourite(context);
        }

        ViewAwardQueryParameters parameters = ViewAwardQueryParameters.builder()
                .sortOrder(sortOrder)
                .filterCategory(filterCategory)
                .filterGenre(filterGenre)
                .filterWishlist(filterWishlist)
                .filterWatched(filterWatched)
                .filterFavourite(filterFavourite)
                .build();

        if (uri == null) {
            uri = DataContract.ViewAwardEntry.buildUriWithParameters(parameters);
        }

        // Get the selection and selectionArgs corresponding to the parameters
        String selection = DataProvider.getSelectionForViewAwardQueryParams();
        String[] selectionArgs = DataProvider.getSelectionArgsForViewAwardQueryParams(parameters);

        return new CursorLoader(context,
                uri,
                DataContract.ViewAwardEntry.ALL_COLUMNS,
                selection,
                selectionArgs,
                sortOrder);
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link android.support.v4.app.FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     * <p/>
     *
     * <p>
     * This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * </p>
     * <ul>
     * <li> The Loader will monitor for changes to the data, and report
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
     * here.</li>
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.</li>
     * </ul>
     *
     * @param loader The Loader that has finished loading.
     * @param cursor The cursor generated by the Loader.
     */
    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor) {
        mViewAwardAdapter.swapCursor(cursor);

        // If there's a desired position to restore to, do so now.
        if (mSelectedPosition != RecyclerView.NO_POSITION) {
            mRecyclerView.smoothScrollToPosition(mSelectedPosition);
        }

        // If there is no data, set an appropriate message in the 'empty view'
        updateEmptyView(getActivity());
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        if (loader != null) {
            mViewAwardAdapter.swapCursor(null);
        }
    }

    /**
     * Perform processing required when the list data has changed, i.e. reload the loader data,
     * so that onLoadFinished() can update the adapter data, hence updating the view.
     */
    private void onDataChanged() {
        Loader loader = getLoaderManager().getLoader(AWARD_LIST_LOADER_ID);
        if (loader != null) {
            loader.forceLoad();
        }
    }
//    public void onDataChanged(Bundle bundle) {
//        getLoaderManager().restartLoader(AWARD_LIST_LOADER_ID, bundle, this);
//    }

    //--------------------------------------------------------------
    // Empty list methods

    /**
     * Sets an appropriate message in the 'empty view', based on the network status and feed status.
     * @param context the context
     */
    private void updateEmptyView(@Nullable Context context) {
        if (mViewAwardAdapter.getItemCount() == 0) {
            Activity activity = getActivity();
            if (activity != null) {
                // Get the appropriate message depending on the state of the network
                @StringRes int message = getNoDataMessage(context);

                // Write the message to the empty list view
                TextView emptyListView = (TextView) activity.findViewById(R.id.viewAwardListEmpty);
                emptyListView.setText(message);
            }
        }
    }

    /**
     * Returns the appropriate message to display when there is no data,
     * based on the network status and whether there are active filters.
     * @param context the context
     * @return the appropriate message to display, as a String resource
     */
    private @StringRes int getNoDataMessage(@Nullable Context context) {
        boolean isNetworkAvailable = getNetUtils().isNetworkAvailable(context);

        if (!isNetworkAvailable) {
            return R.string.no_data_no_connection;
        } else if (PrefUtils.isFilterActive(context)) {
            return R.string.no_data_filter_active;
        } else {
            return R.string.no_data_available;
        }
    }

    //--------------------------------------------------------------
    // Getters and setters

    /**
     * Convenience method which returns a reference to a local database.
     * @return a reference to a local database
     */
    private static LocalDatabase getLocalDatabase() {
        return ObjectFactory.getLocalDatabase();
    }

    /**
     * Returns a NetUtils object.
     * @return a NetUtils object
     */
    private NetUtils getNetUtils() {
        return NetUtils.getInstance();
    }

    /**
     * Convenience method which returns a reference to a ViewUtils object.
     * @return a reference to a ViewUtils object
     */
    @NonNull
    private static ViewUtils getViewUtils() {
        return ObjectFactory.getViewUtils();
    }

    //--------------------------------------------------------------
    // SharedPreference listener

    /**
     * Called when a shared preference is changed, added or removed. This
     * may be called even if a preference is set to its existing value.
     * This callback will be run on the main thread.
     * @param sharedPreferences The {@link SharedPreferences} that received the change.
     * @param key               The key of the preference that was changed, added or removed
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Context context = getActivity();
        if (context == null) {
            return;
        }
        // If the sort order or a filter has changed, restart the loader to requery the data
        if (PrefUtils.isAwardListSortOrderKey(context, key)
                || PrefUtils.isAwardListFilterCategoryKey(context, key)
                || PrefUtils.isAwardListFilterGenreKey(context, key)
                || PrefUtils.isAwardListFilterWishlistKey(context, key)
                || PrefUtils.isAwardListFilterWatchedKey(context, key)
                || PrefUtils.isAwardListFilterFavouriteKey(context, key)) {

            // Construct the URI using the sort and filter parameters
            ViewAwardQueryParameters parameters = ViewAwardQueryParameters.builder()
                    .sortOrder(PrefUtils.getAwardListSortOrder(context))
                    .filterCategory(PrefUtils.getAwardListFilterCategory(context))
                    .filterGenre(PrefUtils.getAwardListFilterGenre(context))
                    .filterWishlist(PrefUtils.getAwardListFilterWishlist(context))
                    .filterWatched(PrefUtils.getAwardListFilterWatched(context))
                    .filterFavourite(PrefUtils.getAwardListFilterFavourite(context))
                    .build();
            Uri uri = DataContract.ViewAwardEntry.buildUriWithParameters(parameters);

            // Restart the loader
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY_VIEW_AWARD_URI, uri);
            getLoaderManager().restartLoader(AWARD_LIST_LOADER_ID, bundle, this);

            // Redisplay the filter menu icon - it may have changed
            displayMenuOptionForFilter(context, mMenu);
        }
    }

    //--------------------------------------------------------------
    // ContentObserver

    private ViewAwardContentObserver getViewAwardContentObserver() {
        if (mViewAwardContentObserver == null) {
            mViewAwardContentObserver = new ViewAwardContentObserver(new Handler());
        }
        return mViewAwardContentObserver;
    }

    /**
     * A class whose implementations can be used to observe changes to data at a URI,
     * and update the cursor loader when there is a change.
     */
    public class ViewAwardContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        ViewAwardContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            onDataChanged();
        }
    }

    //--------------------------------------------------------------------------

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections, for example.
     */
    public interface ListFragmentContainer {
        /**
         * Callback for when an item has been selected.
         * @param context the context
         * @param uri the URI of the selected list item
         */
        void onItemSelected(Context context, Uri uri);
    }

}
