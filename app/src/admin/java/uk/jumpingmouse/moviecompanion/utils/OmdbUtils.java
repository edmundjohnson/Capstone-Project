package uk.jumpingmouse.moviecompanion.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.net.HttpURLConnection;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.net.OmdbClient;
import uk.jumpingmouse.moviecompanion.net.OmdbMovie;
import uk.jumpingmouse.moviecompanion.net.OmdbServiceGenerator;

/**
 * Class containing utility methods related to the open movie database API.
 * @author Edmund Johnson
 */
public class OmdbUtils {

    /** The singleton instance of this class. */
    private static OmdbUtils sOmdbUtils = null;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    public static OmdbUtils getInstance() {
        if (sOmdbUtils == null) {
            sOmdbUtils = new OmdbUtils();
        }
        return sOmdbUtils;
    }

    /** Private default constructor, to prevent instantiation from outside this class. */
    private OmdbUtils() {
    }

    //---------------------------------------------------------------------
    // Utility methods

    /**
     * Fetches and returns a movie from the Omdb.
     * @param omdbApiKey the OMDb API key
     * @param imdbId the movie's IMDb id
     * @return the movie with the specified IMDb id.
     */
    @Nullable
    @WorkerThread
    public Movie fetchMovie(@Nullable String omdbApiKey, @Nullable String imdbId) {
        if (omdbApiKey == null || imdbId == null || imdbId.isEmpty()) {
            return null;
        }
        // Create a client to read the OMDb API
        OmdbClient client = OmdbServiceGenerator.createService(OmdbClient.class);
        // Create a call to read the JSON
        Call<OmdbMovie> call = client.movieByImdbIdCall(omdbApiKey, imdbId);

        try {
            // Execute the call to obtain the data from the server
            Response response = call.execute();

            if (!response.isSuccessful()) {
                // Handle any connection errors
                Timber.w("movieByImdbIdCall: response.code(): " + response.code());
                switch (response.code()) {
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        //getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_SERVER_INVALID);
                        return null;
                    default:
                        //getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_SERVER_ERROR);
                        return null;
                }
            } else {
                // Response received successfully, parse it to get the OMDb API data
                OmdbMovie omdbMovie = (OmdbMovie) response.body();
                if (omdbMovie == null) {
                    Timber.e("movieByImdbIdCall: response body does not contain OmdbMovie");
                    //getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_SERVER_NO_DATA);
                    return null;
                } else if (omdbMovie.getImdbID() == null) {
                    Timber.e("movieByImdbIdCall: response body does not contain OmdbMovie.imdbID");
                    //getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_SERVER_NO_DATA);
                    return null;
                } else if (omdbMovie.getTitle() == null) {
                    Timber.e("movieByImdbIdCall: response body does not contain OmdbMovie.title");
                    //getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_SERVER_NO_DATA);
                    return null;
                } else {
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    Movie movie = Movie.builder()
                            .id(omdbMovie.getImdbID())
                            .imdbId(omdbMovie.getImdbID())
                            .title(omdbMovie.getTitle())
                            .genre(omdbMovie.getGenre())
                            .runtime(toIntOmdbRuntime(omdbMovie.getRuntime()))
                            .poster(omdbMovie.getPoster())
                            .year(omdbMovie.getYear())
                            .released(DateUtils.toLongOmdbReleased(omdbMovie.getReleased()))
                            .build();
                    // If the movie data has been obtained, return the movie
                    //getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_OK);
                    return movie;
                }
            }
        } catch (Exception e) {
            Timber.e("movieByImdbIdCall: Exception: ", e);
            // This is probably an error parsing the JSON into the Movie object
//            getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_SERVER_DATA_INVALID);
            return null;
        }
    }

    /**
     * Returns an OMDb runtime as an int, e.g. returns "144 min" as 144
     * @param omdbRuntime the OMDb runtime, e.g. "144 min"
     * @return the runtime as an int, e.g. 144
     */
    static int toIntOmdbRuntime(@Nullable String omdbRuntime) {
        if (omdbRuntime != null) {
            String[] split = omdbRuntime.split(" ", 2);
            // split.length is always at least 1
            try {
                return Integer.decode(split[0]);
            } catch (NumberFormatException e) {
                Timber.w(String.format(
                        "NumberFormatException while attempting to decode OMDb runtime to int: \"%s\"",
                        omdbRuntime));
            }
        }
        return Movie.RUNTIME_UNKNOWN;
    }

    /**
     * Returns an OMDb-formatted runtime string representing a number of minutes.
     * @param runtime the runtime in minutes
     * @return an OMDb-formatted runtime string corresponding to the runtime,
     *         or an empty String if runtime indicates an unknown runtime
     */
    @NonNull
    public static String toStringOmdbRuntime(final int runtime) {
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
    @NonNull
    public static String toStringOmdbReleased(final long released) {
        if (released == Movie.RELEASED_UNKNOWN || released < 0) {
            return "";
        }
        Date dateReleased = new Date(released);
        return DateUtils.toStringOmdbReleased(dateReleased);
    }

}
