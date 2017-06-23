package uk.jumpingmouse.moviecompanion.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * The ViewMovie model class.
 * This represents a movie as displayed on the movie screen, and contains fields from:
 * - Movie
 * - the movie's Awards
 * - the UserMovie, i.e. whether the movie is on the user's wishlist, see list, favourites, etc.
 * @author Edmund Johnson
 * @deprecated Use ViewAward instead
 */
@Deprecated
public class ViewMovie implements Parcelable {

    private Movie movie;
    private Award award;

    private ViewMovie() {
    }

    public ViewMovie(@NonNull Movie movie, @NonNull Award award) {
        this.movie = movie;
        this.award = award;
    }

    //---------------------------------------------------------------
    // Getters

    @NonNull
    public Movie getMovie() {
        return movie;
    }

    @NonNull
    public Award getAward() {
        return award;
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
                movie.getId(),
                movie.getImdbId(),
                movie.getTitle(),
                movie.getYear(),
                movie.getReleased(),
                movie.getRuntime(),
                movie.getGenre(),
                movie.getPoster()
        };
    }

    //---------------------------------------------------------------
    // Parcelable implementation

    /**
     * Private constructor to create an object of this class from a parcel.
     * @param in a Parcel containing the object
     */
    private ViewMovie(@NonNull final Parcel in) {
        movie = in.readParcelable(ClassLoader.getSystemClassLoader());
        award = in.readParcelable(ClassLoader.getSystemClassLoader());
    }

    /**
     * Flatten this object into a Parcel.
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(movie, flags);
        dest.writeParcelable(award, flags);
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
    // Override object methods

    @Override
    public String toString() {
        return "ViewMovie{"
                + "movie=" + movie
                + ", award=" + award
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ViewMovie) {
            ViewMovie that = (ViewMovie) o;
            return (this.movie.equals(that.movie)
                    && (this.award.equals(that.award)));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.movie.hashCode();
        h *= 1000003;
        h ^= this.award.hashCode();
        return h;
    }

}
