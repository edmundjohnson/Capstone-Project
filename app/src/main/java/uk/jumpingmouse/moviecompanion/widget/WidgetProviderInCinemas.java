package uk.jumpingmouse.moviecompanion.widget;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import uk.jumpingmouse.moviecompanion.R;

/**
 * Provider for the scrollable In Cinemas widget.
 */
public class WidgetProviderInCinemas extends WidgetProviderBase {

    private static final String ACTION_DATA_UPDATED =
            "uk.jumpingmouse.moviecompanion.ACTION_DATA_UPDATED";

    /**
     * Returns the string resource id of the widget title.
     * @return the string resource id of the widget title
     */
    @Override
    protected @StringRes int getWidgetTitleResId() {
        return R.string.widget_title_in_cinemas;
    }

    /**
     * Returns the RemoteViewsService class for the widget.
     * @return the RemoteViewsService class for the widget
     */
    @Override
    @NonNull
    protected Class getRemoteViewsServiceClass() {
        return RemoteViewsServiceInCinemas.class;
    }

}
