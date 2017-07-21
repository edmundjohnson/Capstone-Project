package uk.jumpingmouse.moviecompanion.data;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Comparator;

import uk.jumpingmouse.moviecompanion.model.DataContract;

/**
 * The Movie model class.
 * This is similar to the OmdbMovie class, which contains unprocessed data from the
 * Open Movie Database.  However, this class stores data in a more useful form,
 * e.g. runtime is stored as an int (e.g. 144), rather than a String (e.g. "144 min").
 * @author Edmund Johnson
 */
public final class Movie implements Parcelable {
    public static final int RUNTIME_UNKNOWN = -1;
    public static final int RELEASED_UNKNOWN = -1;

    // The unique identifier of the movie, e.g. "4016934".
    private String id;
    // The IMDb id, e.g. "tt4016934"
    private String imdbId;
    // The title, e.g. "The Handmaiden"
    private String title;
    // The year of the movie's release (not the year of this award), e.g. "2017"
    private String year;
    // The US rating, e.g. "R"
    private String rated;
    // The release date, as a millisecond value
    private long released;
    // The length in minutes
    private int runtime;
    // A comma-separated list of genres, e.g. "Drama, Mystery, Romance"
    private String genre;
    // The director, e.g. "Chan-wook Park"
    private String director;
    // The writer, e.g. "Sarah Waters"
    private String writer;
    // A comma-separated list of actors, e.g. "Min-hee Kim, Tae-ri Kim, Jung-woo Ha, Jin-woong Jo"
    private String actors;
    // The short-form plot, e.g. "A woman is hired as a handmaiden to a Japanese heiress, but
    // secretly she is involved in a plot to defraud her."
    private String plot;
    // A comma-separated list of languages, e.g. "Korean, Japanese"
    private String language;
    // The country, e.g. "South Korea"
    private String country;
    // The URL of the movie poster image
    private String poster;

    private Movie() {
    }

    private Movie(
            @Nullable String id,
            @Nullable String imdbId,
            @Nullable String title,
            @Nullable String year,
            @Nullable String rated,
            long released,
            int runtime,
            @Nullable String genre,
            @Nullable String director,
            @Nullable String writer,
            @Nullable String actors,
            @Nullable String plot,
            @Nullable String language,
            @Nullable String country,
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
        this.rated = rated;
        this.released = released;
        this.runtime = runtime;
        this.genre = genre;
        this.director = director;
        this.writer = writer;
        this.actors = actors;
        this.plot = plot;
        this.language = language;
        this.country = country;
        this.poster = poster;
    }

    //---------------------------------------------------------------
    // Getters
    // These MUST all be public - if not, Firebase will fail to
    // load the Movie from the database.

    /**
     * Returns the movie's unique id, e.g. "4016934".
     * @return the movie's unique id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the movie's IMDb id, e.g. "tt4016934".
     * @return the movie's IMDb id
     */
    @NonNull
    public String getImdbId() {
        return imdbId;
    }

    /**
     * Returns the movie's title, e.g. "The Handmaiden".
     * @return the movie's title
     */
    @NonNull
    public String getTitle() {
        return title;
    }

    /**
     * Returns the year of the movie's release (not the year of this award), e.g. "2017".
     * @return the year of the movie's release
     */
    @Nullable
    public String getYear() {
        return year;
    }

    /**
     * Returns the US rating, e.g. "R".
     * @return the US rating
     */
    @Nullable
    public String getRated() {
        return rated;
    }

    /**
     * Returns the released date as a millisecond value.
     * @return the released date as a millisecond value
     */
    public long getReleased() {
        return released;
    }

    /**
     * Returns the runtime in minutes, e.g. 144.
     * @return the runtime in minutes
     */
    public int getRuntime() {
        return runtime;
    }

    /**
     * Returns a comma-separated list of genres, e.g. "Drama, Mystery, Romance".
     * @return a comma-separated list of genres
     */
    @Nullable
    public String getGenre() {
        return genre;
    }

    @Nullable
    public String getDirector() {
        return director;
    }

    @Nullable
    public String getWriter() {
        return writer;
    }

    @Nullable
    public String getActors() {
        return actors;
    }

    @Nullable
    public String getPlot() {
        return plot;
    }

    @Nullable
    public String getLanguage() {
        return language;
    }

    @Nullable
    public String getCountry() {
        return country;
    }

    /**
     * Returns the poster URL.
     * @return the poster URL
     */
    @Nullable
    public String getPoster() {
        return poster;
    }

    //---------------------------------------------------------------
    // Parcelable implementation

    /**
     * Private constructor to create an object of this class from a parcel.
     * @param in a Parcel containing the object
     */
    private Movie(@NonNull final Parcel in) {
        id = in.readString();
        imdbId = in.readString();
        title = in.readString();
        year = in.readString();
        rated = in.readString();
        released = in.readLong();
        runtime = in.readInt();
        genre = in.readString();
        director = in.readString();
        writer = in.readString();
        actors = in.readString();
        plot = in.readString();
        language = in.readString();
        country = in.readString();
        poster = in.readString();
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
        dest.writeString(imdbId);
        dest.writeString(title);
        dest.writeString(year);
        dest.writeString(rated);
        dest.writeLong(released);
        dest.writeInt(runtime);
        dest.writeString(genre);
        dest.writeString(director);
        dest.writeString(writer);
        dest.writeString(actors);
        dest.writeString(plot);
        dest.writeString(language);
        dest.writeString(country);
        dest.writeString(poster);
    }

    /**
     * Parcel creator object.
     */
    public static final Parcelable.Creator<Movie> CREATOR =
            new Parcelable.Creator<Movie>() {
                @NonNull
                public Movie createFromParcel(@NonNull final Parcel in) {
                    return new Movie(in);
                }

                @NonNull
                public Movie[] newArray(final int size) {
                    return new Movie[size];
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
     *   Movie movie = Movie.builder()
     *         .id("4016934")
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
        private String rated;
        private Long released;
        private Integer runtime;
        private String genre;
        private String director;
        private String writer;
        private String actors;
        private String plot;
        private String language;
        private String country;
        private String poster;

        Builder() {
        }

        Builder(@NonNull Movie source) {
            this.id = source.id;
            this.imdbId = source.imdbId;
            this.title = source.title;
            this.year = source.year;
            this.rated = source.rated;
            this.released = source.released;
            this.runtime = source.runtime;
            this.genre = source.genre;
            this.director = source.director;
            this.writer = source.writer;
            this.actors = source.actors;
            this.plot = source.plot;
            this.language = source.language;
            this.country = source.country;
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
        public Movie.Builder rated(@Nullable String rated) {
            this.rated = rated;
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
        public Movie.Builder director(@Nullable String director) {
            this.director = director;
            return this;
        }
        public Movie.Builder writer(@Nullable String writer) {
            this.writer = writer;
            return this;
        }
        public Movie.Builder actors(@Nullable String actors) {
            this.actors = actors;
            return this;
        }
        public Movie.Builder plot(@Nullable String plot) {
            this.plot = plot;
            return this;
        }
        public Movie.Builder language(@Nullable String language) {
            this.language = language;
            return this;
        }
        public Movie.Builder country(@Nullable String country) {
            this.country = country;
            return this;
        }
        public Movie.Builder poster(@Nullable String poster) {
            this.poster = poster;
            return this;
        }
        /**
         * Builds and returns a Movie object.
         * @return a Movie object
         */
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
                    this.rated,
                    this.released,
                    this.runtime,
                    this.genre,
                    this.director,
                    this.writer,
                    this.actors,
                    this.plot,
                    this.language,
                    this.country,
                    this.poster);
        }
    }

    //---------------------------------------------------------------
    // Utilities

    /**
     * Returns a set of ContentValues corresponding to the movie.
     * @return the set of ContentValues corresponding to the movie
     */
    @NonNull
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(DataContract.MovieEntry.COLUMN_ID, getId());
        values.put(DataContract.MovieEntry.COLUMN_IMDB_ID, getImdbId());
        values.put(DataContract.MovieEntry.COLUMN_TITLE, getTitle());
        values.put(DataContract.MovieEntry.COLUMN_YEAR, getYear());
        values.put(DataContract.MovieEntry.COLUMN_RATED, getRated());
        values.put(DataContract.MovieEntry.COLUMN_RELEASED, getReleased());
        values.put(DataContract.MovieEntry.COLUMN_RUNTIME, getRuntime());
        values.put(DataContract.MovieEntry.COLUMN_GENRE, getGenre());
        values.put(DataContract.MovieEntry.COLUMN_DIRECTOR, getDirector());
        values.put(DataContract.MovieEntry.COLUMN_WRITER, getWriter());
        values.put(DataContract.MovieEntry.COLUMN_ACTORS, getActors());
        values.put(DataContract.MovieEntry.COLUMN_PLOT, getPlot());
        values.put(DataContract.MovieEntry.COLUMN_LANGUAGE, getLanguage());
        values.put(DataContract.MovieEntry.COLUMN_COUNTRY, getCountry());
        values.put(DataContract.MovieEntry.COLUMN_POSTER, getPoster());

        return values;
    }

    /**
     * Returns the movie as an object array, one element per field value.
     * @return the movie as an Object array
     */
    public Object[] toObjectArray() {
        return new Object[] {
                // This must match the order of columns in DataContract.MovieEntry.getAllColumns().
                id,
                imdbId,
                title,
                year,
                rated,
                released,
                runtime,
                genre,
                director,
                writer,
                actors,
                plot,
                language,
                country,
                poster
        };
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
                + ", rated=" + rated
                + ", released=" + released
                + ", runtime=" + runtime
                + ", genre=" + genre
                + ", director=" + director
                + ", writer=" + writer
                + ", actors=" + actors
                + ", plot=" + plot
                + ", language=" + language
                + ", country=" + country
                + ", poster=" + poster
                + "}";
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Movie) {
            Movie that = (Movie) o;
            return (this.id.equals(that.id))
                    && (this.imdbId.equals(that.imdbId))
                    && (this.title.equals(that.title))
                    && ((this.year == null) ? (that.year == null) : this.year.equals(that.year))
                    && ((this.rated == null) ? (that.rated == null) : this.rated.equals(that.rated))
                    && (this.released == that.released)
                    && (this.runtime == that.runtime)
                    && ((this.genre == null) ? (that.genre == null) : this.genre.equals(that.genre))
                    && ((this.director == null) ? (that.director == null)
                            : this.director.equals(that.director))
                    && ((this.writer == null) ? (that.writer == null) : this.writer.equals(that.writer))
                    && ((this.actors == null) ? (that.actors == null) : this.actors.equals(that.actors))
                    && ((this.plot == null) ? (that.plot == null) : this.plot.equals(that.plot))
                    && ((this.language == null) ? (that.language == null)
                            : this.language.equals(that.language))
                    && ((this.country == null) ? (that.country == null)
                            : this.country.equals(that.country))
                    && ((this.poster == null) ? (that.poster == null)
                            : this.poster.equals(that.poster));
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
        h ^= (rated == null) ? 0 : this.rated.hashCode();
        h *= 1000003;
        h ^= (this.released >>> 32) ^ this.released;
        h *= 1000003;
        h ^= this.runtime;
        h *= 1000003;
        h ^= (genre == null) ? 0 : this.genre.hashCode();
        h *= 1000003;
        h ^= (director == null) ? 0 : this.director.hashCode();
        h *= 1000003;
        h ^= (writer == null) ? 0 : this.writer.hashCode();
        h *= 1000003;
        h ^= (actors == null) ? 0 : this.actors.hashCode();
        h *= 1000003;
        h ^= (plot == null) ? 0 : this.plot.hashCode();
        h *= 1000003;
        h ^= (language == null) ? 0 : this.language.hashCode();
        h *= 1000003;
        h ^= (country == null) ? 0 : this.country.hashCode();
        h *= 1000003;
        h ^= (poster == null) ? 0 : this.poster.hashCode();
        return h;
    }

    //---------------------------------------------------------------
    // Comparators

    /** Comparator for ordering by imdbId. */
    public static final Comparator<Movie> MOVIE_COMPARATOR_IMDB_ID
            = new Comparator<Movie>() {
                public int compare(Movie movie1, Movie movie2) {
                    // ascending order
                    return movie1.imdbId.compareTo(movie2.imdbId);
                    // descending order
                    //return movie2.imdbId.compareTo(movie1.imdbId);
                }
            };

    /** Comparator for ordering by title. */
    public static final Comparator<Movie> MOVIE_COMPARATOR_TITLE
            = new Comparator<Movie>() {
                public int compare(Movie movie1, Movie movie2) {
                    // ascending order
                    return movie1.title.compareTo(movie2.title);
                    // descending order
                    //return movie2.title.compareTo(movie1.title);
                }
            };

}
