package uk.jumpingmouse.moviecompanion.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for Film model class.
 * @author Edmund Johnson
 */
public class FilmTest {
    @SuppressWarnings("SpellCheckingInspection")
    private static final String POSTER_URL =
            "https://images-na.ssl-images-amazon.com/images/M/MV5BYTBjYjllZTctMTdkMy00MmE5LTllYjctYzg3OTc1MTFjZGYzXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_.jpg";

    private Film film;

    @Before
    public void setUp() {
        film = Film.builder()
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .genre("Drama, Mystery, Romance")
                .runtime(144)
                .posterUrl(POSTER_URL)
                .year("2017")
                .build();
    }

    @After
    public void tearDown() {
        film = null;
    }

    @Test
    public void builder() {

        // test that the fields return the expected values
        assertEquals("tt4016934", film.imdbId());
        assertEquals("The Handmaiden", film.title());
        assertEquals("Drama, Mystery, Romance", film.genre());
        assertEquals(144, film.runtime());
        assertEquals(POSTER_URL, film.posterUrl());
        assertEquals("2017", film.year());

        // test that the nullable fields are nullable, i.e. no exception thrown
        Film filmWithNulls = Film.builder()
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .genre(null)
                .runtime(Film.RUNTIME_UNKNOWN)
                .posterUrl(null)
                .year(null)
                .build();
        assertNotNull(filmWithNulls);
    }

    @Test
    public void testEquals() {
        // test that building with the same parameter values results in equals(...) returning true
        assertTrue(Film.builder()
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .genre("Drama, Mystery, Romance")
                .runtime(144)
                .posterUrl(POSTER_URL)
                .year("2017")
                .build()
                .equals(film));

        // test that building with a different imdbId results in equals(...) returning false
        assertFalse(Film.builder()
                .imdbId("tt4016935")
                .title("The Handmaiden")
                .genre("Drama, Mystery, Romance")
                .runtime(144)
                .posterUrl(POSTER_URL)
                .year("2017")
                .build()
                .equals(film));
    }

    @Test
    public void testToString() {
        assertEquals(
                "Film{imdbId=tt4016934, title=The Handmaiden, genre=Drama, Mystery, Romance," +
                        " runtime=144, posterUrl=" + POSTER_URL + ", year=2017}",
                film.toString());
    }

}