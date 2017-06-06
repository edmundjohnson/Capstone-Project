package uk.jumpingmouse.moviecompanion.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * The Award model class.
 * An Award is uniquely identified by the combination of awardDate, category and id.
 * An example of an award is "Movie of the Week" for 12/05/17.
 * Note that there could be more than one Movie of the Week for a given week.
 * The Movie-Award relationship is one-many.
 * @author Edmund Johnson
 */
public class Award {
    static final String CATEGORY_MOVIE = "M";
    public static final String CATEGORY_DVD = "D";

    // id is a unique identifier for award
    private String id;
    // movieId is a "foreign key" to Movie
    private int movieId;
    // awardDate is formatted as "YYMMDD"
    private String awardDate;
    // categoryId is one of CATEGORY_MOVIE, CATEGORY_DVD
    private String category;
    // displayOrder determines the displayed order of awards for a movie
    private int displayOrder;
    // free text
    private String review;

    private Award() {
    }

    private Award(
            @Nullable String id,
            int movieId,
            @Nullable String awardDate,
            @Nullable String category,
            @Nullable String review,
            int displayOrder) {
        if (id == null) {
            throw new NullPointerException("Null id");
        }
        if (movieId <= 0) {
            throw new NullPointerException("Invalid movieId");
        }
        if (awardDate == null) {
            throw new NullPointerException("Null awardDate");
        }
        if (category == null) {
            throw new NullPointerException("Null category");
        }
        if (review == null) {
            throw new NullPointerException("Null review");
        }
        if (displayOrder <= 0) {
            throw new NullPointerException("Invalid displayOrder");
        }
        this.id = id;
        this.movieId = movieId;
        this.awardDate = awardDate;
        this.category = category;
        this.review = review;
        this.displayOrder = displayOrder;
    }

    //---------------------------------------------------------------
    // Getters

    @NonNull
    public String getId() {
        return id;
    }

    public int getMovieId() {
        return movieId;
    }

    @NonNull
    public String getAwardDate() {
        return awardDate;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    @NonNull
    public String getReview() {
        return review;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    //---------------------------------------------------------------
    // Builders

    /**
     * Builder for this class.  Usage:
     * <blockquote><pre>
     * {@code
     *   Award award = Award.builder()
     *         .id([push_id])
     *         .movieId(4016934)
     *         .awardDate("170512")
     *         .category(Award.CATEGORY_MOVIE)
     *         .review("A great movie")
     *         .displayOrder(2)
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
        private String id;
        private int movieId;
        private String awardDate;
        private String category;
        private String review;
        private int displayOrder;

        Builder() {
        }

        Builder(Award source) {
            this.id = source.id;
            this.movieId = source.movieId;
            this.awardDate = source.awardDate;
            this.category = source.category;
            this.review = source.review;
            this.displayOrder = source.displayOrder;
        }

        public Award.Builder id(@NonNull String id) {
            this.id = id;
            return this;
        }

        public Award.Builder movieId(int movieId) {
            this.movieId = movieId;
            return this;
        }

        public Award.Builder awardDate(@NonNull String awardDate) {
            this.awardDate = awardDate;
            return this;
        }

        public Award.Builder category(@NonNull String category) {
            this.category = category;
            return this;
        }

        public Award.Builder review(@NonNull String review) {
            this.review = review;
            return this;
        }

        public Award.Builder displayOrder(int displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }

        /** Builds and returns an object of this class. */
        public Award build() {
            String missing = "";
            if (id == null) {
                missing += " id";
            }
            if (movieId <= 0) {
                missing += " movieId";
            }
            if (awardDate == null) {
                missing += " awardDate";
            }
            if (category == null) {
                missing += " category";
            }
            if (review == null) {
                missing += " review";
            }
            if (displayOrder <= 0) {
                missing += " displayOrder";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new Award(
                    this.id,
                    this.movieId,
                    this.awardDate,
                    this.category,
                    this.review,
                    this.displayOrder);
        }
    }

    //---------------------------------------------------------------
    // Override object methods

    @Override
    public String toString() {
        return "Award{"
                + "id=" + id
                + ", movieId=" + movieId
                + ", awardDate=" + awardDate
                + ", category=" + category
                + ", review=" + review
                + ", displayOrder=" + displayOrder
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Award) {
            Award that = (Award) o;
            return (this.id.equals(that.id))
                    && (this.movieId == that.movieId)
                    && (this.awardDate.equals(that.awardDate))
                    && (this.category.equals(that.category))
                    && (this.review.equals(that.review))
                    && (this.displayOrder == that.displayOrder);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.id.hashCode();
        h *= 1000003;
        h ^= this.movieId;
        h *= 1000003;
        h ^= this.awardDate.hashCode();
        h *= 1000003;
        h ^= this.category.hashCode();
        h *= 1000003;
        h ^= this.review.hashCode();
        h *= 1000003;
        h ^= this.displayOrder;
        return h;
    }

}
