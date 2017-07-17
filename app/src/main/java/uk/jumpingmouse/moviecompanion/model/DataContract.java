package uk.jumpingmouse.moviecompanion.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import uk.jumpingmouse.moviecompanion.BuildConfig;
import uk.jumpingmouse.moviecompanion.data.ViewAwardQueryParameters;

/**
 * Class which defines the contract between the model and view layers.
 * @author Edmund Johnson
 */
public final class DataContract {

    /**
     * The "content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    static final String CONTENT_AUTHORITY;

    /** The base of all the URIs which apps will use to contact the content provider. */
    private static final Uri BASE_CONTENT_URI;

    /**
     * relative paths appended to base content URI.
     * For instance, "content://uk.jumpingmouse.moviecompanion/movie/" is a valid path for
     * looking at movie data.
     * "content://uk.jumpingmouse.moviecompanion/give-me-root/" will fail, as the
     * ContentProvider hasn't been given any information on what to do with "give-me-root".
     */
    static final String URI_PATH_MOVIE = "movie";
    static final String URI_PATH_AWARD = "award";
    static final String URI_PATH_USER_MOVIE = "userMovie";
    static final String URI_PATH_VIEW_AWARD = "viewAward";

    // Query parameters
    public static final String PARAM_SORT_ORDER = "sortOrder";
    public static final String PARAM_FILTER_CATEGORY = "filterCategory";
    public static final String PARAM_FILTER_GENRE = "filterGenre";
    public static final String PARAM_FILTER_WISHLIST = "filterWishlist";
    public static final String PARAM_FILTER_WATCHED = "filterWatched";
    public static final String PARAM_FILTER_FAVOURITE = "filterFavourite";
    static final String PARAM_LIMIT = "limit";

    // Values for sort direction (part of PARAM_SORT_ORDER)
    static final String SORT_DIRECTION_ASC = "ASC";
    static final String SORT_DIRECTION_DESC = "DESC";

    static {
        // The content authority for the admin product flavour has a different name so that the
        // different apps can coexist on the same device.
        CONTENT_AUTHORITY = BuildConfig.FLAVOR_mode.equals("admin") ?
                "uk.jumpingmouse.moviecompanion.admin" :
                "uk.jumpingmouse.moviecompanion";

        BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    }

    /** Private default constructor to prevent instantiation. */
    private DataContract() {
    }

    /** Inner class that defines the contract for movie information. */
    public static final class MovieEntry implements BaseColumns {

        static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH_MOVIE;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH_MOVIE;

        // Data

        public static final String COLUMN_ID = MovieEntry._ID;
        public static final String COLUMN_IMDB_ID = "imdbId";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_RELEASED = "released";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_GENRE = "genre";
        public static final String COLUMN_POSTER = "poster";

        // Note: arrays are mutable, so ALL_COLUMNS should not be public.
        // See Effective Java, Item 13.
        private static final String[] ALL_COLUMNS = {
                COLUMN_ID,
                COLUMN_IMDB_ID,
                COLUMN_TITLE,
                COLUMN_YEAR,
                COLUMN_RELEASED,
                COLUMN_RUNTIME,
                COLUMN_GENRE,
                COLUMN_POSTER
        };
        public static String[] getAllColumns() {
            return ALL_COLUMNS.clone();
        }

        public static final int COL_ID = 0;
        public static final int COL_IMDB_ID = COL_ID + 1;
        public static final int COL_TITLE = COL_IMDB_ID + 1;
        public static final int COL_YEAR = COL_TITLE + 1;
        public static final int COL_RELEASED = COL_YEAR + 1;
        public static final int COL_RUNTIME = COL_RELEASED + 1;
        public static final int COL_GENRE = COL_RUNTIME + 1;
        public static final int COL_POSTER = COL_GENRE + 1;

        // URIs

        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(URI_PATH_MOVIE).build();

        /**
         * Build and return the URI for a movie identified by its id.
         * e.g. "content://uk.jumpingmouse.moviecompanion/movie/4016934"
         * @param id the id of the movie, e.g. 4016934
         * @return the URI for obtaining the specific movie
         */
        @NonNull
        public static Uri buildUriForRowById(final int id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(id))
                    .build();
        }

        /**
         * Create and return a URI for querying all movies.
         * i.e. "content://uk.jumpingmouse.moviecompanion/movie".
         * @return the URI for querying all movies
         */
        @NonNull
        public static Uri buildUriForAllRows() {
            return CONTENT_URI;
        }

    }

    /** Inner class that defines the contract for award information. */
    public static final class AwardEntry implements BaseColumns {

        static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH_AWARD;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH_AWARD;

        // Data

        public static final String COLUMN_ID = MovieEntry._ID;
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_AWARD_DATE = "awardDate";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_REVIEW = "review";
        public static final String COLUMN_DISPLAY_ORDER = "displayOrder";

        // Note: arrays are mutable, so ALL_COLUMNS should not be public.
        // See Effective Java, Item 13.
        private static final String[] ALL_COLUMNS = {
                COLUMN_ID,
                COLUMN_MOVIE_ID,
                COLUMN_AWARD_DATE,
                COLUMN_CATEGORY,
                COLUMN_REVIEW,
                COLUMN_DISPLAY_ORDER
        };
        static String[] getAllColumns() {
            return ALL_COLUMNS.clone();
        }

        static final int COL_ID = 0;
        static final int COL_MOVIE_ID = COL_ID + 1;
        static final int COL_AWARD_DATE = COL_MOVIE_ID + 1;
        static final int COL_CATEGORY = COL_AWARD_DATE + 1;
        static final int COL_REVIEW = COL_CATEGORY + 1;
        static final int COL_DISPLAY_ORDER = COL_REVIEW + 1;

        // URIs

        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(URI_PATH_AWARD).build();

        /**
         * Build and return the URI for an award identified by its id.
         * e.g. "content://uk.jumpingmouse.moviecompanion/award/4016934/[push_id]"
         * @param awardId the id of the award, e.g. "[push_id]"
         * @return the URI for obtaining the specific award
         */
        @NonNull
        static Uri buildUriForRowById(@NonNull final String awardId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(awardId)
                    .build();
        }

        /**
         * Create and return a URI for querying all the awards for a movie.
         * i.e. "content://uk.jumpingmouse.moviecompanion/award/4016934".
         * @param movieId the id of the movie, e.g. 4016934
         * @return the URI for querying all awards for the movie
         */
        @NonNull
        static Uri buildUriForAllRowsForMovie(final int movieId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(movieId))
                    .build();
        }

    }

    /** Inner class that defines the contract for user movie information. */
    public static final class UserMovieEntry implements BaseColumns {

        static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH_USER_MOVIE;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH_USER_MOVIE;

        // Data

        public static final String COLUMN_ID = MovieEntry._ID;
        public static final String COLUMN_ON_WISHLIST = "onWishlist";
        public static final String COLUMN_WATCHED = "watched";
        public static final String COLUMN_FAVOURITE = "favourite";

        // Note: arrays are mutable, so ALL_COLUMNS should not be public.
        // See Effective Java, Item 13.
        private static final String[] ALL_COLUMNS = {
                COLUMN_ID,
                COLUMN_ON_WISHLIST,
                COLUMN_WATCHED,
                COLUMN_FAVOURITE
        };
        static String[] getAllColumns() {
            return ALL_COLUMNS.clone();
        }

        // URIs

        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(URI_PATH_USER_MOVIE).build();

        /**
         * Build and return the URI for a user movie identified by its movie id.
         * e.g. "content://uk.jumpingmouse.moviecompanion/userMovies/4016934"
         * @param movieId the id of the user movie, e.g. 4016934
         * @return the URI for obtaining the specific user movie
         */
        @NonNull
        static Uri buildUriForRowById(final int movieId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(movieId))
                    .build();
        }

        /**
         * Create and return the URI for querying all of the user movies
         * for the user who is signed into the app on this device.
         * i.e. "content://uk.jumpingmouse.moviecompanion/userMovie".
         * @return the URI for querying all of the user movies for the signed-in user
         */
        @NonNull
        public static Uri buildUriForAllRows() {
            return CONTENT_URI;
        }

    }

    /** Inner class that defines the contract for view award information. */
    public static final class ViewAwardEntry implements BaseColumns {

        static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH_VIEW_AWARD;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH_VIEW_AWARD;

        // Data

        static final String COLUMN_ID = ViewAwardEntry._ID;
        static final String COLUMN_MOVIE_ID = "movieId";
        static final String COLUMN_IMDB_ID = "imdbId";
        static final String COLUMN_AWARD_DATE = "awardDate";
        static final String COLUMN_CATEGORY = "category";
        static final String COLUMN_REVIEW = "review";
        static final String COLUMN_DISPLAY_ORDER = "displayOrder";
        static final String COLUMN_TITLE = "title";
        static final String COLUMN_RUNTIME = "runtime";
        static final String COLUMN_GENRE = "genre";
        static final String COLUMN_POSTER = "poster";
        static final String COLUMN_ON_WISHLIST = "onWishlist";
        static final String COLUMN_WATCHED = "watched";
        static final String COLUMN_FAVOURITE = "favourite";

        // Note: arrays are mutable, so ALL_COLUMNS should not be public.
        // See Effective Java, Item 13.
        public static final String[] ALL_COLUMNS = {
                COLUMN_ID,
                COLUMN_MOVIE_ID,
                COLUMN_IMDB_ID,
                COLUMN_AWARD_DATE,
                COLUMN_CATEGORY,
                COLUMN_REVIEW,
                COLUMN_DISPLAY_ORDER,
                COLUMN_TITLE,
                COLUMN_RUNTIME,
                COLUMN_GENRE,
                COLUMN_POSTER,
                COLUMN_ON_WISHLIST,
                COLUMN_WATCHED,
                COLUMN_FAVOURITE
        };

        public static String[] getAllColumns() {
            return ALL_COLUMNS.clone();
        }

        public static final int COL_ID = 0;
        public static final int COL_MOVIE_ID = COL_ID + 1;
        public static final int COL_IMDB_ID = COL_MOVIE_ID + 1;
        public static final int COL_AWARD_DATE = COL_IMDB_ID + 1;
        public static final int COL_CATEGORY = COL_AWARD_DATE + 1;
        public static final int COL_REVIEW = COL_CATEGORY + 1;
        public static final int COL_DISPLAY_ORDER = COL_REVIEW + 1;
        public static final int COL_TITLE = COL_DISPLAY_ORDER + 1;
        public static final int COL_RUNTIME = COL_TITLE + 1;
        public static final int COL_GENRE = COL_RUNTIME + 1;
        public static final int COL_POSTER = COL_GENRE + 1;
        public static final int COL_ON_WISHLIST = COL_POSTER + 1;
        public static final int COL_WATCHED = COL_ON_WISHLIST + 1;
        public static final int COL_FAVOURITE = COL_WATCHED + 1;

        // Award List Sort Orders

        // "awardDate ASC"
        public static final String SORT_ORDER_AWARD_DATE_ASC = COLUMN_AWARD_DATE + " " + SORT_DIRECTION_ASC;
        // "awardDate DESC"
        public static final String SORT_ORDER_AWARD_DATE_DESC = COLUMN_AWARD_DATE + " " + SORT_DIRECTION_DESC;
        // "title ASC"
        public static final String SORT_ORDER_TITLE_ASC = COLUMN_TITLE + " " + SORT_DIRECTION_ASC;
        // "title DESC"
        public static final String SORT_ORDER_TITLE_DESC = COLUMN_TITLE + " " + SORT_DIRECTION_DESC;
        // "runtime ASC"
        public static final String SORT_ORDER_RUNTIME_ASC = COLUMN_RUNTIME + " " + SORT_DIRECTION_ASC;
        // "runtime DESC"
        public static final String SORT_ORDER_RUNTIME_DESC = COLUMN_RUNTIME + " " + SORT_DIRECTION_DESC;
        // default sort order
        public static final String SORT_ORDER_DEFAULT = SORT_ORDER_AWARD_DATE_DESC;

        // Award List Filters
        // Strings are used for the filter values so they can be used in URIs.

        // These values must match the values in arrays.xml "filter_category_pref_key"
        static final String FILTER_CATEGORY_ANY = "filter_category_any";
        public static final String FILTER_CATEGORY_MOVIE = "filter_category_movie";
        public static final String FILTER_CATEGORY_DVD = "filter_category_dvd";
        public static final String FILTER_CATEGORY_DEFAULT = FILTER_CATEGORY_ANY;

        public static final String FILTER_GENRE_ALL = "filter_genre_all";
        public static final String FILTER_GENRE_DEFAULT = FILTER_GENRE_ALL;

        // This map contains a mapping between genre filters and genres as stored in the database.
        // The map keys must match the values in arrays.xml "filter_genre_pref_key".
        // The map values must match the values stored in the database and hence
        // MUST NOT be translated!
        private static final Map<String, String> GENRES_STORED;
        static {
            Map<String, String> genresStoredModifiable = new HashMap<>();
            genresStoredModifiable.put("filter_genre_action", "Action");
            genresStoredModifiable.put("filter_genre_adventure", "Adventure");
            genresStoredModifiable.put("filter_genre_animation", "Animation");
            genresStoredModifiable.put("filter_genre_biography", "Biography");
            genresStoredModifiable.put("filter_genre_comedy", "Comedy");
            genresStoredModifiable.put("filter_genre_crime", "Crime");
            genresStoredModifiable.put("filter_genre_documentary", "Documentary");
            genresStoredModifiable.put("filter_genre_drama", "Drama");
            genresStoredModifiable.put("filter_genre_fantasy", "Fantasy");
            genresStoredModifiable.put("filter_genre_horror", "Horror");
            genresStoredModifiable.put("filter_genre_music", "Music");
            genresStoredModifiable.put("filter_genre_mystery", "Mystery");
            genresStoredModifiable.put("filter_genre_romance", "Romance");
            genresStoredModifiable.put("filter_genre_thriller", "Thriller");
            GENRES_STORED = Collections.unmodifiableMap(genresStoredModifiable);
        }

        /**
         * Returns a genre as stored in the database for a supplied genre key.
         * @param genreKey a genre key, e.g."filter_genre_comedy"
         * @return the genre as stored in the database, e.g."Comedy", or null if there is
         *         no stored genre corresponding to the genreKey
         */
        @Nullable
        static String getGenreStoredForGenreKey(@NonNull String genreKey) {
            return GENRES_STORED.get(genreKey);
        }

        // These values must match the values in arrays.xml "filter_wishlist_pref_key"
        public static final String FILTER_WISHLIST_ANY = "filter_wishlist_any";
        static final String FILTER_WISHLIST_SHOW = "filter_wishlist_show";
        static final String FILTER_WISHLIST_HIDE = "filter_wishlist_hide";
        public static final String FILTER_WISHLIST_DEFAULT = FILTER_WISHLIST_ANY;

        // These values must match the values in arrays.xml "filter_watched_pref_key"
        public static final String FILTER_WATCHED_ANY = "filter_watched_any";
        static final String FILTER_WATCHED_SHOW = "filter_watched_show";
        static final String FILTER_WATCHED_HIDE = "filter_watched_hide";
        public static final String FILTER_WATCHED_DEFAULT = FILTER_WATCHED_ANY;

        // These values must match the values in arrays.xml "filter_favourite_pref_key"
        public static final String FILTER_FAVOURITE_ANY = "filter_favourite_any";
        static final String FILTER_FAVOURITE_SHOW = "filter_favourite_show";
        static final String FILTER_FAVOURITE_HIDE = "filter_favourite_hide";
        public static final String FILTER_FAVOURITE_DEFAULT = FILTER_FAVOURITE_ANY;

        // URIs

        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(URI_PATH_VIEW_AWARD).build();

        /**
         * Build and return the URI for a view award identified by its id.
         * e.g. "content://uk.jumpingmouse.moviecompanion/viewAward/4016934/[push_id]"
         * @param awardId the id of the award, e.g. "[push_id]"
         * @return the URI for obtaining the specific award
         */
        @NonNull
        public static Uri buildUriForRowById(@NonNull final String awardId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(awardId)
                    .build();
        }

        /**
         * Create and return a URI for querying all the view awards.
         * i.e. "content://uk.jumpingmouse.moviecompanion/viewAward".
         * @return the URI for querying all awards for the movie
         */
        @NonNull
        public static Uri buildUriForAllRows() {
            return CONTENT_URI;
        }

        /**
         * Create and return a URI for querying all the view awards.
         * e.g. "content://uk.jumpingmouse.moviecompanion/viewAward?sortOrder=awardDate DESC&filterWishlist=wishlistAll".
         * @param parameters the parameters affecting the display of the view award list
         * @return the URI for querying all awards for the movie
         */
        @NonNull
        public static Uri buildUriWithParameters(@NonNull ViewAwardQueryParameters parameters) {
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(PARAM_SORT_ORDER, parameters.getSortOrder())
                    .appendQueryParameter(PARAM_FILTER_CATEGORY, parameters.getFilterCategory())
                    .appendQueryParameter(PARAM_FILTER_GENRE, parameters.getFilterGenre())
                    .appendQueryParameter(PARAM_FILTER_WISHLIST, parameters.getFilterWishlist())
                    .appendQueryParameter(PARAM_FILTER_WATCHED, parameters.getFilterWatched())
                    .appendQueryParameter(PARAM_FILTER_FAVOURITE, parameters.getFilterFavourite())
                    .build();
        }

    }

}
