package uk.jumpingmouse.moviecompanion.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the parameters which affect the ViewAward list, such as sort order
 * and filters.
 * @author Edmund Johnson
 */

public class ViewAwardListParameters {

    /** The number of filters which can be applied to the list. */
    public static final int VIEW_AWARD_LIST_FILTERS_MAX = 4;

    // parameter values

    public static final String SORT_ORDER_AWARD_DATE_ASC = "awardDate ASC";
    public static final String SORT_ORDER_AWARD_DATE_DESC = "awardDate DESC";
    public static final String SORT_ORDER_TITLE_ASC = "title ASC";
    public static final String SORT_ORDER_TITLE_DESC = "title DESC";
    public static final String SORT_ORDER_RUNTIME_ASC = "runtime ASC";
    public static final String SORT_ORDER_RUNTIME_DESC = "runtime DESC";
    public static final String SORT_ORDER_DEFAULT = SORT_ORDER_AWARD_DATE_DESC;

    // Strings are used for the filter values so they can be used in URIs.

    // These values must match the values in arrays.xml "filter_genre_pref_key"
    public static final String FILTER_GENRE_ALL = "filter_genre_all";
    private static final String FILTER_GENRE_ACTION = "filter_genre_action";
    private static final String FILTER_GENRE_ANIMATION = "filter_genre_animation";
    private static final String FILTER_GENRE_BIOGRAPHY = "filter_genre_biography";
    private static final String FILTER_GENRE_COMEDY = "filter_genre_comedy";
    private static final String FILTER_GENRE_CRIME = "filter_genre_crime";
    private static final String FILTER_GENRE_DOCUMENTARY = "filter_genre_documentary";
    private static final String FILTER_GENRE_DRAMA = "filter_genre_drama";
    private static final String FILTER_GENRE_FANTASY = "filter_genre_fantasy";
    private static final String FILTER_GENRE_HORROR = "filter_genre_horror";
    private static final String FILTER_GENRE_MUSIC = "filter_genre_music";
    private static final String FILTER_GENRE_MYSTERY = "filter_genre_mystery";
    private static final String FILTER_GENRE_ROMANCE = "filter_genre_romance";
    private static final String FILTER_GENRE_THRILLER = "filter_genre_thriller";
    public static final String FILTER_GENRE_DEFAULT = FILTER_GENRE_ALL;

    // These values must match the values in arrays.xml "filter_wishlist_pref_key"
    public static final String FILTER_WISHLIST_ANY = "filter_wishlist_any";
    public static final String FILTER_WISHLIST_SHOW = "filter_wishlist_show";
    public static final String FILTER_WISHLIST_HIDE = "filter_wishlist_hide";
    public static final String FILTER_WISHLIST_DEFAULT = FILTER_WISHLIST_ANY;

    // These values must match the values in arrays.xml "filter_watched_pref_key"
    public static final String FILTER_WATCHED_ANY = "filter_watched_any";
    public static final String FILTER_WATCHED_SHOW = "filter_watched_show";
    public static final String FILTER_WATCHED_HIDE = "filter_watched_hide";
    public static final String FILTER_WATCHED_DEFAULT = FILTER_WATCHED_ANY;

    // These values must match the values in arrays.xml "filter_favourite_pref_key"
    public static final String FILTER_FAVOURITE_ANY = "filter_favourite_any";
    public static final String FILTER_FAVOURITE_SHOW = "filter_favourite_show";
    public static final String FILTER_FAVOURITE_HIDE = "filter_favourite_hide";
    public static final String FILTER_FAVOURITE_DEFAULT = FILTER_FAVOURITE_ANY;

    // This map contains the genres as stored in the database.
    // The values therefore MUST NOT be translated!
    private static final Map<String, String> GENRES_STORED;
    static {
        Map<String, String> genresStoredModifiable = new HashMap<>();
        genresStoredModifiable.put(FILTER_GENRE_ACTION, "Action");
        genresStoredModifiable.put(FILTER_GENRE_ANIMATION, "Animation");
        genresStoredModifiable.put(FILTER_GENRE_BIOGRAPHY, "Biography");
        genresStoredModifiable.put(FILTER_GENRE_COMEDY, "Comedy");
        genresStoredModifiable.put(FILTER_GENRE_CRIME, "Crime");
        genresStoredModifiable.put(FILTER_GENRE_DOCUMENTARY, "Documentary");
        genresStoredModifiable.put(FILTER_GENRE_DRAMA, "Drama");
        genresStoredModifiable.put(FILTER_GENRE_FANTASY, "Fantasy");
        genresStoredModifiable.put(FILTER_GENRE_HORROR, "Horror");
        genresStoredModifiable.put(FILTER_GENRE_MUSIC, "Music");
        genresStoredModifiable.put(FILTER_GENRE_MYSTERY, "Mystery");
        genresStoredModifiable.put(FILTER_GENRE_ROMANCE, "Romance");
        genresStoredModifiable.put(FILTER_GENRE_THRILLER, "Thriller");
        GENRES_STORED = Collections.unmodifiableMap(genresStoredModifiable);
    }

    // parameters
    private String sortOrder;
    private String filterGenre;
    private String filterWishlist;
    private String filterWatched;
    private String filterFavourite;

    // utilities

    /**
     * Returns a map of the genres as stored in the database.
     * The map keys are of the form "filter_genre_comedy", "filter_genre_drama", etc.
     * The map values are of the form "Comedy", "Drama", etc.
     * @return a map of the genres as stored in the database
     */
    public static Map getGenresStored() {
        return GENRES_STORED;
    }

    // Getters and setters

    @NonNull
    public String getSortOrder() {
        return sortOrder == null ? SORT_ORDER_DEFAULT : sortOrder;
    }
    public void setSortOrder(@Nullable String sortOrder) {
        this.sortOrder = sortOrder;
    }

    @NonNull
    public String getFilterGenre() {
        return filterGenre == null ? FILTER_GENRE_DEFAULT : filterGenre;
    }
    public void setFilterGenre(@Nullable String filterGenre) {
        this.filterGenre = filterGenre;
    }

    @NonNull
    public String getFilterWishlist() {
        return filterWishlist == null ? FILTER_WISHLIST_DEFAULT : filterWishlist;
    }
    public void setFilterWishlist(@Nullable String filterWishlist) {
        this.filterWishlist = filterWishlist;
    }

    @NonNull
    public String getFilterWatched() {
        return filterWatched == null ? FILTER_WATCHED_DEFAULT : filterWatched;
    }
    public void setFilterWatched(@Nullable String filterWatched) {
        this.filterWatched = filterWatched;
    }

    @NonNull
    public String getFilterFavourite() {
        return filterFavourite == null ? FILTER_FAVOURITE_DEFAULT : filterFavourite;
    }
    public void setFilterFavourite(@Nullable String filterFavourite) {
        this.filterFavourite = filterFavourite;
    }

}
