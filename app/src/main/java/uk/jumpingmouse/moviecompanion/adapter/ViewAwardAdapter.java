package uk.jumpingmouse.moviecompanion.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.activity.AwardListFragment;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

/**
 * The adapter for the award list.
 * This adapter exposes a list of awards from a {@link Cursor}
 * to a {@link android.support.v7.widget.RecyclerView}.
 * @author Edmund Johnson
 */
public final class ViewAwardAdapter extends RecyclerView.Adapter<ViewAwardAdapter.ViewHolder> {
//    /** Log tag for this class. */
//    private static final String TAG = "ViewAwardAdapter";

    /** The position of the currently selected item. */
    private int mSelectedPosition = RecyclerView.NO_POSITION;

    private final Context mContext;
    private Cursor mCursor;
    private final AdapterOnClickHandler mClickHandler;
    private final View mEmptyListView;
    private @LayoutRes int mListLayout;

    /**
     * Private constructor to prevent direct instantiation from outside this class.
     * @param context the context
     * @param clickHandler the item click handler
     * @param emptyListView the view to display if the list is empty
     */
    private ViewAwardAdapter(final Context context, final AdapterOnClickHandler clickHandler,
                             final View emptyListView) {
        mContext = context;
        mClickHandler = clickHandler;
        mEmptyListView = emptyListView;
        setListLayoutList();
    }

    /**
     * Returns a new instance of the adapter.
     * @param context the context
     * @param clickHandler the item click handler
     * @param emptyListView the view to display if the list is empty
     * @return a new instance of the adapter
     */
    public static ViewAwardAdapter newInstance(final Context context, final AdapterOnClickHandler clickHandler,
                                               final View emptyListView) {
        return new ViewAwardAdapter(context, clickHandler, emptyListView);
    }

    @Override
    public @LayoutRes int getItemViewType(int position) {
        return getListLayout();
    }

    /**
     * Create a new view holder for an item (invoked by the layout manager).
     * @param viewGroup the parent view group
     * @param viewType the view type
     * @return a view holder for an item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType) {
        //Timber.d("onCreateViewHolder() called with: viewGroup = ["
        //                 + viewGroup + "], viewType = [" + viewType + "]");
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(viewType, viewGroup, false);
        return new ViewHolder(itemView);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item
     * at the given position.
     *
     * <p>Note that unlike ListView, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()}
     * which will have the updated adapter position.
     *
     * @param viewHolder The ViewHolder which should be updated to represent the contents of the
     *                   item at the given position in the data set.
     * @param cursorPosition The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int cursorPosition) {
        mCursor.moveToPosition(cursorPosition);

        if (cursorPosition == getSelectedPosition()) {
            viewHolder.itemView.setSelected(true);
        }

        // Highlight the background of the selected item
        if (cursorPosition == getSelectedPosition()) {
            viewHolder.itemView.setActivated(true);
        } else {
            viewHolder.itemView.setActivated(false);
        }

        // Extract the award info from the cursor
        String awardId = mCursor.getString(DataContract.ViewAwardEntry.COL_ID);
        int movieId = mCursor.getInt(DataContract.ViewAwardEntry.COL_MOVIE_ID);
        String awardDate = getViewUtils().getAwardDateDisplayable(
                mCursor.getString(DataContract.ViewAwardEntry.COL_AWARD_DATE));
        String categoryCode = mCursor.getString(DataContract.ViewAwardEntry.COL_CATEGORY);
        @DrawableRes int categoryRes = getViewUtils().getCategoryRes(categoryCode);
        String categoryText = getViewUtils().getCategoryText(mContext, categoryCode);
        int displayOrder = mCursor.getInt(DataContract.ViewAwardEntry.COL_DISPLAY_ORDER);

        String movieTitle = mCursor.getString(DataContract.ViewAwardEntry.COL_TITLE);
        int runtime = mCursor.getInt(DataContract.ViewAwardEntry.COL_RUNTIME);
        String runtimeText = getViewUtils().getRuntimeText(mContext, runtime);
        String genre = mCursor.getString(DataContract.ViewAwardEntry.COL_GENRE);
        String poster = mCursor.getString(DataContract.ViewAwardEntry.COL_POSTER);
        // the values of onWishlist etc. affect the menu only and are handled in the fragment

        // replace the contents of the item view with the data for the award
        Picasso.with(mContext).load(poster).into(viewHolder.getImgPoster());
        viewHolder.getTxtMovieTitle().setText(movieTitle);
        viewHolder.getTxtRuntime().setText(runtimeText);
        viewHolder.getTxtGenre().setText(genre);
        viewHolder.getImgCategory().setImageResource(categoryRes);
        viewHolder.getImgCategory().setContentDescription(categoryText);
        viewHolder.getTxtAwardDate().setText(awardDate);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    /**
     * Return the cursor.
     * @return the cursor
     */
    @Nullable
    private Cursor getCursor() {
        return mCursor;
    }

    /**
     * Swap in a new cursor.
     * @param newCursor the new cursor
     */
    public void swapCursor(@Nullable final Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();

        int emptyViewVisibility = getItemCount() == 0 ? View.VISIBLE : View.GONE;
        mEmptyListView.setVisibility(emptyViewVisibility);
    }

    /**
     * Interface for the item click handler.
     */
    public interface AdapterOnClickHandler {
        void onClick(String id, int selectedPosition);
    }

    /**
     * Handler method invoked when an item is clicked.
     * @param id the id of the view award which has been clicked
     * @param selectedPosition the item's position in the list
     * @param listFragmentContainer the activity containing the fragment in which the list is displayed
     */
    public void handleItemClick(final String id, final int selectedPosition,
                             final AwardListFragment.ListFragmentContainer listFragmentContainer) {
//        // Ensure the previously-selected item is no longer highlighted
//        notifyItemChanged(getSelectedPosition());

        // Save the position of the item that was clicked
        setSelectedPosition(selectedPosition);

//        // Ensure the newly-selected item is highlighted
//        notifyItemChanged(getSelectedPosition());

        // Callback the list fragment container (i.e. the list activity), so it can
        // display the selected movie
        Uri uri = DataContract.ViewAwardEntry.buildUriForRowById(id);
        listFragmentContainer.onItemSelected(mContext, uri);
    }

//    /**
//     * Select one of the items on the list.
//     * @param selectedPosition the position of the list item to be selected
//     * @param listFragmentContainer the activity containing the fragment in which the list is displayed
//     */
//    public void selectItemAtPosition(final int selectedPosition,
//                         final AwardListFragment.ListFragmentContainer listFragmentContainer) {
//        Cursor cursor = getCursor();
//        if (cursor != null && cursor.moveToPosition(selectedPosition)) {
//            int movieId = cursor.getInt(DataContract.ViewAwardEntry.COL_MOVIE_ID);
//            handleItemClick(movieId, selectedPosition, listFragmentContainer);
//        }
//    }

    // Getters and setters


    public @LayoutRes int getListLayout() {
        return mListLayout;
    }
    public void setListLayout(final @LayoutRes int listLayout) {
        mListLayout = listLayout;
    }
    public boolean isListLayoutList() {
        return R.layout.award_list_item_list == getListLayout();
    }
    public boolean isListLayoutGrid() {
        return R.layout.award_list_item_grid == getListLayout();
    }
    public void setListLayoutList() {
        setListLayout(R.layout.award_list_item_list);
    }
    public void setListLayoutGrid() {
        setListLayout(R.layout.award_list_item_grid);
    }

    private int getSelectedPosition() {
        return mSelectedPosition;
    }
    private void setSelectedPosition(final int selectedPosition) {
        mSelectedPosition = selectedPosition;
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
     * The ViewHolder class, which provides a cache of the views within a list item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /** The image view containing the poster. */
        private final ImageView imgPoster;
        /** The view containing the movie title. */
        private final TextView txtMovieTitle;
        /** The view containing the movie runtime. */
        private final TextView txtRuntime;
        /** The view containing the movie genre. */
        private final TextView txtGenre;
        /** The image view containing the category drawable icon. */
        private final ImageView imgCategory;
        /** The view containing the award date. */
        private final TextView txtAwardDate;

        /**
         * Constructor.
         * @param view the view containing the item
         */
        ViewHolder(final View view) {
            super(view);
            imgPoster = (ImageView) view.findViewById(R.id.imgPoster);
            txtMovieTitle = (TextView) view.findViewById(R.id.txtTitle);
            txtRuntime = (TextView) view.findViewById(R.id.txtRuntime);
            txtGenre = (TextView) view.findViewById(R.id.txtGenre);
            imgCategory = (ImageView) view.findViewById(R.id.imgCategory);
            txtAwardDate = (TextView) view.findViewById(R.id.txtAwardDate);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (mCursor.moveToPosition(position)) {
                String id = mCursor.getString(DataContract.ViewAwardEntry.COL_ID);
                mClickHandler.onClick(id, position);
            }
        }

        // Getters

        @NonNull
        final ImageView getImgPoster() {
            return imgPoster;
        }

        @NonNull
        final TextView getTxtMovieTitle() {
            return txtMovieTitle;
        }

        @NonNull
        final TextView getTxtRuntime() {
            return txtRuntime;
        }

        @NonNull
        final TextView getTxtGenre() {
            return txtGenre;
        }

        @NonNull
        final ImageView getImgCategory() {
            return imgCategory;
        }

        @NonNull
        final TextView getTxtAwardDate() {
            return txtAwardDate;
        }
    }

}
