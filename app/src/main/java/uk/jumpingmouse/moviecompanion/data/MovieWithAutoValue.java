package uk.jumpingmouse.moviecompanion.data;

import com.google.auto.value.AutoValue;
import com.google.firebase.database.DataSnapshot;

import android.support.annotation.Nullable;

import me.mattlogan.auto.value.firebase.annotation.FirebaseValue;

import java.util.Comparator;

/**
 * The Movie model class.
 * The imdbId is entered by an admin user.
 * The remainder of the Movie information is obtained from the Open Movie Database.
 * @author Edmund Johnson
 */
@AutoValue
@FirebaseValue
public abstract class MovieWithAutoValue {
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

    //---------------------------------------------------------
    // Firebase conversion methods

    public static MovieWithAutoValue create(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(AutoValue_MovieWithAutoValue.FirebaseValue.class).toAutoValue();
    }

    public Object toFirebaseValue() {
        return new AutoValue_MovieWithAutoValue.FirebaseValue(this);
    }

    //---------------------------------------------------------
    // Builder

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
        public abstract MovieWithAutoValue build();
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
        return new AutoValue_MovieWithAutoValue.Builder();
    }

    //---------------------------------------------------------
    // Comparators

    /** Comparator for ordering by imdbId. */
    public static final Comparator<MovieWithAutoValue> MovieComparatorImdbId
            = new Comparator<MovieWithAutoValue>() {
                public int compare(MovieWithAutoValue movie1, MovieWithAutoValue movie2) {
                    // ascending order
                    return movie1.imdbId().compareTo(movie2.imdbId());
                    // descending order
                    //return movie2.imdbId().compareTo(movie1.imdbId());
                }
            };

    /** Comparator for ordering by title. */
    public static final Comparator<MovieWithAutoValue> MovieComparatorTitle
            = new Comparator<MovieWithAutoValue>() {
                public int compare(MovieWithAutoValue movie1, MovieWithAutoValue movie2) {
                    // ascending order
                    return movie1.title().compareTo(movie2.title());
                    // descending order
                    //return movie2.title().compareTo(movie1.title());
                }
            };

}
