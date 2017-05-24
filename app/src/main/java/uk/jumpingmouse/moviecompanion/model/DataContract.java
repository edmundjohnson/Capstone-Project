package uk.jumpingmouse.moviecompanion.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

/**
 * Class which defines the contract between the model (database) and view layers.
 * @author Edmund Johnson
 */
public final class DataContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "uk.jumpingmouse.moviecompanion";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URIs which apps will use to contact
     * the content provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * relative paths appended to base content URI.
     * For instance, "content://uk.jumpingmouse.moviecompanion/movie/" is a valid path for
     * looking at movie data.
     * "content://uk.jumpingmouse.moviecompanion/give-me-root/" will fail, as the
     * ContentProvider hasn't been given any information on what to do with "give-me-root".
     */
    public static final String URI_PATH_MOVIE = "movie";
    //public static final String URI_PATH_AWARD = "award";
    //public static final String URI_PATH_USER = "user";

    // Query parameters
    public static final String PARAM_ALL = "all";

    // Values for sort direction (PARAM_SORT_DIRECTION)
    public static final String SORT_DIRECTION_ASC = "ASC";
    public static final String SORT_DIRECTION_DESC = "DESC";

    // Query parameters which are not columns
//        public static final String PARAM_LIMIT = "limit";
//        public static final String PARAM_EXCLUDINGNEWEST = "excludingnewest";
//        public static final String PARAM_SORT_COLUMN = "sortColumn";
//        public static final String PARAM_SORT_DIRECTION = "sortDirection";

//    // Default values for limit queries
//    private static final int MAX_ROWS_DEFAULT = 0;
//    private static final int OFFSET_DEFAULT = 0;

    /** Private default constructor to prevent instantiation. */
    private DataContract() {
    }

    /** Inner class that defines the contents of the movie node. */
    public static final class MovieEntry implements BaseColumns {

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH_MOVIE;

        // Database

        public static final String ROOT_NODE = "movies";

        public static final String COLUMN_ID = MovieEntry._ID;
        public static final String COLUMN_IMDB_ID = "imdbId";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_GENRE = "genre";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_POSTER_URL = "posterUrl";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_RELEASED = "released";

        // Note: arrays are mutable, so ALL_COLUMNS should not be public.
        // See Effective Java, Item 13.
        private static final String[] ALL_COLUMNS = {
                COLUMN_ID,
                COLUMN_IMDB_ID,
                COLUMN_TITLE,
                COLUMN_GENRE,
                COLUMN_RUNTIME,
                COLUMN_POSTER_URL,
                COLUMN_YEAR,
                COLUMN_RELEASED
        };
        public static String[] getAllColumns() {
            return ALL_COLUMNS.clone();
        }

        public static final int COL_ID = 0;
        public static final int COL_IMDB_ID = COL_ID + 1;
        public static final int COL_TITLE = COL_IMDB_ID + 1;
        public static final int COL_GENRE = COL_TITLE + 1;
        public static final int COL_RUNTIME = COL_GENRE + 1;
        public static final int COL_POSTER_URL = COL_RUNTIME + 1;
        public static final int COL_YEAR = COL_POSTER_URL + 1;
        public static final int COL_RELEASED = COL_YEAR + 1;

        // URIs

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(URI_PATH_MOVIE).build();

        /**
         * Build and return the URI for a movie identified by its id.
         * e.g. "content://uk.jumpingmouse.moviecompanion/movie/tt4016934"
         * @param id the id of the movie, e.g. "tt4016934"
         * @return the URI for obtaining the specific movie
         */
        @NonNull
        public static Uri buildUriMovieId(final String id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(id)
                    .build();
        }

        /**
         * Create and return a URI for querying all movies.
         * i.e. "content://uk.jumpingmouse.moviecompanion/movie/all".
         * @return the URI for querying all movies
         */
        @NonNull
        public static Uri buildUriMovieAll() {
            return CONTENT_URI.buildUpon()
                    .appendPath(PARAM_ALL)
                    .build();
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
//        public static Uri buildUriMovieLimit(final int limit) {
//            return CONTENT_URI.buildUpon()
//                    .appendPath(PARAM_LIMIT)
//                    .appendPath(String.valueOf(limit))
//                    .build();
//        }

    }

}
