package uk.jumpingmouse.moviecompanion.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Comparator;

/**
 * The ViewAward model class.
 * This class represents an entry in the displayed award list.
 * It contains fields from the Award and from the Movie which received the Award.
 * @author Edmund Johnson
 */
public class ViewAward {

    // the unique identifier for the award, a push id
    private String id;
    // the unique identifier for the movie
    private int movieId;
    // awardDate is formatted as "YYMMDD"
    private String awardDate;
    // categoryId is one of CATEGORY_MOVIE, CATEGORY_DVD
    private String category;
    // displayOrder determines the displayed order of awards for a movie
    private int displayOrder;
    // The movie title, e.g. "The Handmaiden"
    private String title;
    // The length in minutes
    private int runtime;
    // A comma-separated list of genres, e.g. "Drama, Mystery, Romance"
    private String genre;
    // The URL of the movie poster image
    private String poster;

    private ViewAward() {
    }

    public ViewAward(@NonNull Award award, @NonNull Movie movie) {
        this.id = award.getId();
        this.movieId = award.getMovieId();
        this.awardDate = award.getAwardDate();
        this.category = award.getCategory();
        this.displayOrder = award.getDisplayOrder();
        this.title = movie.getTitle();
        this.runtime = movie.getRuntime();
        this.genre = movie.getGenre();
        this.poster = movie.getPoster();
    }

    //---------------------------------------------------------------
    // Getters

    @NonNull
    private String getId() {
        return id;
    }

    private int getMovieId() {
        return movieId;
    }

    @NonNull
    private String getAwardDate() {
        return awardDate;
    }

    @NonNull
    private String getCategory() {
        return category;
    }

    private int getDisplayOrder() {
        return displayOrder;
    }

    @NonNull
    private String getTitle() {
        return title;
    }

    /** Returns the runtime in minutes, e.g. 144. */
    private int getRuntime() {
        return runtime;
    }

    /** Returns a comma-separated list of genres, e.g. "Drama, Mystery, Romance". */
    @Nullable
    private String getGenre() {
        return genre;
    }

    @Nullable
    private String getPoster() {
        return poster;
    }

    //---------------------------------------------------------------
    // Utilities

    /**
     * Returns a view award as an object array, one element per field value.
     * @return the view award as an Object array
     */
    public Object[] toObjectArray() {
        return new Object[] {
                // This must match the order of columns in DataContract.ViewAwardEntry.getAllColumns().
                getId(),
                getMovieId(),
                getAwardDate(),
                getCategory(),
                getDisplayOrder(),
                getTitle(),
                getRuntime(),
                getGenre(),
                getPoster()
        };
    }

    //---------------------------------------------------------------
    // Override object methods

    @Override
    public String toString() {
        return "ViewAward{"
                + "id=" + id
                + ", movieId=" + movieId
                + ", awardDate=" + awardDate
                + ", category=" + category
                + ", displayOrder=" + displayOrder
                + ", title=" + title
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
        if (o instanceof ViewAward) {
            ViewAward that = (ViewAward) o;
            return (this.id.equals(that.id))
                    && (this.movieId == that.movieId)
                    && (this.awardDate.equals(that.awardDate))
                    && (this.category.equals(that.category))
                    && (this.displayOrder == that.displayOrder)
                    && (this.title.equals(that.title))
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
        h ^= this.movieId;
        h *= 1000003;
        h ^= this.awardDate.hashCode();
        h *= 1000003;
        h ^= this.category.hashCode();
        h *= 1000003;
        h ^= this.displayOrder;
        h *= 1000003;
        h ^= this.title.hashCode();
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

    /** Comparator for ordering by movie id. */
    public static final Comparator<ViewAward> ViewAwardComparatorMovieId
            = new Comparator<ViewAward>() {
        public int compare(ViewAward viewAward1, ViewAward viewAward2) {
            // ascending order
            if (viewAward1.movieId == viewAward2.movieId) {
                // movieId, then awardDate, then reverse category ("M" > "D")
                if (viewAward1.awardDate.equals(viewAward2.awardDate)) {
                    return viewAward2.category.compareTo(viewAward1.category);
                }
                return viewAward1.awardDate.compareTo(viewAward2.awardDate);
            }
            return viewAward1.movieId - viewAward2.movieId;
        }
    };

    /** Comparator for ordering by award date. */
    public static final Comparator<ViewAward> ViewAwardComparatorAwardDate
            = new Comparator<ViewAward>() {
        public int compare(ViewAward viewAward1, ViewAward viewAward2) {
            // ascending order
            if (viewAward1.awardDate.equals(viewAward2.awardDate)) {
                // awardDate ascending, then category ("D" before "M")
                // This unintuitive ordering of category is so that when the comparator is
                // reversed, as it is by default, Movie comes before DVD
                return viewAward1.category.compareTo(viewAward2.category);
            }
            return viewAward1.awardDate.compareTo(viewAward2.awardDate);
        }
    };

}
