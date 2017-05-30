package uk.jumpingmouse.moviecompanion.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.List;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.utils.DateUtils;
import uk.jumpingmouse.moviecompanion.utils.ModelUtils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for DataProvider, the content provider.
 * @author Edmund Johnson
 */
@RunWith(AndroidJUnit4.class)
public class DataProviderTest {

    //private Context mContext;
    private ContentResolver mContentResolver;

    @SuppressWarnings("SpellCheckingInspection")
    private static final String TEST_POSTER =
            "https://images-na.ssl-images-amazon.com/images/M/MV5BYTBjYjllZTctMTdkMy00MmE5LTllYjctYzg3OTc1MTFjZGYzXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_.jpg";

    private static final Movie TEST_MOVIE_1;
    private static final Movie TEST_MOVIE_1_MODIFIED;
    private static final Movie TEST_MOVIE_2;
    private static final Movie TEST_MOVIE_3;

    private static final Uri URI_TEST_MOVIE_1;
    private static final Uri URI_TEST_MOVIE_2;
    private static final Uri URI_TEST_MOVIE_3;

    static {
        // DO NOT USE REAL MOVIES, as these tests are destructive!
        TEST_MOVIE_1 = Movie.builder()
                .id("tt9999991")
                .imdbId("tt9999991")
                .title("Test Movie 1")
                .year("2011")
                .released(DateUtils.toLongOmdbReleased("01 Jun 2011"))
                .runtime(111)
                .genre("Drama, Mystery, Romance")
                .poster(TEST_POSTER)
                .build();
        TEST_MOVIE_1_MODIFIED = Movie.builder()
                .id("tt9999991")
                .imdbId("tt9999991")
                .title("Test Movie 1 modified")
                .year("2012")
                .released(DateUtils.toLongOmdbReleased("01 Jun 2012"))
                .runtime(121)
                .genre("Comedy")
                .poster(TEST_POSTER + ".modified")
                .build();
        TEST_MOVIE_2 = Movie.builder()
                .id("tt9999992")
                .imdbId("tt9999992")
                .title("Test Movie 2")
                .year("2012")
                .released(DateUtils.toLongOmdbReleased("01 Jun 2012"))
                .runtime(122)
                .genre("Drama, Mystery, Romance")
                .poster(TEST_POSTER)
                .build();
        TEST_MOVIE_3 = Movie.builder()
                .id("tt9999993")
                .imdbId("tt9999993")
                // Do not change the 0 to a 3! 0 is required for query order test.
                .title("Test Movie 0")
                .year("2013")
                .released(DateUtils.toLongOmdbReleased("01 Jun 2013"))
                .runtime(133)
                .genre("Drama, Mystery, Romance")
                .poster(TEST_POSTER)
                .build();

        URI_TEST_MOVIE_1 = DataContract.MovieEntry.buildUriForRowById(TEST_MOVIE_1.getId());
        URI_TEST_MOVIE_2 = DataContract.MovieEntry.buildUriForRowById(TEST_MOVIE_2.getId());
        URI_TEST_MOVIE_3 = DataContract.MovieEntry.buildUriForRowById(TEST_MOVIE_3.getId());
    }

    // By default, expect no exceptions.
    // thrown must be public.
    @SuppressWarnings("WeakerAccess")
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    /**
     * Constructor.
     */
    public DataProviderTest() {
        Timber.plant(new Timber.DebugTree());
    }

    @Before
    public void setUp() {
        //mContext = InstrumentationRegistry.getTargetContext();
        mContentResolver = InstrumentationRegistry.getTargetContext().getContentResolver();

        // Ensure that none of the test movies are in the database
        mContentResolver.delete(URI_TEST_MOVIE_1, null, null);
        mContentResolver.delete(URI_TEST_MOVIE_2, null, null);
        mContentResolver.delete(URI_TEST_MOVIE_3, null, null);
    }

    @Test
    public void getType() {
        String type;

        // URI: content://uk.jumpingmouse.moviecompanion/invalid
        type = mContentResolver.getType(Uri.parse("content://uk.jumpingmouse.moviecompanion/invalid"));
        // Expected getType(URI): null
        assertNull("Error: ContentProvider.getType() for an invalid URI should be null", type);

        // URI: content://uk.jumpingmouse.moviecompanion/movie
        type = mContentResolver.getType(DataContract.MovieEntry.CONTENT_URI);
        // Expected getType(URI): vnd.android.cursor.dir/uk.jumpingmouse.moviecompanion/movie
        assertEquals("Error: ContentProvider.getType() for movie CONTENT_URI type should be" +
                        " DataContract.MovieEntry.CONTENT_DIR_TYPE",
                DataContract.MovieEntry.CONTENT_DIR_TYPE, type);

        // URI: content://uk.jumpingmouse.moviecompanion/movie/all
        type = mContentResolver.getType(DataContract.MovieEntry.buildUriForAllRows());
        // Expected getType(URI): vnd.android.cursor.dir/uk.jumpingmouse.moviecompanion/movie
        assertEquals("Error: ContentProvider.getType() for all movies URI should be" +
                        " DataContract.MovieEntry.CONTENT_DIR_TYPE",
                DataContract.MovieEntry.CONTENT_DIR_TYPE, type);

        // URI: content://uk.jumpingmouse.moviecompanion/movie/*
        type = mContentResolver.getType(DataContract.MovieEntry.buildUriForRowById("tt1234567"));
        // Expected getType(URI): vnd.android.cursor.item/uk.jumpingmouse.moviecompanion/movie
        assertEquals("Error: ContentProvider.getType() for specific movie URI should be" +
                        " DataContract.MovieEntry.CONTENT_ITEM_TYPE",
                DataContract.MovieEntry.CONTENT_ITEM_TYPE, type);

        // URI: content://uk.jumpingmouse.moviecompanion/movie/tt1234567/invalid
        type = mContentResolver.getType(Uri.parse("content://uk.jumpingmouse.moviecompanion/movie/tt1234567/invalid"));
        // Expected getType(URI): null
        assertNull("Error: ContentProvider.getType() for a non-matching URI should be null", type);

    }

    /**
     * Test the content provider query ".../movie"
     */
    @Test
    public void queryMultipleRows() {
        Cursor cursor;
        int rowsDeleted;
        int rowsCurrent;
        Movie movie;
        List<Movie> movieList;

        // Save the initial number of rows returned by the query
        cursor = mContentResolver.query(
                DataContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        int rowsInitial = cursor == null ? 0 : cursor.getCount();
        closeCursor(cursor);

        // insert test rows
        Uri uriInserted1 = mContentResolver.insert(DataContract.MovieEntry.CONTENT_URI, toContentValues(TEST_MOVIE_1));
        Uri uriInserted2 = mContentResolver.insert(DataContract.MovieEntry.CONTENT_URI, toContentValues(TEST_MOVIE_2));
        Uri uriInserted3 = mContentResolver.insert(DataContract.MovieEntry.CONTENT_URI, toContentValues(TEST_MOVIE_3));
        assertNotNull(uriInserted1);
        assertNotNull(uriInserted2);
        assertNotNull(uriInserted3);

        // query for specific movie TEST_MOVIE_1 - should now be there
        cursor = mContentResolver.query(URI_TEST_MOVIE_1, null, null, null, null);
        assertNotNull(cursor);
        // The query should return a movie which is equal to TEST_MOVIE_1
        assertEquals("The query should return 1 row", 1, cursor.getCount());
        cursor.moveToFirst();
        movie = ModelUtils.toMovie(cursor);
        assertEquals("TEST_MOVIE_1 should be returned by the query", TEST_MOVIE_1, movie);
        closeCursor(cursor);

        // query for specific movie TEST_MOVIE_2 - should now be there
        // Note: this tests an additional execution path to the query for TEST_MOVIE_1
        cursor = mContentResolver.query(URI_TEST_MOVIE_2, null, null, null, null);
        assertNotNull(cursor);
        // The query should return a movie which is equal to TEST_MOVIE_2
        assertEquals("The query should return 1 row", 1, cursor.getCount());
        cursor.moveToFirst();
        movie = ModelUtils.toMovie(cursor);
        assertEquals("TEST_MOVIE_2 should be returned by the query", TEST_MOVIE_2, movie);
        closeCursor(cursor);

        // query for specific movie TEST_MOVIE_3 - should now be there
        cursor = mContentResolver.query(URI_TEST_MOVIE_3, null, null, null, null);
        assertNotNull(cursor);
        // The query should return a movie which is equal to TEST_MOVIE_3
        assertEquals("The query should return 1 row", 1, cursor.getCount());
        cursor.moveToFirst();
        movie = ModelUtils.toMovie(cursor);
        assertEquals("TEST_MOVIE_3 should be returned by the query", TEST_MOVIE_3, movie);
        closeCursor(cursor);

        //------------------------------------
        // Verify that the total number of rows has increased by 3.
        // Also check that the currently-unsupported parameters do not cause problems.
        cursor = mContentResolver.query(
                DataContract.MovieEntry.CONTENT_URI,
                new String[]{},
                "",
                new String[]{},
                null
        );
        assertNotNull(cursor);
        rowsCurrent = cursor.getCount();
        assertEquals("rowsCurrent should be 3 greater than rowsInitial", rowsInitial + 3, rowsCurrent);
        closeCursor(cursor);

        //------------------------------------
        // Verify ordering by imdbId ascending
        cursor = mContentResolver.query(
                DataContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                "imdbId ASC"
        );
        assertNotNull(cursor);
        movieList = ModelUtils.toMovieList(cursor);
        assertNotNull(movieList);
        assertTrue("Sort imdbId ASC: Movie 1 before Movie 2",
                movieList.indexOf(TEST_MOVIE_1) < movieList.indexOf(TEST_MOVIE_2));
        assertTrue("Sort imdbId ASC: Movie 2 before Movie 3",
                movieList.indexOf(TEST_MOVIE_2) < movieList.indexOf(TEST_MOVIE_3));
        closeCursor(cursor);

        // Verify ordering by imdbId descending
        cursor = mContentResolver.query(
                DataContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                "imdbId DESC"
        );
        assertNotNull(cursor);
        movieList = ModelUtils.toMovieList(cursor);
        assertNotNull(movieList);
        assertTrue("Sort imdbId DESC: Movie 3 before Movie 2",
                movieList.indexOf(TEST_MOVIE_3) < movieList.indexOf(TEST_MOVIE_2));
        assertTrue("Sort imdbId DESC: Movie 2 before Movie 1",
                movieList.indexOf(TEST_MOVIE_2) < movieList.indexOf(TEST_MOVIE_1));
        closeCursor(cursor);

        // Verify ordering by title ascending
        cursor = mContentResolver.query(
                DataContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                "title ASC"
        );
        assertNotNull(cursor);
        movieList = ModelUtils.toMovieList(cursor);
        assertNotNull(movieList);
        assertTrue("Sort title ASC: Movie 3 before Movie 1",
                movieList.indexOf(TEST_MOVIE_3) < movieList.indexOf(TEST_MOVIE_1));
        assertTrue("Sort title ASC: Movie 1 before Movie 2",
                movieList.indexOf(TEST_MOVIE_1) < movieList.indexOf(TEST_MOVIE_2));
        closeCursor(cursor);

        // Verify ordering by title descending
        cursor = mContentResolver.query(
                DataContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                "title DESC"
        );
        assertNotNull(cursor);
        movieList = ModelUtils.toMovieList(cursor);
        assertNotNull(movieList);
        assertTrue("Sort title DESC: Movie 2 before Movie 1",
                movieList.indexOf(TEST_MOVIE_2) < movieList.indexOf(TEST_MOVIE_1));
        assertTrue("Sort title DESC: Movie 1 before Movie 3",
                movieList.indexOf(TEST_MOVIE_1) < movieList.indexOf(TEST_MOVIE_3));
        closeCursor(cursor);

        // Verify that the default ordering (title ASC) is used for empty sort order
        cursor = mContentResolver.query(
                DataContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                ""
        );
        assertNotNull(cursor);
        movieList = ModelUtils.toMovieList(cursor);
        assertNotNull(movieList);
        assertTrue("Sort title ASC: Movie 3 before Movie 1",
                movieList.indexOf(TEST_MOVIE_3) < movieList.indexOf(TEST_MOVIE_1));
        assertTrue("Sort title ASC: Movie 1 before Movie 2",
                movieList.indexOf(TEST_MOVIE_1) < movieList.indexOf(TEST_MOVIE_2));
        closeCursor(cursor);

        // Verify that the default ordering (title ASC) is used for invalid sort order
        cursor = mContentResolver.query(
                DataContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                "InvalidFieldName NOT_ASC_OR_DESC"
        );
        assertNotNull(cursor);
        movieList = ModelUtils.toMovieList(cursor);
        assertNotNull(movieList);
        assertTrue("Sort title ASC: Movie 3 before Movie 1",
                movieList.indexOf(TEST_MOVIE_3) < movieList.indexOf(TEST_MOVIE_1));
        assertTrue("Sort title ASC: Movie 1 before Movie 2",
                movieList.indexOf(TEST_MOVIE_1) < movieList.indexOf(TEST_MOVIE_2));
        closeCursor(cursor);

        //----------------------
        // delete the added rows
        rowsDeleted = mContentResolver.delete(URI_TEST_MOVIE_1, null, null);
        assertEquals("rowsDeleted should be 1", 1, rowsDeleted);
        rowsDeleted = mContentResolver.delete(URI_TEST_MOVIE_2, null, null);
        assertEquals("rowsDeleted should be 1", 1, rowsDeleted);
        rowsDeleted = mContentResolver.delete(URI_TEST_MOVIE_3, null, null);
        assertEquals("rowsDeleted should be 1", 1, rowsDeleted);

        // Verify that the number of rows returned by the query is back to its original value
        cursor = mContentResolver.query(
                DataContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        rowsCurrent = cursor == null ? 0 : cursor.getCount();
        assertEquals("rowsCurrent should be equal to rowsInitial", rowsInitial, rowsCurrent);
        closeCursor(cursor);

        // Verify the other URI query works ".../movie/all" rather than ".../movie"
        cursor = mContentResolver.query(
                DataContract.MovieEntry.buildUriForAllRows(),
                null,
                null,
                null,
                null
        );
        rowsCurrent = cursor == null ? 0 : cursor.getCount();
        assertEquals("rowsCurrent should be equal to rowsInitial", rowsInitial, rowsCurrent);
        closeCursor(cursor);
    }

//    /**
//     * Test querying a movie using an empty id.
//     * No, this returns all movies.
//     */
//    @Test
//    public void queryMovieWithNullId() {
//        Uri uriQuery = Uri.parse("content://uk.jumpingmouse.moviecompanion/movie/");
//        Cursor cursor = mContentResolver.query(uriQuery, null, null, null, null, null);
//        assertNull(cursor);
//    }

    @Test
    public void insertASpecificMovie() {
        Cursor cursor;
        int rowsCurrent;
//        int rowsDeleted;

        // query for specific movie TEST_MOVIE_1 - initially should not be there
        cursor = mContentResolver.query(URI_TEST_MOVIE_1, null, null, null, null);
        rowsCurrent = cursor == null ? 0 : cursor.getCount();
        assertEquals(String.format("Movie \"%s\" should not initially be in database", TEST_MOVIE_1.getId()),
                0, rowsCurrent);
        closeCursor(cursor);

        // insert TEST_MOVIE_1
        Uri uriInserted = mContentResolver.insert(DataContract.MovieEntry.CONTENT_URI, toContentValues(TEST_MOVIE_1));
        assertNotNull(uriInserted);
        assertTrue(uriInserted.equals(URI_TEST_MOVIE_1));

// This will only work when query is implemented for Firebase
//        // query for specific movie TEST_MOVIE_1 - should now be there
//        cursor = mContentResolver.query(URI_TEST_MOVIE_1, null, null, null, null);
//        assertNotNull(cursor);
//        // The query should return a movie which is equal to TEST_MOVIE_1
//        assertEquals("The query should return 1 row", 1, cursor.getCount());
//        cursor.moveToFirst();
//        Movie movie = ModelUtils.toMovie(cursor);
//        assertEquals("TEST_MOVIE_1 should be returned by the query", TEST_MOVIE_1, movie);
//        closeCursor(cursor);

// This will only work when delete is implemented for Firebase
//        // delete the added row
//        rowsDeleted = mContentResolver.delete(URI_TEST_MOVIE_1, null, null);
//        assertEquals("rowsDeleted should be 1", 1, rowsDeleted);
//
//        // query for specific movie TEST_MOVIE_1 - should no longer be there
//        cursor = mContentResolver.query(URI_TEST_MOVIE_1, null, null, null, null);
//        rowsCurrent = cursor == null ? 0 : cursor.getCount();
//        assertEquals(String.format("Movie \"%s\" should no longer be in database", TEST_MOVIE_1.getId()),
//                0, rowsCurrent);
//        closeCursor(cursor);
    }

    @Test
    public void insertDeleteAndQuerySpecificMovie() {
        Cursor cursor;
        int rowsDeleted;
        int rowsCurrent;

        // query for specific movie TEST_MOVIE_1 - initially should not be there
        cursor = mContentResolver.query(URI_TEST_MOVIE_1, null, null, null, null);
        rowsCurrent = cursor == null ? 0 : cursor.getCount();
        assertEquals(String.format("Movie \"%s\" should not initially be in database", TEST_MOVIE_1.getId()),
                0, rowsCurrent);
        closeCursor(cursor);

        // insert TEST_MOVIE_1
        Uri uriInserted = mContentResolver.insert(DataContract.MovieEntry.CONTENT_URI, toContentValues(TEST_MOVIE_1));
        assertNotNull(uriInserted);
        assertTrue(uriInserted.equals(URI_TEST_MOVIE_1));

//TODO: This fails because the insert adds the movie to the local database asynchronously,
//      so it's not there yet.
        // query for specific movie TEST_MOVIE_1 - should now be there
        cursor = mContentResolver.query(URI_TEST_MOVIE_1, null, null, null, null);
        assertNotNull(cursor);
        // The query should return a movie which is equal to TEST_MOVIE_1
        assertEquals("The query should return 1 row", 1, cursor.getCount());
        cursor.moveToFirst();
        Movie movie = ModelUtils.toMovie(cursor);
        assertEquals("TEST_MOVIE_1 should be returned by the query", TEST_MOVIE_1, movie);
        closeCursor(cursor);

        // delete the added row
        rowsDeleted = mContentResolver.delete(URI_TEST_MOVIE_1, null, null);
        assertEquals("rowsDeleted should be 1", 1, rowsDeleted);

        // query for specific movie TEST_MOVIE_1 - should no longer be there
        cursor = mContentResolver.query(URI_TEST_MOVIE_1, null, null, null, null);
        rowsCurrent = cursor == null ? 0 : cursor.getCount();
        assertEquals(String.format("Movie \"%s\" should no longer be in database", TEST_MOVIE_1.getId()),
                0, rowsCurrent);
        closeCursor(cursor);
    }

    /**
     * Test inserting a movie using null content values.
     */
    @Test
    public void insertMovieNullValues() {
        Uri uriInserted = mContentResolver.insert(DataContract.MovieEntry.CONTENT_URI, null);
        assertNull(uriInserted);
    }

    /**
     * Test inserting a movie using values from which a movie cannot be created.
     */
    @Test
    public void insertMovieInvalid() {
        ContentValues values = toContentValues(TEST_MOVIE_1);
        values.remove(DataContract.MovieEntry.COLUMN_IMDB_ID);
        Uri uriInserted = mContentResolver.insert(DataContract.MovieEntry.CONTENT_URI, values);
        assertNull(uriInserted);

        // query for specific movie TEST_MOVIE_1 - should not be there
        Cursor cursor = mContentResolver.query(URI_TEST_MOVIE_1, null, null, null, null);
        int rowsCurrent = cursor == null ? 0 : cursor.getCount();
        assertEquals(String.format("Movie \"%s\" should not initially be in database", TEST_MOVIE_1.getId()),
                0, rowsCurrent);
        closeCursor(cursor);
    }

    @Test
    public void updateMovieWhichExists() {
        Cursor cursor;
        int rowsCurrent;
        int rowsUpdated;

        // query for specific movie TEST_MOVIE_1 - initially should not be there
        cursor = mContentResolver.query(URI_TEST_MOVIE_1, null, null, null, null);
        rowsCurrent = cursor == null ? 0 : cursor.getCount();
        assertEquals(String.format("Movie \"%s\" should not initially be in database", TEST_MOVIE_1.getId()),
                0, rowsCurrent);
        closeCursor(cursor);

        // insert TEST_MOVIE_1
        Uri uriInserted = mContentResolver.insert(DataContract.MovieEntry.CONTENT_URI, toContentValues(TEST_MOVIE_1));
        assertNotNull(uriInserted);

        // query for specific row TEST_MOVIE_1 - should now be there
        cursor = mContentResolver.query(URI_TEST_MOVIE_1, null, null, null, null);
        assertNotNull("TEST_MOVIE_1 should be in database", cursor);
        // The query should return a movie which is equal to TEST_MOVIE_1
        assertEquals("The query should return 1 row", 1, cursor.getCount());
        cursor.moveToFirst();
        Movie movie = ModelUtils.toMovie(cursor);
        assertEquals("TEST_MOVIE_1 should be returned by the query", TEST_MOVIE_1, movie);
        closeCursor(cursor);

        // update the added row
        rowsUpdated = mContentResolver.update(
                URI_TEST_MOVIE_1, toContentValues(TEST_MOVIE_1_MODIFIED), null, null);
        assertEquals("rowsUpdated should be 1", 1, rowsUpdated);

        // query for specific row TEST_MOVIE_1 - should still be there, with new values
        cursor = mContentResolver.query(
                URI_TEST_MOVIE_1, null, null, null, null);
        assertNotNull("TEST_MOVIE_1 should still be in database", cursor);
        assertEquals("TEST_MOVIE_1 should still be in database", 1, cursor.getCount());
        cursor.moveToFirst();
        Movie movieUpdated = ModelUtils.toMovie(cursor);
        assertNotNull(movieUpdated);
        assertEquals("id should be unchanged", TEST_MOVIE_1.getId(), movieUpdated.getId());
        assertEquals("imdbId should be unchanged", TEST_MOVIE_1.getImdbId(), movieUpdated.getImdbId());
        assertEquals("title should be updated", TEST_MOVIE_1_MODIFIED.getTitle(), movieUpdated.getTitle());
        assertEquals("year should be updated", TEST_MOVIE_1_MODIFIED.getYear(), movieUpdated.getYear());
        assertEquals("released should be updated", TEST_MOVIE_1_MODIFIED.getReleased(), movieUpdated.getReleased());
        assertEquals("runtime should be updated", TEST_MOVIE_1_MODIFIED.getRuntime(), movieUpdated.getRuntime());
        assertEquals("genre should be updated", TEST_MOVIE_1_MODIFIED.getGenre(), movieUpdated.getGenre());
        assertEquals("poster should be updated", TEST_MOVIE_1_MODIFIED.getPoster(), movieUpdated.getPoster());
        closeCursor(cursor);

        // delete the added row
        int rowsDeleted = mContentResolver.delete(URI_TEST_MOVIE_1, null, null);
        assertEquals("rowsDeleted should be 1", 1, rowsDeleted);

        // query for specific movie TEST_MOVIE_1_MODIFIED - should no longer be there
        cursor = mContentResolver.query(URI_TEST_MOVIE_1, null, null, null, null);
        rowsCurrent = cursor == null ? 0 : cursor.getCount();
        assertEquals(String.format("Movie \"%s\" should no longer be in database", TEST_MOVIE_1_MODIFIED.getId()),
                0, rowsCurrent);
        closeCursor(cursor);
    }

    /**
     * Test updating a movie which is not in the database.
     */
    @Test
    public void updateMovieWhichDoesNotExist() {
        Cursor cursor;
        int rowsCurrent;
        int rowsUpdated;

        // query for specific movie TEST_MOVIE_2 - initially should not be there
        cursor = mContentResolver.query(URI_TEST_MOVIE_2, null, null, null, null);
        rowsCurrent = cursor == null ? 0 : cursor.getCount();
        assertEquals(String.format("Movie \"%s\" should not initially be in database", TEST_MOVIE_2.getId()),
                0, rowsCurrent);
        closeCursor(cursor);

        rowsUpdated = mContentResolver.update(URI_TEST_MOVIE_2, toContentValues(TEST_MOVIE_2), null, null);
        // update adds the movie if it does not already exist
        assertEquals(1, rowsUpdated);

        // query for specific row TEST_MOVIE_2 - should now be there
        cursor = mContentResolver.query(URI_TEST_MOVIE_2, null, null, null, null);
        assertNotNull("TEST_MOVIE_2 should be in database", cursor);
        // The query should return a movie which is equal to TEST_MOVIE_2
        assertEquals("The query should return 1 row", 1, cursor.getCount());
        cursor.moveToFirst();
        Movie movie = ModelUtils.toMovie(cursor);
        assertEquals("TEST_MOVIE_2 should be returned by the query", TEST_MOVIE_2, movie);
        closeCursor(cursor);

        // delete the added row
        int rowsDeleted = mContentResolver.delete(URI_TEST_MOVIE_2, null, null);
        assertEquals("rowsDeleted should be 1", 1, rowsDeleted);

        // query for specific movie TEST_MOVIE_2 - should no longer be there
        cursor = mContentResolver.query(URI_TEST_MOVIE_2, null, null, null, null);
        rowsCurrent = cursor == null ? 0 : cursor.getCount();
        assertEquals(String.format("Movie \"%s\" should no longer be in database", TEST_MOVIE_2.getId()),
                0, rowsCurrent);
        closeCursor(cursor);
    }

    /**
     * Test updating a movie using null content values.
     */
    @Test
    public void updateMovieNullValues() {
        int rowsUpdated = mContentResolver.update(URI_TEST_MOVIE_1, null, null, null);
        assertEquals(0, rowsUpdated);
    }

    /**
     * Test updating a movie which has a null id.
     */
    @Test
    public void updateMovieNullId() {
        ContentValues values = toContentValues(TEST_MOVIE_1);
        values.remove(DataContract.MovieEntry.COLUMN_ID);
        //values.remove(DataContract.MovieEntry.COLUMN_IMDB_ID);

        int rowsUpdated = mContentResolver.update(URI_TEST_MOVIE_1, values, null, null);
        assertEquals(0, rowsUpdated);
    }

    /**
     * Test updating a movie when the id in the URI does not match the one in the content values.
     */
    @Test
    public void updateMovieMismatch() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Id mismatch between URL and body of update request");

        Uri uriUpdate = DataContract.MovieEntry.buildUriForRowById("noSuchMovie");
        int rowsUpdated = mContentResolver.update(uriUpdate, toContentValues(TEST_MOVIE_1), null, null);
        assertEquals(0, rowsUpdated);
    }

    /**
     * Test deleting a movie which is not in the database.
     */
    @Test
    public void deleteMovieNotFound() {
        int initialRows = getMovieCount();
        int rowsDeleted = mContentResolver.delete(URI_TEST_MOVIE_2, null, null);
        // delete is asynchronous, so 1 is always returned
        //assertEquals(0, rowsDeleted);
        assertEquals(1, rowsDeleted);
        // instead of rowsDeleted being 0, check that the number of movies is unchanged
        assertEquals(initialRows, getMovieCount());
    }

    private int getMovieCount() {
        int count = 0;
        Cursor cursor = mContentResolver.query(DataContract.MovieEntry.buildUriForAllRows(),
                null, null, null, null);
        if (cursor != null) {
            count = cursor.getCount();
            closeCursor(cursor);
        }
        return count;
    }

    /**
     * Test querying using an unsupported URI.
     */
    @Test
    public void queryUnsupportedUri() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Unknown URI for query: content://" + DataContract.CONTENT_AUTHORITY + "/film");

        Uri uriQuery = Uri.parse("content://" + DataContract.CONTENT_AUTHORITY + "/film");
        Cursor cursor = mContentResolver.query(uriQuery, null, null, null, null, null);
        //assertNull(cursor);
        closeCursor(cursor);
    }

    /**
     * Test inserting using an unsupported URI.
     */
    @Test
    public void insertUnsupportedUri() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Unknown URI for insert: " + DataContract.MovieEntry.buildUriForAllRows().toString());

        Uri uriInsert = DataContract.MovieEntry.buildUriForAllRows();
        Uri uriInserted = mContentResolver.insert(uriInsert, toContentValues(TEST_MOVIE_1));
        assertNull(uriInserted);
    }

    /**
     * Test updating using an unsupported URI.
     */
    @Test
    public void updateUnsupportedUri() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Unknown URI for update: " + DataContract.MovieEntry.buildUriForAllRows().toString());

        Uri uriUpdate = DataContract.MovieEntry.buildUriForAllRows();
        int rowsUpdated = mContentResolver.update(uriUpdate, toContentValues(TEST_MOVIE_1), null, null);
        assertEquals(0, rowsUpdated);
    }

    /**
     * Test deleting using an unsupported URI.
     */
    @Test
    public void deleteUnsupportedUri() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Unknown URI for delete: " + DataContract.MovieEntry.buildUriForAllRows().toString());

        Uri uriDelete = DataContract.MovieEntry.buildUriForAllRows();
        int rowsDeleted = mContentResolver.delete(uriDelete, null, null);
        assertEquals(0, rowsDeleted);
    }

    //---------------------------------------------------------------------
    // Utility methods

    /**
     * Returns a set of ContentValues corresponding to a movie.
     * @param movie the movie
     * @return the set of ContentValues corresponding to the movie
     */
    @NonNull
    private static ContentValues toContentValues(@NonNull Movie movie) {
        return movie.toContentValues();
    }

    /**
     * Silently close a cursor.
     * @param cursor the cursor
     */
    private void closeCursor(@Nullable Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            //cursor = null;
        }
    }

}