package uk.jumpingmouse.moviecompanion.moviedb;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbFind;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.FindResults;
import info.movito.themoviedbapi.model.MovieDb;

import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;

/**
 * A task which fetches a movie from the TMDb and returns it to a supplied handler.
 * @author Edmund Johnson
 */
final class TmdbTaskFetchMovie extends AsyncTask<String, Integer, MovieDb> {

    private static final String LANGUAGE_DEFAULT = "en";

    private final WeakReference<TmdbHandler> mTmdbHandler;

    //---------------------------------------------------------------------
    // Lifecycle methods

    /**
     * Constructor.
     * @param tmdbHandler the handler object which initiated this task
     */
    TmdbTaskFetchMovie(@NonNull TmdbHandler tmdbHandler) {
        super();
        mTmdbHandler = new WeakReference<>(tmdbHandler);
    }

    /**
     * Fetches a movie from the TMDb and returns it.
     * This is run in the background thread.
     * @param args the arguments passed to the background task, the first is the IMDb id
     * @return the TMDb movie with the specified IMDb id, or null if the movie was not found
     */
    @Override
    @WorkerThread
    @Nullable
    protected MovieDb doInBackground(@Nullable final String... args) {
        if (args == null || args.length < 2) {
            Timber.e("TmdbTaskFetchMovie was not passed the required arguments");
            return null;
        }

        String tmdbApiKey = args[0];
        if (tmdbApiKey == null || tmdbApiKey.isEmpty()) {
            Timber.e("tmdbApiKey is null or empty");
            return null;
        }
        TmdbApi tmdbApi = new TmdbApi(tmdbApiKey);

        String imdbId = args[1];
        if (imdbId == null || imdbId.isEmpty()) {
            Timber.e("imdbId is null or empty");
            return null;
        }

        // fetch and return the Movie
        return fetchMovie(tmdbApi, imdbId);
    }

    /**
     * This is run in the UI thread when doInBackground() has completed.
     * @param tmdbMovie the movie that was fetched in the background
     */
    @Override
    @UiThread
    protected void onPostExecute(@NonNull final MovieDb tmdbMovie) {
        if (mTmdbHandler.get() != null) {
            mTmdbHandler.get().onFetchMovieCompleted(tmdbMovie);
        }
    }

    //---------------------------------------------------------------------
    // Fetch data from TMDb

    /**
     * Fetches and returns a movie from the TMDb.
     * Logs very specifically any problems, to help users of the library see what's going on.
     * @param tmdbApi the TMDb API object
     * @param imdbId the movie's IMDb id
     * @return the TMDb movie with the specified TMDb id, or null if the movie was not found.
     */
    @Nullable
    @WorkerThread
    private MovieDb fetchMovie(@NonNull TmdbApi tmdbApi, @NonNull String imdbId) {

        TmdbFind tmdbFind = tmdbApi.getFind();
        FindResults findResults = tmdbFind.find(
                imdbId, TmdbFind.ExternalSource.imdb_id, LANGUAGE_DEFAULT);
        List<MovieDb> movieList = findResults.getMovieResults();

        if (movieList == null || movieList.size() == 0) {
            Timber.e("Movie not found with imdbId: " + imdbId);
            return null;
        } else if (movieList.size() > 1) {
            Timber.e("More than one movie found with imdbId: " + imdbId);
            return null;
        }

        MovieDb movieDbLite = movieList.get(0);

        // We now have the right MovieDb, but without many of the fields we need.
        // For that, a further query is required.
        TmdbMovies tmdbMovies = tmdbApi.getMovies();
        return tmdbMovies.getMovie(movieDbLite.getId(), LANGUAGE_DEFAULT,
                TmdbMovies.MovieMethod.credits, TmdbMovies.MovieMethod.images,
                TmdbMovies.MovieMethod.releases);
    }

}
