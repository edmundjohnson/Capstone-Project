package uk.jumpingmouse.moviecompanion.analytics;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

/**
 * Interface for classes which implement analytics.
 * @author Edmund Johnson
 */
public interface AnalyticsManager {

    /**
     * Performs analytics processing required when creating activity.
     * @param activity the activity being created
     */
    void onCreateActivity(@NonNull AppCompatActivity activity);

    /**
     * Logs a user clicking on the IMDb link to a movie.
     * @param imdbId the movie imdbId
     * @param movieTitle the movie title
     */
    void logImdbLink(@NonNull String imdbId, @NonNull String movieTitle);
}
