package uk.jumpingmouse.moviecompanion.utils;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.model.DataContract;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test class for ModelUtils.
 * @author Edmund Johnson
 */
@RunWith(AndroidJUnit4.class)
public class ModelUtilsTest {

    private static final String MOVIE_IMDB_ID = "tt9999991";
    private static final String MOVIE_TITLE = "Test Movie";
    private static final String MOVIE_GENRE = "Drama, Mystery, Romance";
    private static final String MOVIE_RUNTIME_STR = "114 min";
    private static final int MOVIE_RUNTIME_INT = 114;
    @SuppressWarnings("SpellCheckingInspection")
    private static final String MOVIE_POSTER_URL =
            "https://images-na.ssl-images-amazon.com/images/M/MV5BYTBjYjllZTctMTdkMy00MmE5LTllYjctYzg3OTc1MTFjZGYzXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_.jpg";
    private static final String MOVIE_YEAR = "2011";
    private static final String MOVIE_RELEASED_STR = "01 Jun 2011";
    private static final long MOVIE_RELEASED_LNG = DateUtils.toLongOmdbReleased(MOVIE_RELEASED_STR);
    private static final String MOVIE_RELEASED_INVALID = "this is not a date";

    private static final ContentValues VALUES_FIELDS_SET;
    private static final ContentValues VALUES_FIELDS_NULL;
    private static final ContentValues VALUES_FIELDS_INVALID_1;
    private static final ContentValues VALUES_FIELDS_INVALID_2;
    private static final ContentValues VALUES_IMDB_DB_NULL;
    private static final ContentValues VALUES_TITLE_NULL;

    private static final MatrixCursor CURSOR_FIELDS_SET;
    private static final MatrixCursor CURSOR_FIELDS_NULL;
    private static final MatrixCursor CURSOR_FIELDS_INVALID;
    private static final MatrixCursor CURSOR_IMDB_ID_NULL;
    private static final MatrixCursor CURSOR_TITLE_NULL;

    static {
        VALUES_FIELDS_SET = new ContentValues();
        VALUES_FIELDS_SET.put(DataContract.MovieEntry.COLUMN_ID, MOVIE_IMDB_ID);
        VALUES_FIELDS_SET.put(DataContract.MovieEntry.COLUMN_IMDB_ID, MOVIE_IMDB_ID);
        VALUES_FIELDS_SET.put(DataContract.MovieEntry.COLUMN_TITLE, MOVIE_TITLE);
        VALUES_FIELDS_SET.put(DataContract.MovieEntry.COLUMN_GENRE, MOVIE_GENRE);
        VALUES_FIELDS_SET.put(DataContract.MovieEntry.COLUMN_RUNTIME, MOVIE_RUNTIME_STR);
        VALUES_FIELDS_SET.put(DataContract.MovieEntry.COLUMN_POSTER_URL, MOVIE_POSTER_URL);
        VALUES_FIELDS_SET.put(DataContract.MovieEntry.COLUMN_YEAR, MOVIE_YEAR);
        VALUES_FIELDS_SET.put(DataContract.MovieEntry.COLUMN_RELEASED, MOVIE_RELEASED_STR);

        VALUES_FIELDS_NULL = new ContentValues();
        VALUES_FIELDS_NULL.put(DataContract.MovieEntry.COLUMN_ID, "tt9999992");
        VALUES_FIELDS_NULL.put(DataContract.MovieEntry.COLUMN_IMDB_ID, "tt9999992");
        VALUES_FIELDS_NULL.put(DataContract.MovieEntry.COLUMN_TITLE, "Movie Title With Nulls");
        // remaining fields are not set

        VALUES_FIELDS_INVALID_1 = new ContentValues();
        VALUES_FIELDS_INVALID_1.put(DataContract.MovieEntry.COLUMN_ID, MOVIE_IMDB_ID);
        VALUES_FIELDS_INVALID_1.put(DataContract.MovieEntry.COLUMN_IMDB_ID, MOVIE_IMDB_ID);
        VALUES_FIELDS_INVALID_1.put(DataContract.MovieEntry.COLUMN_TITLE, MOVIE_TITLE);
        VALUES_FIELDS_INVALID_1.put(DataContract.MovieEntry.COLUMN_GENRE, MOVIE_GENRE);
        VALUES_FIELDS_INVALID_1.put(DataContract.MovieEntry.COLUMN_RUNTIME, "NotANumber min");
        VALUES_FIELDS_INVALID_1.put(DataContract.MovieEntry.COLUMN_POSTER_URL, MOVIE_POSTER_URL);
        VALUES_FIELDS_INVALID_1.put(DataContract.MovieEntry.COLUMN_YEAR, MOVIE_YEAR);
        VALUES_FIELDS_INVALID_1.put(DataContract.MovieEntry.COLUMN_RELEASED, "not a valid date");

        VALUES_FIELDS_INVALID_2 = new ContentValues();
        VALUES_FIELDS_INVALID_2.put(DataContract.MovieEntry.COLUMN_ID, MOVIE_IMDB_ID);
        VALUES_FIELDS_INVALID_2.put(DataContract.MovieEntry.COLUMN_IMDB_ID, MOVIE_IMDB_ID);
        VALUES_FIELDS_INVALID_2.put(DataContract.MovieEntry.COLUMN_TITLE, MOVIE_TITLE);
        VALUES_FIELDS_INVALID_2.put(DataContract.MovieEntry.COLUMN_GENRE, MOVIE_GENRE);
        // runtime with no tokens tests a different execution path
        VALUES_FIELDS_INVALID_2.put(DataContract.MovieEntry.COLUMN_RUNTIME, "");
        VALUES_FIELDS_INVALID_2.put(DataContract.MovieEntry.COLUMN_POSTER_URL, MOVIE_POSTER_URL);
        VALUES_FIELDS_INVALID_2.put(DataContract.MovieEntry.COLUMN_YEAR, MOVIE_YEAR);
        VALUES_FIELDS_INVALID_2.put(DataContract.MovieEntry.COLUMN_RELEASED, "30 Xyz 2012");

        VALUES_IMDB_DB_NULL = new ContentValues();
        VALUES_IMDB_DB_NULL.put(DataContract.MovieEntry.COLUMN_ID, MOVIE_IMDB_ID);
        // imdbId is not set
        VALUES_IMDB_DB_NULL.put(DataContract.MovieEntry.COLUMN_TITLE, MOVIE_TITLE);
        VALUES_IMDB_DB_NULL.put(DataContract.MovieEntry.COLUMN_GENRE, MOVIE_GENRE);
        VALUES_IMDB_DB_NULL.put(DataContract.MovieEntry.COLUMN_RUNTIME, MOVIE_RUNTIME_STR);
        VALUES_IMDB_DB_NULL.put(DataContract.MovieEntry.COLUMN_POSTER_URL, MOVIE_POSTER_URL);
        VALUES_IMDB_DB_NULL.put(DataContract.MovieEntry.COLUMN_YEAR, MOVIE_YEAR);
        VALUES_IMDB_DB_NULL.put(DataContract.MovieEntry.COLUMN_RELEASED, MOVIE_RELEASED_STR);

        VALUES_TITLE_NULL = new ContentValues();
        VALUES_TITLE_NULL.put(DataContract.MovieEntry.COLUMN_ID, MOVIE_IMDB_ID);
        VALUES_TITLE_NULL.put(DataContract.MovieEntry.COLUMN_IMDB_ID, MOVIE_IMDB_ID);
        // title is not set
        VALUES_TITLE_NULL.put(DataContract.MovieEntry.COLUMN_GENRE, MOVIE_GENRE);
        VALUES_TITLE_NULL.put(DataContract.MovieEntry.COLUMN_RUNTIME, MOVIE_RUNTIME_STR);
        VALUES_TITLE_NULL.put(DataContract.MovieEntry.COLUMN_POSTER_URL, MOVIE_POSTER_URL);
        VALUES_TITLE_NULL.put(DataContract.MovieEntry.COLUMN_YEAR, MOVIE_YEAR);
        VALUES_TITLE_NULL.put(DataContract.MovieEntry.COLUMN_RELEASED, MOVIE_RELEASED_STR);

        CURSOR_FIELDS_SET = new MatrixCursor(DataContract.MovieEntry.getAllColumns());
        CURSOR_FIELDS_SET.addRow(new Object[] {
                // This must match the order of columns in DataContract.MovieEntry.getAllColumns().
                // We are using imdbId as the _id, so it is repeated.
                MOVIE_IMDB_ID,
                MOVIE_IMDB_ID,
                MOVIE_TITLE,
                MOVIE_GENRE,
                MOVIE_RUNTIME_INT,
                MOVIE_POSTER_URL,
                MOVIE_YEAR,
                MOVIE_RELEASED_LNG
        });

        CURSOR_FIELDS_NULL = new MatrixCursor(DataContract.MovieEntry.getAllColumns());
        CURSOR_FIELDS_NULL.addRow(new Object[] {
                // This must match the order of columns in DataContract.MovieEntry.getAllColumns().
                // We are using imdbId as the _id, so it is repeated.
                "tt9999992",
                "tt9999992",
                "Movie Title With Nulls",
                null,
                Movie.RUNTIME_UNKNOWN,
                null,
                null,
                Movie.RELEASED_UNKNOWN
        });

        CURSOR_FIELDS_INVALID = new MatrixCursor(DataContract.MovieEntry.getAllColumns());
        CURSOR_FIELDS_INVALID.addRow(new Object[] {
                // This must match the order of columns in DataContract.MovieEntry.getAllColumns().
                // We are using imdbId as the _id, so it is repeated.
                MOVIE_IMDB_ID,
                MOVIE_IMDB_ID,
                MOVIE_TITLE,
                MOVIE_GENRE,
                -2,
                MOVIE_POSTER_URL,
                MOVIE_YEAR,
                -2
        });

        CURSOR_IMDB_ID_NULL = new MatrixCursor(DataContract.MovieEntry.getAllColumns());
        CURSOR_IMDB_ID_NULL.addRow(new Object[] {
                // This must match the order of columns in DataContract.MovieEntry.getAllColumns().
                // We are using imdbId as the _id, so it is repeated.
                MOVIE_IMDB_ID,
                null,
                MOVIE_TITLE,
                MOVIE_GENRE,
                MOVIE_RUNTIME_INT,
                MOVIE_POSTER_URL,
                MOVIE_YEAR,
                MOVIE_RELEASED_LNG
        });

        CURSOR_TITLE_NULL = new MatrixCursor(DataContract.MovieEntry.getAllColumns());
        CURSOR_TITLE_NULL.addRow(new Object[] {
                // This must match the order of columns in DataContract.MovieEntry.getAllColumns().
                // We are using imdbId as the _id, so it is repeated.
                MOVIE_IMDB_ID,
                MOVIE_IMDB_ID,
                null,
                MOVIE_GENRE,
                MOVIE_RUNTIME_INT,
                MOVIE_POSTER_URL,
                MOVIE_YEAR,
                MOVIE_RELEASED_LNG
        });

    }

    @Test
    public void toMovieContentValues() {
        Movie movie;

        // toMovie(ContentValues) works correctly when all fields are set
        movie = ModelUtils.toMovie(VALUES_FIELDS_SET);
        assertNotNull(movie);
        assertEquals(movie.imdbId(), MOVIE_IMDB_ID);
        assertEquals(movie.title(), MOVIE_TITLE);
        assertEquals(movie.genre(), MOVIE_GENRE);
        assertEquals(movie.runtime(), MOVIE_RUNTIME_INT);
        assertEquals(movie.posterUrl(), MOVIE_POSTER_URL);
        assertEquals(movie.year(), MOVIE_YEAR);
        assertEquals(movie.released(), MOVIE_RELEASED_LNG);

        // toMovie(ContentValues) works correctly when non-mandatory fields are not set
        movie = ModelUtils.toMovie(VALUES_FIELDS_NULL);
        assertNotNull(movie);
        assertEquals(movie.imdbId(), "tt9999992");
        assertEquals(movie.title(), "Movie Title With Nulls");
        assertNull(movie.genre());
        assertEquals(movie.runtime(), Movie.RUNTIME_UNKNOWN);
        assertNull(movie.posterUrl());
        assertNull(movie.year());
        assertEquals(movie.released(), Movie.RELEASED_UNKNOWN);

        // toMovie(ContentValues) works correctly when fields have invalid values
        movie = ModelUtils.toMovie(VALUES_FIELDS_INVALID_1);
        assertNotNull(movie);
        assertEquals(movie.imdbId(), MOVIE_IMDB_ID);
        assertEquals(movie.title(), MOVIE_TITLE);
        assertEquals(movie.genre(), MOVIE_GENRE);
        assertEquals(movie.runtime(), Movie.RUNTIME_UNKNOWN);
        assertEquals(movie.posterUrl(), MOVIE_POSTER_URL);
        assertEquals(movie.year(), MOVIE_YEAR);
        assertEquals(movie.released(), Movie.RELEASED_UNKNOWN);

        // toMovie(ContentValues) works correctly when fields have invalid values
        movie = ModelUtils.toMovie(VALUES_FIELDS_INVALID_2);
        assertNotNull(movie);
        assertEquals(movie.imdbId(), MOVIE_IMDB_ID);
        assertEquals(movie.title(), MOVIE_TITLE);
        assertEquals(movie.genre(), MOVIE_GENRE);
        assertEquals(movie.runtime(), Movie.RUNTIME_UNKNOWN);
        assertEquals(movie.posterUrl(), MOVIE_POSTER_URL);
        assertEquals(movie.year(), MOVIE_YEAR);
        assertEquals(movie.released(), Movie.RELEASED_UNKNOWN);

        // toMovie(ContentValues) returns null when mandatory field imdbId is not set in values
        assertNull(ModelUtils.toMovie(VALUES_IMDB_DB_NULL));

        // toMovie(ContentValues) returns null when mandatory field title is not set in values
        assertNull(ModelUtils.toMovie(VALUES_TITLE_NULL));
    }

    @Test
    public void toMovieCursor() {
        Movie movie;

        // toMovie(Cursor) works correctly when all fields are set
        CURSOR_FIELDS_SET.moveToFirst();
        movie = ModelUtils.toMovie(CURSOR_FIELDS_SET);
        assertNotNull(movie);
        assertEquals(movie.imdbId(), MOVIE_IMDB_ID);
        assertEquals(movie.title(), MOVIE_TITLE);
        assertEquals(movie.genre(), MOVIE_GENRE);
        assertEquals(movie.runtime(), MOVIE_RUNTIME_INT);
        assertEquals(movie.posterUrl(), MOVIE_POSTER_URL);
        assertEquals(movie.year(), MOVIE_YEAR);
        assertEquals(movie.released(), MOVIE_RELEASED_LNG);

        // toMovie(Cursor) works correctly when non-mandatory fields are not set
        CURSOR_FIELDS_NULL.moveToFirst();
        movie = ModelUtils.toMovie(CURSOR_FIELDS_NULL);
        assertNotNull(movie);
        assertEquals(movie.imdbId(), "tt9999992");
        assertEquals(movie.title(), "Movie Title With Nulls");
        assertNull(movie.genre());
        assertEquals(movie.runtime(), Movie.RUNTIME_UNKNOWN);
        assertNull(movie.posterUrl());
        assertNull(movie.year());
        assertEquals(movie.released(), Movie.RELEASED_UNKNOWN);

        // toMovie(Cursor) works correctly when fields have invalid values
        CURSOR_FIELDS_INVALID.moveToFirst();
        movie = ModelUtils.toMovie(CURSOR_FIELDS_INVALID);
        assertNotNull(movie);
        assertEquals(movie.imdbId(), MOVIE_IMDB_ID);
        assertEquals(movie.title(), MOVIE_TITLE);
        assertEquals(movie.genre(), MOVIE_GENRE);
        assertEquals(movie.runtime(), Movie.RUNTIME_UNKNOWN);
        assertEquals(movie.posterUrl(), MOVIE_POSTER_URL);
        assertEquals(movie.year(), MOVIE_YEAR);
        assertEquals(movie.released(), Movie.RELEASED_UNKNOWN);

        // toMovie(Cursor) returns null when mandatory field imdbId is not set in cursor row
        CURSOR_IMDB_ID_NULL.moveToFirst();
        assertNull(ModelUtils.toMovie(CURSOR_IMDB_ID_NULL));

        // toMovie(Cursor) returns null when mandatory field title is not set in cursor row
        CURSOR_TITLE_NULL.moveToFirst();
        assertNull(ModelUtils.toMovie(CURSOR_TITLE_NULL));

    }

}