package uk.jumpingmouse.omdbapi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import java.security.InvalidParameterException;
import java.util.Date;

/**
 * The manager class for the OMDb library.
 * Clients of this library can only call methods in this class.
 * @author Edmund Johnson
 */
public final class OmdbApi {

    /**
     * The singleton instance of this class.
     */
    private static OmdbApi sOmdbApi = null;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     *
     * @return an instance of this class
     */
    @NonNull
    public static OmdbApi getInstance() {
        if (sOmdbApi == null) {
            sOmdbApi = new OmdbApi();
        }
        return sOmdbApi;
    }

    /**
     * Private default constructor, to prevent instantiation from outside this class.
     */
    private OmdbApi() {
    }

    //---------------------------------------------------------------------
    // Methods available to client

    /**
     * Asynchronously fetches an OMDb movie, calling omdbHandler.onFetchMovieCompleted(OmdbMovie)
     * when the OMDb movie has been fetched.
     *
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
     * Returns a Date object representing an OMDb-formatted date string, e.g. "11 Jun 2016".
     * @param strDate an OMDb-formatted date string, e.g. "11 Jun 2016"
     * @return a Date object representing the supplied String,
     *         or null if the String could not be parsed as a date
     */
    public static Date toDateOmdbReleased(@Nullable final String strDate) {
        return OmdbUtils.toDateOmdbReleased(strDate);
    }

}
