package uk.jumpingmouse.moviecompanion.utils;

import android.content.ContentValues;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.model.DataContract;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static uk.jumpingmouse.moviecompanion.utils.ModelUtils.toMovie;

/**
 * Test class for ModelUtils.
 * @author Edmund Johnson
 */
@RunWith(AndroidJUnit4.class)
public class ModelUtilsTest {

    @SuppressWarnings("SpellCheckingInspection")
    private static final String MOCK_POSTER_URL =
            "https://images-na.ssl-images-amazon.com/images/M/MV5BYTBjYjllZTctMTdkMy00MmE5LTllYjctYzg3OTc1MTFjZGYzXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_.jpg";

    private ContentValues mValues;
    private ContentValues mValuesWithNulls;

    @Before
    public void setUp() throws Exception {
        mValues = new ContentValues();
        mValues.put(DataContract.MovieEntry.COLUMN_ID, "tt9999991");
        mValues.put(DataContract.MovieEntry.COLUMN_IMDB_ID, "tt9999991");
        mValues.put(DataContract.MovieEntry.COLUMN_TITLE, "Test Movie 1");
        mValues.put(DataContract.MovieEntry.COLUMN_GENRE, "Drama, Mystery, Romance");
        mValues.put(DataContract.MovieEntry.COLUMN_RUNTIME, "114 min");
        mValues.put(DataContract.MovieEntry.COLUMN_POSTER_URL, MOCK_POSTER_URL);
        mValues.put(DataContract.MovieEntry.COLUMN_YEAR, "2011");
        mValues.put(DataContract.MovieEntry.COLUMN_RELEASED, "01 Jun 2011");

        mValuesWithNulls = new ContentValues();
        mValuesWithNulls.put(DataContract.MovieEntry.COLUMN_ID, "tt9999992");
        mValuesWithNulls.put(DataContract.MovieEntry.COLUMN_IMDB_ID, "tt9999992");
        mValuesWithNulls.put(DataContract.MovieEntry.COLUMN_TITLE, "Test Movie With Nulls");
    }

    @Test
    public void toMovieContentValues() {
        // toMovie(ContentValues) works correctly when all fields are set
        Movie movie = toMovie(mValues);
        assertNotNull(movie);
        assertEquals(movie.imdbId(), "tt9999991");
        assertEquals(movie.title(), "Test Movie 1");
        assertEquals(movie.genre(), "Drama, Mystery, Romance");
        assertEquals(movie.runtime(), 114);
        assertEquals(movie.posterUrl(), MOCK_POSTER_URL);
        assertEquals(movie.year(), "2011");
        assertEquals(movie.released(), ModelUtils.toLongOmdbReleased("01 Jun 2011"));

        // if the mandatory attribute imdbId is missing, null is returned
        mValues.remove(DataContract.MovieEntry.COLUMN_IMDB_ID);
        assertNull(toMovie(mValues));
        // if the mandatory attribute title is missing, null is returned
        mValues.put(DataContract.MovieEntry.COLUMN_IMDB_ID, "tt9999991");
        mValues.remove(DataContract.MovieEntry.COLUMN_TITLE);
        assertNull(toMovie(mValues));

        // toMovie(ContentValues) works correctly when non-mandatory fields are not set
        movie = toMovie(mValuesWithNulls);
        assertNotNull(movie);
        assertEquals(movie.imdbId(), "tt9999992");
        assertEquals(movie.title(), "Test Movie With Nulls");
        assertNull(movie.genre());
        assertEquals(movie.runtime(), Movie.RUNTIME_UNKNOWN);
        assertNull(movie.posterUrl());
        assertNull(movie.year());
        assertEquals(movie.released(), Movie.RELEASED_UNKNOWN);
    }

    @Test
    public void toMovieCursor() {

    }

    @Test
    public void toMovie1() {

    }

    @Test
    public void toLongOmdbReleased() {

    }

}