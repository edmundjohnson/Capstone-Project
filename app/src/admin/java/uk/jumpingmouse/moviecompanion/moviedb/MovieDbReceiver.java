package uk.jumpingmouse.moviecompanion.moviedb;

import android.content.Context;
import android.support.annotation.Nullable;

import uk.jumpingmouse.moviecompanion.data.Movie;

/**
 * Interface for classes which are passed movie information which has been
 * fetched from a remote database.
 * @author Edmund Johnson
 */

public interface MovieDbReceiver {

    /**
     * Handles the completion of a movie being fetched from the movie database.
     * @param movie the movie that was fetched
     */
    void onFetchMovieCompleted(@Nullable Movie movie);

    /**
     * Return the receiver's context.
     * @return the receiver's context
     */
    @Nullable
    Context getReceiverContext();

}
