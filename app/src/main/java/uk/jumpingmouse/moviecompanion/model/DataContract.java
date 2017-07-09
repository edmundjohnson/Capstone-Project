package uk.jumpingmouse.moviecompanion.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uk.jumpingmouse.moviecompanion.BuildConfig;
import uk.jumpingmouse.moviecompanion.data.ViewAwardListParameters;

/**
 * Class which defines the contract between the model (database) and view layers.
 * @author Edmund Johnson
 */
public final class DataContract {

    /** Database node for user-related data. */
    private static final String ROOT_NODE_USERS = "users";

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
    public static final String PARAM_FILTER_GENRE = "filterGenre";
    public static final String PARAM_FILTER_WISHLIST = "filterWishlist";
    public static final String PARAM_FILTER_WATCHED = "filterWatched";
    public static final String PARAM_FILTER_FAVOURITE = "filterFavourite";

    // Values for sort direction (PARAM_SORT_DIRECTION)
    static final String SORT_DIRECTION_ASC = "ASC";
    static final String SORT_DIRECTION_DESC = "DESC";

    // Query parameters which are not columns
//        public static final String PARAM_LIMIT = "limit";
//        public static final String PARAM_EXCLUDINGNEWEST = "excludingnewest";
//        public static final String PARAM_SORT_COLUMN = "sortColumn";
//        public static final String PARAM_SORT_DIRECTION = "sortDirection";

//    // Default values for limit queries
//    private static final int MAX_ROWS_DEFAULT = 0;
//    private static final int OFFSET_DEFAULT = 0;

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

    /** Inner class that defines the contents of the movie node. */
    public static final class MovieEntry implements BaseColumns {

        static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH_MOVIE;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH_MOVIE;

        // Database

        static final String ROOT_NODE = "movies";

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

//        /**
//         * Create and return a URI for querying movies with a specified max number of rows,
//         * using the default offset for the first returned row and the default sort order.
//         * e.g. "content://org.balancedview.dailybenefit/movie/limit/100".
//         * @param limit the maximum number of rows to return
//         * @return the URI for querying movies with the specified max number of rows, using the
//         *         default offset and sort order
//         */
//        @NonNull
//        public static Uri buildUriForAllRowsWithLimit(final int limit) {
//            return CONTENT_URI.buildUpon()
//                    .appendPath(PARAM_LIMIT)
//                    .appendPath(String.valueOf(limit))
//                    .build();
//        }

    }

    /** Inner class that defines the contents of the award node. */
    public static final class AwardEntry implements BaseColumns {

        static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH_AWARD;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH_AWARD;

        // Database

        static final String ROOT_NODE = "awards";

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

    /** Inner class that defines the contents of a user movie node. */
    public static final class UserMovieEntry implements BaseColumns {

        static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH_USER_MOVIE;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH_USER_MOVIE;

        // Database

        private static final String ROOT_NODE = "userMovies" ;

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
        public static String[] getAllColumns() {
            return ALL_COLUMNS.clone();
        }

        public static final int COL_ID = 0;
        public static final int COL_ON_WISHLIST = COL_ID + 1;
        public static final int COL_WATCHED = COL_ON_WISHLIST + 1;
        public static final int COL_FAVOURITE = COL_WATCHED + 1;

        // URIs

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(URI_PATH_USER_MOVIE).build();

        /**
         * Build and return the URI for a user movie identified by its movie id.
         * e.g. "content://uk.jumpingmouse.moviecompanion/userMovies/4016934"
         * @param movieId the id of the user movie, e.g. 4016934
         * @return the URI for obtaining the specific user movie
         */
        @NonNull
        public static Uri buildUriForRowById(final int movieId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(movieId))
                    .build();
        }

//        /**
//         * Create and return a URI for querying all userMovies.
//         * i.e. "content://uk.jumpingmouse.moviecompanion/userMovie".
//         * @return the URI for querying all user movies
//         */
//        @NonNull
//        public static Uri buildUriForAllRows() {
//            return CONTENT_URI;
//        }

        /**
         * Returns the user movies node for a user, i.e. "/users/[uid]/userMovies".
         * @param uid the user's uid
         * @return the user movies node for a user
         */
        @Nullable
        public static String getUserMoviesNode(@Nullable String uid) {
            if (uid == null) {
                return null;
            }
            return ROOT_NODE_USERS + "/" + uid + "/" + ROOT_NODE;
        }
    }

    /**
     * Inner class that defines the contents of a ViewAward.
     */
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

//        /**
//         * Create and return a URI for querying all the view awards.
//         * i.e. "content://uk.jumpingmouse.moviecompanion/viewAward".
//         * @return the URI for querying all awards for the movie
//         */
//        @NonNull
//        public static Uri buildUriForAllRowsWithSortOrder(@NonNull String sortOrder) {
//            return CONTENT_URI.buildUpon()
//                    .appendQueryParameter(PARAM_SORT_ORDER, sortOrder)
//                    .build();
//        }

        /**
         * Create and return a URI for querying all the view awards.
         * e.g. "content://uk.jumpingmouse.moviecompanion/viewAward?sortOrder=awardDate DESC&filterWishlist=wishlistAll".
         * @param parameters the parameters affecting the display of the view award list
         * @return the URI for querying all awards for the movie
         */
        @NonNull
        public static Uri buildUriWithParameters(@NonNull ViewAwardListParameters parameters) {
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(PARAM_SORT_ORDER, parameters.getSortOrder())
                    .appendQueryParameter(PARAM_FILTER_GENRE, parameters.getFilterGenre())
                    .appendQueryParameter(PARAM_FILTER_WISHLIST, parameters.getFilterWishlist())
                    .appendQueryParameter(PARAM_FILTER_WATCHED, parameters.getFilterWatched())
                    .appendQueryParameter(PARAM_FILTER_FAVOURITE, parameters.getFilterFavourite())
                    .build();
        }

    }

}
