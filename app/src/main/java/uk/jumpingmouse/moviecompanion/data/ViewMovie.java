package uk.jumpingmouse.moviecompanion.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.security.InvalidParameterException;

/**
 * The ViewMovie model class.
 * This represents a movie as displayed on the movie screen, and contains fields from:
 * - Movie
 * - the movie's Awards
 * - the UserMovie, i.e. whether the movie is on the user's wishlist, see list, favourites, etc.
 * @author Edmund Johnson
 */
public class ViewMovie implements Parcelable {

    // The unique identifier of the ViewMovie and the corresponding movie, e.g. 4016934.
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

    private ViewMovie() {
    }

    public ViewMovie(@NonNull Movie movie) {
        this.id = movie.getId();
        this.imdbId = movie.getImdbId();
        this.title = movie.getTitle();
        this.year = movie.getYear();
        this.released = movie.getReleased();
        this.runtime = movie.getRuntime();
        this.genre = movie.getGenre();
        this.poster = movie.getPoster();
    }

    private ViewMovie(
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

    /** Returns the movie's unique id, e.g. 4016934. */
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
     * Returns the ViewMovie as an object array, one element per field value.
     * @return the ViewMovie as an Object array
     */
    public Object[] toObjectArray() {
        return new Object[] {
                // This must match the order of columns in DataContract.ViewMovieEntry.getAllColumns().
                getId(),
                getImdbId(),
                getTitle(),
                getYear(),
                getReleased(),
                getRuntime(),
                getGenre(),
                getPoster()
        };
    }

    //---------------------------------------------------------------
    // Parcelable implementation

    /**
     * Private constructor to create an object of this class from a parcel.
     * @param in a Parcel containing the object
     */
    private ViewMovie(@NonNull final Parcel in) {
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
    public static final Creator<ViewMovie> CREATOR =
            new Creator<ViewMovie>() {
                @NonNull
                public ViewMovie createFromParcel(@NonNull final Parcel in) {
                    return new ViewMovie(in);
                }

                @NonNull
                public ViewMovie[] newArray(final int size) {
                    return new ViewMovie[size];
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
     *   ViewMovie viewMovie = ViewMovie.builder()
     *         .id("tt4016934")
     *         .imdbId("tt4016934")
     *         .title("The Handmaiden")
     *         // etc
     *        .build();
     * }
     * </pre></blockquote>
     * @return an instance of the Movie class.
     */
    public static ViewMovie.Builder builder() {
        return new ViewMovie.Builder();
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

        Builder(ViewMovie source) {
            this.id = source.id;
            this.imdbId = source.imdbId;
            this.title = source.title;
            this.year = source.year;
            this.released = source.released;
            this.runtime = source.runtime;
            this.genre = source.genre;
            this.poster = source.poster;
        }

        public ViewMovie.Builder id(int id) {
            this.id = id;
            return this;
        }
        public ViewMovie.Builder imdbId(@NonNull String imdbId) {
            this.imdbId = imdbId;
            return this;
        }
        public ViewMovie.Builder title(@NonNull String title) {
            this.title = title;
            return this;
        }
        public ViewMovie.Builder year(@Nullable String year) {
            this.year = year;
            return this;
        }
        public ViewMovie.Builder released(long released) {
            this.released = released;
            return this;
        }
        public ViewMovie.Builder runtime(int runtime) {
            this.runtime = runtime;
            return this;
        }
        public ViewMovie.Builder genre(@Nullable String genre) {
            this.genre = genre;
            return this;
        }
        public ViewMovie.Builder poster(@Nullable String poster) {
            this.poster = poster;
            return this;
        }
        /** Builds and returns an object of this class. */
        public ViewMovie build() {
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
            return new ViewMovie(
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
        return "ViewMovie{"
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
        if (o instanceof ViewMovie) {
            ViewMovie that = (ViewMovie) o;
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

//    //---------------------------------------------------------------
//    // Comparators
//
//    /** Comparator for ordering by imdbId. */
//    public static final Comparator<ViewMovie> ViewMovieComparatorImdbId
//            = new Comparator<ViewMovie>() {
//                public int compare(ViewMovie viewMovie1, ViewMovie viewMovie2) {
//                    // ascending order
//                    return viewMovie1.imdbId.compareTo(viewMovie2.imdbId);
//                }
//            };
//
//    /** Comparator for ordering by title. */
//    public static final Comparator<ViewMovie> ViewMovieComparatorTitle
//            = new Comparator<ViewMovie>() {
//                public int compare(ViewMovie viewMovie1, ViewMovie viewMovie2) {
//                    // ascending order
//                    return viewMovie1.title.compareTo(viewMovie2.title);
//                }
//            };

}
