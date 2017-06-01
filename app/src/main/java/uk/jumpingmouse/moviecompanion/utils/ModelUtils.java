package uk.jumpingmouse.moviecompanion.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.moviecompanion.omdb.OmdbManager;

/**
 * Class for model utilities.
 * @author Edmund Johnson
 */
public final class ModelUtils {

    /** Private default constructor to prevent instantiation. */
    private ModelUtils() {
    }

    /**
     * Creates and returns a Movie object based on a set of ContentValues.
     * @param values the ContentValues
     * @return a Movie object corresponding to the ContentValues, or null if the ContentValues
     *         do not contain values for any of the fields which are mandatory for Movie
     */
    @Nullable
    public static Movie toMovie(@NonNull final ContentValues values) {
        // if any mandatory attribute is missing, return null
        String id = (String) values.get(DataContract.MovieEntry.COLUMN_ID);
        if (id == null) {
            Timber.w("toMovie: missing id");
            return null;
        }
        String imdbId = (String) values.get(DataContract.MovieEntry.COLUMN_IMDB_ID);
        if (imdbId == null) {
            Timber.w("toMovie: missing imdbId");
            return null;
        }
        String title = (String) values.get(DataContract.MovieEntry.COLUMN_TITLE);
        if (title == null) {
            Timber.w("toMovie: missing title");
            return null;
        }

        // Convert runtime from String to int
        String strRuntime = (String) values.get(DataContract.MovieEntry.COLUMN_RUNTIME);
        int runtime = getOmdbManager().toIntOmdbRuntime(strRuntime);
        // Convert released from String to Date
        String strReleased = (String) values.get(DataContract.MovieEntry.COLUMN_RELEASED);
        long released = getOmdbManager().toLongOmdbReleased(strReleased);

        return Movie.builder()
                .id(id)
                .imdbId(imdbId)
                .title(title)
                .year((String) values.get(DataContract.MovieEntry.COLUMN_YEAR))
                .released(released)
                .runtime(runtime)
                .genre((String) values.get(DataContract.MovieEntry.COLUMN_GENRE))
                .poster((String) values.get(DataContract.MovieEntry.COLUMN_POSTER))
                .build();
    }

    /**
     * Creates and returns a movie based on the values of a cursor row.
     * @param cursor a cursor positioned at the required row
     * @return a movie based on the values of the cursor row, or null if the cursor row
     *         does not contain values for any of the fields which are mandatory for Movie
     */
    @Nullable
    public static Movie toMovie(@NonNull Cursor cursor) {
        final String id = cursor.getString(DataContract.MovieEntry.COL_ID);
        final String imdbId = cursor.getString(DataContract.MovieEntry.COL_IMDB_ID);
        final String title = cursor.getString(DataContract.MovieEntry.COL_TITLE);
        final String year = cursor.getString(DataContract.MovieEntry.COL_YEAR);
        final String genre = cursor.getString(DataContract.MovieEntry.COL_GENRE);
        final String poster = cursor.getString(DataContract.MovieEntry.COL_POSTER);

        // if the id mandatory attribute is missing, return null
        if (id == null) {
            Timber.w("toMovie(Cursor): missing id");
            return null;
        }
        // if the imdbId mandatory attribute is missing, return null
        if (imdbId == null) {
            Timber.w("toMovie(Cursor): missing imdbId");
            return null;
        }
        // if the title mandatory attribute is missing, return null
        if (title == null) {
            Timber.w("toMovie(Cursor): missing title");
            return null;
        }
        // if the released date is invalid set it to unknown
        long released = cursor.getLong(DataContract.MovieEntry.COL_RELEASED);
        if (released < 0) {
            Timber.w("toMovie(Cursor): invalid released");
            released = Movie.RELEASED_UNKNOWN;
        }
        // if the runtime is invalid set it to unknown
        int runtime = cursor.getInt(DataContract.MovieEntry.COL_RUNTIME);
        if (runtime < 1) {
            Timber.w("toMovie(Cursor): invalid runtime");
            runtime = Movie.RUNTIME_UNKNOWN;
        }

        return Movie.builder()
                .id(id)
                .imdbId(imdbId)
                .title(title)
                .year(year)
                .released(released)
                .runtime(runtime)
                .genre(genre)
                .poster(poster)
                .build();
    }

    /**
     * Returns the list of movies represented by a cursor.
     * @param cursor the cursor
     * @return the list of movies represented by the cursor
     */
    @Nullable
    public static List<Movie> toMovieList(@NonNull Cursor cursor) {
        if (cursor.getCount() == 0) {
            return null;
        }
        List<Movie> movieList = new ArrayList<>();
        // Assume that the cursor is in its initial position before the first row.
        // The next line is safer if the initial position of the cursor is unknown:
        //for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
        while (cursor.moveToNext()) {
            Movie movie = toMovie(cursor);
            if (movie != null) {
                movieList.add(movie);
            }
        }
        return movieList;
    }

    //---------------------------------------------------------------------
    // Getters

    /**
     * Convenience method which returns a reference to an OmdbManager object.
     * @return a reference to an OmdbManager object
     */
    @NonNull
    private static OmdbManager getOmdbManager() {
        return OmdbManager.getInstance();
    }

}
