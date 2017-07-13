package uk.jumpingmouse.moviecompanion.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.activity.MainActivity;
import uk.jumpingmouse.moviecompanion.activity.MovieActivity;

/**
 * Provider for the scrollable list widgets.
 */
public abstract class WidgetProviderBase extends AppWidgetProvider {

    // For now, the same changes in data trigger the update of the widgets
    private static final String ACTION_DATA_UPDATED =
            "uk.jumpingmouse.moviecompanion.ACTION_DATA_UPDATED";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop for each app widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.list_widget);

            String widgetTitle = context.getString(getWidgetTitleResId());
            views.setTextViewText(R.id.txtWidgetTitle, widgetTitle);
            views.setEmptyView(R.id.listWidget, R.id.txtWidgetEmpty);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.txtWidgetTitle, pendingIntent);

            setRemoteAdapter(context, views);

            // If a list item is clicked on, launch the MovieActivity
            Intent clickIntentTemplate = new Intent(context, MovieActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.listWidget, clickPendingIntentTemplate);

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
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listWidget);
        }
    }

    /**
     * Sets the remote adapter used to fill in the list items.
     * @param context the context
     * @param views RemoteViews to set the RemoteAdapter
     */
    private void setRemoteAdapter(@Nullable Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.listWidget,
                new Intent(context, getRemoteViewsServiceClass()));
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

    /**
     * Returns the string resource id of the widget title.
     * @return the string resource id of the widget title
     */
    protected abstract @StringRes int getWidgetTitleResId();

    /**
     * Returns the RemoteViewsService class for the widget.
     * @return the RemoteViewsService class for the widget
     */
    @NonNull
    protected abstract Class getRemoteViewsServiceClass();

}
