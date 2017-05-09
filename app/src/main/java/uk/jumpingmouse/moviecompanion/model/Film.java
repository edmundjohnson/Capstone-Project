package uk.jumpingmouse.moviecompanion.model;

import com.google.auto.value.AutoValue;

import android.support.annotation.Nullable;

/**
 * The Film model class.
 * The imdbId is entered by an admin user.
 * The remainder of the Film information is obtained from the Open Movie Database.
 * @author Edmund Johnson
 */
@AutoValue
public abstract class Film {
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

    // The year of the film's release (not the year of this award), e.g. "2017"
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
        abstract Film build();
    }

    /**
     * Builder for this class.  Usage:
     * <blockquote><pre>
     * {@code
     *   Film film = Film.builder()
     *         .imdbId("tt4016934")
     *         .title("The Handmaiden")
     *         // etc
     *        .build();
     * }
     * </pre></blockquote>
     * @return an instance of the Film class.
     */
    public static Builder builder() {
        return new AutoValue_Film.Builder();
    }

}
