package uk.jumpingmouse.moviecompanion.widget;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import uk.jumpingmouse.moviecompanion.R;

/**
 * Provider for the scrollable Latest DVDs widget.
 */
public class WidgetProviderLatestDvds extends WidgetProviderBase {

    /**
     * Returns the string resource id of the widget title.
     * @return the string resource id of the widget title
     */
    protected @StringRes int getWidgetTitleResId() {
        return R.string.widget_title_latest_dvds;
    }

    /**
     * Returns the RemoteViewsService class for the widget.
     * @return the RemoteViewsService class for the widget
     */
    @NonNull
    protected Class getRemoteViewsServiceClass() {
        return RemoteViewsServiceLatestDvds.class;
    }

}
