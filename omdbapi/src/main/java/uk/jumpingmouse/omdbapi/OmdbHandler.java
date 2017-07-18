package uk.jumpingmouse.omdbapi;

import android.support.annotation.Nullable;

/**
 * Interface which must be implemented by classes which use the OMDb
 * 'library' to interact with the OMDb API.
 * @author Edmund Johnson
 */
public interface OmdbHandler {

    /**
     * Handles the completion of a movie being fetched from the OMDb API.
     * @param omdbMovie the movie that was fetched
     */
    void onFetchMovieCompleted(@Nullable OmdbMovie omdbMovie);

}
