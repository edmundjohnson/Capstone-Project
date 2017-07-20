package uk.jumpingmouse.moviecompanion.utils;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import uk.jumpingmouse.moviecompanion.AndroidTestUtils;
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

    private static final int MOVIE_ID = 9999991;
    private static final String MOVIE_IMDB_ID = "tt9999991";
    private static final String MOVIE_TITLE = "Test Movie";
    private static final String MOVIE_YEAR = "2011";
    private static final String MOVIE_RATED = "R";
    private static final String MOVIE_RELEASED_OMDB = "01 Jun 2011";
    private static final long MOVIE_RELEASED = AndroidTestUtils.toLongOmdbReleased(MOVIE_RELEASED_OMDB);
    private static final String MOVIE_RELEASED_INVALID = "this is not a date";
    //private static final String MOVIE_RUNTIME_OMDB = "114 min";
    private static final int MOVIE_RUNTIME = 114;
    private static final String MOVIE_GENRE = "Drama, Mystery, Romance";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String MOVIE_POSTER =
            "https://images-na.ssl-images-amazon.com/images/M/MV5BYTBjYjllZTctMTdkMy00MmE5LTllYjctYzg3OTc1MTFjZGYzXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_.jpg";

    private static final ContentValues VALUES_FIELDS_VALID;
    private static final ContentValues VALUES_FIELDS_NULL;
    private static final ContentValues VALUES_FIELDS_INVALID_1;
    private static final ContentValues VALUES_FIELDS_INVALID_2;
    private static final ContentValues VALUES_ID_NULL;
    private static final ContentValues VALUES_IMDB_ID_NULL;
    private static final ContentValues VALUES_TITLE_NULL;

    private static final MatrixCursor CURSOR_FIELDS_VALID;
    private static final MatrixCursor CURSOR_FIELDS_NULL;
    private static final MatrixCursor CURSOR_FIELDS_INVALID;
    private static final MatrixCursor CURSOR_ID_NULL;
    private static final MatrixCursor CURSOR_IMDB_ID_NULL;
    private static final MatrixCursor CURSOR_TITLE_NULL;

    static {
        VALUES_FIELDS_VALID = new ContentValues();
        VALUES_FIELDS_VALID.put(DataContract.MovieEntry.COLUMN_ID, MOVIE_ID);
        VALUES_FIELDS_VALID.put(DataContract.MovieEntry.COLUMN_IMDB_ID, MOVIE_IMDB_ID);
        VALUES_FIELDS_VALID.put(DataContract.MovieEntry.COLUMN_TITLE, MOVIE_TITLE);
        VALUES_FIELDS_VALID.put(DataContract.MovieEntry.COLUMN_YEAR, MOVIE_YEAR);
        VALUES_FIELDS_VALID.put(DataContract.MovieEntry.COLUMN_RELEASED, MOVIE_RELEASED);
        VALUES_FIELDS_VALID.put(DataContract.MovieEntry.COLUMN_RUNTIME, MOVIE_RUNTIME);
        VALUES_FIELDS_VALID.put(DataContract.MovieEntry.COLUMN_GENRE, MOVIE_GENRE);
        VALUES_FIELDS_VALID.put(DataContract.MovieEntry.COLUMN_POSTER, MOVIE_POSTER);

        VALUES_FIELDS_NULL = new ContentValues();
        VALUES_FIELDS_NULL.put(DataContract.MovieEntry.COLUMN_ID, 9999992);
        VALUES_FIELDS_NULL.put(DataContract.MovieEntry.COLUMN_IMDB_ID, "tt9999992");
        VALUES_FIELDS_NULL.put(DataContract.MovieEntry.COLUMN_TITLE, "Movie Title With Nulls");
        VALUES_FIELDS_NULL.put(DataContract.MovieEntry.COLUMN_RELEASED, Movie.RELEASED_UNKNOWN);
        VALUES_FIELDS_NULL.put(DataContract.MovieEntry.COLUMN_RUNTIME, Movie.RUNTIME_UNKNOWN);
        // remaining fields are not set

        VALUES_FIELDS_INVALID_1 = new ContentValues();
        VALUES_FIELDS_INVALID_1.put(DataContract.MovieEntry.COLUMN_ID, MOVIE_ID);
        VALUES_FIELDS_INVALID_1.put(DataContract.MovieEntry.COLUMN_IMDB_ID, MOVIE_IMDB_ID);
        VALUES_FIELDS_INVALID_1.put(DataContract.MovieEntry.COLUMN_TITLE, MOVIE_TITLE);
        VALUES_FIELDS_INVALID_1.put(DataContract.MovieEntry.COLUMN_YEAR, MOVIE_YEAR);
        VALUES_FIELDS_INVALID_1.put(DataContract.MovieEntry.COLUMN_RELEASED, -23L);
        VALUES_FIELDS_INVALID_1.put(DataContract.MovieEntry.COLUMN_RUNTIME, -14);
        VALUES_FIELDS_INVALID_1.put(DataContract.MovieEntry.COLUMN_GENRE, MOVIE_GENRE);
        VALUES_FIELDS_INVALID_1.put(DataContract.MovieEntry.COLUMN_POSTER, MOVIE_POSTER);

        VALUES_FIELDS_INVALID_2 = new ContentValues();
        VALUES_FIELDS_INVALID_2.put(DataContract.MovieEntry.COLUMN_ID, MOVIE_ID);
        VALUES_FIELDS_INVALID_2.put(DataContract.MovieEntry.COLUMN_IMDB_ID, MOVIE_IMDB_ID);
        VALUES_FIELDS_INVALID_2.put(DataContract.MovieEntry.COLUMN_TITLE, MOVIE_TITLE);
        VALUES_FIELDS_INVALID_2.put(DataContract.MovieEntry.COLUMN_YEAR, MOVIE_YEAR);
        VALUES_FIELDS_INVALID_2.put(DataContract.MovieEntry.COLUMN_RELEASED, 0);
        VALUES_FIELDS_INVALID_2.put(DataContract.MovieEntry.COLUMN_RUNTIME, 0);
        VALUES_FIELDS_INVALID_2.put(DataContract.MovieEntry.COLUMN_GENRE, MOVIE_GENRE);
        VALUES_FIELDS_INVALID_2.put(DataContract.MovieEntry.COLUMN_POSTER, MOVIE_POSTER);

        VALUES_ID_NULL = new ContentValues();
        // id is not set
        VALUES_ID_NULL.put(DataContract.MovieEntry.COLUMN_IMDB_ID, MOVIE_IMDB_ID);
        VALUES_ID_NULL.put(DataContract.MovieEntry.COLUMN_TITLE, MOVIE_TITLE);
        VALUES_ID_NULL.put(DataContract.MovieEntry.COLUMN_YEAR, MOVIE_YEAR);
        VALUES_ID_NULL.put(DataContract.MovieEntry.COLUMN_RELEASED, MOVIE_RELEASED);
        VALUES_ID_NULL.put(DataContract.MovieEntry.COLUMN_RUNTIME, MOVIE_RUNTIME);
        VALUES_ID_NULL.put(DataContract.MovieEntry.COLUMN_GENRE, MOVIE_GENRE);
        VALUES_ID_NULL.put(DataContract.MovieEntry.COLUMN_POSTER, MOVIE_POSTER);

        VALUES_IMDB_ID_NULL = new ContentValues();
        VALUES_IMDB_ID_NULL.put(DataContract.MovieEntry.COLUMN_ID, MOVIE_ID);
        // imdbId is not set
        VALUES_IMDB_ID_NULL.put(DataContract.MovieEntry.COLUMN_TITLE, MOVIE_TITLE);
        VALUES_IMDB_ID_NULL.put(DataContract.MovieEntry.COLUMN_YEAR, MOVIE_YEAR);
        VALUES_IMDB_ID_NULL.put(DataContract.MovieEntry.COLUMN_RELEASED, MOVIE_RELEASED);
        VALUES_IMDB_ID_NULL.put(DataContract.MovieEntry.COLUMN_RUNTIME, MOVIE_RUNTIME);
        VALUES_IMDB_ID_NULL.put(DataContract.MovieEntry.COLUMN_GENRE, MOVIE_GENRE);
        VALUES_IMDB_ID_NULL.put(DataContract.MovieEntry.COLUMN_POSTER, MOVIE_POSTER);

        VALUES_TITLE_NULL = new ContentValues();
        VALUES_TITLE_NULL.put(DataContract.MovieEntry.COLUMN_ID, MOVIE_ID);
        VALUES_TITLE_NULL.put(DataContract.MovieEntry.COLUMN_IMDB_ID, MOVIE_IMDB_ID);
        // title is not set
        VALUES_TITLE_NULL.put(DataContract.MovieEntry.COLUMN_YEAR, MOVIE_YEAR);
        VALUES_TITLE_NULL.put(DataContract.MovieEntry.COLUMN_RELEASED, MOVIE_RELEASED);
        VALUES_TITLE_NULL.put(DataContract.MovieEntry.COLUMN_RUNTIME, MOVIE_RUNTIME);
        VALUES_TITLE_NULL.put(DataContract.MovieEntry.COLUMN_GENRE, MOVIE_GENRE);
        VALUES_TITLE_NULL.put(DataContract.MovieEntry.COLUMN_POSTER, MOVIE_POSTER);

        CURSOR_FIELDS_VALID = new MatrixCursor(DataContract.MovieEntry.getAllColumns());
        CURSOR_FIELDS_VALID.addRow(new Object[]{
                // This must match the order of columns in DataContract.MovieEntry.getAllColumns().
                MOVIE_ID,
                MOVIE_IMDB_ID,
                MOVIE_TITLE,
                MOVIE_YEAR,
                MOVIE_RELEASED,
                MOVIE_RUNTIME,
                MOVIE_GENRE,
                MOVIE_POSTER
        });

        CURSOR_FIELDS_NULL = new MatrixCursor(DataContract.MovieEntry.getAllColumns());
        CURSOR_FIELDS_NULL.addRow(new Object[]{
                // This must match the order of columns in DataContract.MovieEntry.getAllColumns().
                9999992,
                "tt9999992",
                "Movie Title With Nulls",
                null,
                Movie.RELEASED_UNKNOWN,
                Movie.RUNTIME_UNKNOWN,
                null,
                null
        });

        CURSOR_FIELDS_INVALID = new MatrixCursor(DataContract.MovieEntry.getAllColumns());
        CURSOR_FIELDS_INVALID.addRow(new Object[]{
                // This must match the order of columns in DataContract.MovieEntry.getAllColumns().
                MOVIE_ID,
                MOVIE_IMDB_ID,
                MOVIE_TITLE,
                MOVIE_YEAR,
                -2,
                -2,
                MOVIE_GENRE,
                MOVIE_POSTER
        });

        CURSOR_ID_NULL = new MatrixCursor(DataContract.MovieEntry.getAllColumns());
        CURSOR_ID_NULL.addRow(new Object[]{
                // This must match the order of columns in DataContract.MovieEntry.getAllColumns().
                null,
                MOVIE_IMDB_ID,
                MOVIE_TITLE,
                MOVIE_YEAR,
                MOVIE_RELEASED,
                MOVIE_RUNTIME,
                MOVIE_GENRE,
                MOVIE_POSTER
        });

        CURSOR_IMDB_ID_NULL = new MatrixCursor(DataContract.MovieEntry.getAllColumns());
        CURSOR_IMDB_ID_NULL.addRow(new Object[]{
                // This must match the order of columns in DataContract.MovieEntry.getAllColumns().
                MOVIE_ID,
                null,
                MOVIE_TITLE,
                MOVIE_YEAR,
                MOVIE_RELEASED,
                MOVIE_RUNTIME,
                MOVIE_GENRE,
                MOVIE_POSTER
        });

        CURSOR_TITLE_NULL = new MatrixCursor(DataContract.MovieEntry.getAllColumns());
        CURSOR_TITLE_NULL.addRow(new Object[]{
                // This must match the order of columns in DataContract.MovieEntry.getAllColumns().
                MOVIE_ID,
                MOVIE_IMDB_ID,
                null,
                MOVIE_YEAR,
                MOVIE_RELEASED,
                MOVIE_RUNTIME,
                MOVIE_GENRE,
                MOVIE_POSTER
        });

    }

    @Test
    public void toMovieContentValues() {
        Movie movie;

        // newMovie(ContentValues) works correctly when all fields are set
        movie = ModelUtils.newMovie(VALUES_FIELDS_VALID);
        assertNotNull(movie);
        assertEquals(movie.getId(), MOVIE_ID);
        assertEquals(movie.getImdbId(), MOVIE_IMDB_ID);
        assertEquals(movie.getTitle(), MOVIE_TITLE);
        assertEquals(movie.getYear(), MOVIE_YEAR);
        assertEquals(movie.getReleased(), MOVIE_RELEASED);
        assertEquals(movie.getRuntime(), MOVIE_RUNTIME);
        assertEquals(movie.getGenre(), MOVIE_GENRE);
        assertEquals(movie.getPoster(), MOVIE_POSTER);

        // newMovie(ContentValues) works correctly when non-mandatory fields are not set
        movie = ModelUtils.newMovie(VALUES_FIELDS_NULL);
        assertNotNull(movie);
        assertEquals(movie.getId(), 9999992);
        assertEquals(movie.getImdbId(), "tt9999992");
        assertEquals(movie.getTitle(), "Movie Title With Nulls");
        assertNull(movie.getYear());
        assertEquals(movie.getReleased(), Movie.RELEASED_UNKNOWN);
        assertEquals(movie.getRuntime(), Movie.RUNTIME_UNKNOWN);
        assertNull(movie.getGenre());
        assertNull(movie.getPoster());

        // newMovie(ContentValues) works correctly when fields have invalid values
        movie = ModelUtils.newMovie(VALUES_FIELDS_INVALID_1);
        assertNotNull(movie);
        assertEquals(movie.getId(), MOVIE_ID);
        assertEquals(movie.getImdbId(), MOVIE_IMDB_ID);
        assertEquals(movie.getTitle(), MOVIE_TITLE);
        assertEquals(movie.getYear(), MOVIE_YEAR);
        assertEquals(movie.getReleased(), -23L);
        assertEquals(movie.getRuntime(), -14);
        assertEquals(movie.getGenre(), MOVIE_GENRE);
        assertEquals(movie.getPoster(), MOVIE_POSTER);

        // newMovie(ContentValues) works correctly when fields have invalid values
        movie = ModelUtils.newMovie(VALUES_FIELDS_INVALID_2);
        assertNotNull(movie);
        assertEquals(movie.getId(), MOVIE_ID);
        assertEquals(movie.getImdbId(), MOVIE_IMDB_ID);
        assertEquals(movie.getTitle(), MOVIE_TITLE);
        assertEquals(movie.getYear(), MOVIE_YEAR);
        assertEquals(movie.getReleased(), 0);
        assertEquals(movie.getRuntime(), 0);
        assertEquals(movie.getGenre(), MOVIE_GENRE);
        assertEquals(movie.getPoster(), MOVIE_POSTER);

        // newMovie(ContentValues) returns null when mandatory field id is not set in values
        assertNull(ModelUtils.newMovie(VALUES_ID_NULL));

        // newMovie(ContentValues) returns null when mandatory field imdbId is not set in values
        assertNull(ModelUtils.newMovie(VALUES_IMDB_ID_NULL));

        // newMovie(ContentValues) returns null when mandatory field title is not set in values
        assertNull(ModelUtils.newMovie(VALUES_TITLE_NULL));
    }

    @Test
    public void newMovieCursor() {
        Movie movie;

        // newMovie(Cursor) works correctly when all fields are set
        CURSOR_FIELDS_VALID.moveToFirst();
        movie = ModelUtils.newMovie(CURSOR_FIELDS_VALID);
        assertNotNull(movie);
        assertEquals(movie.getId(), MOVIE_ID);
        assertEquals(movie.getImdbId(), MOVIE_IMDB_ID);
        assertEquals(movie.getTitle(), MOVIE_TITLE);
        assertEquals(movie.getYear(), MOVIE_YEAR);
        assertEquals(movie.getRated(), MOVIE_RATED);
        assertEquals(movie.getReleased(), MOVIE_RELEASED);
        assertEquals(movie.getRuntime(), MOVIE_RUNTIME);
        assertEquals(movie.getGenre(), MOVIE_GENRE);
        assertEquals(movie.getPoster(), MOVIE_POSTER);

        // newMovie(Cursor) works correctly when non-mandatory fields are not set
        CURSOR_FIELDS_NULL.moveToFirst();
        movie = ModelUtils.newMovie(CURSOR_FIELDS_NULL);
        assertNotNull(movie);
        assertEquals(movie.getId(), 9999992);
        assertEquals(movie.getImdbId(), "tt9999992");
        assertEquals(movie.getTitle(), "Movie Title With Nulls");
        assertNull(movie.getYear());
        assertEquals(movie.getReleased(), Movie.RELEASED_UNKNOWN);
        assertEquals(movie.getRuntime(), Movie.RUNTIME_UNKNOWN);
        assertNull(movie.getGenre());
        assertNull(movie.getPoster());

        // newMovie(Cursor) works correctly when fields have invalid values
        CURSOR_FIELDS_INVALID.moveToFirst();
        movie = ModelUtils.newMovie(CURSOR_FIELDS_INVALID);
        assertNotNull(movie);
        assertEquals(movie.getId(), MOVIE_ID);
        assertEquals(movie.getImdbId(), MOVIE_IMDB_ID);
        assertEquals(movie.getTitle(), MOVIE_TITLE);
        assertEquals(movie.getYear(), MOVIE_YEAR);
        assertEquals(movie.getReleased(), Movie.RELEASED_UNKNOWN);
        assertEquals(movie.getRuntime(), Movie.RUNTIME_UNKNOWN);
        assertEquals(movie.getGenre(), MOVIE_GENRE);
        assertEquals(movie.getPoster(), MOVIE_POSTER);

        // newMovie(Cursor) returns null when mandatory field id is not set in cursor row
        CURSOR_ID_NULL.moveToFirst();
        assertNull(ModelUtils.newMovie(CURSOR_ID_NULL));

        // newMovie(Cursor) returns null when mandatory field imdbId is not set in cursor row
        CURSOR_IMDB_ID_NULL.moveToFirst();
        assertNull(ModelUtils.newMovie(CURSOR_IMDB_ID_NULL));

        // newMovie(Cursor) returns null when mandatory field title is not set in cursor row
        CURSOR_TITLE_NULL.moveToFirst();
        assertNull(ModelUtils.newMovie(CURSOR_TITLE_NULL));
    }

    @Test
    public void toMovieList() {
        List<Movie> movieList;
        Movie movie;
        MatrixCursor cursor;

        // toMovieList(Cursor) returns null when cursor has no rows
        cursor = new MatrixCursor(DataContract.MovieEntry.getAllColumns());
        movieList = ModelUtils.toMovieList(cursor);
        assertNull(movieList);
        getAndroidTestUtils().closeCursor(cursor);

        // toMovieList(Cursor) does not include elements for cursor rows which cannot
        // be converted to Movies
        cursor = new MatrixCursor(DataContract.MovieEntry.getAllColumns());
        cursor.addRow(new Object[] {
                // This must match the order of columns in DataContract.MovieEntry.getAllColumns().
                // We are using imdbId as the _id, so it is repeated.
                null,
                null,
                MOVIE_TITLE,
                MOVIE_YEAR,
                MOVIE_RELEASED,
                MOVIE_RUNTIME,
                MOVIE_GENRE,
                MOVIE_POSTER
        });
        movieList = ModelUtils.toMovieList(cursor);
        assertNotNull(movieList);
        assertEquals(0, movieList.size());
        getAndroidTestUtils().closeCursor(cursor);

        // toMovieList(Cursor) converts each valid cursor row into a Movie and adds it to the list
        cursor = new MatrixCursor(DataContract.MovieEntry.getAllColumns());
        cursor.addRow(new Object[] {
                // This must match the order of columns in DataContract.MovieEntry.getAllColumns().
                // We are using imdbId as the _id, so it is repeated.
                111,
                "imdbId1",
                "Title 1",
                "2011",
                MOVIE_RELEASED,
                MOVIE_RUNTIME,
                "Genre1",
                "poster1.jpg"
        });
        cursor.addRow(new Object[] {
                // This must match the order of columns in DataContract.MovieEntry.getAllColumns().
                // We are using imdbId as the _id, so it is repeated.
                222,
                "imdbId2",
                "Title 2",
                "2011",
                MOVIE_RELEASED,
                MOVIE_RUNTIME,
                "Genre2",
                "poster2.jpg"
        });
        movieList = ModelUtils.toMovieList(cursor);
        assertNotNull(movieList);
        assertEquals(2, movieList.size());
        movie = movieList.get(0);
        assertNotNull(movie);
        assertEquals(111, movie.getId());
        assertEquals("imdbId1", movie.getImdbId());
        assertEquals("Title 1", movie.getTitle());
        movie = movieList.get(1);
        assertNotNull(movie);
        assertEquals(222, movie.getId());
        assertEquals("imdbId2", movie.getImdbId());
        assertEquals("Title 2", movie.getTitle());
        getAndroidTestUtils().closeCursor(cursor);
    }

    //---------------------------------------------------------------------
    // Getters

    /**
     * Convenience method which returns a reference to a AndroidTestUtils object.
     * @return a reference to a AndroidTestUtils object
     */
    @NonNull
    private static AndroidTestUtils getAndroidTestUtils() {
        return AndroidTestUtils.getInstance();
    }

}