package uk.jumpingmouse.moviecompanion.model;

import com.google.auto.value.AutoValue;

import android.support.annotation.Nullable;

/**
 * The Movie model class.
 * The imdbId is entered by an admin user.
 * The remainder of the Movie information is obtained from the Open Movie Database.
 * @author Edmund Johnson
 */
@AutoValue
public abstract class Movie {
    public static final int RUNTIME_UNKNOWN = -1;

    // e.g. "tt4016934"
    abstract String imdbId();

    // e.g. "The Handmaiden"
    abstract String title();

    // a comma-separated list of genres, e.g. "Drama, Mystery, Romance"
    @Nullable
    abstract String genre();

    // length in minutes
    abstract int runtime();

    // We may not have a poster URL, the view must take this into account
    @Nullable
    abstract String posterUrl();

    // The year of the movie's release (not the year of this award), e.g. "2017"
    @Nullable
    abstract String year();

    @AutoValue.Builder
    abstract static class Builder {
        abstract Builder imdbId(String value);
        abstract Builder title(String value);
        abstract Builder genre(String value);
        abstract Builder runtime(int value);
        abstract Builder posterUrl(String value);
        abstract Builder year(String value);
        abstract Movie build();
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

}
