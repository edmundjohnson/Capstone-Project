package uk.jumpingmouse.moviecompanion.data;

import android.support.annotation.NonNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.jumpingmouse.moviecompanion.omdb.OmdbManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the Movie data class.
 * @author Edmund Johnson
 */
public class MovieTest {

    @SuppressWarnings("SpellCheckingInspection")
    private static final String POSTER =
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
                .id("tt4016934")
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .year("2017")
                .released(getOmdbManager().toLongOmdbReleased("01 Jun 2017"))
                .runtime(144)
                .genre("Drama, Mystery, Romance")
                .poster(POSTER)
                .build();

        mMovieWithNulls = Movie.builder()
                .id("tt4016934")
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .year(null)
                .released(Movie.RELEASED_UNKNOWN)
                .runtime(Movie.RUNTIME_UNKNOWN)
                .genre(null)
                .poster(null)
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
        assertEquals("tt4016934", mMovie.getId());
        assertEquals("tt4016934", mMovie.getImdbId());
        assertEquals("The Handmaiden", mMovie.getTitle());
        assertEquals("2017", mMovie.getYear());
        assertEquals(getOmdbManager().toLongOmdbReleased("01 Jun 2017"), mMovie.getReleased());
        assertEquals(144, mMovie.getRuntime());
        assertEquals("Drama, Mystery, Romance", mMovie.getGenre());
        assertEquals(POSTER, mMovie.getPoster());

        // test that the nullable fields are nullable, i.e. no exception thrown
        Movie movieWithNulls = Movie.builder()
                .id("tt4016934")
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .year(null)
                .released(Movie.RELEASED_UNKNOWN)
                .runtime(Movie.RUNTIME_UNKNOWN)
                .genre(null)
                .poster(null)
                .build();
        assertNotNull(movieWithNulls);
    }

    /**
     * Test that the id field is mandatory, i.e. if it is null, an exception is thrown.
     */
    @Test
    public void builderIdIsMandatory() {
        // We expect an exception to be thrown
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Missing required properties: id");

        @SuppressWarnings({"unused", "UnusedAssignment", "ConstantConditions"})
        Movie movie = Movie.builder()
                .id(null)
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .year("2017")
                .released(getOmdbManager().toLongOmdbReleased("01 Jun 2017"))
                .runtime(144)
                .genre("Drama, Mystery, Romance")
                .poster(POSTER)
                .build();
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
                .id("tt4016934")
                .imdbId(null)
                .title("The Handmaiden")
                .year("2017")
                .released(getOmdbManager().toLongOmdbReleased("01 Jun 2017"))
                .runtime(144)
                .genre("Drama, Mystery, Romance")
                .poster(POSTER)
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
                .id("tt4016934")
                .imdbId("tt4016934")
                .title(null)
                .year("2017")
                .released(getOmdbManager().toLongOmdbReleased("01 Jun 2017"))
                .runtime(144)
                .genre("Drama, Mystery, Romance")
                .poster(POSTER)
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
                .id("tt4016934")
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .year("2017")
                .released(getOmdbManager().toLongOmdbReleased("01 Jun 2017"))
                .runtime(144)
                .genre("Drama, Mystery, Romance")
                .poster(POSTER)
                .build()
                .equals(mMovie));

        // test that building with the same null parameter values results in equals(...) returning true
        assertTrue(Movie.builder()
                .id("tt4016934")
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .genre(null)
                .runtime(Movie.RUNTIME_UNKNOWN)
                .poster(null)
                .year(null)
                .released(Movie.RELEASED_UNKNOWN)
                .build()
                .equals(mMovieWithNulls));

        // test that building with a different imdbId results in equals(...) returning false
        assertFalse(Movie.builder()
                .id("tt4016934")
                .imdbId("tt4016935")
                .title("The Handmaiden")
                .year("2017")
                .released(getOmdbManager().toLongOmdbReleased("01 Jun 2017"))
                .runtime(144)
                .genre("Drama, Mystery, Romance")
                .poster(POSTER)
                .build()
                .equals(mMovie));
    }

    @Test
    public void testHashcode() {
        //assertTrue(mMovie.hashCode() > 1); // could be negative
        assertTrue(mMovie.hashCode() != mMovieWithNulls.hashCode());
    }

    @Test
    public void testToString() {
        assertEquals(
                "Movie{id=tt4016934, imdbId=tt4016934, title=The Handmaiden" +
                        ", year=2017, released=" + getOmdbManager().toLongOmdbReleased("01 Jun 2017") +
                        ", runtime=144, genre=Drama, Mystery, Romance, poster=" + POSTER +
                        "}",
                mMovie.toString());
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