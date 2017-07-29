package uk.jumpingmouse.moviecompanion.moviedb;

import android.support.annotation.Nullable;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Interface which must be implemented by classes which use the TMDb 'fetch' classes.
 * @author Edmund Johnson
 */
public interface TmdbHandler {

    /**
     * Handles the completion of a movie being fetched from the OMDb API.
     * @param tmdbMovie the movie that was fetched
     */
    void onFetchMovieCompleted(@Nullable MovieDb tmdbMovie);

}
