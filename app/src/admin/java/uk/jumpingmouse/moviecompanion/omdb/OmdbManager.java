package uk.jumpingmouse.moviecompanion.omdb;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import java.security.InvalidParameterException;

/**
 * The manager class for the OMDb library.
 * Clients of this library can only call methods in this class.
 * @author Edmund Johnson
 */
public class OmdbManager {

    /** The singleton instance of this class. */
    private static OmdbManager sOmdbManager = null;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    public static OmdbManager getInstance() {
        if (sOmdbManager == null) {
            sOmdbManager = new OmdbManager();
        }
        return sOmdbManager;
    }

    /** Private default constructor, to prevent instantiation from outside this class. */
    private OmdbManager() {
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
     * Returns an OMDb runtime as an int, e.g. returns "144 min" as 144
     * @param omdbRuntime the OMDb runtime, e.g. "144 min"
     * @return the runtime as an int, e.g. 144
     */
    public int toIntOmdbRuntime(@Nullable String omdbRuntime) {
        return getOmdbUtils().toIntOmdbRuntime(omdbRuntime);
    }

    /**
     * Returns an OMDb-formatted runtime string representing a number of minutes.
     * @param runtime the runtime in minutes
     * @return an OMDb-formatted runtime string corresponding to the runtime,
     *         or an empty String if runtime indicates an unknown runtime
     */
    @NonNull
    public String toStringOmdbRuntime(final int runtime) {
        return getOmdbUtils().toStringOmdbRuntime(runtime);
    }

    /**
     * Returns a long representing an OMDb-formatted released date as a number of milliseconds.
     * @param omdbReleased an OMDb released date, formatted as "dd MMM yyyy"
     * @return a long object representing omdbReleased as a number of milliseconds,
     *         or RELEASED_UNKNOWN if omdbReleased could not be converted to a long
     */
    public long toLongOmdbReleased(@Nullable final String omdbReleased) {
        return getOmdbUtils().toLongOmdbReleased(omdbReleased);
    }

    /**
     * Returns an OMDb-formatted released date string representing a number of milliseconds.
     * @param released a release date as a number of milliseconds
     * @return an OMDb-formatted released date string corresponding to released, e.g. "21 Apr 2017",
     *         or an empty String if released indicates an unknown release date
     */
    @NonNull
    public String toStringOmdbReleased(final long released) {
        return getOmdbUtils().toStringOmdbReleased(released);
    }
    //---------------------------------------------------------------------
    // Getters

    /**
     * Convenience method which returns a reference to a OmdbUtils object.
     * @return a reference to a OmdbUtils object
     */
    @NonNull
    private static OmdbUtils getOmdbUtils() {
        return OmdbUtils.getInstance();
    }

}
