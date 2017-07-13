package uk.jumpingmouse.moviecompanion.widget;

import uk.jumpingmouse.moviecompanion.model.DataContract;

/**
 * RemoteViewsService which controls the data shown in the scrollable Latest DVDs widget.
 */
public class RemoteViewsServiceLatestDvds extends RemoteViewsServiceBase {

    private static final int VIEW_AWARD_LIMIT_LATEST_DVDS = 10;

    /**
     * Returns the category of ViewAwards displayed in this widget.
     * @return the category of ViewAwards displayed in this widget
     */
    @Override
    protected String getFilterCategory() {
        return DataContract.ViewAwardEntry.FILTER_CATEGORY_DVD;
    }

    /**
     * Returns the number of ViewAwards displayed in this widget.
     * @return the number of ViewAwards displayed in this widget
     */
    @Override
    protected int getViewAwardLimit() {
        return VIEW_AWARD_LIMIT_LATEST_DVDS;
    }

}
