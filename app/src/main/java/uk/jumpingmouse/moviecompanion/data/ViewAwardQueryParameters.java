package uk.jumpingmouse.moviecompanion.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uk.jumpingmouse.moviecompanion.model.DataContract;

/**
 * This class represents the parameters which can be supplied to a ViewAward list query,
 * such as sort order and filters.
 * @author Edmund Johnson
 */

public class ViewAwardQueryParameters {

    // parameters
    private String sortOrder;
    private String filterGenre;
    private String filterWishlist;
    private String filterWatched;
    private String filterFavourite;

    // Getters and setters

    @NonNull
    public String getSortOrder() {
        return sortOrder == null ? DataContract.ViewAwardEntry.SORT_ORDER_DEFAULT : sortOrder;
    }
    public void setSortOrder(@Nullable String sortOrder) {
        this.sortOrder = sortOrder;
    }

    @NonNull
    public String getFilterGenre() {
        return filterGenre == null ? DataContract.ViewAwardEntry.FILTER_GENRE_DEFAULT : filterGenre;
    }
    public void setFilterGenre(@Nullable String filterGenre) {
        this.filterGenre = filterGenre;
    }

    @NonNull
    public String getFilterWishlist() {
        return filterWishlist == null ? DataContract.ViewAwardEntry.FILTER_WISHLIST_DEFAULT : filterWishlist;
    }
    public void setFilterWishlist(@Nullable String filterWishlist) {
        this.filterWishlist = filterWishlist;
    }

    @NonNull
    public String getFilterWatched() {
        return filterWatched == null ? DataContract.ViewAwardEntry.FILTER_WATCHED_DEFAULT : filterWatched;
    }
    public void setFilterWatched(@Nullable String filterWatched) {
        this.filterWatched = filterWatched;
    }

    @NonNull
    public String getFilterFavourite() {
        return filterFavourite == null ? DataContract.ViewAwardEntry.FILTER_FAVOURITE_DEFAULT : filterFavourite;
    }
    public void setFilterFavourite(@Nullable String filterFavourite) {
        this.filterFavourite = filterFavourite;
    }

}
