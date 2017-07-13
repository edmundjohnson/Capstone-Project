package uk.jumpingmouse.moviecompanion.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.support.annotation.NonNull;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.ViewAwardQueryParameters;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.model.DataProvider;
import uk.jumpingmouse.moviecompanion.utils.ViewUtils;

/**
 * RemoteViewsService which controls the data shown in the scrollable In Cinemas Now widget.
 */
public class InCinemasWidgetRemoteViewsService extends RemoteViewsService {

    private static final int IN_CINEMAS_WIDGET_VIEW_AWARD_LIMIT = 5;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor mCursor = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (mCursor != null) {
                    mCursor.close();
                }
                // This method is called by the app hosting the widget.
                // However, the ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                ViewAwardQueryParameters parameters = ViewAwardQueryParameters.builder()
                        .sortOrder(DataContract.ViewAwardEntry.SORT_ORDER_AWARD_DATE_DESC)
                        .filterCategory(DataContract.ViewAwardEntry.FILTER_CATEGORY_MOVIE)
                        .filterGenre(DataContract.ViewAwardEntry.FILTER_GENRE_ALL)
                        .filterWishlist(DataContract.ViewAwardEntry.FILTER_WISHLIST_ANY)
                        .filterWatched(DataContract.ViewAwardEntry.FILTER_WATCHED_ANY)
                        .filterFavourite(DataContract.ViewAwardEntry.FILTER_FAVOURITE_ANY)
                        .limit(IN_CINEMAS_WIDGET_VIEW_AWARD_LIMIT)
                        .build();

                // Get the selection and selectionArgs corresponding to the parameters
                String selection = DataProvider.getSelectionForViewAwardQueryParams();
                String[] selectionArgs = DataProvider.getSelectionArgsForViewAwardQueryParams(parameters);

                mCursor = getContentResolver().query(
                        DataContract.ViewAwardEntry.buildUriWithParameters(parameters),
                        null,
                        selection,
                        selectionArgs,
                        DataContract.ViewAwardEntry.SORT_ORDER_AWARD_DATE_DESC);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (mCursor != null) {
                    mCursor.close();
                    mCursor = null;
                }
            }

            @Override
            public int getCount() {
                return mCursor == null ? 0 : mCursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION
                        || mCursor == null
                        || !mCursor.moveToPosition(position)) {
                    return null;
                }

                // Get data from cursor
                String viewAwardId = mCursor.getString(DataContract.ViewAwardEntry.COL_ID);
                String poster = mCursor.getString(DataContract.ViewAwardEntry.COL_POSTER);
                String awardDate = getViewUtils().getAwardDateDisplayable(
                        mCursor.getString(DataContract.ViewAwardEntry.COL_AWARD_DATE));
                String title = mCursor.getString(DataContract.ViewAwardEntry.COL_TITLE);

                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_in_cinemas_list_item);

                // Load data into view
                Context context = getBaseContext();
                views.setTextViewText(R.id.txtAwardDate, awardDate);
                views.setTextViewText(R.id.txtTitle, title);
                try {
                    Bitmap bitmap = Picasso.with(context).load(poster).get();
                    views.setImageViewBitmap(R.id.imgPoster, bitmap);
                } catch (IOException e) {
                    Timber.w("getViewAt: IOException loading poster", e);
                }

                // If a list item is clicked on, pass the URI for the selected ViewAward to the
                // launched activity
                final Intent fillInIntent = new Intent();
                Uri uri = DataContract.ViewAwardEntry.buildUriForRowById(viewAwardId);
                fillInIntent.setData(uri);
                views.setOnClickFillInIntent(R.id.layoutWidgetInCinemasListItem, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_in_cinemas_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
//                if (mCursor.moveToPosition(position)) {
//                    return mCursor.getString(DataContract.ViewAwardEntry.COL_ID);
//                }
                return position;
            }

            /**
             * Returns whether the itemId of each list item is stable.  As each list item does not
             * have a numeric id (other than its position in the list, which may change),
             * false is returned.
             * @return false
             */
            @Override
            public boolean hasStableIds() {
                return false;
            }
        };
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
