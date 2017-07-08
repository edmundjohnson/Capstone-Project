package uk.jumpingmouse.moviecompanion.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * This class represents the parameters which affect the ViewAward list, such as sort order
 * and filters.
 * @author Edmund Johnson
 */

public class ViewAwardListParameters {

    // parameter values

    public static final String SORT_ORDER_AWARD_DATE_ASC = "awardDate ASC";
    public static final String SORT_ORDER_AWARD_DATE_DESC = "awardDate DESC";
    public static final String SORT_ORDER_TITLE_ASC = "title ASC";
    public static final String SORT_ORDER_TITLE_DESC = "title DESC";
    public static final String SORT_ORDER_RUNTIME_ASC = "runtime ASC";
    public static final String SORT_ORDER_RUNTIME_DESC = "runtime DESC";
    public static final String SORT_ORDER_DEFAULT = SORT_ORDER_AWARD_DATE_DESC;

    // Strings are used for the filter values so they can be used in URIs.

    public static final String FILTER_WISHLIST_ANY = "wishlistAny";
    public static final String FILTER_WISHLIST_ONLY = "wishlistOnly";
    public static final String FILTER_WISHLIST_EXCLUDE = "wishlistExclude";
    public static final String FILTER_WISHLIST_DEFAULT = FILTER_WISHLIST_ANY;
    // options must match the order in arrays.xml
    public static final String[] FILTER_WISHLIST_OPTIONS = new String[] {
            FILTER_WISHLIST_ANY, FILTER_WISHLIST_ONLY, FILTER_WISHLIST_EXCLUDE
    };

    public static final String FILTER_WATCHED_ANY = "watchedAny";
    public static final String FILTER_WATCHED_ONLY = "watchedOnly";
    public static final String FILTER_WATCHED_EXCLUDE = "watchedExclude";
    public static final String FILTER_WATCHED_DEFAULT = FILTER_WATCHED_ANY;
    // options must match the order in arrays.xml
    public static final String[] FILTER_WATCHED_OPTIONS = new String[] {
            FILTER_WATCHED_ANY, FILTER_WATCHED_ONLY, FILTER_WATCHED_EXCLUDE
    };

    public static final String FILTER_FAVOURITE_ANY = "favouriteAny";
    public static final String FILTER_FAVOURITE_ONLY = "favouriteOnly";
    public static final String FILTER_FAVOURITE_EXCLUDE = "favouriteExclude";
    public static final String FILTER_FAVOURITE_DEFAULT = FILTER_FAVOURITE_ANY;
    // options must match the order in arrays.xml
    public static final String[] FILTER_FAVOURITE_OPTIONS = new String[] {
            FILTER_FAVOURITE_ANY, FILTER_FAVOURITE_ONLY, FILTER_FAVOURITE_EXCLUDE
    };

    // parameters
    private String sortOrder;
    private String filterWishlist;
    private String filterWatched;
    private String filterFavourite;

    // Getters and setters

    @NonNull
    public String getSortOrder() {
        return sortOrder == null ? SORT_ORDER_DEFAULT : sortOrder;
    }
    public void setSortOrder(@Nullable String sortOrder) {
        this.sortOrder = sortOrder;
    }

    @NonNull
    public String getFilterWishlist() {
        return filterWishlist == null ? FILTER_WISHLIST_DEFAULT : filterWishlist;
    }
    public void setFilterWishlist(@Nullable String filterWishlist) {
        this.filterWishlist = filterWishlist;
    }

    @NonNull
    public String getFilterWatched() {
        return filterWatched == null ? FILTER_WATCHED_DEFAULT : filterWatched;
    }
    public void setFilterWatched(@Nullable String filterWatched) {
        this.filterWatched = filterWatched;
    }

    @NonNull
    public String getFilterFavourite() {
        return filterFavourite == null ? FILTER_FAVOURITE_DEFAULT : filterFavourite;
    }
    public void setFilterFavourite(@Nullable String filterFavourite) {
        this.filterFavourite = filterFavourite;
    }

}
