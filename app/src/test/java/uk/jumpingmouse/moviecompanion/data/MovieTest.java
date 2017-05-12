package uk.jumpingmouse.moviecompanion.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.jumpingmouse.moviecompanion.utils.ModelUtils;

import static org.junit.Assert.*;

/**
 * Test class for the Movie data class.
 * @author Edmund Johnson
 */
public class MovieTest {

    @SuppressWarnings("SpellCheckingInspection")
    private static final String POSTER_URL =
            "https://images-na.ssl-images-amazon.com/images/M/MV5BYTBjYjllZTctMTdkMy00MmE5LTllYjctYzg3OTc1MTFjZGYzXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_.jpg";

    private Movie movie;

    @Before
    public void setUp() {
        movie = Movie.builder()
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .genre("Drama, Mystery, Romance")
                .runtime(144)
                .posterUrl(POSTER_URL)
                .year("2017")
                .released(ModelUtils.toLongOmdbReleased("01 Jun 2017"))
                .build();
    }

    @After
    public void tearDown() {
        movie = null;
    }

    @Test
    public void builder() {

        // test that the fields return the expected values
        assertEquals("tt4016934", movie.imdbId());
        assertEquals("The Handmaiden", movie.title());
        assertEquals("Drama, Mystery, Romance", movie.genre());
        assertEquals(144, movie.runtime());
        assertEquals(POSTER_URL, movie.posterUrl());
        assertEquals("2017", movie.year());

        // test that the nullable fields are nullable, i.e. no exception thrown
        Movie movieWithNulls = Movie.builder()
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .genre(null)
                .runtime(Movie.RUNTIME_UNKNOWN)
                .posterUrl(null)
                .year(null)
                .released(Movie.RELEASED_UNKNOWN)
                .build();
        assertNotNull(movieWithNulls);
    }

    @Test
    public void testEquals() {
        // test that building with the same parameter values results in equals(...) returning true
        assertTrue(Movie.builder()
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .genre("Drama, Mystery, Romance")
                .runtime(144)
                .posterUrl(POSTER_URL)
                .year("2017")
                .released(ModelUtils.toLongOmdbReleased("01 Jun 2017"))
                .build()
                .equals(movie));

        // test that building with a different imdbId results in equals(...) returning false
        assertFalse(Movie.builder()
                .imdbId("tt4016935")
                .title("The Handmaiden")
                .genre("Drama, Mystery, Romance")
                .runtime(144)
                .posterUrl(POSTER_URL)
                .year("2017")
                .released(ModelUtils.toLongOmdbReleased("01 Jun 2017"))
                .build()
                .equals(movie));
    }

    @Test
    public void testToString() {
        assertEquals(
                "Movie{imdbId=tt4016934, title=The Handmaiden, genre=Drama, Mystery, Romance, runtime=144, " +
                        "posterUrl=" + POSTER_URL + ", year=2017, " +
                        "released=" + ModelUtils.toLongOmdbReleased("01 Jun 2017") + "}",
                movie.toString());
    }

}