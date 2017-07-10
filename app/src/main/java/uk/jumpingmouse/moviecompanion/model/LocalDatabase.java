package uk.jumpingmouse.moviecompanion.model;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import uk.jumpingmouse.moviecompanion.data.Award;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.data.UserMovie;
import uk.jumpingmouse.moviecompanion.data.ViewAward;

/**
 * Interface for database implementation classes.
 * @author Edmund Johnson
 */
public interface LocalDatabase {

    String MOVIE_SORT_COLUMN_DEFAULT = DataContract.MovieEntry.COLUMN_TITLE;
    boolean MOVIE_SORT_ASCENDING_DEFAULT = true;
    String AWARD_SORT_COLUMN_DEFAULT = DataContract.AwardEntry.COLUMN_AWARD_DATE;
    boolean AWARD_SORT_ASCENDING_DEFAULT = false;
    String VIEW_AWARD_SORT_COLUMN_DEFAULT = DataContract.ViewAwardEntry.COLUMN_AWARD_DATE;
    boolean VIEW_AWARD_SORT_ASCENDING_DEFAULT = false;

    //---------------------------------------------------------------------
    // Movie methods

    /**
     * Adds a movie's details to the database.
     * If the movie does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param movie the movie to insert or update
     * @return the number of rows inserted or updated
     */
    int addMovie(@NonNull Movie movie);

    /**
     * Deletes a movie from the database.
     * @param id the id of the movie to be deleted
     * @return the number of rows deleted
     */
    int deleteMovie(int id);

    /**
     * Returns the movie with a specified id.
     * @param id the id of the movie to be returned
     * @return the movie with the specified id
     */
    @Nullable
    Movie selectMovieById(int id);

    /**
     * Returns a list of movies from the database.
     * @param projection The list of columns to put into the cursor.
     *                   If this is {@code null} all columns are included.
     * @param selection A selection criteria to apply when filtering rows.
     *                  If this is {@code null} then all rows are included.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in the order that they appear in the selection.
     *      The values will be bound as Strings.
     * @param sortOrder How the rows in the cursor should be sorted.
     *      If this is {@code null}, the sort order is undefined.
     * @return a list of movies from the database
     */
    @Nullable
    List<Movie> selectMovies(
            @Nullable final String[] projection, @Nullable final String selection,
            @Nullable final String[] selectionArgs, @Nullable final String sortOrder);

    //---------------------------------------------------------------------
    // Award methods

    /**
     * Adds an award's details to the database.
     * If the award does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param award the award to insert or update
     * @return the number of rows inserted or updated
     */
    int addAward(@NonNull Award award);

    /**
     * Deletes an award from the database.
     * @param id the id of the award to be deleted
     * @return the number of rows deleted
     */
    int deleteAward(@Nullable String id);

    /**
     * Returns the award with a specified id.
     * @param id the id of the award to be returned
     * @return the award with the specified id
     */
    @Nullable
    Award selectAwardById(@Nullable String id);

    /**
     * Returns a list of awards from the database.
     * @param projection The list of columns to put into the cursor.
     *                   If this is {@code null} all columns are included.
     * @param selection A selection criteria to apply when filtering rows.
     *                  If this is {@code null} then all rows are included.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in the order that they appear in the selection.
     *      The values will be bound as Strings.
     * @param sortOrder How the rows in the cursor should be sorted.
     *      If this is {@code null}, the sort order is undefined.
     * @return a list of awards from the database
     */
    @Nullable
    List<Award> selectAwards(
            @Nullable final String[] projection, @Nullable final String selection,
            @Nullable final String[] selectionArgs, @Nullable final String sortOrder);

    //---------------------------------------------------------------------
    // UserMovie methods

    /**
     * Adds a user movie's details to the database.
     * If the user movie does not exist in the database, it is inserted.
     * If it already exists in the database, it is updated.
     * @param userMovie the user movie to insert or update
     * @return the number of rows inserted or updated. This is currently always 1,
     * but that could change if the local database is implemented in SQLite.
     */
    int addUserMovie(@NonNull UserMovie userMovie);

    /**
     * Deletes all of the signed-in user's user movies from the database.
     * @return the number of rows deleted
     */
    int deleteUserMoviesAll();

    /**
     * Deletes a user movie from the database.
     * @param id the id of the user movie to be deleted
     * @return the number of rows deleted
     */
    int deleteUserMovie(int id);

    /**
     * Returns the movie with a specified movie id.
     * @param id the movie's id
     * @return the user movie with the specified id, or null if there is no matching user movie
     */
    @Nullable
    UserMovie selectUserMovieById(int id);

    //---------------------------------------------------------------------
    // ViewAward methods

    /**
     * Returns the view award with a specified id.
     * @param id the id of the view award to be returned
     * @return the award with the specified id
     */
    @Nullable
    ViewAward selectViewAwardById(@Nullable String id);

    /**
     * Returns a list of view awards from the database.
     * @param projection The list of columns to put into the cursor.
     *                   If this is {@code null} all columns are included.
     * @param selection A selection criteria to apply when filtering rows.
     *                  If this is {@code null} then all rows are included.
     * @param selectionArgs Any ?s included in selection will be replaced by
     *      the values from selectionArgs, in the order that they appear in the selection.
     *      The values will be bound as Strings.
     * @param sortOrder How the rows in the cursor should be sorted.
     *      If this is {@code null}, the sort order is undefined.
     * @return a cursor containing a list of view awards from the database
     */
    @Nullable
    Cursor selectViewAwards(
            @Nullable final String[] projection, @Nullable final String selection,
            @Nullable final String[] selectionArgs, @Nullable final String sortOrder);

}
