package uk.jumpingmouse.moviecompanion.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.data.Award;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.model.DataContract;
import uk.jumpingmouse.omdbapi.OmdbApi;
import uk.jumpingmouse.omdbapi.OmdbMovie;

/**
 * Class for model utilities.
 * @author Edmund Johnson
 */
public final class ModelUtils {

    /**
     * Private default constructor to prevent instantiation.
     */
    private ModelUtils() {
    }

    /**
     * Converts a String movie id to an int movie id and returns it.
     * @param strId the id as a String, e.g. "4061234"
     * @return the movie id, e.g. 4061234
     */
    public static int idToMovieId(String strId) {
        if (strId != null && !strId.isEmpty()) {
            try {
                return Integer.parseInt(strId);
            } catch (Exception e) {
                Timber.w("Exception while converting String movie id to int id", e);
                return Movie.ID_UNKNOWN;
            }
        }
        return Movie.ID_UNKNOWN;
    }

    /**
     * Converts an IMDb id to a movie id and returns it.
     * @param imdbId the IMDb id, e.g. "tt4061234"
     * @return the movie id, e.g. 4061234
     */
    public static int imdbIdToMovieId(String imdbId) {
        if (imdbId != null && imdbId.length() > 2) {
            try {
                return Integer.parseInt(imdbId.substring(2));
            } catch (Exception e) {
                Timber.w("Exception while converting IMDb id to movie id", e);
                return Movie.ID_UNKNOWN;
            }
        }
        return Movie.ID_UNKNOWN;
    }

    /**
     * Converts an OmdbMovie to a Movie and returns it.
     * @param omdbMovie the OmdbMovie
     * @return a Movie corresponding to omdbMovie
     */
    @Nullable
    public static Movie toMovie(OmdbMovie omdbMovie) {
        if (omdbMovie == null) {
            Timber.w("toMovie: omdbMovie is null");
            return null;
        } else if (omdbMovie.getImdbID() == null) {
            Timber.w("toMovie: omdbMovie.imdbId is null");
            return null;
        } else if (omdbMovie.getTitle() == null) {
            Timber.w("toMovie: omdbMovie.title is null");
            return null;
        }

        // Build and return the movie
        int id = imdbIdToMovieId(omdbMovie.getImdbID());
        if (id == Movie.ID_UNKNOWN) {
            Timber.w("toMovie: could not obtain valid id from imdbID: " + omdbMovie.getImdbID());
            return null;
        }
        int runtime = OmdbApi.toIntOmdbRuntime(omdbMovie.getRuntime());
        long released = OmdbApi.toLongOmdbReleased(omdbMovie.getReleased());
        return Movie.builder()
                .id(id)
                .imdbId(omdbMovie.getImdbID())
                .title(omdbMovie.getTitle())
                .genre(omdbMovie.getGenre())
                .runtime(runtime)
                .poster(omdbMovie.getPoster())
                .year(omdbMovie.getYear())
                .released(released)
                .build();
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

        // id
        int id;
        try {
            id = values.getAsInteger(DataContract.MovieEntry.COLUMN_ID);
            if (id <= 0) {
                Timber.w("toMovie: invalid id");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("toMovie: id is null ContentValues", e);
            return null;
        }

        // imdbId
        String imdbId;
        try {
            imdbId = values.getAsString(DataContract.MovieEntry.COLUMN_IMDB_ID);
            if (imdbId == null) {
                Timber.w("toMovie: missing imdbId");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("toMovie: imdbId is null ContentValues", e);
            return null;
        }

        // title
        String title;
        try {
            title = values.getAsString(DataContract.MovieEntry.COLUMN_TITLE);
            if (title == null) {
                Timber.w("toMovie: missing title");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("toMovie: title is null ContentValues", e);
            return null;
        }

        return Movie.builder()
                .id(id)
                .imdbId(imdbId)
                .title(title)
                .year(values.getAsString(DataContract.MovieEntry.COLUMN_YEAR))
                .released(values.getAsLong(DataContract.MovieEntry.COLUMN_RELEASED))
                .runtime(values.getAsInteger(DataContract.MovieEntry.COLUMN_RUNTIME))
                .genre(values.getAsString(DataContract.MovieEntry.COLUMN_GENRE))
                .poster(values.getAsString(DataContract.MovieEntry.COLUMN_POSTER))
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
        final int id = cursor.getInt(DataContract.MovieEntry.COL_ID);
        final String imdbId = cursor.getString(DataContract.MovieEntry.COL_IMDB_ID);
        final String title = cursor.getString(DataContract.MovieEntry.COL_TITLE);
        final String year = cursor.getString(DataContract.MovieEntry.COL_YEAR);
        final String genre = cursor.getString(DataContract.MovieEntry.COL_GENRE);
        final String poster = cursor.getString(DataContract.MovieEntry.COL_POSTER);

        // if the id mandatory attribute is missing, return null
        if (id <= 0) {
            Timber.w("toMovie(Cursor): invalid id");
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
        if (released <= 0) {
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

    /**
     * Creates and returns an Award object based on a set of ContentValues.
     * @param values the ContentValues
     * @return an Award object corresponding to the ContentValues, or null if the ContentValues
     *         do not contain values for any of the fields which are mandatory for Award
     */
    @Nullable
    public static Award toAward(@NonNull final ContentValues values) {
        // if any mandatory attribute is missing, return null

        // id
        String id;
        try {
            // id can be null when inserting a new award
            id = values.getAsString(DataContract.AwardEntry.COLUMN_ID);
            if (id == null) {
                Timber.d("toAward: id is null in ContentValues");
            }
        } catch (NullPointerException e) {
            Timber.e("toAward: id is missing from ContentValues", e);
            return null;
        }

        // movieId
        int movieId;
        try {
            movieId = values.getAsInteger(DataContract.AwardEntry.COLUMN_MOVIE_ID);
            if (movieId <= 0) {
                Timber.w("toAward: invalid movieId in ContentValues");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("toAward: movieId is missing from ContentValues", e);
            return null;
        }

        // awardDate
        String awardDate;
        try {
            awardDate = values.getAsString(DataContract.AwardEntry.COLUMN_AWARD_DATE);
            if (awardDate == null) {
                Timber.w("toAward: awardDate is null in ContentValues");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("toAward: awardDate is missing from ContentValues", e);
            return null;
        }

        // category
        String category;
        try {
            category = values.getAsString(DataContract.AwardEntry.COLUMN_CATEGORY);
            if (category == null) {
                Timber.w("toAward: category is null in ContentValues");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("toAward: category is missing from ContentValues", e);
            return null;
        }

        // review
        String review;
        try {
            review = values.getAsString(DataContract.AwardEntry.COLUMN_REVIEW);
            if (review == null) {
                Timber.w("toAward: review is null in ContentValues");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("toAward: review is missing from ContentValues", e);
            return null;
        }

        // displayOrder
        int displayOrder;
        try {
            displayOrder = values.getAsInteger(DataContract.AwardEntry.COLUMN_DISPLAY_ORDER);
            if (displayOrder <= 0) {
                Timber.w("toAward: invalid displayOrder in ContentValues");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("toAward: displayOrder is missing from ContentValues", e);
            return null;
        }

        return Award.builder()
                .id(id)
                .movieId(movieId)
                .awardDate(awardDate)
                .category(category)
                .review(review)
                .displayOrder(displayOrder)
                .build();
    }

}
