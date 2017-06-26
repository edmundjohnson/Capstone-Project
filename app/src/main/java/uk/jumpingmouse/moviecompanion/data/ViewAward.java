package uk.jumpingmouse.moviecompanion.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Comparator;

/**
 * The ViewAward model class.
 * This class represents an entry in the displayed award list.
 * It contains fields from the Award and from the Movie which received the Award.
 * @author Edmund Johnson
 */
public class ViewAward {

    // the unique identifier for the award, a push id
    private String id;
    // the unique identifier for the movie, e.g. 4016934
    private int movieId;
    // The IMDb identifier for the movie, e.g. "tt4016934"
    private String imdbId;
    // awardDate is formatted as "YYMMDD"
    private String awardDate;
    // categoryId is one of CATEGORY_MOVIE, CATEGORY_DVD
    private String category;
    // the review for the award (free text)
    private String review;
    // displayOrder determines the displayed order of awards for a movie
    private int displayOrder;
    // The movie title, e.g. "The Handmaiden"
    private String title;
    // The length in minutes
    private int runtime;
    // A comma-separated list of genres, e.g. "Drama, Mystery, Romance"
    private String genre;
    // The URL of the movie poster image
    private String poster;

    private ViewAward() {
    }


    private ViewAward(
            @NonNull String id,
            int movieId,
            @NonNull String imdbId,
            @NonNull String awardDate,
            @NonNull String category,
            @NonNull String review,
            int displayOrder,
            @NonNull String title,
            int runtime,
            @Nullable String genre,
            @Nullable String poster) {
        if (movieId <= 0) {
            throw new NullPointerException("Invalid movieId");
        }
        this.id = id;
        this.movieId = movieId;
        this.imdbId = imdbId;
        this.awardDate = awardDate;
        this.category = category;
        this.review = review;
        this.displayOrder = displayOrder;
        this.title = title;
        this.runtime = runtime;
        this.genre = genre;
        this.poster = poster;
    }

    public ViewAward(@NonNull Award award, @NonNull Movie movie) {
        this.id = award.getId();
        this.movieId = movie.getId();
        this.imdbId = movie.getImdbId();
        this.awardDate = award.getAwardDate();
        this.category = award.getCategory();
        this.review = award.getReview();
        this.displayOrder = award.getDisplayOrder();
        this.title = movie.getTitle();
        this.runtime = movie.getRuntime();
        this.genre = movie.getGenre();
        this.poster = movie.getPoster();
    }

    //---------------------------------------------------------------
    // Getters

    @NonNull
    private String getId() {
        return id;
    }

    private int getMovieId() {
        return movieId;
    }

    @NonNull
    public String getImdbId() {
        return imdbId;
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

    private int getDisplayOrder() {
        return displayOrder;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    /** Returns the runtime in minutes, e.g. 144. */
    public int getRuntime() {
        return runtime;
    }

    /** Returns a comma-separated list of genres, e.g. "Drama, Mystery, Romance". */
    @Nullable
    public String getGenre() {
        return genre;
    }

    @Nullable
    public String getPoster() {
        return poster;
    }

    //---------------------------------------------------------------
    // Utilities

    /**
     * Returns a view award as an object array, one element per field value.
     * @return the view award as an Object array
     */
    public Object[] toObjectArray() {
        return new Object[] {
                // This must match the order of columns in DataContract.ViewAwardEntry.getAllColumns().
                id,
                movieId,
                imdbId,
                awardDate,
                category,
                review,
                displayOrder,
                title,
                runtime,
                genre,
                poster
        };
    }

    //---------------------------------------------------------------
    // Builder implementation

    /**
     * Builder for this class.  Usage:
     * <blockquote><pre>
     * {@code
     *   ViewAward award = ViewAward.builder()
     *         .id(id)
     *         .movieId(4016934)
     *         .imdbId("tt4016934")
     *         .awardDate("170512")
     *         [etc]
     *        .build();
     * }
     * </pre></blockquote>
     * @return an instance of the ViewAward class.
     */
    public static ViewAward.Builder builder() {
        return new ViewAward.Builder();
    }

    @SuppressWarnings("WeakerAccess")
    public static final class Builder {
        private String id;
        private int movieId;
        private String imdbId;
        private String awardDate;
        private String category;
        private String review;
        private int displayOrder;
        private String title;
        private int runtime;
        private String genre;
        private String poster;

        Builder() {
        }

        Builder(ViewAward source) {
            this.id = source.id;
            this.movieId = source.movieId;
            this.imdbId = source.imdbId;
            this.awardDate = source.awardDate;
            this.category = source.category;
            this.review = source.review;
            this.displayOrder = source.displayOrder;
            this.title = source.title;
            this.runtime = source.runtime;
            this.genre = source.genre;
            this.poster = source.poster;
        }

        public ViewAward.Builder id(@NonNull String id) {
            this.id = id;
            return this;
        }

        public ViewAward.Builder movieId(int movieId) {
            this.movieId = movieId;
            return this;
        }

        public ViewAward.Builder imdbId(@NonNull String imdbId) {
            this.imdbId = imdbId;
            return this;
        }

        public ViewAward.Builder awardDate(@NonNull String awardDate) {
            this.awardDate = awardDate;
            return this;
        }

        public ViewAward.Builder category(@NonNull String category) {
            this.category = category;
            return this;
        }

        public ViewAward.Builder review(@NonNull String review) {
            this.review = review;
            return this;
        }

        public ViewAward.Builder displayOrder(int displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }

        public ViewAward.Builder title(@NonNull String title) {
            this.title = title;
            return this;
        }

        public ViewAward.Builder runtime(int runtime) {
            this.runtime = runtime;
            return this;
        }

        public ViewAward.Builder genre(@NonNull String genre) {
            this.genre = genre;
            return this;
        }

        public ViewAward.Builder poster(@NonNull String poster) {
            this.poster = poster;
            return this;
        }

        /** Builds and returns an object of this class. */
        public ViewAward build() {
            String missing = "";
            if (id == null) {
                missing += " id";
            }
            if (movieId <= 0) {
                missing += " movieId";
            }
            if (imdbId == null) {
                missing += " imdbId";
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
            if (title == null) {
                missing += " title";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new ViewAward(
                    this.id,
                    this.movieId,
                    this.imdbId,
                    this.awardDate,
                    this.category,
                    this.review,
                    this.displayOrder,
                    this.title,
                    this.runtime,
                    this.genre,
                    this.poster);
        }
    }

    //---------------------------------------------------------------
    // Override object methods

    @Override
    public String toString() {
        return "ViewAward{"
                + "id=" + id
                + ", movieId=" + movieId
                + ", imdbId=" + imdbId
                + ", awardDate=" + awardDate
                + ", category=" + category
                + ", review=" + review
                + ", displayOrder=" + displayOrder
                + ", title=" + title
                + ", runtime=" + runtime
                + ", genre=" + genre
                + ", poster=" + poster
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ViewAward) {
            ViewAward that = (ViewAward) o;
            return (this.id.equals(that.id))
                    && (this.movieId == that.movieId)
                    && (this.imdbId.equals(that.imdbId))
                    && (this.awardDate.equals(that.awardDate))
                    && (this.category.equals(that.category))
                    && (this.review.equals(that.review))
                    && (this.displayOrder == that.displayOrder)
                    && (this.title.equals(that.title))
                    && (this.runtime == that.runtime)
                    && ((this.genre == null) ? (that.genre == null) : this.genre.equals(that.genre))
                    && ((this.poster == null) ? (that.poster == null) : this.poster.equals(that.poster));
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
        h ^= this.imdbId.hashCode();
        h *= 1000003;
        h ^= this.awardDate.hashCode();
        h *= 1000003;
        h ^= this.category.hashCode();
        h *= 1000003;
        h ^= this.review.hashCode();
        h *= 1000003;
        h ^= this.displayOrder;
        h *= 1000003;
        h ^= this.title.hashCode();
        h *= 1000003;
        h ^= this.runtime;
        h *= 1000003;
        h ^= (genre == null) ? 0 : this.genre.hashCode();
        h *= 1000003;
        h ^= (poster == null) ? 0 : this.poster.hashCode();
        return h;
    }

    //---------------------------------------------------------------
    // Comparators

    /** Comparator for ordering by movie id. */
    public static final Comparator<ViewAward> ViewAwardComparatorMovieId
            = new Comparator<ViewAward>() {
        public int compare(ViewAward viewAward1, ViewAward viewAward2) {
            // ascending order
            if (viewAward1.movieId == viewAward2.movieId) {
                // movieId, then awardDate, then reverse category ("M" > "D")
                if (viewAward1.awardDate.equals(viewAward2.awardDate)) {
                    return viewAward2.category.compareTo(viewAward1.category);
                }
                return viewAward1.awardDate.compareTo(viewAward2.awardDate);
            }
            return viewAward1.movieId - viewAward2.movieId;
        }
    };

    /** Comparator for ordering by award date. */
    public static final Comparator<ViewAward> ViewAwardComparatorAwardDate
            = new Comparator<ViewAward>() {
        public int compare(ViewAward viewAward1, ViewAward viewAward2) {
            // ascending order
            if (viewAward1.awardDate.equals(viewAward2.awardDate)) {
                // awardDate ascending, then category ("D" before "M")
                // This unintuitive ordering of category is so that when the comparator is
                // reversed, as it is by default, Movie comes before DVD
                return viewAward1.category.compareTo(viewAward2.category);
            }
            return viewAward1.awardDate.compareTo(viewAward2.awardDate);
        }
    };

}
