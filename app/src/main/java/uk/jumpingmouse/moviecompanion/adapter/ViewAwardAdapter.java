package uk.jumpingmouse.moviecompanion.adapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.activity.MovieActivity;
import uk.jumpingmouse.moviecompanion.analytics.AnalyticsManager;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.utils.ModelUtils;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

/**
 * The adapter for the view award list.
 * This adapter exposes a list of view awards from a {@link Cursor}
 * to a {@link android.support.v7.widget.RecyclerView}.
 * @author Edmund Johnson
 */
public final class ViewAwardAdapter extends RecyclerView.Adapter<ViewAwardAdapter.ViewHolder> {

    /** The position of the most recently clicked item. */
    private int mSelectedPosition = RecyclerView.NO_POSITION;

    private final Activity mActivity;
    private Cursor mCursor;
    private final View mEmptyListView;
    private @LayoutRes int mListLayout;

    /**
     * Private constructor to prevent direct instantiation from outside this class.
     * @param activity the adapter's activity
     * @param emptyListView the view to display if the list is empty
     */
    private ViewAwardAdapter(final Activity activity, final View emptyListView) {
        mActivity = activity;
        mEmptyListView = emptyListView;
        setListLayoutList();
    }

    /**
     * Returns a new instance of the adapter.
     * @param activity the adapter's activity
     * @param emptyListView the view to display if the list is empty
     * @return a new instance of the adapter
     */
    public static ViewAwardAdapter newInstance(final Activity activity, final View emptyListView) {
        return new ViewAwardAdapter(activity, emptyListView);
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
        String viewAwardId = mCursor.getString(DataContract.ViewAwardEntry.COL_ID);
        //int movieId = mCursor.getInt(DataContract.ViewAwardEntry.COL_MOVIE_ID);
        String awardDate = getViewUtils().getAwardDateDisplayable(
                mCursor.getString(DataContract.ViewAwardEntry.COL_AWARD_DATE));
        String categoryCode = mCursor.getString(DataContract.ViewAwardEntry.COL_CATEGORY);
        @DrawableRes int categoryRes = getViewUtils().getCategoryRes(categoryCode);
        String categoryText = getViewUtils().getCategoryText(mActivity, categoryCode);
        //int displayOrder = mCursor.getInt(DataContract.ViewAwardEntry.COL_DISPLAY_ORDER);

        String movieTitle = mCursor.getString(DataContract.ViewAwardEntry.COL_TITLE);
        int runtime = mCursor.getInt(DataContract.ViewAwardEntry.COL_RUNTIME);
        String runtimeText = getViewUtils().getRuntimeText(mActivity, runtime);
        String genre = mCursor.getString(DataContract.ViewAwardEntry.COL_GENRE);
        String poster = mCursor.getString(DataContract.ViewAwardEntry.COL_POSTER);
        String thumbnailUrl = ModelUtils.getThumbnailUrl(poster);
        // the values of onWishlist etc. affect the menu only and are handled in the fragment

        // replace the contents of the item view with the data for the award
        if (mActivity != null) {
            Picasso.with(mActivity).load(thumbnailUrl).into(viewHolder.getImgPoster());
            viewHolder.getTxtMovieTitle().setText(movieTitle);
            viewHolder.getTxtRuntime().setText(runtimeText);
            viewHolder.getTxtGenre().setText(ModelUtils.toGenreNameCsv(mActivity, genre));
            viewHolder.getImgCategory().setImageResource(categoryRes);
            viewHolder.getImgCategory().setContentDescription(categoryText);
            viewHolder.getTxtAwardDate().setText(awardDate);

            // Assign the shared element name to the list item's poster
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String transitionName = mActivity.getString(R.string.transition_movie, viewAwardId);
                viewHolder.getImgPoster().setTransitionName(transitionName);
                // Set the tag so the view can be found when the list activity is returned to.
                viewHolder.getImgPoster().setTag(transitionName);
            }
        }
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
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
            imgPoster = view.findViewById(R.id.imgPoster);
            txtMovieTitle = view.findViewById(R.id.txtTitle);
            txtRuntime = view.findViewById(R.id.txtRuntime);
            txtGenre = view.findViewById(R.id.txtGenre);
            imgCategory = view.findViewById(R.id.imgCategory);
            txtAwardDate = view.findViewById(R.id.txtAwardDate);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mActivity == null || mCursor == null) {
                return;
            }

            int position = getAdapterPosition();
            if (mCursor.moveToPosition(position)) {
                final String viewAwardId = mCursor.getString(DataContract.ViewAwardEntry.COL_ID);
                String movieId = mCursor.getString(DataContract.ViewAwardEntry.COL_MOVIE_ID);
                String movieTitle = mCursor.getString(DataContract.ViewAwardEntry.COL_TITLE);

                // log the event in analytics
                getAnalyticsManager().logViewMovie(movieId, movieTitle);

                // Build an intent for launching the movie activity
                Uri uri = DataContract.ViewAwardEntry.buildUriForRowById(viewAwardId);
                Intent intent = new Intent(mActivity, MovieActivity.class).setData(uri);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                        && imgPoster != null) {

                    // Start the movie activity with a shared element transition
                    String transitionName = mActivity.getString(R.string.transition_movie, viewAwardId);
                    intent.setAction(Intent.ACTION_VIEW);
                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            mActivity,
                            imgPoster,
                            transitionName)
                            .toBundle();
                    mActivity.startActivity(intent, bundle);
                } else {
                    // Start the movie activity without a shared element transition
                    mActivity.startActivity(intent);
                }
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

    /**
     * Convenience method which returns an AnalyticsManager.
     * @return an AnalyticsManager
     */
    @NonNull
    private static AnalyticsManager getAnalyticsManager() {
        return ObjectFactory.getAnalyticsManager();
    }

}
