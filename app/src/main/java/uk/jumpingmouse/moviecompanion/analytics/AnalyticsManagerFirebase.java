package uk.jumpingmouse.moviecompanion.analytics;

import com.google.firebase.analytics.FirebaseAnalytics;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * A Firebase implementation of AnalyticsManager.
 * @author Edmund Johnson
 */
public class AnalyticsManagerFirebase implements AnalyticsManager {

    // A link to a movie on the IMDb website
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
     * Logs a user clicking on the IMDb link to a movie.
     * @param movieId the movie id
     * @param movieTitle the movie title
     */
    @Override
    public void logImdbLink(int movieId, @NonNull String movieTitle) {
        logSelectContentEvent(Integer.toString(movieId), movieTitle, CONTENT_TYPE_IMDB_LINK_MOVIE);
    }

    /**
     * Logs a Firebase SELECT_CONTENT event.
     * @param context the context
     * @param itemId the event id
     * @param itemName the event name
     * @param contentType the event content type
     */
    private void logEventSelectContent(@Nullable Context context, @NonNull String itemId,
                                       @NonNull String itemName, @NonNull String contentType) {
        FirebaseAnalytics firebaseAnalytics = getFirebaseAnalytics(context);

        if (firebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);

            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
    }

    /**
     * Logs a Firebase SELECT_CONTENT event.
     * @param id the event id
     * @param name the event name
     * @param contentType the event content type
     */
    private void logSelectContentEvent(@NonNull String id, @NonNull String name,
                                       @SuppressWarnings("SameParameterValue") @NonNull String contentType) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
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
