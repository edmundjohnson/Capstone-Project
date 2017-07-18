package uk.jumpingmouse.omdbapi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import java.security.InvalidParameterException;

/**
 * The manager class for the OMDb library.
 * Clients of this library can only call methods in this class.
 * @author Edmund Johnson
 */
public final class OmdbApi {

    /** The singleton instance of this class. */
    private static OmdbApi sOmdbApi = null;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    public static OmdbApi getInstance() {
        if (sOmdbApi == null) {
            sOmdbApi = new OmdbApi();
        }
        return sOmdbApi;
    }

    /** Private default constructor, to prevent instantiation from outside this class. */
    private OmdbApi() {
    }

    //---------------------------------------------------------------------
    // Methods available to client

    /**
     * Asynchronously fetches an OMDb movie, calling omdbHandler.onFetchMovieCompleted(OmdbMovie)
     * when the OMDb movie has been fetched.
     * @param omdbApiKey the OMDb API key
     * @param imdbId the IMDb id of the movie to fetch
     * @param omdbHandler the OMDb handler to be called back on completion
     */
    @UiThread
    public void fetchMovie(@Nullable String omdbApiKey, @Nullable String imdbId,
                           @Nullable OmdbHandler omdbHandler) throws InvalidParameterException {
        if (omdbApiKey == null || omdbApiKey.trim().isEmpty()) {
            throw new InvalidParameterException("The omdbApiKey passed to fetchMovie was null or empty");
        }
        if (imdbId == null || imdbId.trim().isEmpty()) {
            throw new InvalidParameterException("The imdbId passed to fetchMovie was null or empty");
        }
        if (omdbHandler == null) {
            throw new InvalidParameterException("The omdbHandler passed to fetchMovie was null");
        }

        new OmdbFetchMovieTask(omdbHandler).execute(imdbId, omdbApiKey);
    }

    /**
     * Returns an OMDb 'runtime' String as an int, e.g. returns "144 min" as 144
     * @param omdbRuntime the OMDb runtime, e.g. "144 min"
     * @return the runtime as an int, e.g. 144,
     *         or OmdbMovie.RUNTIME_UNKNOWN if omdbRuntime could not be converted to an int
     */
    public static int toIntOmdbRuntime(@Nullable String omdbRuntime) {
        return OmdbUtils.toIntOmdbRuntime(omdbRuntime);
    }

    /**
     * Returns a long representing an OMDb-formatted released date as a number of milliseconds.
     * @param omdbReleased an OMDb released date, formatted as "dd MMM yyyy"
     * @return a long representing omdbReleased as a number of milliseconds,
     *         or OmdbMovie.RELEASED_UNKNOWN if omdbReleased could not be converted to a long
     */
    public static long toLongOmdbReleased(@Nullable final String omdbReleased) {
        return OmdbUtils.toLongOmdbReleased(omdbReleased);
    }

}
