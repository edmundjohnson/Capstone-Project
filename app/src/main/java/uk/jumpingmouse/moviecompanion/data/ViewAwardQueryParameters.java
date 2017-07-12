package uk.jumpingmouse.moviecompanion.data;

import android.support.annotation.NonNull;

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
                                     @NonNull String filterWatched, @NonNull String filterFavourite) {
        this.sortOrder = sortOrder;
        this.filterCategory = filterCategory;
        this.filterGenre = filterGenre;
        this.filterWishlist = filterWishlist;
        this.filterWatched = filterWatched;
        this.filterFavourite = filterFavourite;
    }

    // parameters
    private String sortOrder;
    private String filterCategory;
    private String filterGenre;
    private String filterWishlist;
    private String filterWatched;
    private String filterFavourite;

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

        Builder() {
        }

        Builder(ViewAwardQueryParameters source) {
            this.sortOrder = source.sortOrder;
            this.filterCategory = source.filterCategory;
            this.filterGenre = source.filterGenre;
            this.filterWishlist = source.filterWishlist;
            this.filterWatched = source.filterWatched;
            this.filterFavourite = source.filterFavourite;
        }

        public ViewAwardQueryParameters.Builder sortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }
        public ViewAwardQueryParameters.Builder filterCategory(String filterCategory) {
            this.filterCategory = filterCategory;
            return this;
        }
        public ViewAwardQueryParameters.Builder filterGenre(String filterGenre) {
            this.filterGenre = filterGenre;
            return this;
        }
        public ViewAwardQueryParameters.Builder filterWishlist(String filterWishlist) {
            this.filterWishlist = filterWishlist;
            return this;
        }
        public ViewAwardQueryParameters.Builder filterWatched(String filterWatched) {
            this.filterWatched = filterWatched;
            return this;
        }
        public ViewAwardQueryParameters.Builder filterFavourite(String filterFavourite) {
            this.filterFavourite = filterFavourite;
            return this;
        }

        /** Builds and returns an object of this class. */
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
                    this.filterFavourite);
        }
    }

}
