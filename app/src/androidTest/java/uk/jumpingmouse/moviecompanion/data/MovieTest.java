package uk.jumpingmouse.moviecompanion.data;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import uk.jumpingmouse.moviecompanion.AndroidTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the Movie data class.
 * @author Edmund Johnson
 */
@RunWith(AndroidJUnit4.class)
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
                .id("4016934")
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .certificate("R")
                .released(AndroidTestUtils.toLongOmdbReleased("01 Jun 2017"))
                .runtime(144)
                .genre("Drama, Mystery, Romance")
                .director("Christopher Nolan")
                .screenplay("Joss Whedon")
                .cast("Harrison Ford, Mark Hamill")
                .plot("Forrest Gump on a tractor.")
                .language("en")
                .country("UK")
                .poster(POSTER)
                .build();

        mMovieWithNulls = Movie.builder()
                .id("4016934")
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .certificate(null)
                .released(Movie.RELEASED_UNKNOWN)
                .runtime(Movie.RUNTIME_UNKNOWN)
                .genre(null)
                .director(null)
                .screenplay(null)
                .cast(null)
                .plot(null)
                .language(null)
                .country(null)
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
        assertEquals("4016934", mMovie.getId());
        assertEquals("tt4016934", mMovie.getImdbId());
        assertEquals("The Handmaiden", mMovie.getTitle());
        assertEquals(AndroidTestUtils.toLongOmdbReleased("01 Jun 2017"), mMovie.getReleased());
        assertEquals(144, mMovie.getRuntime());
        assertEquals("Drama, Mystery, Romance", mMovie.getGenre());
        assertEquals(POSTER, mMovie.getPoster());

        // test that the nullable fields are nullable, i.e. no exception thrown
        Movie movieWithNulls = Movie.builder()
                .id("4016934")
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .certificate(null)
                .released(Movie.RELEASED_UNKNOWN)
                .runtime(Movie.RUNTIME_UNKNOWN)
                .genre(null)
                .director(null)
                .screenplay(null)
                .cast(null)
                .plot(null)
                .language(null)
                .country(null)
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
                .certificate("R")
                .released(AndroidTestUtils.toLongOmdbReleased("01 Jun 2017"))
                .runtime(144)
                .genre("Drama, Mystery, Romance")
                .director("Christopher Nolan")
                .screenplay("Joss Whedon")
                .cast("Harrison Ford, Mark Hamill")
                .plot("Forrest Gump on a tractor.")
                .language("en")
                .country("UK")
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
                .id("4016934")
                .imdbId(null)
                .title("The Handmaiden")
                .certificate("R")
                .released(AndroidTestUtils.toLongOmdbReleased("01 Jun 2017"))
                .runtime(144)
                .genre("Drama, Mystery, Romance")
                .director("Christopher Nolan")
                .screenplay("Joss Whedon")
                .cast("Harrison Ford, Mark Hamill")
                .plot("Forrest Gump on a tractor.")
                .language("en")
                .country("UK")
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
                .id("4016934")
                .imdbId("tt4016934")
                .title(null)
                .certificate("R")
                .released(AndroidTestUtils.toLongOmdbReleased("01 Jun 2017"))
                .runtime(144)
                .genre("Drama, Mystery, Romance")
                .director("Christopher Nolan")
                .screenplay("Joss Whedon")
                .cast("Harrison Ford, Mark Hamill")
                .plot("Forrest Gump on a tractor.")
                .language("en")
                .country("UK")
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

        // test that building with the same id but different values for other fields
        // results in equals(...) returning true
        assertTrue(Movie.builder()
                .id("4016934")
                .imdbId("tt4016938")
                .title("The Handmaiden Returns")
                .certificate("PG")
                .released(AndroidTestUtils.toLongOmdbReleased("15 Jul 2012"))
                .runtime(90)
                .genre("18")
                .director("Martin Scorsese")
                .screenplay("Joss Whedon")
                .cast("Emilia Clarke, Kit Harington")
                .plot("Brilliantly devastating.")
                .language("de")
                .country("DE")
                .poster(POSTER + ".extended")
                .build()
                .equals(mMovie));

        // test that building with the same null parameter values results in equals(...) returning true
        assertTrue(Movie.builder()
                .id("4016934")
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .released(Movie.RELEASED_UNKNOWN)
                .genre(null)
                .runtime(Movie.RUNTIME_UNKNOWN)
                .director(null)
                .screenplay(null)
                .cast(null)
                .plot(null)
                .language(null)
                .country(null)
                .poster(null)
                .build()
                .equals(mMovieWithNulls));

        // test that building with a different id but all other values the same
        // results in equals(...) returning false
        assertFalse(Movie.builder()
                .id("4016938")
                .imdbId("tt4016934")
                .title("The Handmaiden")
                .certificate("R")
                .released(AndroidTestUtils.toLongOmdbReleased("01 Jun 2017"))
                .runtime(144)
                .genre("Drama, Mystery, Romance")
                .director("Christopher Nolan")
                .screenplay("Joss Whedon")
                .cast("Harrison Ford, Mark Hamill")
                .plot("Forrest Gump on a tractor.")
                .language("en")
                .country("UK")
                .poster(POSTER)
                .build()
                .equals(mMovie));
    }

    @Test
    public void testHashcode() {
        //assertTrue(mMovie.hashCode() > 1); // could be negative
        assertEquals(mMovie.hashCode(), mMovieWithNulls.hashCode());

        assertNotEquals(mMovie.hashCode(),
                Movie.builder()
                        // id is the only different field
                        .id("4016938")
                        .imdbId("tt4016934")
                        .title("The Handmaiden")
                        .certificate("R")
                        .released(AndroidTestUtils.toLongOmdbReleased("01 Jun 2017"))
                        .runtime(144)
                        .genre("Drama, Mystery, Romance")
                        .director("Christopher Nolan")
                        .screenplay("Joss Whedon")
                        .cast("Harrison Ford, Mark Hamill")
                        .plot("Forrest Gump on a tractor.")
                        .language("en")
                        .country("UK")
                        .poster(POSTER)
                        .build());
    }

    @Test
    public void testToString() {
        assertEquals(
                "Movie{id=4016934, imdbId=tt4016934, title=The Handmaiden" +
                        ", certificate=R, released="
                        + AndroidTestUtils.toLongOmdbReleased("01 Jun 2017")
                        + ", runtime=144, genre=Drama, Mystery, Romance"
                        + ", director=Christopher Nolan, screenplay=Joss Whedon"
                        + ", cast=Harrison Ford, Mark Hamill, plot=Forrest Gump on a tractor."
                        + ", language=en, country=UK"
                        + ", poster=" + POSTER +
                        "}",
                mMovie.toString());
    }

    //---------------------------------------------------------------------
    // Getters

}