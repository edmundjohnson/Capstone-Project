package uk.jumpingmouse.moviecompanion.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.utils.DateUtils;
import uk.jumpingmouse.moviecompanion.utils.ModelUtils;

import static android.support.test.InstrumentationRegistry.getTargetContext;

import static org.junit.Assert.*;

/**
 * Test class for DataProvider, the content provider.
 * @author Edmund Johnson
 */
@RunWith(AndroidJUnit4.class)
public class DataProviderTest {

//    private Context mContext;
    private ContentResolver mContentResolver;

    @SuppressWarnings("SpellCheckingInspection")
    private static final String MOCK_POSTER_URL =
            "https://images-na.ssl-images-amazon.com/images/M/MV5BYTBjYjllZTctMTdkMy00MmE5LTllYjctYzg3OTc1MTFjZGYzXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_.jpg";
    private static final Movie TEST_MOVIE_1;
    private static final Movie TEST_MOVIE_2;
    //private static final Movie TEST_MOVIE_3;


    static {
        // DO NOT USE REAL MOVIES, as these tests are destructive!
        TEST_MOVIE_1 = Movie.builder()
                .imdbId("tt9999991")
                .title("Test Movie 1")
                .genre("Drama, Mystery, Romance")
                .runtime(111)
                .posterUrl(MOCK_POSTER_URL)
                .year("2011")
                .released(ModelUtils.toLongOmdbReleased("01 Jun 2011"))
                .build();
        TEST_MOVIE_2 = Movie.builder()
                .imdbId("tt9999992")
                .title("Test Movie 2")
                .genre("Drama, Mystery, Romance")
                .runtime(122)
                .posterUrl(MOCK_POSTER_URL)
                .year("2012")
                .released(ModelUtils.toLongOmdbReleased("01 Jun 2012"))
                .build();
//        TEST_MOVIE_3 = Movie.builder()
//                .imdbId("tt9999993")
//                .title("Test Movie 3")
//                .genre("Drama, Mystery, Romance")
//                .runtime(133)
//                .posterUrl(MOCK_POSTER_URL)
//                .year("2013")
//                .released(ModelUtils.toLongOmdbReleased("01 Jun 2013"))
//                .build();
    }

    @Before
    public void setUp() throws Exception {
//        mContext = getTargetContext();
        mContentResolver = getTargetContext().getContentResolver();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getType() throws Exception {
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
        type = mContentResolver.getType(DataContract.MovieEntry.buildUriMovieAll());
        // Expected getType(URI): vnd.android.cursor.dir/uk.jumpingmouse.moviecompanion/movie
        assertEquals("Error: ContentProvider.getType() for all movies URI should be" +
                        " DataContract.MovieEntry.CONTENT_DIR_TYPE",
                DataContract.MovieEntry.CONTENT_DIR_TYPE, type);

        // URI: content://uk.jumpingmouse.moviecompanion/movie/*
        type = mContentResolver.getType(DataContract.MovieEntry.buildUriMovieId("tt1234567"));
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
    public void queryMovie() {
        Cursor cursor;
        int rowsDeleted;
        int rowsCurrent;
        Uri uri;

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

        // insert two rows
        mContentResolver.insert(DataContract.MovieEntry.CONTENT_URI, getContentValues(TEST_MOVIE_1));
        mContentResolver.insert(DataContract.MovieEntry.CONTENT_URI, getContentValues(TEST_MOVIE_2));

        // Verify that the number of rows returned by the query has increased by 2
        cursor = mContentResolver.query(
                DataContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        rowsCurrent = cursor == null ? 0 : cursor.getCount();
        assertEquals("rowsCurrent should be 2 greater than rowsInitial", rowsInitial + 2, rowsCurrent);
        closeCursor(cursor);

        // delete the added rows
        uri = DataContract.MovieEntry.buildUriMovieId(TEST_MOVIE_1.imdbId());
        rowsDeleted = mContentResolver.delete(uri, null, null);
        assertEquals("rowsDeleted should be 1", 1, rowsDeleted);
        uri = DataContract.MovieEntry.buildUriMovieId(TEST_MOVIE_2.imdbId());
        rowsDeleted = mContentResolver.delete(uri, null, null);
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
    }

    @Test
    public void insertDeleteAndQuerySpecificMovie() throws Exception {
        Cursor cursor;
        int rowsDeleted;
        int rowsCurrent;
        // URI for querying for TEST_MOVIE_1
        Uri uri = DataContract.MovieEntry.buildUriMovieId(TEST_MOVIE_1.imdbId());

        // query for specific movie TEST_MOVIE_1 - initially should not be there
        cursor = mContentResolver.query(uri, null, null, null, null);
        rowsCurrent = cursor == null ? 0 : cursor.getCount();
        assertEquals("TEST_MOVIE_1 should not initially be in database", 0, rowsCurrent);
        closeCursor(cursor);

        // insert TEST_MOVIE_1
        mContentResolver.insert(DataContract.MovieEntry.CONTENT_URI, getContentValues(TEST_MOVIE_1));

        // query for specific movie TEST_MOVIE_1 - should now be there
        cursor = mContentResolver.query(uri, null, null, null, null);
        rowsCurrent = cursor == null ? 0 : cursor.getCount();
        // The query should return a movie which is equal to TEST_MOVIE_1
        assertEquals("The query should return 1 row", 1, rowsCurrent);
        if (cursor != null) {
            cursor.moveToFirst();
            Movie movie = ModelUtils.toMovie(cursor);
            assertEquals("TEST_MOVIE_1 should be returned by the query", TEST_MOVIE_1, movie);
        }
        closeCursor(cursor);

        // delete the added row
        rowsDeleted = mContentResolver.delete(uri, null, null);
        assertEquals("rowsDeleted should be 1", 1, rowsDeleted);

        // query for specific movie TEST_MOVIE_1 - should no longer be there
        cursor = mContentResolver.query(uri, null, null, null, null);
        rowsCurrent = cursor == null ? 0 : cursor.getCount();
        assertEquals("TEST_MOVIE_1 should no longer be in database", 0, rowsCurrent);
        closeCursor(cursor);
    }

    @Test
    public void updateMovie() throws Exception {
        // TODO
    }

    //---------------------------------------------------------------------
    // Utility methods

    /**
     * Return a set of ContentValues corresponding to a movie.
     * @param movie the movie
     * @return the set of ContentValues corresponding to the movie
     */
    private ContentValues getContentValues(Movie movie) {
        ContentValues values = new ContentValues();

        values.put(DataContract.MovieEntry.COLUMN_ID, movie.imdbId());
        values.put(DataContract.MovieEntry.COLUMN_IMDB_ID, movie.imdbId());
        values.put(DataContract.MovieEntry.COLUMN_TITLE, movie.title());
        values.put(DataContract.MovieEntry.COLUMN_GENRE, movie.genre());
        values.put(DataContract.MovieEntry.COLUMN_RUNTIME, toStringOmdbRuntime(movie.runtime()));
        values.put(DataContract.MovieEntry.COLUMN_POSTER_URL, movie.posterUrl());
        values.put(DataContract.MovieEntry.COLUMN_YEAR, movie.year());
        values.put(DataContract.MovieEntry.COLUMN_RELEASED, toStringOmdbReleased(movie.released()));

        return values;
    }

    /**
     * Returns an OMDb-formatted runtime string representing a number of minutes.
     * @param runtime the runtime in minutes
     * @return an OMDb-formatted released date string corresponding to the runtime,
     *         or an empty String if runtime indicates an unknown runtime
     */
    @Nullable
    private static String toStringOmdbRuntime(final int runtime) {
        if (runtime == Movie.RUNTIME_UNKNOWN) {
            return "";
        }
        return runtime + " min";
    }

    /**
     * Returns an OMDb-formatted released date string representing a number of milliseconds.
     * @param released a release date as a number of milliseconds
     * @return an OMDb-formatted released date string corresponding to released,
     *         or an empty String if released indicates an unknown release date
     */
    @Nullable
    private static String toStringOmdbReleased(final long released) {
        if (released == Movie.RELEASED_UNKNOWN) {
            return "";
        }
        Date dateReleased = new Date(released);
        return DateUtils.toStringOmdbReleased(dateReleased);
    }

    /**
     * Silently close a cursor.
     * @param cursor the cursor
     */
    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            //cursor = null;
        }
    }

}