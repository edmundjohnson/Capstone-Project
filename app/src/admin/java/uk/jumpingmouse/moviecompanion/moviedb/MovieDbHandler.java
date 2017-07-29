package uk.jumpingmouse.moviecompanion.moviedb;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Interface for classes which handle fetching movie information from a remote database.
 * @author Edmund Johnson
 */
public interface MovieDbHandler {

    /** Return value from fetch methods, indicating success. */
    int STATUS_SUCCESS = 0;
    /** Status indicating that the receiver no longer exists. */
    int STATUS_RECEIVER_CONTEXT_IS_NULL = 100;
    /** Status indicating that there is no internet connection. */
    int STATUS_NETWORK_UNAVAILABLE = 101;
    /** Status indicating that the TMDb API key was not found. */
    int STATUS_TMDB_API_KEY_KEY_NOT_FOUND = 103;
    /** Status indicating that the OMDb API key was not found. */
    int STATUS_OMDB_API_KEY_KEY_NOT_FOUND = 104;

    /**
     * Fetches a movie from the remote database.
     * @param imdbId the movie's IMDb id
     * @return a status code, with STATUS_SUCCESS indicating that the operation can be attempted,
     *         any other value indicating that an error has occurred, the operation cannot
     *         be attempted, and movieDbReceiver.onFetchMovieCompleted(...) will not be called
     */
    int fetchMovieByImdbId(@NonNull String imdbId);

    /**
     * Returns a string resource for a message corresponding to a MovieDbHandler status code.
     * @param status a MovieDbHandler status code
     * @return a string resource for a message corresponding to status
     */
    @StringRes int getErrorMessageForStatus(int status);

}
