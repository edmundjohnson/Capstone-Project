package uk.jumpingmouse.moviecompanion.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;

import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.model.DataContract;

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
        long released = DateUtils.toLongOmdbReleased(strReleased);

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
     * Creates and returns a movie based on the values of a cursor row.
     * @param cursor a cursor positioned at the required row
     * @return a movie based on the values of the cursor row, or null if the cursor row
     *         does not contain values for any of the fields which are mandatory for Movie
     */
    @Nullable
    public static Movie toMovie(@NonNull Cursor cursor) {
        final String imdbId = cursor.getString(DataContract.MovieEntry.COL_IMDB_ID);
        final String title = cursor.getString(DataContract.MovieEntry.COL_TITLE);
        final String genre = cursor.getString(DataContract.MovieEntry.COL_GENRE);
        final String posterUrl = cursor.getString(DataContract.MovieEntry.COL_POSTER_URL);
        final String year = cursor.getString(DataContract.MovieEntry.COL_YEAR);

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
        // if the runtime is invalid set it to unknown
        int runtime = cursor.getInt(DataContract.MovieEntry.COL_RUNTIME);
        if (runtime < 1) {
            Timber.w("toMovie(Cursor): invalid runtime");
            runtime = Movie.RUNTIME_UNKNOWN;
        }
        // if the release date is invalid set it to unknown
        long released = cursor.getLong(DataContract.MovieEntry.COL_RELEASED);
        if (released < 0) {
            Timber.w("toMovie(Cursor): invalid released");
            released = Movie.RELEASED_UNKNOWN;
        }

        return Movie.builder()
                .imdbId(imdbId)
                .title(title)
                .genre(genre)
                .runtime(runtime)
                .posterUrl(posterUrl)
                .year(year)
                .released(released)
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

}
