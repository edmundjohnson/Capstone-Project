package uk.jumpingmouse.moviecompanion.moviedb;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.config.TmdbConfiguration;

import java.lang.ref.WeakReference;

import timber.log.Timber;

/**
 * A task which fetches the TMDb configuration and returns it to a supplied handler.
 * @author Edmund Johnson
 */
final class TmdbTaskFetchConfiguration extends AsyncTask<String, Integer, TmdbConfiguration> {

    private static final String LANGUAGE_DEFAULT = "en";

    private final WeakReference<TmdbHandler> mTmdbHandler;

    //---------------------------------------------------------------------
    // Lifecycle methods

    /**
     * Constructor.
     * @param tmdbHandler the handler object which initiated this task
     */
    TmdbTaskFetchConfiguration(@NonNull TmdbHandler tmdbHandler) {
        super();
        mTmdbHandler = new WeakReference<>(tmdbHandler);
    }

    /**
     * Fetches the TMDb configuration and returns it.
     * This is run in the background thread.
     * @param args the arguments passed to the background task, the first is the IMDb id
     * @return the TMDb configuration, or null if the TMDb configuration could not be determined
     */
    @Override
    @WorkerThread
    @Nullable
    protected TmdbConfiguration doInBackground(@Nullable final String... args) {
        if (args == null || args.length < 0) {
            Timber.e("TmdbTaskFetchConfiguration was not passed the required argument (API key)");
            return null;
        }

        String tmdbApiKey = args[0];
        if (tmdbApiKey == null || tmdbApiKey.isEmpty()) {
            Timber.e("tmdbApiKey is null or empty");
            return null;
        }
        TmdbApi tmdbApi = new TmdbApi(tmdbApiKey);

        // fetch and return the TMDb configuration
        TmdbConfiguration tmdbConfiguration = tmdbApi.getConfiguration();
        if (tmdbConfiguration == null) {
            Timber.e("The TMDb configuration could not be obtained TMDb");
        }
        return tmdbConfiguration;
    }

    /**
     * This is run in the UI thread when doInBackground() has completed.
     * @param tmdbConfiguration the TMDb configuration that was fetched in the background
     */
    @Override
    @UiThread
    protected void onPostExecute(@NonNull final TmdbConfiguration tmdbConfiguration) {
        if (mTmdbHandler.get() != null) {
            mTmdbHandler.get().onFetchTmdbConfigurationCompleted(tmdbConfiguration);
        }
    }

}
