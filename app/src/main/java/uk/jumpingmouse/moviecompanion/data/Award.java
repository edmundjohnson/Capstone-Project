package uk.jumpingmouse.moviecompanion.data;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Comparator;

import uk.jumpingmouse.moviecompanion.model.DataContract;

/**
 * The Award model class.
 * An example of an award is "Movie of the Week" for 12/05/17.
 * Note that there could be more than one Movie of the Week for a given week.
 * The Movie-Award relationship is one-many.
 * @author Edmund Johnson
 */
public final class Award implements Parcelable {
    public static final String CATEGORY_MOVIE = "M";
    public static final String CATEGORY_DVD = "D";

    // id is a unique identifier for award
    private String id;
    // movieId is a "foreign key" to Movie
    private String movieId;
    // awardDate is formatted as "YYMMDD"
    private String awardDate;
    // categoryId is one of CATEGORY_MOVIE, CATEGORY_DVD
    private String category;
    // free text
    private String review;
    // displayOrder determines the displayed order of awards for a movie
    private int displayOrder;

    private Award() {
    }

    private Award(
            @Nullable String id,
            @Nullable String movieId,
            @Nullable String awardDate,
            @Nullable String category,
            @Nullable String review,
            int displayOrder) {
        if (id == null) {
            throw new NullPointerException("Null id");
        }
        if (movieId == null) {
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
    // These MUST all be public - if not, Firebase will fail to
    // load the Award from the database.

    @NonNull
    public String getId() {
        return id;
    }

    public String getMovieId() {
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

    @SuppressWarnings("WeakerAccess")
    public int getDisplayOrder() {
        return displayOrder;
    }

    //---------------------------------------------------------------
    // Utilities

    /**
     * Returns a set of ContentValues corresponding to an award.
     * @return the set of ContentValues corresponding to the award
     */
    @NonNull
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(DataContract.AwardEntry.COLUMN_ID, getId());
        values.put(DataContract.AwardEntry.COLUMN_MOVIE_ID, getMovieId());
        values.put(DataContract.AwardEntry.COLUMN_AWARD_DATE, getAwardDate());
        values.put(DataContract.AwardEntry.COLUMN_CATEGORY, getCategory());
        values.put(DataContract.AwardEntry.COLUMN_REVIEW, getReview());
        values.put(DataContract.AwardEntry.COLUMN_DISPLAY_ORDER, getDisplayOrder());

        return values;
    }

    /**
     * Returns an award as an object array, one element per field value.
     * @return the award as an Object array
     */
    public Object[] toObjectArray() {
        return new Object[] {
                // This must match the order of columns in DataContract.AwardEntry.getAllColumns().
                id,
                movieId,
                awardDate,
                category,
                review,
                displayOrder
        };
    }
    //---------------------------------------------------------------
    // Parcelable implementation

    /**
     * Private constructor to create an object of this class from a parcel.
     * @param in a Parcel containing the object
     */
    private Award(@NonNull final Parcel in) {
        id = in.readString();
        movieId = in.readString();
        awardDate = in.readString();
        category = in.readString();
        review = in.readString();
        displayOrder = in.readInt();
    }

    /**
     * Flatten this object into a Parcel.
     * @param dest The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(movieId);
        dest.writeString(awardDate);
        dest.writeString(category);
        dest.writeString(review);
        dest.writeInt(displayOrder);
    }

    /**
     * Parcel creator object.
     */
    public static final Parcelable.Creator<Award> CREATOR =
            new Parcelable.Creator<Award>() {
                @NonNull
                public Award createFromParcel(@NonNull final Parcel in) {
                    return new Award(in);
                }

                @NonNull
                public Award[] newArray(final int size) {
                    return new Award[size];
                }
            };

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshalled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshalled
     *     by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    //---------------------------------------------------------------
    // Builder implementation

    /**
     * Builder for this class.  Usage:
     * <blockquote><pre>
     * {@code
     *   Award award = Award.builder()
     *         .id([null or push_id])
     *         .movieId("4016934")
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
        private String movieId;
        private String awardDate;
        private String category;
        private String review;
        private int displayOrder;

        Builder() {
        }

        Builder(@NonNull Award source) {
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

        public Award.Builder movieId(String movieId) {
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

        /**
         * Builds and returns an Award object.
         * @return an Award object
         */
        public Award build() {
            String missing = "";
            if (id == null) {
                missing += " id";
            }
            if (movieId == null) {
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
    public boolean equals(@Nullable Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Award) {
            Award that = (Award) o;
            return (this.id.equals(that.id))
                    || ((this.movieId.equals(that.movieId))
                    && (this.awardDate.equals(that.awardDate))
                    && (this.category.equals(that.category)));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.id.hashCode();
        h *= 1000003;
        h ^= this.movieId.hashCode();
        h *= 1000003;
        h ^= this.awardDate.hashCode();
        h *= 1000003;
        h ^= this.category.hashCode();
        return h;
    }

    //---------------------------------------------------------------
    // Comparators

    /** Comparator for ordering by movie id. */
    public static final Comparator<Award> AWARD_COMPARATOR_MOVIE_ID
            = new Comparator<Award>() {
                public int compare(Award award1, Award award2) {
            // ascending order
                    if (award1.movieId.equals(award2.movieId)) {
                        // movieId, then awardDate, then reverse category ("M" > "D")
                        if (award1.awardDate.equals(award2.awardDate)) {
                            return award2.category.compareTo(award1.category);
                        }
                        return award1.awardDate.compareTo(award2.awardDate);
                    }
                    return award1.movieId.compareTo(award2.movieId);
                }
            };

    /** Comparator for ordering by award date. */
    public static final Comparator<Award> AWARD_COMPARATOR_AWARD_DATE
            = new Comparator<Award>() {
                public int compare(Award award1, Award award2) {
                    // ascending order
                    if (award1.awardDate.equals(award2.awardDate)) {
                        // awardDate, then reverse category ("M" > "D")
                        return award2.category.compareTo(award1.category);
                    }
                    return award1.awardDate.compareTo(award2.awardDate);
                }
            };

}
