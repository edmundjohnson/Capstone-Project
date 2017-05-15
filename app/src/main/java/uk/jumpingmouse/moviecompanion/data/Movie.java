package uk.jumpingmouse.moviecompanion.data;

import com.google.auto.value.AutoValue;

import android.support.annotation.Nullable;

import java.util.Comparator;

/**
 * The Movie model class.
 * The imdbId is entered by an admin user.
 * The remainder of the Movie information is obtained from the Open Movie Database.
 * @author Edmund Johnson
 */
@AutoValue
public abstract class Movie {
    public static final int RUNTIME_UNKNOWN = -1;
    public static final int RELEASED_UNKNOWN = -1;

    // e.g. "tt4016934"
    public abstract String imdbId();

    // e.g. "The Handmaiden"
    public abstract String title();

    // a comma-separated list of genres, e.g. "Drama, Mystery, Romance"
    @Nullable
    public abstract String genre();

    // length in minutes
    public abstract int runtime();

    // We may not have a poster URL, the view must take this into account
    @Nullable
    public abstract String posterUrl();

    // The year of the movie's release (not the year of this award), e.g. "2017"
    @Nullable
    public abstract String year();

    // The release date, as a millisecond value
    public abstract long released();

    /**
     * Builder class for Movie, must be public.
     */
    @SuppressWarnings("WeakerAccess")
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder imdbId(String value);
        public abstract Builder title(String value);
        public abstract Builder genre(String value);
        public abstract Builder runtime(int value);
        public abstract Builder posterUrl(String value);
        public abstract Builder year(String value);
        public abstract Builder released(long value);
        public abstract Movie build();
    }

    /**
     * Builder for this class.  Usage:
     * <blockquote><pre>
     * {@code
     *   Movie movie = Movie.builder()
     *         .imdbId("tt4016934")
     *         .title("The Handmaiden")
     *         // etc
     *        .build();
     * }
     * </pre></blockquote>
     * @return an instance of the Movie class.
     */
    public static Builder builder() {
        return new AutoValue_Movie.Builder();
    }

    /** Comparator for ordering by imdbId. */
    public static final Comparator<Movie> MovieComparatorImdbId = new Comparator<Movie>() {
        public int compare(Movie movie1, Movie movie2) {
            // ascending order
            return movie1.imdbId().compareTo(movie2.imdbId());
            // descending order
            //return movie2.imdbId().compareTo(movie1.imdbId());
        }
    };

    /** Comparator for ordering by title. */
    public static final Comparator<Movie> MovieComparatorTitle = new Comparator<Movie>() {
        public int compare(Movie movie1, Movie movie2) {
            // ascending order
            return movie1.title().compareTo(movie2.title());
            // descending order
            //return movie2.title().compareTo(movie1.title());
        }
    };

}
