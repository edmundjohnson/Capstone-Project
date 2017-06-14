package uk.jumpingmouse.moviecompanion.data;

import android.support.annotation.NonNull;

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

    private ViewAward() {
    }

    public ViewAward(@NonNull Award award, @NonNull Movie movie) {
        this.id = award.getId();
        this.movieId = award.getMovieId();
        this.awardDate = award.getAwardDate();
        this.category = award.getCategory();
        this.displayOrder = award.getDisplayOrder();
        this.title = movie.getTitle();
    }

    //---------------------------------------------------------------
    // Getters

    @NonNull
    public String getId() {
        return id;
    }

    public int getMovieId() {
        return movieId;
    }

    @NonNull
    public String getAwardDate() {
        return awardDate;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    //---------------------------------------------------------------
    // Parcelable implementation


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
                    && (this.title.equals(that.title));
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
                // awardDate, then reverse category ("M" > "D")
                return viewAward2.category.compareTo(viewAward1.category);
            }
            return viewAward1.awardDate.compareTo(viewAward2.awardDate);
        }
    };

}
