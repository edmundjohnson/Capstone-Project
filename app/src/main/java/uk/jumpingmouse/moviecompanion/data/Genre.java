package uk.jumpingmouse.moviecompanion.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * The Genre model class.
 * @author Edmund Johnson
 * @deprecated See arrays.xml and DataContract class
 */
@Deprecated
public final class Genre implements Parcelable {

    // The unique identifier of the genre, e.g. "genre_comedy".
    private String id;
    // The value stored for this genre in the movie 'genre' field, e.g. "Comedy".
    // The storedValue is always in English.
    private String storedValue;
    // Whether to display the genre in the genre filter. This should be false
    // for any genre which does not yet apply to any movie.
    private boolean displayInFilter;

    private Genre() {
    }

    private Genre(
            @Nullable String id,
            @Nullable String storedValue,
            boolean displayInFilter) {
        if (id == null) {
            throw new NullPointerException("Null id");
        }
        if (storedValue == null) {
            throw new NullPointerException("Null storedValue");
        }
        this.id = id;
        this.storedValue = storedValue;
        this.displayInFilter = displayInFilter;
    }

    //---------------------------------------------------------------
    // Getters
    // These MUST all be public - if not, Firebase will fail to
    // load the Genre from the database.

    /**
     * Returns the genre's unique id, e.g. "genre_comedy".
     * @return the genre's unique id
     */
    @NonNull
    public String getId() {
        return id;
    }

    /**
     * Returns the genre's stored value, e.g. "Comedy".
     * @return the genre's stored value, e.g. "Comedy"
     */
    @NonNull
    public String getStoredValue() {
        return storedValue;
    }

    /**
     * Returns whether to display the genre in the genre filter.
     * @return whether to display the genre in the genre filter
     */
    public boolean isDisplayInFilter() {
        return displayInFilter;
    }

    //---------------------------------------------------------------
    // Parcelable implementation

    /**
     * Private constructor to create an object of this class from a parcel.
     * @param in a Parcel containing the object
     */
    private Genre(@NonNull final Parcel in) {
        id = in.readString();
        storedValue = in.readString();
        displayInFilter = in.readInt() == 1;
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
        dest.writeString(storedValue);
        dest.writeInt(displayInFilter ? 1 : 0);
    }

    /**
     * Parcel creator object.
     */
    public static final Creator<Genre> CREATOR =
            new Creator<Genre>() {
                @NonNull
                public Genre createFromParcel(@NonNull final Parcel in) {
                    return new Genre(in);
                }

                @NonNull
                public Genre[] newArray(final int size) {
                    return new Genre[size];
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
     *   Genre genre = Genre.builder()
     *         .id("genre_comedy")
     *         .storedValue("Comedy")
     *         .displayInFilter(true)
     *        .build();
     * }
     * </pre></blockquote>
     * @return an instance of the Genre class.
     */
    public static Builder builder() {
        return new Genre.Builder();
    }

    @SuppressWarnings("WeakerAccess")
    public static final class Builder {
        private String id;
        private String storedValue;
        private boolean displayInFilter;

        Builder() {
        }

        Builder(@NonNull Genre source) {
            this.id = source.id;
            this.storedValue = source.storedValue;
            this.displayInFilter = source.displayInFilter;
        }

        public Genre.Builder id(@NonNull String id) {
            this.id = id;
            return this;
        }
        public Genre.Builder storedValue(@NonNull String storedValue) {
            this.storedValue = storedValue;
            return this;
        }
        public Genre.Builder displayInFilter(boolean displayInFilter) {
            this.displayInFilter = displayInFilter;
            return this;
        }
        /**
         * Builds and returns a Genre object.
         * @return a Genre object
         */
        public Genre build() {
            String missing = "";
            if (id == null) {
                missing += " id";
            }
            if (storedValue == null) {
                missing += " storedValue";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new Genre(
                    this.id,
                    this.storedValue,
                    this.displayInFilter);
        }
    }

    //---------------------------------------------------------------
    // Utilities

//    /**
//     * Returns a set of ContentValues corresponding to the genre.
//     * @return the set of ContentValues corresponding to the genre
//     */
//    @NonNull
//    public ContentValues toContentValues() {
//        ContentValues values = new ContentValues();
//
//        values.put(DataContract.MovieEntry.COLUMN_ID, getId());
//        values.put(DataContract.MovieEntry.COLUMN_IMDB_ID, getImdbId());
//        values.put(DataContract.MovieEntry.COLUMN_TITLE, getTitle());
//        values.put(DataContract.MovieEntry.COLUMN_YEAR, getYear());
//        values.put(DataContract.MovieEntry.COLUMN_RATED, getRated());
//        values.put(DataContract.MovieEntry.COLUMN_RELEASED, getReleased());
//        values.put(DataContract.MovieEntry.COLUMN_RUNTIME, getRuntime());
//        values.put(DataContract.MovieEntry.COLUMN_GENRE, getGenre());
//        values.put(DataContract.MovieEntry.COLUMN_DIRECTOR, getDirector());
//        values.put(DataContract.MovieEntry.COLUMN_WRITER, getWriter());
//        values.put(DataContract.MovieEntry.COLUMN_ACTORS, getActors());
//        values.put(DataContract.MovieEntry.COLUMN_PLOT, getPlot());
//        values.put(DataContract.MovieEntry.COLUMN_LANGUAGE, getLanguage());
//        values.put(DataContract.MovieEntry.COLUMN_COUNTRY, getCountry());
//        values.put(DataContract.MovieEntry.COLUMN_POSTER, getPoster());
//
//        return values;
//    }

    /**
     * Returns the genre as an object array, one element per field value.
     * @return the genre as an Object array
     */
    public Object[] toObjectArray() {
        return new Object[] {
                // This must match the order of columns in DataContract.GenreEntry.getAllColumns().
                id,
                storedValue,
                displayInFilter
        };
    }

    //---------------------------------------------------------------
    // Override object methods

    @Override
    public String toString() {
        return "Genre{"
                + "id=" + id
                + ", storedValue=" + storedValue
                + ", displayInFilter=" + displayInFilter
                + "}";
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Genre) {
            Genre that = (Genre) o;
            return (this.id.equals(that.id));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.id.hashCode();
        return h;
    }

}
