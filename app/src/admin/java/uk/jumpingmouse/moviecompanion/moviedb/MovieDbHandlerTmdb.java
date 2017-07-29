package uk.jumpingmouse.moviecompanion.moviedb;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.utils.ModelUtils;
import uk.jumpingmouse.moviecompanion.utils.NetUtils;

/**
 * Class which handles fetching movie information using the OMDb API.
 * @author Edmund Johnson
 */

public class MovieDbHandlerTmdb implements MovieDbHandler, TmdbHandler {

    private MovieDbReceiver mMovieDbReceiver;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Constructor.
     * @param movieDbReceiver the receiver to be passed information fetched from
     *                        the remote database.
     */
    private MovieDbHandlerTmdb(@NonNull MovieDbReceiver movieDbReceiver) {
        mMovieDbReceiver = movieDbReceiver;
    }

    /**
     * Returns a new instance of this class.
     * A new instance of the class must be created for OMDb client class, otherwise
     * the results of successive calls could be passed to the wrong MovieDbReceiver.
     * @param movieDbReceiver the receiver which is to be passed the fetched data
     * @return a new instance of this class
     */
    @NonNull
    public static MovieDbHandler newInstance(@NonNull MovieDbReceiver movieDbReceiver) {
        return new MovieDbHandlerTmdb(movieDbReceiver);
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

        // Attempt to get the TMDb API key, returning a failure status if unsuccessful
        String apiKey = getApiKey(context);
        if (apiKey == null) {
            return STATUS_TMDB_API_KEY_KEY_NOT_FOUND;
        }
//        // An API key was obtained, fetch the movie from the remote database
//        getOmdbApi().fetchMovie(apiKey, imdbId, this);

        // TODO: fetch the movie from TMDb
        new TmdbFetchMovieTask(this).execute(imdbId, apiKey);

        return STATUS_SUCCESS;
    }

    /**
     * Returns a string resource for a message corresponding to a MovieDbHandler status code.
     * @param status a MovieDbHandler status code
     * @return a string resource for a message corresponding to status
     */
    @Override
    public int getErrorMessageForStatus(int status) {
        return MovieDbUtils.getErrorMessageForStatus(status);
    }

    //---------------------------------------------------------------------
    // Implementation of the TmdbHandler interface

    /**
     * Handles the completion of a movie being fetched from TMDb.
     * @param tmdbMovie the movie that was fetched
     */
    @Override
    public void onFetchMovieCompleted(@Nullable MovieDb tmdbMovie) {
        Timber.i("tmdbMovie = " + tmdbMovie);
        Movie movie = newMovie(tmdbMovie);
        mMovieDbReceiver.onFetchMovieCompleted(movie);
    }

    //---------------------------------------------------------------------
    // Private methods

    /**
     * Returns the API key for the OMDb interface.
     * @param context the context
     * @return the API key for the OMDb interface
     */
    @Nullable
    private String getApiKey(@NonNull Context context) {
        // Get the TMDb API key from the local.properties file
        String apiKey = context.getString(R.string.tmdbapi_key);
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
     * Creates a Movie from an TMDb movie and returns it.
     * @param tmdbMovie the TMDb movie
     * @return a Movie corresponding to tmdbMovie
     */
    @Nullable
    private Movie newMovie(@Nullable MovieDb tmdbMovie) {
        if (tmdbMovie == null) {
            Timber.w("newMovie: tmdbMovie is null");
            return null;
        } else if (tmdbMovie.getImdbID() == null) {
            Timber.w("newMovie: tmdbMovie.imdbId is null");
            return null;
        } else if (tmdbMovie.getTitle() == null) {
            Timber.w("newMovie: tmdbMovie.title is null");
            return null;
        }

        // Build and return the movie
        String id = ModelUtils.imdbIdToMovieId(tmdbMovie.getImdbID());
        if (id == null) {
            Timber.w("newMovie: could not obtain valid id from imdbID: " + tmdbMovie.getImdbID());
            return null;
        }

        // TODO - required field
        //long released = toLongTmdbReleased(tmdbMovie.getReleased());
        long released = 0;

        String genre = getMovieGenreCsvFromTmdbMovieGenre(tmdbMovie.getGenres());

        return Movie.builder()
                .id(id)
                .imdbId(tmdbMovie.getImdbID())
                .title(tmdbMovie.getTitle())
                //.year(tmdbMovie.getYear())
                //.rated(tmdbMovie.getRated())
                .released(released)
                .runtime(tmdbMovie.getRuntime())
                .genre(genre)
                //.director(tmdbMovie.getDirector())
                //.writer(tmdbMovie.getWriter())
                //.actors(tmdbMovie.getActors())
                .plot(tmdbMovie.getOverview())
                //.language(tmdbMovie.getLanguage())
                //.country(tmdbMovie.getCountry())
                //.poster(tmdbMovie.getPoster())
                .build();
    }

    @Nullable
    private String getMovieGenreCsvFromTmdbMovieGenre(@Nullable List<Genre> tmdbGenres) {
        StringBuilder genre = new StringBuilder();
        if (tmdbGenres == null) {
            return null;
        }
        for (Genre tmdbGenre : tmdbGenres) {
            if (genre.length() > 0) {
                genre.append(",");
            }
            genre.append(tmdbGenre.getId());
        }
        return genre.toString();
    }

//    /**
//     * Returns a long representing an OMDb-formatted released date as a number of milliseconds.
//     * @param omdbReleased an OMDb released date, formatted as "dd MMM yyyy"
//     * @return a long object representing omdbReleased as a number of milliseconds,
//     *         or OmdbMovie.RELEASED_UNKNOWN if omdbReleased could not be converted to a long
//     */
//    private static long toLongOmdbReleased(@Nullable final String omdbReleased) {
//        Date dateReleased = OmdbApi.toDateOmdbReleased(omdbReleased);
//        if (dateReleased == null) {
//            return Movie.RELEASED_UNKNOWN;
//        } else {
//            return dateReleased.getTime();
//        }
//    }

    //---------------------------------------------------------------------
    // Getters

    /**
     * Convenience method which returns a reference to a NetUtils object.
     * @return a reference to a NetUtils object
     */
    private NetUtils getNetUtils() {
        return ObjectFactory.getNetUtils();
    }

}
