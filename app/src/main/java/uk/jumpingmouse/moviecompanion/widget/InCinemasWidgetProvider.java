package uk.jumpingmouse.moviecompanion.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.activity.MainActivity;
import uk.jumpingmouse.moviecompanion.activity.MovieActivity;

/**
 * Provider for the scrollable 'In Cinemas Now' widget.
 */
public class InCinemasWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_DATA_UPDATED = "uk.jumpingmouse.moviecompanion.ACTION_DATA_UPDATED";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop for each app widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_in_cinemas);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.txtWidgetTitleInCinemas, pendingIntent);

            // Set up the collection
            setRemoteAdapter(context, views);

            // If a list item is clicked on, launch the MovieActivity
            Intent clickIntentTemplate = new Intent(context, MovieActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.listWidgetInCinemas, clickPendingIntentTemplate);
            views.setEmptyView(R.id.listWidgetInCinemas, R.id.txtWidgetInCinemasEmpty);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listWidgetInCinemas);
        }
    }

    /**
     * Sets the remote adapter used to fill in the list items.
     * @param views RemoteViews to set the RemoteAdapter
     */
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.listWidgetInCinemas,
                new Intent(context, InCinemasWidgetRemoteViewsService.class));
    }

    /**
     * Notify any listening receivers (e.g. the widget provider) that the data has changed.
     * "notifyWidgetProvider" might be a better name.
     * @param context the context
     */
    public static void updateWidgets(@NonNull Context context) {
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED).setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

}
