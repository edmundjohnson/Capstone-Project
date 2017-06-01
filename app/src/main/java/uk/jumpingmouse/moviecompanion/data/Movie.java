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

    // The unique identifier of the movie. For now, this has the same value as imdbId.
    private String id;
    // e.g. "tt4016934"
    private String imdbId;
    // e.g. "The Handmaiden"
    private String title;
    // The year of the movie's release (not the year of this award), e.g. "2017"
    private String year;
    // The release date, as a millisecond value
    private long released;
    // length in minutes
    private int runtime;
    // a comma-separated list of genres, e.g. "Drama, Mystery, Romance"
    private String genre;
    // We may not have a poster URL, the view must take this into account
    private String poster;

    private Movie() {
    }

    private Movie(
            @Nullable String id,
            @Nullable String imdbId,
            @Nullable String title,
            @Nullable String year,
            long released,
            int runtime,
            @Nullable String genre,
            @Nullable String poster) {
        if (id == null) {
            throw new NullPointerException("Null id");
        }
        if (imdbId == null) {
            throw new NullPointerException("Null imdbId");
        }
        if (title == null) {
            throw new NullPointerException("Null title");
        }
        this.id = id;
        this.imdbId = imdbId;
        this.title = title;
        this.year = year;
        this.released = released;
        this.runtime = runtime;
        this.genre = genre;
        this.poster = poster;
    }

    //---------------------------------------------------------------
    // Getters

    /** Returns the unique id. */
    @NonNull
    public String getId() {
        return id;
    }

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

    /** Returns the year of the movie's release (not the year of this award), e.g. "2017". */
    @Nullable
    public String getYear() {
        return year;
    }

    /** Returns the released date, as a millisecond value. */
    public long getReleased() {
        return released;
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

    /** Returns the poster URL. */
    @Nullable
    public String getPoster() {
        return poster;
    }

    //---------------------------------------------------------------
    // Builders

    /**
     * Builder for this class.  Usage:
     * <blockquote><pre>
     * {@code
     *   Movie movie = Movie.builder()
     *         .id("tt4016934")
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
        private String id;
        private String imdbId;
        private String title;
        private String year;
        private Long released;
        private Integer runtime;
        private String genre;
        private String poster;

        Builder() {
        }

        Builder(Movie source) {
            this.id = source.id;
            this.imdbId = source.imdbId;
            this.title = source.title;
            this.year = source.year;
            this.released = source.released;
            this.runtime = source.runtime;
            this.genre = source.genre;
            this.poster = source.poster;
        }

        public Movie.Builder id(@NonNull String id) {
            this.id = id;
            return this;
        }
        public Movie.Builder imdbId(@NonNull String imdbId) {
            this.imdbId = imdbId;
            return this;
        }
        public Movie.Builder title(@NonNull String title) {
            this.title = title;
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
        public Movie.Builder runtime(int runtime) {
            this.runtime = runtime;
            return this;
        }
        public Movie.Builder genre(@Nullable String genre) {
            this.genre = genre;
            return this;
        }
        public Movie.Builder poster(@Nullable String poster) {
            this.poster = poster;
            return this;
        }
        /** Builds and returns an object of this class. */
        public Movie build() {
            String missing = "";
            if (id == null) {
                missing += " id";
            }
            if (imdbId == null) {
                missing += " imdbId";
            }
            if (title == null) {
                missing += " title";
            }
            if (released == null) {
                missing += " released";
            }
            if (runtime == null) {
                missing += " runtime";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new Movie(
                    this.id,
                    this.imdbId,
                    this.title,
                    this.year,
                    this.released,
                    this.runtime,
                    this.genre,
                    this.poster);
        }
    }

    //---------------------------------------------------------------
    // Override object methods

    @Override
    public String toString() {
        return "Movie{"
                + "id=" + id
                + ", imdbId=" + imdbId
                + ", title=" + title
                + ", year=" + year
                + ", released=" + released
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
        if (o instanceof Movie) {
            Movie that = (Movie) o;
            return (this.id.equals(that.id))
                    && (this.imdbId.equals(that.imdbId))
                    && (this.title.equals(that.title))
                    && ((this.year == null) ? (that.year == null) : this.year.equals(that.year))
                    && (this.released == that.released)
                    && (this.runtime == that.runtime)
                    && ((this.genre == null) ? (that.genre == null) : this.genre.equals(that.genre))
                    && ((this.poster == null) ? (that.poster == null) :
                            this.poster.equals(that.poster));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.id.hashCode();
        h *= 1000003;
        h ^= this.imdbId.hashCode();
        h *= 1000003;
        h ^= this.title.hashCode();
        h *= 1000003;
        h ^= (year == null) ? 0 : this.year.hashCode();
        h *= 1000003;
        h ^= (this.released >>> 32) ^ this.released;
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
