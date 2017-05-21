package uk.jumpingmouse.moviecompanion.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * The Award model class.
 * An Award is uniquely identified by the combination of awardDate, category and imdbId.
 * An example of an award is "Movie of the Week" for 12/05/17.
 * Note that there could be more than one Movie of the Week for a given week.
 * The Movie-Award relationship is one-many.
 * @author Edmund Johnson
 */
public class Award {
    static final String CATEGORY_MOVIE = "M";
    public static final String CATEGORY_DVD = "D";

    // awardDate format is "YYMMDD"
    private String awardDate;
    // categoryId is one of CATEGORY_MOVIE, CATEGORY_DVD
    private String category;
    // imdbId is a "foreign key" to Movie
    private String imdbId;
    // free text
    private String review;

    private Award() {
    }

    private Award(
            @Nullable String awardDate,
            @Nullable String category,
            @Nullable String imdbId,
            @Nullable String review) {
        if (awardDate == null) {
            throw new NullPointerException("Null awardDate");
        }
        if (category == null) {
            throw new NullPointerException("Null category");
        }
        if (imdbId == null) {
            throw new NullPointerException("Null imdbId");
        }
        if (review == null) {
            throw new NullPointerException("Null review");
        }
        this.awardDate = awardDate;
        this.category = category;
        this.imdbId = imdbId;
        this.review = review;
    }

    //---------------------------------------------------------------
    // Getters

    @NonNull
    public String getAwardDate() {
        return awardDate;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    @NonNull
    public String getImdbId() {
        return imdbId;
    }

    @NonNull
    public String getReview() {
        return review;
    }

    //---------------------------------------------------------------
    // Builders

    /**
     * Builder for this class.  Usage:
     * <blockquote><pre>
     * {@code
     *   Award award = Award.builder()
     *         .awardDate("170512")
     *         .category(Award.CATEGORY_MOVIE)
     *         .imdbId("tt4016934")
     *         .review("A great movie")
     *        .build();
     * }
     * </pre></blockquote>
     * @return an instance of the Award class.
     */
    public static Builder builder() {
        return new Award.Builder();
    }

    @SuppressWarnings("WeakerAccess")
    public static final class Builder {
        private String awardDate;
        private String category;
        private String imdbId;
        private String review;

        Builder() {
        }

        Builder(Award source) {
            this.awardDate = source.awardDate;
            this.category = source.category;
            this.imdbId = source.imdbId;
            this.review = source.review;
        }

        public Award.Builder awardDate(@NonNull String awardDate) {
            this.awardDate = awardDate;
            return this;
        }

        public Award.Builder category(@NonNull String category) {
            this.category = category;
            return this;
        }

        public Award.Builder imdbId(@NonNull String imdbId) {
            this.imdbId = imdbId;
            return this;
        }

        public Award.Builder review(@NonNull String review) {
            this.review = review;
            return this;
        }
        /** Builds and returns an object of this class. */
        public Award build() {
            String missing = "";
            if (awardDate == null) {
                missing += " awardDate";
            }
            if (category == null) {
                missing += " category";
            }
            if (imdbId == null) {
                missing += " imdbId";
            }
            if (review == null) {
                missing += " review";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new Award(
                    this.awardDate,
                    this.category,
                    this.imdbId,
                    this.review);
        }
    }

    //---------------------------------------------------------------
    // Override object methods

    @Override
    public String toString() {
        return "Movie{"
                + "awardDate=" + awardDate + ", "
                + "category=" + category + ", "
                + "imdbId=" + imdbId + ", "
                + "review=" + review
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Award) {
            Award that = (Award) o;
            return (this.awardDate.equals(that.awardDate))
                    && (this.category.equals(that.category))
                    && (this.imdbId.equals(that.imdbId))
                    && (this.review.equals(that.review));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.awardDate.hashCode();
        h *= 1000003;
        h ^= this.category.hashCode();
        h *= 1000003;
        h ^= this.imdbId.hashCode();
        h *= 1000003;
        h ^= this.review.hashCode();
        return h;
    }

}
