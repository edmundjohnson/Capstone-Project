package uk.jumpingmouse.moviecompanion.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Comparator;

/**
 * The Movie model class.
 * The imdbId is entered by an admin user.
 * The remainder of the Movie information is obtained from the Open Movie Database.
 * @author Edmund Johnson
 */
public class Movie {
    public static final int RUNTIME_UNKNOWN = -1;
    public static final int RELEASED_UNKNOWN = -1;

    // e.g. "tt4016934"
    private String imdbId;
    // e.g. "The Handmaiden"
    private String title;
    // a comma-separated list of genres, e.g. "Drama, Mystery, Romance"
    private String genre;
    // length in minutes
    private int runtime;
    // We may not have a poster URL, the view must take this into account
    private String posterUrl;
    // The year of the movie's release (not the year of this award), e.g. "2017"
    private String year;
    // The release date, as a millisecond value
    private long released;

    private Movie() {
    }

    private Movie(
            @Nullable String imdbId,
            @Nullable String title,
            @Nullable String genre,
            int runtime,
            @Nullable String posterUrl,
            @Nullable String year,
            long released) {
        if (imdbId == null) {
            throw new NullPointerException("Null imdbId");
        }
        this.imdbId = imdbId;
        if (title == null) {
            throw new NullPointerException("Null title");
        }
        this.title = title;
        this.genre = genre;
        this.runtime = runtime;
        this.posterUrl = posterUrl;
        this.year = year;
        this.released = released;
    }

    //---------------------------------------------------------------
    // Getters

    /** Returns the IMDb id, e.g. "tt4016934". */
    @NonNull
    public String getImdbId() {
        return imdbId;
    }

    /** Returns the title, e.g. "The Handmaiden". */
    @NonNull
    public String getTitle() {
        return title;
    }

    /** Returns a comma-separated list of genres, e.g. "Drama, Mystery, Romance". */
    @Nullable
    public String getGenre() {
        return genre;
    }

    /** Returns the runtime in minutes, e.g. 144. */
    public int getRuntime() {
        return runtime;
    }

    /** Returns the poster URL. */
    @Nullable
    public String getPosterUrl() {
        return posterUrl;
    }

    /** Returns the year of the movie's release (not the year of this award), e.g. "2017". */
    @Nullable
    public String getYear() {
        return year;
    }

    /** Returns the released date, as a millisecond value. */
    public long getReleased() {
        return released;
    }

    //---------------------------------------------------------------
    // Builders

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
        return new Movie.Builder();
    }

    @SuppressWarnings("WeakerAccess")
    public static final class Builder {
        private String imdbId;
        private String title;
        private String genre;
        private Integer runtime;
        private String posterUrl;
        private String year;
        private Long released;

        Builder() {
        }

        Builder(Movie source) {
            this.imdbId = source.imdbId;
            this.title = source.title;
            this.genre = source.genre;
            this.runtime = source.runtime;
            this.posterUrl = source.posterUrl;
            this.year = source.year;
            this.released = source.released;
        }

        public Movie.Builder imdbId(String imdbId) {
            this.imdbId = imdbId;
            return this;
        }
        public Movie.Builder title(String title) {
            this.title = title;
            return this;
        }
        public Movie.Builder genre(@Nullable String genre) {
            this.genre = genre;
            return this;
        }
        public Movie.Builder runtime(int runtime) {
            this.runtime = runtime;
            return this;
        }
        public Movie.Builder posterUrl(@Nullable String posterUrl) {
            this.posterUrl = posterUrl;
            return this;
        }
        public Movie.Builder year(@Nullable String year) {
            this.year = year;
            return this;
        }
        public Movie.Builder released(long released) {
            this.released = released;
            return this;
        }
        /** Builds and returns an object of this class. */
        public Movie build() {
            String missing = "";
            if (imdbId == null) {
                missing += " imdbId";
            }
            if (title == null) {
                missing += " title";
            }
            if (runtime == null) {
                missing += " runtime";
            }
            if (released == null) {
                missing += " released";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new Movie(
                    this.imdbId,
                    this.title,
                    this.genre,
                    this.runtime,
                    this.posterUrl,
                    this.year,
                    this.released);
        }
    }

    //---------------------------------------------------------------
    // Override object methods

    @Override
    public String toString() {
        return "Movie{"
                + "imdbId=" + imdbId + ", "
                + "title=" + title + ", "
                + "genre=" + genre + ", "
                + "runtime=" + runtime + ", "
                + "posterUrl=" + posterUrl + ", "
                + "year=" + year + ", "
                + "released=" + released
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Movie) {
            Movie that = (Movie) o;
            return (this.imdbId.equals(that.imdbId))
                    && (this.title.equals(that.title))
                    && ((this.genre == null) ? (that.genre == null) : this.genre.equals(that.genre))
                    && (this.runtime == that.runtime)
                    && ((this.posterUrl == null) ? (that.posterUrl == null) :
                    this.posterUrl.equals(that.posterUrl))
                    && ((this.year == null) ? (that.year == null) : this.year.equals(that.year))
                    && (this.released == that.released);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.imdbId.hashCode();
        h *= 1000003;
        h ^= this.title.hashCode();
        h *= 1000003;
        h ^= (genre == null) ? 0 : this.genre.hashCode();
        h *= 1000003;
        h ^= this.runtime;
        h *= 1000003;
        h ^= (posterUrl == null) ? 0 : this.posterUrl.hashCode();
        h *= 1000003;
        h ^= (year == null) ? 0 : this.year.hashCode();
        h *= 1000003;
        h ^= (this.released >>> 32) ^ this.released;
        return h;
    }

    //---------------------------------------------------------------
    // Comparators

    /** Comparator for ordering by imdbId. */
    public static final Comparator<Movie> MovieComparatorImdbId
            = new Comparator<Movie>() {
                public int compare(Movie movie1, Movie movie2) {
                    // ascending order
                    return movie1.imdbId.compareTo(movie2.imdbId);
                    // descending order
                    //return movie2.imdbId.compareTo(movie1.imdbId);
                }
            };

    /** Comparator for ordering by title. */
    public static final Comparator<Movie> MovieComparatorTitle
            = new Comparator<Movie>() {
                public int compare(Movie movie1, Movie movie2) {
                    // ascending order
                    return movie1.title.compareTo(movie2.title);
                    // descending order
                    //return movie2.title.compareTo(movie1.title);
                }
            };

}
