package uk.jumpingmouse.moviecompanion.analytics;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * A Firebase implementation of AnalyticsManager.
 * @author Edmund Johnson
 */
public final class AnalyticsManagerFirebase implements AnalyticsManager {

    // Content types
    // User signing into app
    private static final String CONTENT_TYPE_USER_SIGN_IN = "CONTENT_TYPE_USER_SIGN_IN";
    // View a movie within the app
    private static final String CONTENT_TYPE_VIEW_MOVIE = "CONTENT_TYPE_VIEW_MOVIE";
    // Click on the IMDb link for a movie
    private static final String CONTENT_TYPE_IMDB_LINK_MOVIE = "CONTENT_TYPE_IMDB_LINK_MOVIE";

    // The singleton instance of this class.
    private static AnalyticsManagerFirebase sAnalyticsManager;

    // Firebase analytics instance
    private FirebaseAnalytics mFirebaseAnalytics;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Return a Firebase implementation of AnalyticsManager.
     * @return a Firebase implementation of AnalyticsManager
     */
    public static AnalyticsManager getInstance() {
        if (sAnalyticsManager == null) {
            sAnalyticsManager = new AnalyticsManagerFirebase();
        }
        return sAnalyticsManager;
    }

    /** Private default constructor to prevent instantiation from outside this class. */
    private AnalyticsManagerFirebase() {
    }

    //---------------------------------------------------------------------
    // Lifecycle methods

    /**
     * Performs analytics processing required when creating activity.
     * @param activity the activity being created
     */
    @Override
    public void onCreateActivity(@NonNull final AppCompatActivity activity) {
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
    }

    /**
     * Logs a user signing into the app.
     * @param uid the user uid
     * @param providerId the provider id
     */
    @Override
    public void logUserSignIn(@NonNull String uid, @NonNull String providerId) {
        logSelectContentEvent(CONTENT_TYPE_USER_SIGN_IN, uid, providerId);
    }

    /**
     * Logs a user clicking on a list item to go to the movie page.
     * @param movieId the movie id
     * @param movieTitle the movie title
     */
    @Override
    public void logViewMovie(@NonNull String movieId, @NonNull String movieTitle) {
        logSelectContentEvent(CONTENT_TYPE_VIEW_MOVIE, movieId, movieTitle);
    }

    /**
     * Logs a user clicking on the IMDb link to a movie.
     * @param imdbId the movie imdbId
     * @param movieTitle the movie title
     */
    @Override
    public void logImdbLink(@NonNull String imdbId, @NonNull String movieTitle) {
        logSelectContentEvent(CONTENT_TYPE_IMDB_LINK_MOVIE, imdbId, movieTitle);
    }

    /**
     * Logs a Firebase SELECT_CONTENT event.
     * @param id the event id
     * @param name the event name
     * @param contentType the event content type
     */
    private void logSelectContentEvent(
            @SuppressWarnings("SameParameterValue") @NonNull String contentType,
            @NonNull String id, @NonNull String name) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    //---------------------------------------------------------------------
    // Getters

    /**
     * Returns a reference to the FirebaseAnalytics instance.
     * @param context the context
     * @return a reference to the FirebaseAnalytics instance
     */
    @Nullable
    private FirebaseAnalytics getFirebaseAnalytics(@Nullable Context context) {
        // We do not need a non-null context if mFirebaseAnalytics is not null
        if (mFirebaseAnalytics != null) {
            return mFirebaseAnalytics;
        }
        if (context != null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
        return mFirebaseAnalytics;
    }

}
