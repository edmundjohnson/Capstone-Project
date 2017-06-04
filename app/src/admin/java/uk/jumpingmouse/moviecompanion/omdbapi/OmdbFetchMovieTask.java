package uk.jumpingmouse.moviecompanion.omdbapi;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

/**
 * A task which fetches a movie from the OMDb and displays it on the screen.
 * @author Edmund Johnson
 */
class OmdbFetchMovieTask extends AsyncTask<String, Integer, OmdbMovie> {
    private final WeakReference<OmdbHandler> mOmdbHandler;

    //---------------------------------------------------------------------
    // Lifecycle methods

    /**
     * Constructor.
     * @param omdbHandler the activity which initiated this task
     */
    OmdbFetchMovieTask(@NonNull OmdbHandler omdbHandler) {
        super();
        mOmdbHandler = new WeakReference<>(omdbHandler);
    }

    /**
     * Fetches a movie from the OMDb and returns it.
     * This is run in the background thread.
     * @param args the arguments passed to the background task, the first is the IMDb id
     * @return the OMDb movie with the specified IMDb id, or null if the movie was not found
     */
    @Override
    @WorkerThread
    @Nullable
    protected OmdbMovie doInBackground(@Nullable final String... args) {
        if (mOmdbHandler == null || args == null || args.length < 2) {
            return null;
        }
        String imdbId = args[0];
        String omdbApiKey = args[1];
        // fetch and return the Movie
        return fetchMovie(omdbApiKey, imdbId);
    }

    /**
     * This is run in the UI thread when doInBackground() has completed.
     * @param omdbMovie the movie that was fetched in the background
     */
    @Override
    @UiThread
    protected void onPostExecute(final OmdbMovie omdbMovie) {
        if (mOmdbHandler != null && mOmdbHandler.get() != null) {
            mOmdbHandler.get().onFetchMovieCompleted(omdbMovie);
        }
    }

    //---------------------------------------------------------------------
    // fetchMovie

    /**
     * Fetches and returns a movie from the OMDb.
     * Logs very specifically any problems, to help users of the library see what's going on.
     * @param omdbApiKey the OMDb API key
     * @param imdbId the movie's IMDb id
     * @return the OMDb movie with the specified IMDb id, or null if the movie was not found.
     */
    @Nullable
    @WorkerThread
    private OmdbMovie fetchMovie(@Nullable String omdbApiKey, @Nullable String imdbId) {
        if (omdbApiKey == null || omdbApiKey.isEmpty()) {
            Timber.e("omdbApiKey is null or empty");
            return null;
        }
        if (imdbId == null || imdbId.isEmpty()) {
            Timber.e("imdbId is null or empty");
            return null;
        }
        // Create a client to read the OMDb API
        OmdbClient client = OmdbServiceGenerator.createService(OmdbClient.class);
        // Create a call to read the JSON
        Call<OmdbMovie> call = client.movieByImdbIdCall(omdbApiKey, imdbId);

        Response response;
        try {
            // Execute the call to obtain the data from OMDb
            response = call.execute();
        } catch (IOException e) {
            Timber.e("ERROR: IOException while fetching movie from OMDb: ", e);
            return null;
        } catch (Exception e) {
            Timber.e("ERROR: Exception while fetching movie from OMDb: ", e);
            return null;
        }

        try {
            if (!response.isSuccessful()) {
                // Handle any connection errors
                Timber.e("Response code from OMDb indicates a failure: " + response.code());
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
                if (!(response.body() instanceof OmdbMovie)) {
                    Timber.e("Response body received from OMDb did not contain an OmdbMovie");
                    return null;
                }
                OmdbMovie omdbMovie = (OmdbMovie) response.body();
                if (omdbMovie == null) {
                    Timber.w("Response body from OMDb did not contain an OmdbMovie: \""
                            + response.body().toString() + "\"");
                }
                //if (omdbMovie == null) {
                //    getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_SERVER_NO_DATA);
                //} else {
                //    getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_OK);
                //}
                // success!
                return omdbMovie;
            }
        } catch (Exception e) {
            // This is probably an error parsing the JSON into the OmdbMovie object
            //getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_SERVER_DATA_INVALID);
            Timber.e("ERROR: Exception while fetching OmdbMovie from OMDb: ", e);
            return null;
        }
    }

}
