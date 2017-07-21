package uk.jumpingmouse.moviecompanion.data;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uk.jumpingmouse.moviecompanion.model.DataContract;

/**
 * The UserMovie model class.
 * This class represents information about a movie related to a particular user,
 * such as whether the movie is on the user's wishlist.
 * @author Edmund Johnson
 */
public final class UserMovie implements Parcelable {

    // The unique identifier of the movie, e.g. "4016934".
    private String id;
    // Whether the movie is on the current user's wishlist
    private boolean onWishlist;
    // Whether the movie is on the current user's watched list
    private boolean watched;
    // Whether the movie is on the current user's list of favourites
    private boolean favourite;

    private UserMovie() {
    }

    private UserMovie(
            @NonNull String id,
            boolean onWishlist,
            boolean watched,
            boolean favourite) {
        this.id = id;
        this.onWishlist = onWishlist;
        this.watched = watched;
        this.favourite = favourite;
    }

    //---------------------------------------------------------------
    // Getters
    // These MUST all be public - if not, Firebase will fail to
    // load the UserMovie from the database.

    /**
     * Returns the unique id, e.g. 4016934.
     * @return the movie's unique id
     */
    @NonNull
    public String getId() {
        return id;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isOnWishlist() {
        return onWishlist;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isWatched() {
        return watched;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isFavourite() {
        return favourite;
    }

    //---------------------------------------------------------------
    // Utilities

    /**
     * Returns a set of ContentValues corresponding to the user movie.
     * @return the set of ContentValues corresponding to the user movie
     */
    @NonNull
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(DataContract.UserMovieEntry.COLUMN_ID, getId());
        values.put(DataContract.UserMovieEntry.COLUMN_ON_WISHLIST, isOnWishlist());
        values.put(DataContract.UserMovieEntry.COLUMN_WATCHED, isWatched());
        values.put(DataContract.UserMovieEntry.COLUMN_FAVOURITE, isFavourite());

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
                onWishlist,
                watched,
                favourite
        };
    }

    //---------------------------------------------------------------
    // Parcelable implementation

    /**
     * Private constructor to create an object of this class from a parcel.
     * @param in a Parcel containing the object
     */
    private UserMovie(@NonNull final Parcel in) {
        id = in.readString();
        onWishlist = in.readInt() == 1;
        watched = in.readInt() == 1;
        favourite = in.readInt() == 1;
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
        dest.writeInt(onWishlist ? 1 : 0);
        dest.writeInt(watched ? 1 : 0);
        dest.writeInt(favourite ? 1 : 0);
    }

    /**
     * Parcel creator object.
     */
    public static final Creator<UserMovie> CREATOR =
            new Creator<UserMovie>() {
                @NonNull
                public UserMovie createFromParcel(@NonNull final Parcel in) {
                    return new UserMovie(in);
                }

                @NonNull
                public UserMovie[] newArray(final int size) {
                    return new UserMovie[size];
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
        return new UserMovie.Builder();
    }

    @SuppressWarnings("WeakerAccess")
    public static final class Builder {
        private String id;
        private boolean onWishlist;
        private boolean watched;
        private boolean favourite;

        Builder() {
        }

        Builder(@NonNull UserMovie source) {
            this.id = source.id;
            this.onWishlist = source.onWishlist;
            this.watched = source.watched;
            this.favourite = source.favourite;
        }

        public UserMovie.Builder id(@Nullable String id) {
            this.id = id;
            return this;
        }
        public UserMovie.Builder onWishlist(boolean onWishlist) {
            this.onWishlist = onWishlist;
            return this;
        }
        public UserMovie.Builder watched(boolean watched) {
            this.watched = watched;
            return this;
        }
        public UserMovie.Builder favourite(boolean favourite) {
            this.favourite = favourite;
            return this;
        }
        /**
         * Builds and returns a UserMovie object.
         * @return a UserMovie object
         */
        public UserMovie build() {
            String missing = "";
            if (id == null) {
                missing += " id";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new UserMovie(
                    this.id,
                    this.onWishlist,
                    this.watched,
                    this.favourite);
        }
    }

    //---------------------------------------------------------------
    // Override object methods

    @Override
    public String toString() {
        return "UserMovie{"
                + "id=" + id
                + ", onWishlist=" + onWishlist
                + ", watched=" + watched
                + ", favourite=" + favourite
                + "}";
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof UserMovie) {
            UserMovie that = (UserMovie) o;
            return (this.id.equals(that.id))
                    && (this.onWishlist == that.onWishlist)
                    && (this.watched == that.watched)
                    && (this.favourite == that.favourite);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.id.hashCode();
        h *= 1000003;
        h ^= this.onWishlist ? 1 : 0;
        h *= 1000003;
        h ^= this.watched ? 1 : 0;
        h *= 1000003;
        h ^= this.favourite ? 1 : 0;
        return h;
    }

}
