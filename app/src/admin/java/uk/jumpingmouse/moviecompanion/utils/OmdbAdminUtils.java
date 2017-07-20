package uk.jumpingmouse.moviecompanion.utils;

import android.support.annotation.Nullable;

import java.util.Date;

import timber.log.Timber;

import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.omdbapi.OmdbApi;
import uk.jumpingmouse.omdbapi.OmdbMovie;

/**
 * Class for utilities related to the OMDb interface.
 * @author Edmund Johnson
 */
public final class OmdbAdminUtils {

    /**
     * Private default constructor to prevent instantiation.
     */
    private OmdbAdminUtils() {
    }

    //---------------------------------------------------------------------
    // Movie methods

    /**
     * Creates a Movie from an OmdbMovie and returns it.
     * @param omdbMovie the OmdbMovie
     * @return a Movie corresponding to omdbMovie
     */
    @Nullable
    public static Movie newMovie(OmdbMovie omdbMovie) {
        if (omdbMovie == null) {
            Timber.w("newMovie: omdbMovie is null");
            return null;
        } else if (omdbMovie.getImdbID() == null) {
            Timber.w("newMovie: omdbMovie.imdbId is null");
            return null;
        } else if (omdbMovie.getTitle() == null) {
            Timber.w("newMovie: omdbMovie.title is null");
            return null;
        }

        // Build and return the movie
        int id = ModelUtils.imdbIdToMovieId(omdbMovie.getImdbID());
        if (id == Movie.ID_UNKNOWN) {
            Timber.w("newMovie: could not obtain valid id from imdbID: " + omdbMovie.getImdbID());
            return null;
        }

        int runtime = toIntOmdbRuntime(omdbMovie.getRuntime());
        long released = toLongOmdbReleased(omdbMovie.getReleased());

        return Movie.builder()
                .id(id)
                .imdbId(omdbMovie.getImdbID())
                .title(omdbMovie.getTitle())
                .genre(omdbMovie.getGenre())
                .runtime(runtime)
                .poster(omdbMovie.getPoster())
                .year(omdbMovie.getYear())
                .released(released)
                .build();
    }

    /**
     * Returns a long representing an OMDb-formatted released date as a number of milliseconds.
     * @param omdbReleased an OMDb released date, formatted as "dd MMM yyyy"
     * @return a long object representing omdbReleased as a number of milliseconds,
     *         or OmdbMovie.RELEASED_UNKNOWN if omdbReleased could not be converted to a long
     */
    private static long toLongOmdbReleased(@Nullable final String omdbReleased) {
        Date dateReleased = OmdbApi.toDateOmdbReleased(omdbReleased);
        if (dateReleased == null) {
            return Movie.RELEASED_UNKNOWN;
        } else {
            return dateReleased.getTime();
        }
    }

    /**
     * Returns an OMDb runtime as an int, e.g. returns "144 min" as 144
     * @param omdbRuntime the OMDb runtime, e.g. "144 min"
     * @return the runtime as an int, e.g. 144,
     *         or Movie.RUNTIME_UNKNOWN if omdbRuntime could not be converted to an int
     */
    private static int toIntOmdbRuntime(@Nullable String omdbRuntime) {
        if (omdbRuntime != null) {
            String[] split = omdbRuntime.split(" ", 2);
            // split.length is always at least 1
            try {
                return Integer.decode(split[0]);
            } catch (NumberFormatException e) {
                Timber.w(String.format(
                        "NumberFormatException while attempting to decode OMDb runtime to int: \"%s\"",
                        omdbRuntime));
            }
        }
        return Movie.RUNTIME_UNKNOWN;
    }

}
