package uk.jumpingmouse.moviecompanion.omdb;

import android.support.annotation.Nullable;

/**
 * Interface which must be implemented by activities which use the OMDb
 * 'library' to interact with the OMDb API.
 * @author Edmund Johnson
 */
public interface OmdbHandler {

//    /**
//     * Fetches and returns a movie from the Omdb.
//     * @param omdbApiKey the OMDb API key
//     * @param imdbId the movie's IMDb id
//     * @return the movie with the specified IMDb id.
//     */
//    @Nullable
//    @WorkerThread
//    Movie fetchMovie(@Nullable String omdbApiKey, @Nullable String imdbId);

    /**
     * Handles the completion of a movie being fetched from the OMDb API.
     * @param omdbMovie the movie that was fetched
     */
    void onFetchMovieCompleted(@Nullable OmdbMovie omdbMovie);

}
