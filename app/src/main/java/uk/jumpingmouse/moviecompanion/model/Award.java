package uk.jumpingmouse.moviecompanion.model;

import com.google.auto.value.AutoValue;

/**
 * The Award model class.
 * An Award is uniquely identified by the combination of awardDate, category and imdbId.
 * An example of an award is "Movie of the Week" for 12/05/17.
 * Note that there could be more than one Movie of the Week for a given week.
 * The Movie-Award relationship is one-many.
 * @author Edmund Johnson
 */
@AutoValue
public abstract class Award {
    public static final String CATEGORY_MOVIE = "M";
    public static final String CATEGORY_DVD = "D";

    // awardDate format is "YYMMDD"
    abstract String awardDate();
    // categoryId is one of CATEGORY_MOVIE, CATEGORY_DVD
    abstract String category();
    // imdbId is a "foreign key" to Movie
    abstract String imdbId();
    // free text
    abstract String review();

    @AutoValue.Builder
    abstract static class Builder {
        abstract Builder awardDate(String value);
        abstract Builder category(String value);
        abstract Builder imdbId(String value);
        abstract Builder review(String value);
        abstract Award build();
    }

    /**
     * Builder for this class.  Usage:
     * <blockquote><pre>
     * {@code
     *   Award award = Award.builder()
     *         .awardDate("170512")
     *         .category(Award.CATEGORY_MOVIE)
     *         .imdbId("tt4016934")
     *         // etc
     *        .build();
     * }
     * </pre></blockquote>
     * @return an instance of the Award class.
     */
    public static Builder builder() {
        return new AutoValue_Award.Builder();
    }

}
