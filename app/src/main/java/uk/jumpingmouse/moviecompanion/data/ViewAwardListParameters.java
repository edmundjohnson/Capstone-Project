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

    // parameters
    private String sortOrder;
    private String filterWishlist;

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
}
