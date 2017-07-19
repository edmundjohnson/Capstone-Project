package uk.jumpingmouse.moviecompanion.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * This class represents the parameters which can be supplied to a ViewAward list query,
 * such as sort order and filters.
 * @author Edmund Johnson
 */

public class ViewAwardQueryParameters {

    private ViewAwardQueryParameters() {
    }

    private ViewAwardQueryParameters(@NonNull String sortOrder, @NonNull String filterCategory,
                                     @NonNull String filterGenre, @NonNull String filterWishlist,
                                     @NonNull String filterWatched, @NonNull String filterFavourite,
                                     int limit) {
        this.sortOrder = sortOrder;
        this.filterCategory = filterCategory;
        this.filterGenre = filterGenre;
        this.filterWishlist = filterWishlist;
        this.filterWatched = filterWatched;
        this.filterFavourite = filterFavourite;
        this.limit = limit;
    }

    // parameters
    private String sortOrder;
    private String filterCategory;
    private String filterGenre;
    private String filterWishlist;
    private String filterWatched;
    private String filterFavourite;
    private int limit;

    //---------------------------------------------------------------
    // Getters

    @NonNull
    public String getSortOrder() {
        return sortOrder;
    }

    @NonNull
    public String getFilterCategory() {
        return filterCategory;
    }

    @NonNull
    public String getFilterGenre() {
        return filterGenre;
    }

    @NonNull
    public String getFilterWishlist() {
        return filterWishlist;
    }

    @NonNull
    public String getFilterWatched() {
        return filterWatched;
    }

    @NonNull
    public String getFilterFavourite() {
        return filterFavourite;
    }

    public int getLimit() {
        return limit;
    }

    //---------------------------------------------------------------
    // Builder implementation

    /**
     * Builder for this class.  Usage:
     * <blockquote><pre>
     * {@code
     *   ViewAwardQueryParameters movie = ViewAwardQueryParameters.builder()
     *         .sortOrder("awardDate DESC")
     *         .filterCategory("filter_category_movie")
     *         .filterGenre("filter_genre_comedy")
     *         .filterWishlist("filter_wishlist_any")
     *         // etc
     *        .build();
     * }
     * </pre></blockquote>
     * @return an instance of the ViewAwardQueryParameters class.
     */
    public static ViewAwardQueryParameters.Builder builder() {
        return new ViewAwardQueryParameters.Builder();
    }

    @SuppressWarnings("WeakerAccess")
    public static final class Builder {
        private String sortOrder;
        private String filterCategory;
        private String filterGenre;
        private String filterWishlist;
        private String filterWatched;
        private String filterFavourite;
        private int limit;

        private Builder() {
            this.limit = 0;
        }

        @NonNull
        public ViewAwardQueryParameters.Builder sortOrder(@Nullable String sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }
        @NonNull
        public ViewAwardQueryParameters.Builder filterCategory(@Nullable String filterCategory) {
            this.filterCategory = filterCategory;
            return this;
        }
        @NonNull
        public ViewAwardQueryParameters.Builder filterGenre(@Nullable String filterGenre) {
            this.filterGenre = filterGenre;
            return this;
        }
        @NonNull
        public ViewAwardQueryParameters.Builder filterWishlist(@Nullable String filterWishlist) {
            this.filterWishlist = filterWishlist;
            return this;
        }
        @NonNull
        public ViewAwardQueryParameters.Builder filterWatched(@Nullable String filterWatched) {
            this.filterWatched = filterWatched;
            return this;
        }
        @NonNull
        public ViewAwardQueryParameters.Builder filterFavourite(@Nullable String filterFavourite) {
            this.filterFavourite = filterFavourite;
            return this;
        }
        @NonNull
        public ViewAwardQueryParameters.Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        /** Builds and returns an object of this class. */
        @NonNull
        public ViewAwardQueryParameters build() {
            String missing = "";
            if (sortOrder == null) {
                missing += " sortOrder";
            }
            if (filterCategory == null) {
                missing += " filterCategory";
            }
            if (filterGenre == null) {
                missing += " filterGenre";
            }
            if (filterWishlist == null) {
                missing += " filterWishlist";
            }
            if (filterWatched == null) {
                missing += " filterWatched";
            }
            if (filterFavourite == null) {
                missing += " filterFavourite";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new ViewAwardQueryParameters(
                    this.sortOrder,
                    this.filterCategory,
                    this.filterGenre,
                    this.filterWishlist,
                    this.filterWatched,
                    this.filterFavourite,
                    this.limit);
        }
    }

}
