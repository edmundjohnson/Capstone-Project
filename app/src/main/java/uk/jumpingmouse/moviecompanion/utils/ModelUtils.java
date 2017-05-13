package uk.jumpingmouse.moviecompanion.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;

import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.model.DataContract;

import java.util.Date;

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
     * @return a Movie object corresponding to the ContentValues
     */
    @Nullable
    public static Movie toMovie(@NonNull final ContentValues values) {
        // if any mandatory attribute is missing, return null
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
        int runtime = ModelUtils.toIntOmdbRuntime(strRuntime);
        // Convert released from String to Date
        String strReleased = (String) values.get(DataContract.MovieEntry.COLUMN_RELEASED);
        long released = ModelUtils.toLongOmdbReleased(strReleased);

        return Movie.builder()
                .imdbId(imdbId)
                .title(title)
                .genre((String) values.get(DataContract.MovieEntry.COLUMN_GENRE))
                .runtime(runtime)
                .posterUrl((String) values.get(DataContract.MovieEntry.COLUMN_POSTER_URL))
                .year((String) values.get(DataContract.MovieEntry.COLUMN_YEAR))
                .released(released)
                .build();
    }

    /**
     * Return a movie based on the values of a cursor row.
     * @param cursor a cursor positioned at the required row
     * @return a movie based on the values of the cursor row
     */
    @Nullable
    public static Movie toMovie(@NonNull Cursor cursor) {
        return Movie.builder()
                .imdbId(cursor.getString(DataContract.MovieEntry.COL_IMDB_ID))
                .title(cursor.getString(DataContract.MovieEntry.COL_TITLE))
                .genre(cursor.getString(DataContract.MovieEntry.COL_GENRE))
                .runtime(cursor.getInt(DataContract.MovieEntry.COL_RUNTIME))
                .posterUrl(cursor.getString(DataContract.MovieEntry.COL_POSTER_URL))
                .year(cursor.getString(DataContract.MovieEntry.COL_YEAR))
                .released(cursor.getLong(DataContract.MovieEntry.COL_RELEASED))
                .build();
    }

    /**
     * Returns an OMDb runtime as an int, e.g. returns "144 min" as 144
     * @param omdbRuntime the OMDb runtime, e.g. "144 min"
     * @return the runtime as an int, e.g. 144
     */
    private static int toIntOmdbRuntime(@Nullable String omdbRuntime) {
        if (omdbRuntime != null) {
            String[] split = omdbRuntime.split(" ", 2);
            if (split.length > 0) {
                try {
                    return Integer.decode(split[0]);
                } catch (NumberFormatException e) {
                    Timber.w(String.format(
                            "NumberFormatException while attempting to decode OMDb runtime to int: \"%s\"",
                            omdbRuntime));
                }
            }
        }
        return Movie.RUNTIME_UNKNOWN;
    }

    /**
     * Returns a long representing an OMDb-formatted released date as a number of milliseconds.
     * @param omdbReleased an OMDb released date, formatted as "dd MMM yyyy"
     * @return a long object representing omdbReleased as a number of milliseconds,
     *         or RELEASED_UNKNOWN if omdbReleased could not be converted to a long
     */
    public static long toLongOmdbReleased(@Nullable final String omdbReleased) {
        Date dateReleased = DateUtils.toDateOmdbReleased(omdbReleased);
        if (dateReleased == null) {
            return Movie.RELEASED_UNKNOWN;
        } else {
            return dateReleased.getTime();
        }
    }

}
