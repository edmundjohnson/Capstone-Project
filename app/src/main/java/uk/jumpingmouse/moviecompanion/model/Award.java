package uk.jumpingmouse.moviecompanion.model;

import com.google.auto.value.AutoValue;

/**
 * The Award model class.
 * An Award is uniquely identified by the combination of awardDate, category and imdbId.
 * An example of an award is "Film of the Week" for 12/05/17.
 * Note that there could be more than one Film of the Week for a given week.
 * The Film-Award relationship is one-many.
 * @author Edmund Johnson
 */
@AutoValue
public abstract class Award {
    public static final String CATEGORY_FILM = "F";
    public static final String CATEGORY_DVD = "D";

    // awardDate format is "YYMMDD"
    abstract String awardDate();
    // categoryId is one of CATEGORY_FILM, CATEGORY_DVD
    abstract String category();
    // imdbId is a "foreign key" to Film
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
     *         .category(Award.CATEGORY_FILM)
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
