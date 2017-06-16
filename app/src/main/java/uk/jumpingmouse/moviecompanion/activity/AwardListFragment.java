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
import android.support.annotation.MainThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.adapter.ViewAwardAdapter;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.model.LocalDatabase;

/**
 * The fragment class for the list of posts.
 * @author Edmund Johnson
 */
public class AwardListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
            SharedPreferences.OnSharedPreferenceChangeListener {

    /** The cursor loader id. */
    private static  final int AWARD_LIST_LOADER_ID = 1;

    // Attributes for saving and restoring the fragment's state
    private static final String KEY_POSITION = "KEY_POSITION";

//    /** The maximum number of items to show on the list page. */
//    private static final int LIST_SIZE_MAX = 100;
//    //private static final int LIST_SIZE_MAX = 6;  // Test only !!!

    /** The cursor loader for view awards. */
    private CursorLoader mViewAwardsCursorLoader;

    /** The content observer for view awards. */
    private ViewAwardContentObserver mViewAwardContentObserver;

    /** The data adapter. */
    private ViewAwardAdapter mViewAwardAdapter;

    /** The RecyclerView containing the list of awards. */
    private RecyclerView mRecyclerView;
    /** The position in the list of the currently selected item. */
    private int mSelectedPosition = RecyclerView.NO_POSITION;

    //--------------------------------------------------------------
    // Lifecycle Methods

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // Add the next line if the fragment needs to handle menu events.
//        //setHasOptionsMenu(true);
//    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Create a click handler for the list items
        ViewAwardAdapter.AdapterOnClickHandler clickHandler =
                new ViewAwardAdapter.AdapterOnClickHandler() {
                    @Override
                    public void onClick(final int movieId, final int selectedPosition) {

                        if (mViewAwardAdapter != null && getActivity() != null) {
                            mViewAwardAdapter.handleItemClick(movieId, selectedPosition,
                                    (AwardListFragment.ListFragmentContainer) getActivity());
                        }
                    }
                };

        View rootView = inflater.inflate(R.layout.award_list_fragment, container, false);
        View emptyListView = rootView.findViewById(R.id.viewAwardListEmpty);

        // The adapter will take data and populate the RecyclerView.
        // We cannot use FLAG_AUTO_REQUERY since it is deprecated, so we would end
        // up with an empty list the first time we run.
        mViewAwardAdapter = ViewAwardAdapter.newInstance(getActivity(), clickHandler, emptyListView);

        // Get a reference to the RecyclerView, and attach this adapter to it.
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.viewPostList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mViewAwardAdapter);

        // This setting improves performance as long as changes in content do not change
        // the layout size of the RecyclerView.
        mRecyclerView.setHasFixedSize(true);

        // Restore any saved state
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_POSITION)) {
            // The RecyclerView probably hasn't even been populated yet.
            // Actually perform the swap out in onLoadFinished.
            mSelectedPosition = savedInstanceState.getInt(KEY_POSITION);
        }

        return rootView;
    }


    @Override
    public void onSaveInstanceState(final Bundle outState) {
        // When device rotates, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to RecyclerView.NO_POSITION,
        // so check for that before storing.
        if (mSelectedPosition != RecyclerView.NO_POSITION) {
            outState.putInt(KEY_POSITION, mSelectedPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(final Bundle bundle) {
        super.onActivityCreated(bundle);
        getLoaderManager().initLoader(AWARD_LIST_LOADER_ID, bundle, this);
    }

    /**
     * Perform processing required when the fragment is resumed.
     * i.e. register to be notified of changes to SharedPreferences.
     */
    @Override
    public void onResume() {
        super.onResume();

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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.unregisterOnSharedPreferenceChangeListener(this);

        super.onPause();
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

        // buildUriForAllRows gets a number of view awards in descending order of date
        Uri uri = DataContract.ViewAwardEntry.buildUriForAllRows();

        mViewAwardsCursorLoader = new CursorLoader(getActivity(),
                uri,
                DataContract.ViewAwardEntry.ALL_COLUMNS,
                null,
                null,
                null);

        return mViewAwardsCursorLoader;
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

        // If we don't need to restart the loader, and there's a desired position
        // to restore to, do so now.
        //int selectedPosition = getSelectedPosition();
        if (mSelectedPosition != RecyclerView.NO_POSITION) {
            mRecyclerView.smoothScrollToPosition(mSelectedPosition);

            // If the device has been rotated in 2-pane mode, the post fragment has been
            // created anew without a saved state. Display the post here.
//            mViewAwardAdapter.selectItemAtPosition(selectedPosition);

//            if (cursor != null) {
//                cursor.moveToPosition(selectedPosition);
//                long postId = cursor.getLong(PostListFragment.COL_POST_ID);
//                Uri uri = DataContract.PostEntry.buildUriPostId(postId);
//
//                PostListActivity activity = (PostListActivity) getActivity();
//                activity.displayPost(activity, uri);
//            }

//            RecyclerView.ViewHolder viewHolder = mRecyclerView.
//                                findViewHolderForAdapterPosition(getSelectedPosition());
//            if (viewHolder != null) {
//                viewHolder.itemView.setActivated(true);
//            }

        }
        // If there is no data, set an appropriate message in the 'empty view'
        updateEmptyView();
    }

//    /**
//     * Sets an appropriate message in the 'empty view', based on the network status and feed status.
//     * @param context the context
//     * @param emptyView the view to display when there is no data
//     */
//    private void updateEmptyView(@Nullable final Context context,
//                                 @NonNull final TextView emptyView) {
//        int message;
//        if (!getNetUtils().isNetworkAvailable(context)) {
//            message = R.string.no_data_no_connection;
//        } else {
//            @PrefUtils.FeedStatus int feedStatus = getPrefUtils().getFeedStatus(context);
//            switch (feedStatus) {
//                case PrefUtils.FEED_STATUS_SERVER_INVALID:
//                    message = R.string.no_data_server_invalid;
//                    break;
//                case PrefUtils.FEED_STATUS_SERVER_ERROR:
//                    message = R.string.no_data_server_error;
//                    break;
//                case PrefUtils.FEED_STATUS_SERVER_NO_DATA:
//                    message = R.string.no_data_server_no_data;
//                    break;
//                case PrefUtils.FEED_STATUS_SERVER_DATA_INVALID:
//                    message = R.string.no_data_server_data_invalid;
//                    break;
//                case PrefUtils.FEED_STATUS_OK:
//                case PrefUtils.FEED_STATUS_UNKNOWN:
//                default:
//                    message = R.string.no_data_available;
//                    break;
//            }
//        }
//        emptyView.setText(message);
//    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        mViewAwardAdapter.swapCursor(null);
    }

//    /**
//     * Perform processing required when the list data has changed, i.e. restart the loader.
//     */
//    private void onDataChanged() {
////        // Maybe update the list data via syncImmediately, as per nd-sunshine-2 ???
////        getLoaderManager().restartLoader(AWARD_LIST_LOADER_ID, null, this);
//
//        mViewAwardsCursorLoader.forceLoad();
//    }
//    public void onDataChanged(Bundle bundle) {
//        getLoaderManager().restartLoader(AWARD_LIST_LOADER_ID, bundle, this);
//    }

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
            mViewAwardsCursorLoader.forceLoad();
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
        // TODO
//        if (getString(R.string.pref_feed_status_key).equals(key)) {
//            // If there is no data, set an appropriate message in the 'empty view'
//            updateEmptyView();
//        }
    }

    /**
     * Sets an appropriate message in the 'empty view', based on the network status and feed status.
     */
    private void updateEmptyView() {
        if (mViewAwardAdapter.getItemCount() == 0) {
            Activity activity = getActivity();

//            // Get the appropriate message depending on the state of the network
//            boolean isNetworkAvailable = getNetUtils().isNetworkAvailable(activity);
//            @PrefUtils.FeedStatus int feedStatus = getPrefUtils().getFeedStatus(activity);
//            int message = getPrefUtils().getNoDataMessage(isNetworkAvailable, feedStatus);
//
//            // Write the message to the empty list view
//            TextView emptyListView = (TextView) activity.findViewById(R.id.viewAwardListEmpty);
//            emptyListView.setText(message);
        }
    }

    //--------------------------------------------------------------
    // Getters and setters

//    private int getSelectedPosition() {
//        if (mViewAwardAdapter == null || mViewAwardAdapter.getItemCount() == 0) {
//            return RecyclerView.NO_POSITION;
//        }
//        return mViewAwardAdapter.getSelectedPosition();
//    }
//
//    private void setSelectedPosition(final int selectedPosition) {
//        if (mViewAwardAdapter != null
//                && mViewAwardAdapter.getItemCount() > selectedPosition) {
//            mViewAwardAdapter.setSelectedPosition(selectedPosition);
//        }
//    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
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
