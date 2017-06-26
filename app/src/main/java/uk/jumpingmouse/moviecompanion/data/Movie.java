package uk.jumpingmouse.moviecompanion.data;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.security.InvalidParameterException;
import java.util.Comparator;

import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.omdbapi.OmdbMovie;

/**
 * The Movie model class.
 * This is similar to the OmdbMovie class, which contains unprocessed data from the
 * Open Movie Database.  However, this class stores data in a more useful form,
 * e.g. runtime is stored as an int (e.g. 144), rather than a String (e.g. "144 min").
 * @author Edmund Johnson
 */
public class Movie implements Parcelable {
    public static final int ID_UNKNOWN = -1;
    public static final int RUNTIME_UNKNOWN = OmdbMovie.RUNTIME_UNKNOWN;
    public static final int RELEASED_UNKNOWN = OmdbMovie.RELEASED_UNKNOWN;

    // The unique identifier of the movie, e.g. 4016934.
    // This is the numeric part of the imdbId.
    private int id;
    // The IMDb id, e.g. "tt4016934"
    private String imdbId;
    // The title, e.g. "The Handmaiden"
    private String title;
    // The year of the movie's release (not the year of this award), e.g. "2017"
    private String year;
    // The release date, as a millisecond value
    private long released;
    // The length in minutes
    private int runtime;
    // A comma-separated list of genres, e.g. "Drama, Mystery, Romance"
    private String genre;
    // The URL of the movie poster image
    private String poster;

    private Movie() {
    }

    private Movie(
            int id,
            @Nullable String imdbId,
            @Nullable String title,
            @Nullable String year,
            long released,
            int runtime,
            @Nullable String genre,
            @Nullable String poster) {
        if (id <= 0) {
            throw new InvalidParameterException("id zero or negative");
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

    /** Returns the unique id, e.g. 4016934. */
    public int getId() {
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
        values.put(DataContract.MovieEntry.COLUMN_RELEASED, getReleased());
        values.put(DataContract.MovieEntry.COLUMN_RUNTIME, getRuntime());
        values.put(DataContract.MovieEntry.COLUMN_GENRE, getGenre());
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
                released,
                runtime,
                genre,
                poster
        };
    }

    //---------------------------------------------------------------
    // Parcelable implementation

    /**
     * Private constructor to create an object of this class from a parcel.
     * @param in a Parcel containing the object
     */
    private Movie(@NonNull final Parcel in) {
        id = in.readInt();
        imdbId = in.readString();
        title = in.readString();
        year = in.readString();
        released = in.readLong();
        runtime = in.readInt();
        genre = in.readString();
        poster = in.readString();
    }

    /**
     * Flatten this object into a Parcel.
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(imdbId);
        dest.writeString(title);
        dest.writeString(year);
        dest.writeLong(released);
        dest.writeInt(runtime);
        dest.writeString(genre);
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
     * by this Parcelable object instance.
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
        private int id;
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

        public Movie.Builder id(int id) {
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
            if (id <= 0) {
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
            return (this.id == that.id)
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
        h ^= this.id;
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
