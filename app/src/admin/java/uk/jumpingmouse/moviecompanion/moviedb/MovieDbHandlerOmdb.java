package uk.jumpingmouse.moviecompanion.moviedb;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.util.Date;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.utils.ModelUtils;
import uk.jumpingmouse.moviecompanion.utils.NetUtils;
import uk.jumpingmouse.omdbapi.OmdbApi;
import uk.jumpingmouse.omdbapi.OmdbHandler;
import uk.jumpingmouse.omdbapi.OmdbMovie;

/**
 * Class which handles fetching movie information using the OMDb API.
 * @author Edmund Johnson
 */

public class MovieDbHandlerOmdb implements MovieDbHandler, OmdbHandler {

    private MovieDbReceiver mMovieDbReceiver;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Constructor.
     * @param movieDbReceiver the receiver to be passed information fetched from
     *                        the remote database.
     */
    private MovieDbHandlerOmdb(@NonNull MovieDbReceiver movieDbReceiver) {
        mMovieDbReceiver = movieDbReceiver;
    }

    /**
     * Returns a new instance of this class.
     * A new instance of the class must be created for OMDb client class, otherwise
     * the results of successive calls could be passed to the wrong MovieDbReceiver.
     * @return a new instance of this class
     */
    @NonNull
    public static MovieDbHandler newInstance(@NonNull MovieDbReceiver movieDbReceiver) {
        return new MovieDbHandlerOmdb(movieDbReceiver);
    }

    //---------------------------------------------------------------------
    // Implementation of the MovieDbHandler interface

    /**
     * Fetches a movie from the remote database.
     * @param imdbId the movie's IMDb id
     * @return a status code, with STATUS_SUCCESS indicating that the operation can be attempted,
     *         any other value indicating that an error has occurred, the operation cannot
     *         be attempted, and movieDbReceiver.onFetchMovieCompleted(...) will not be called
     */
    @Override
    public int fetchMovieByImdbId(@NonNull String imdbId) {
        Context context = getContext();
        if (context == null) {
            return STATUS_RECEIVER_CONTEXT_IS_NULL;
        }
        if (!getNetUtils().isNetworkAvailable(context)) {
            return STATUS_NETWORK_UNAVAILABLE;
        }

        // Attempt to get the OMDb API key, returning a failure status if unsuccessful
        String apiKey = getApiKey(context);
        if (apiKey == null) {
            return STATUS_OMDB_API_KEY_KEY_NOT_FOUND;
        }
        // An API key was obtained, fetch the movie from the remote database
        getOmdbApi().fetchMovie(apiKey, imdbId, this);

        return STATUS_SUCCESS;
    }

    /**
     * Returns a string resource for a message corresponding to a MovieDbHandler status code.
     * @param status a MovieDbHandler status code
     * @return a string resource for a message corresponding to status
     */
    public @StringRes int getErrorMessageForStatus(int status) {
        switch (status) {
            case STATUS_RECEIVER_CONTEXT_IS_NULL:
                return R.string.receiver_context_is_null;
            case STATUS_NETWORK_UNAVAILABLE:
                return R.string.network_unavailable;
            case STATUS_OMDB_API_KEY_KEY_NOT_FOUND:
                return R.string.omdbapi_key_missing;
            case STATUS_SUCCESS:
                return R.string.db_handler_success;
            default:
                return R.string.unrecognised_error_status;
        }
    }

    //---------------------------------------------------------------------
    // Implementation of the OmdbHandler interface

    /**
     * Handles the completion of a movie being fetched from the movie database,
     * passing the movie to the MovieDbReceiver.
     * This is called by the OmdbApi library.
     * @param omdbMovie the movie that was fetched
     */
    @Override
    public void onFetchMovieCompleted(@Nullable OmdbMovie omdbMovie) {
        // Convert the received 'remote' movie into a 'local' movie
        Movie movie = newMovie(omdbMovie);

        // Pass the movie to the receiver, if the receiver still exists.
        // Make this call even if the movie is null, so the receiver can display an
        // error message.
        if (mMovieDbReceiver != null) {
            mMovieDbReceiver.onFetchMovieCompleted(movie);
        }
    }

    //---------------------------------------------------------------------
    // Private methods

    /**
     * Returns the API key for the OMDb interface.
     * @return the API key for the OMDb interface
     */
    @Nullable
    private String getApiKey(@NonNull Context context) {
        // Get the OMDb API key from the local.properties file
        String apiKey = context.getString(R.string.omdbapi_key);
        return apiKey.trim().isEmpty() ? null : apiKey.trim();
    }

    /**
     * Return the context, obtained through the MovieDbReceiver.
     * @return the context
     */
    @Nullable
    private Context getContext() {
        if (mMovieDbReceiver == null) {
            return null;
        }
        return mMovieDbReceiver.getReceiverContext();
    }

    /**
     * Creates a Movie from an OmdbMovie and returns it.
     * @param omdbMovie the OmdbMovie
     * @return a Movie corresponding to omdbMovie
     */
    @Nullable
    private static Movie newMovie(OmdbMovie omdbMovie) {
        if (omdbMovie == null) {
            Timber.w("newMovie: omdbMovie is null");
            return null;
        } else if (omdbMovie.getImdbID() == null) {
            Timber.w("newMovie: omdbMovie.imdbId is null");
            return null;
        } else if (omdbMovie.getTitle() == null) {
            Timber.w("newMovie: omdbMovie.title is null");
            return null;
        }

        // Build and return the movie
        String id = ModelUtils.imdbIdToMovieId(omdbMovie.getImdbID());
        if (id == null) {
            Timber.w("newMovie: could not obtain valid id from imdbID: " + omdbMovie.getImdbID());
            return null;
        }

        int runtime = toIntOmdbRuntime(omdbMovie.getRuntime());
        long released = toLongOmdbReleased(omdbMovie.getReleased());

        return Movie.builder()
                .id(id)
                .imdbId(omdbMovie.getImdbID())
                .title(omdbMovie.getTitle())
                .year(omdbMovie.getYear())
                .rated(omdbMovie.getRated())
                .released(released)
                .runtime(runtime)
                .genre(omdbMovie.getGenre())
                .director(omdbMovie.getDirector())
                .writer(omdbMovie.getWriter())
                .actors(omdbMovie.getActors())
                .plot(omdbMovie.getPlot())
                .language(omdbMovie.getLanguage())
                .country(omdbMovie.getCountry())
                .poster(omdbMovie.getPoster())
                .build();
    }

    /**
     * Returns a long representing an OMDb-formatted released date as a number of milliseconds.
     * @param omdbReleased an OMDb released date, formatted as "dd MMM yyyy"
     * @return a long object representing omdbReleased as a number of milliseconds,
     *         or OmdbMovie.RELEASED_UNKNOWN if omdbReleased could not be converted to a long
     */
    private static long toLongOmdbReleased(@Nullable final String omdbReleased) {
        Date dateReleased = OmdbApi.toDateOmdbReleased(omdbReleased);
        if (dateReleased == null) {
            return Movie.RELEASED_UNKNOWN;
        } else {
            return dateReleased.getTime();
        }
    }

    /**
     * Returns an OMDb runtime as an int, e.g. returns "144 min" as 144
     * @param omdbRuntime the OMDb runtime, e.g. "144 min"
     * @return the runtime as an int, e.g. 144,
     *         or Movie.RUNTIME_UNKNOWN if omdbRuntime could not be converted to an int
     */
    private static int toIntOmdbRuntime(@Nullable String omdbRuntime) {
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

    //---------------------------------------------------------------------
    // Getters

    /**
     * Convenience method which returns a reference to an OmdbApi object.
     * @return a reference to an OmdbApi object
     */
    @NonNull
    private static OmdbApi getOmdbApi() {
        return OmdbApi.getInstance();
    }

    /**
     * Convenience method which returns a reference to a NetUtils object.
     * @return a reference to a NetUtils object
     */
    private NetUtils getNetUtils() {
        return ObjectFactory.getNetUtils();
    }

}
