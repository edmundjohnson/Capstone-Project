package uk.jumpingmouse.moviecompanion.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * This class represents the parameters which can be supplied to a ViewAward list query,
 * such as sort order and filters.
 * @author Edmund Johnson
 */

public final class ViewAwardQueryParameters {

    // parameters
    private String mSortOrder;
    private String mFilterGenre;
    private String mFilterWishlist;
    private String mFilterWatched;
    private String mFilterFavourite;
    private String mFilterCategory;
    private int mLimit;

    private ViewAwardQueryParameters() {
    }

    private ViewAwardQueryParameters(@NonNull String sortOrder, @NonNull String filterGenre,
                                     @NonNull String filterWishlist, @NonNull String filterWatched,
                                     @NonNull String filterFavourite, @NonNull String filterCategory,
                                     int limit) {
        this.mSortOrder = sortOrder;
        this.mFilterGenre = filterGenre;
        this.mFilterWishlist = filterWishlist;
        this.mFilterWatched = filterWatched;
        this.mFilterFavourite = filterFavourite;
        this.mFilterCategory = filterCategory;
        this.mLimit = limit;
    }

    //---------------------------------------------------------------
    // Getters

    @NonNull
    public String getSortOrder() {
        return mSortOrder;
    }

    @NonNull
    public String getFilterGenre() {
        return mFilterGenre;
    }

    @NonNull
    public String getFilterWishlist() {
        return mFilterWishlist;
    }

    @NonNull
    public String getFilterWatched() {
        return mFilterWatched;
    }

    @NonNull
    public String getFilterFavourite() {
        return mFilterFavourite;
    }

    @NonNull
    public String getFilterCategory() {
        return mFilterCategory;
    }

    public int getLimit() {
        return mLimit;
    }

    //---------------------------------------------------------------
    // Builder implementation

    /**
     * Builder for this class.  Usage:
     * <blockquote><pre>
     * {@code
     *   ViewAwardQueryParameters movie = ViewAwardQueryParameters.builder()
     *         .sortOrder("awardDate DESC")
     *         .filterGenre("genre_comedy")
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
        private String mSortOrder;
        private String mFilterGenre;
        private String mFilterWishlist;
        private String mFilterWatched;
        private String mFilterFavourite;
        private String mFilterCategory;
        private int mLimit;

        private Builder() {
            this.mLimit = 0;
        }

        @NonNull
        public ViewAwardQueryParameters.Builder sortOrder(@Nullable String sortOrder) {
            this.mSortOrder = sortOrder;
            return this;
        }
        @NonNull
        public ViewAwardQueryParameters.Builder filterGenre(@Nullable String filterGenre) {
            this.mFilterGenre = filterGenre;
            return this;
        }
        @NonNull
        public ViewAwardQueryParameters.Builder filterWishlist(@Nullable String filterWishlist) {
            this.mFilterWishlist = filterWishlist;
            return this;
        }
        @NonNull
        public ViewAwardQueryParameters.Builder filterWatched(@Nullable String filterWatched) {
            this.mFilterWatched = filterWatched;
            return this;
        }
        @NonNull
        public ViewAwardQueryParameters.Builder filterFavourite(@Nullable String filterFavourite) {
            this.mFilterFavourite = filterFavourite;
            return this;
        }
        @NonNull
        public ViewAwardQueryParameters.Builder filterCategory(@Nullable String filterCategory) {
            this.mFilterCategory = filterCategory;
            return this;
        }
        @NonNull
        public ViewAwardQueryParameters.Builder limit(int limit) {
            this.mLimit = limit;
            return this;
        }

        /**
         * Builds and returns a ViewAwardQueryParameters object.
         * @return a ViewAwardQueryParameters object
         */
        @NonNull
        public ViewAwardQueryParameters build() {
            String missing = "";
            if (mSortOrder == null) {
                missing += " sortOrder";
            }
            if (mFilterGenre == null) {
                missing += " filterGenre";
            }
            if (mFilterWishlist == null) {
                missing += " filterWishlist";
            }
            if (mFilterWatched == null) {
                missing += " filterWatched";
            }
            if (mFilterFavourite == null) {
                missing += " filterFavourite";
            }
            if (mFilterCategory == null) {
                missing += " filterCategory";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new ViewAwardQueryParameters(
                    this.mSortOrder,
                    this.mFilterGenre,
                    this.mFilterWishlist,
                    this.mFilterWatched,
                    this.mFilterFavourite,
                    this.mFilterCategory,
                    this.mLimit);
        }
    }

}
