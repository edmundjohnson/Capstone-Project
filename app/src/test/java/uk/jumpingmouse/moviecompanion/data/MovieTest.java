package uk.jumpingmouse.moviecompanion.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.jumpingmouse.moviecompanion.utils.DateUtils;

import static org.junit.Assert.*;

/**
 * Test class for the Movie data class.
 * @author Edmund Johnson
 */
public class MovieTest {

    @SuppressWarnings("SpellCheckingInspection")
    private static final String POSTER_URL =
            "https://images-na.ssl-images-amazon.com/images/M/MV5BYTBjYjllZTctMTdkMy00MmE5LTllYjctYzg3OTc1MTFjZGYzXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_.jpg";

    private Movie mMovie;
    private Movie mMovieWithNulls;

    // By default, expect no exceptions.
    // thrown must be public.
    @SuppressWarnings("WeakerAccess")
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        mMovie = Movie.builder()
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .genre("Drama, Mystery, Romance")
                .runtime(144)
                .posterUrl(POSTER_URL)
                .year("2017")
                .released(DateUtils.toLongOmdbReleased("01 Jun 2017"))
                .build();

        mMovieWithNulls = Movie.builder()
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .genre(null)
                .runtime(Movie.RUNTIME_UNKNOWN)
                .posterUrl(null)
                .year(null)
                .released(Movie.RELEASED_UNKNOWN)
                .build();
    }

    @After
    public void tearDown() {
        mMovie = null;
    }

    /**
     * Test that builder works with valid values.
     */
    @Test
    public void builder() {

        // test that the fields return the expected values
        assertEquals("tt4016934", mMovie.getImdbId());
        assertEquals("The Handmaiden", mMovie.getTitle());
        assertEquals("Drama, Mystery, Romance", mMovie.getGenre());
        assertEquals(144, mMovie.getRuntime());
        assertEquals(POSTER_URL, mMovie.getPosterUrl());
        assertEquals("2017", mMovie.getYear());

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

    /**
     * Test that the imdbId field is mandatory, i.e. if it is null, an exception is thrown.
     */
    @Test
    public void builderImdbIdIsMandatory() {
        // We expect an exception to be thrown
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Missing required properties: imdbId");

        @SuppressWarnings({"unused", "UnusedAssignment", "ConstantConditions"})
        Movie movie = Movie.builder()
                .imdbId(null)
                .title("The Handmaiden")
                .genre("Drama, Mystery, Romance")
                .runtime(144)
                .posterUrl(POSTER_URL)
                .year("2017")
                .released(DateUtils.toLongOmdbReleased("01 Jun 2017"))
                .build();
    }

    /**
     * Test that the title field is mandatory, i.e. if it is null, an exception is thrown.
     */
    @Test
    public void builderTitleIsMandatory() {
        // We expect an exception to be thrown
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Missing required properties: title");

        @SuppressWarnings({"unused", "UnusedAssignment", "ConstantConditions"})
        Movie movie = Movie.builder()
                .imdbId("tt4016934")
                .title(null)
                .genre("Drama, Mystery, Romance")
                .runtime(144)
                .posterUrl(POSTER_URL)
                .year("2017")
                .released(DateUtils.toLongOmdbReleased("01 Jun 2017"))
                .build();
    }

    @Test
    public void testEquals() {
        // test that a movie equals itself
        //noinspection EqualsWithItself
        assertTrue(mMovie.equals(mMovie));

        // test that a movie does not equal a String
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(mMovie.equals(mMovie.toString()));

        // test that building with the same parameter values results in equals(...) returning true
        assertTrue(Movie.builder()
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .genre("Drama, Mystery, Romance")
                .runtime(144)
                .posterUrl(POSTER_URL)
                .year("2017")
                .released(DateUtils.toLongOmdbReleased("01 Jun 2017"))
                .build()
                .equals(mMovie));

        // test that building with the same null parameter values results in equals(...) returning true
        assertTrue(Movie.builder()
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .genre(null)
                .runtime(Movie.RUNTIME_UNKNOWN)
                .posterUrl(null)
                .year(null)
                .released(Movie.RELEASED_UNKNOWN)
                .build()
                .equals(mMovieWithNulls));

        // test that building with a different imdbId results in equals(...) returning false
        assertFalse(Movie.builder()
                .imdbId("tt4016935")
                .title("The Handmaiden")
                .genre("Drama, Mystery, Romance")
                .runtime(144)
                .posterUrl(POSTER_URL)
                .year("2017")
                .released(DateUtils.toLongOmdbReleased("01 Jun 2017"))
                .build()
                .equals(mMovie));
    }

    @Test
    public void testHashcode() {
        assertTrue(mMovie.hashCode() > 1);
        assertTrue(mMovie.hashCode() != mMovieWithNulls.hashCode());
    }

    @Test
    public void testToString() {
        assertEquals(
                "Movie{imdbId=tt4016934, title=The Handmaiden, genre=Drama, Mystery, Romance," +
                        " runtime=144, posterUrl=" + POSTER_URL + ", year=2017, " +
                        "released=" + DateUtils.toLongOmdbReleased("01 Jun 2017") + "}",
                mMovie.toString());
    }

}