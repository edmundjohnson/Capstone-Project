package uk.jumpingmouse.moviecompanion.moviedb;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.Language;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.ProductionCountry;
import info.movito.themoviedbapi.model.ReleaseInfo;
import info.movito.themoviedbapi.model.config.TmdbConfiguration;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonCrew;

import timber.log.Timber;

import uk.jumpingmouse.moviecompanion.ObjectFactory;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.utils.JavaUtils;
import uk.jumpingmouse.moviecompanion.utils.ModelUtils;
import uk.jumpingmouse.moviecompanion.utils.NetUtils;

/**
 * Class which handles fetching movie information using the OMDb API.
 * @author Edmund Johnson
 */
public class MovieDbHandlerTmdb implements MovieDbHandler, TmdbHandler {

    private static final SimpleDateFormat DATE_FORMAT_TMDB;

    // Crew jobs
    private static final String JOB_DIRECTOR = "Director";
    private static final String JOB_SCREENPLAY = "Screenplay";

    // The receiver which initiated this handler, and which will be passed the fetched data
    private MovieDbReceiver mMovieDbReceiver;

    private String mApiKey;
    private TmdbConfiguration mTmdbConfiguration;
    private String mImdbId;

    static {
        DATE_FORMAT_TMDB = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Constructor.
     * @param movieDbReceiver the receiver to be passed information fetched from
     *                        the remote database.
     */
    private MovieDbHandlerTmdb(@NonNull MovieDbReceiver movieDbReceiver) {
        mMovieDbReceiver = movieDbReceiver;
    }

    /**
     * Returns a new instance of this class.
     * A new instance of the class must be created for OMDb client class, otherwise
     * the results of successive calls could be passed to the wrong MovieDbReceiver.
     * @param movieDbReceiver the receiver which is to be passed the fetched data
     * @return a new instance of this class
     */
    @NonNull
    public static MovieDbHandler newInstance(@NonNull MovieDbReceiver movieDbReceiver) {
        return new MovieDbHandlerTmdb(movieDbReceiver);
    }

    //---------------------------------------------------------------------
    // Implementation of the MovieDbHandler interface

    /**
     * Fetches a movie from the remote database.
     * @param imdbId the movie's IMDb id
     * @return a status code, with STATUS_SUCCESS indicating that the operation can be attempted,
     *         any other value indicating that an error has occurred, the operation cannot
     *         be attempted, and movieDbReceiver.onFetchMovieCompleted(...) will not be called
     */
    @Override
    public int fetchMovieByImdbId(@NonNull String imdbId) {
        Context context = getContext();
        if (context == null) {
            return STATUS_RECEIVER_CONTEXT_IS_NULL;
        }
        if (!getNetUtils().isNetworkAvailable(context)) {
            return STATUS_NETWORK_UNAVAILABLE;
        }

        // Attempt to get the TMDb API key, returning a failure status if unsuccessful
        String apiKey = getApiKey(context);
        if (apiKey == null) {
            return STATUS_TMDB_API_KEY_KEY_NOT_FOUND;
        }

        mApiKey = apiKey;
        mImdbId = imdbId;

        // fetch the TMDb configuration
        new TmdbTaskFetchConfiguration(this).execute(apiKey);

        return STATUS_SUCCESS;
    }

    /**
     * Returns a string resource for a message corresponding to a MovieDbHandler status code.
     * @param status a MovieDbHandler status code
     * @return a string resource for a message corresponding to status
     */
    @Override
    public int getErrorMessageForStatus(int status) {
        return MovieDbUtils.getErrorMessageForStatus(status);
    }

    //---------------------------------------------------------------------
    // Implementation of the TmdbHandler interface

    /**
     * Handles the completion of the TMDb configuration being fetched.
     * @param tmdbConfiguration the TMDb configuration that was fetched
     */
    @Override
    public void onFetchTmdbConfigurationCompleted(@Nullable TmdbConfiguration tmdbConfiguration) {
        Timber.d("tmdbConfiguration = " + tmdbConfiguration);
        if (tmdbConfiguration == null) {
            Timber.e("Unable to determine TMDb configuration");
            return;
        }
        mTmdbConfiguration = tmdbConfiguration;

        // fetch the movie
        new TmdbTaskFetchMovie(this).execute(mApiKey, mImdbId);
    }

    /**
     * Handles the completion of the movie being fetched from TMDb.
     * @param tmdbMovie the movie that was fetched
     */
    @Override
    public void onFetchMovieCompleted(@Nullable MovieDb tmdbMovie) {
        Timber.d("tmdbMovie = " + tmdbMovie);
        // create a Movie from the TMDb MovieDb
        Movie movie = newMovie(mTmdbConfiguration, tmdbMovie);
        // return the Movie to the receiver which initiated this handler
        mMovieDbReceiver.onFetchMovieCompleted(movie);
    }

    //---------------------------------------------------------------------
    // Private methods

    /**
     * Returns the API key for the OMDb interface.
     * @param context the context
     * @return the API key for the OMDb interface
     */
    @Nullable
    private String getApiKey(@NonNull Context context) {
        // Get the TMDb API key from the local.properties file
        String apiKey = context.getString(R.string.tmdbapi_key);
        return apiKey.trim().isEmpty() ? null : apiKey.trim();
    }

    /**
     * Return the context, obtained through the MovieDbReceiver.
     * @return the context
     */
    @Nullable
    private Context getContext() {
        if (mMovieDbReceiver == null) {
            return null;
        }
        return mMovieDbReceiver.getReceiverContext();
    }

    /**
     * Creates a Movie from an TMDb movie and returns it.
     * @param tmdbConfiguration the TMDb configuration
     * @param tmdbMovie the TMDb movie
     * @return a Movie corresponding to tmdbMovie
     */
    @Nullable
    private Movie newMovie(@NonNull TmdbConfiguration tmdbConfiguration, @Nullable MovieDb tmdbMovie) {
        if (tmdbMovie == null) {
            Timber.w("newMovie: tmdbMovie is null");
            return null;
        } else if (tmdbMovie.getImdbID() == null) {
            Timber.w("newMovie: tmdbMovie.imdbId is null");
            return null;
        } else if (tmdbMovie.getTitle() == null) {
            Timber.w("newMovie: tmdbMovie.title is null");
            return null;
        }

        // id - create a movie id from the imdbId
        String id = ModelUtils.imdbIdToMovieId(tmdbMovie.getImdbID());
        if (id == null) {
            Timber.w("newMovie: could not obtain valid id from imdbID: " + tmdbMovie.getImdbID());
            return null;
        }
        // certificate
        String certificate = getCertificateCsv(tmdbMovie.getReleases());
        // released
        long released = getReleasedLong(tmdbMovie.getReleaseDate());
        // genre
        String genre = getGenreCsv(tmdbMovie.getGenres());
        // directors
        String director = getDirectorCsv(tmdbMovie.getCredits());
        // screenplay writers
        String screenplay = getScreenplayCsv(tmdbMovie.getCredits());
        // cast
        String cast = getCastCsv(tmdbMovie.getCredits());
        // country
        String country = getCountryCsv(tmdbMovie.getProductionCountries());
        // country
        String language = getLanguageCsv(tmdbMovie.getSpokenLanguages());
        // poster
        String poster = getPosterUrl(tmdbConfiguration, tmdbMovie.getPosterPath());

        // TODO: add tmdbId, thumbnail
        return Movie.builder()
                .id(id)
                .imdbId(tmdbMovie.getImdbID())
                .title(tmdbMovie.getTitle())
                .certificate(certificate)
                .released(released)
                .runtime(tmdbMovie.getRuntime())
                .genre(genre)
                .director(director)
                .screenplay(screenplay)
                .cast(cast)
                .plot(tmdbMovie.getOverview())
                .language(language)
                .country(country)
                .poster(poster)
                .build();
    }

    /**
     * Returns a comma-separated string of certificates corresponding to a list of TMDb releases.
     * @param releaseInfos a list of TMDb releases
     * @return a comma-separated string of certificates corresponding to a list of TMDb releases,
     *         e.g. "US:R,GB:12A,PT:M/12"
     */
    @NonNull
    private static String getCertificateCsv(@Nullable List<ReleaseInfo> releaseInfos) {
        StringBuilder certificateCsv = new StringBuilder();
        if (releaseInfos != null) {
            Set<String> existingCertificates = new HashSet<>();

            for (ReleaseInfo releaseInfo : releaseInfos) {
                if (releaseInfo.getCertification() != null
                        && !releaseInfo.getCertification().isEmpty()
                        && releaseInfo.getCountry() != null
                        && !releaseInfo.getCountry().isEmpty()) {
                    // An example of a certificate here is "GB:12A"
                    String certificate = releaseInfo.getCountry() + ":"
                            + releaseInfo.getCertification();
                    if (!existingCertificates.contains(certificate)) {
                        if (certificateCsv.length() > 0) {
                            certificateCsv.append(",");
                        }
                        certificateCsv.append(certificate);
                        existingCertificates.add(certificate);
                    }
                }
            }
        }
        return certificateCsv.toString();
    }

    /**
     * Returns a long representing a TMDb-formatted released date as a number of milliseconds.
     * @param tmdbReleased a TMDb released date, formatted as "dd MMM yyyy" ????
     * @return a long object representing tmdbReleased as a number of milliseconds,
     *         or Movie.RELEASED_UNKNOWN if tmdbReleased could not be converted to a long
     */
    private static long getReleasedLong(@Nullable final String tmdbReleased) {
        Date dateReleased = JavaUtils.toDate(DATE_FORMAT_TMDB, tmdbReleased);
        if (dateReleased == null) {
            return Movie.RELEASED_UNKNOWN;
        } else {
            return dateReleased.getTime();
        }
    }

    /**
     * Returns a comma-separated string of TMDb genre ids corresponding to a list of TMDb Genres.
     * @param tmdbGenres a list of TMDb Genres
     * @return a comma-separated string of TMDb genre ids corresponding to the list of TMDb Genres,
     *         e.g. "18,44,28"
     */
    @NonNull
    private static String getGenreCsv(@Nullable List<Genre> tmdbGenres) {
        StringBuilder genre = new StringBuilder();
        if (tmdbGenres != null) {
            for (Genre tmdbGenre : tmdbGenres) {
                if (genre.length() > 0) {
                    genre.append(",");
                }
                genre.append(tmdbGenre.getId());
            }
        }
        return genre.toString();
    }

    /**
     * Returns the comma-separated string of directors from the TMDb credits.
     * @param credits the TMDb credits
     * @return the comma-separated string of directors from the TMDb credits
     */
    @NonNull
    private static String getDirectorCsv(@Nullable Credits credits) {
        return getCrewWithJob(credits, JOB_DIRECTOR);
    }

    /**
     * Returns the comma-separated string of directors from the TMDb credits.
     * @param credits the TMDb credits
     * @return the comma-separated string of directors from the TMDb credits
     */
    @NonNull
    private static String getScreenplayCsv(@Nullable Credits credits) {
        return getCrewWithJob(credits, JOB_SCREENPLAY);
    }

    /**
     * Returns a comma-separated string of crew with a specified job from the TMDb credits.
     * @param credits the TMDb credits
     * @param job the job, e.g. "Director"
     * @return a comma-separated string of crew with the specified job from the TMDb credits
     */
    @NonNull
    private static String getCrewWithJob(@Nullable Credits credits, @NonNull String job) {
        StringBuilder crew = new StringBuilder();
        if (credits != null && credits.getCrew() != null) {
            for (PersonCrew personCrew : credits.getCrew()) {
                if (personCrew.getJob() != null && personCrew.getJob().equalsIgnoreCase(job)) {
                    if (crew.length() > 0) {
                        crew.append(", ");
                    }
                    crew.append(personCrew.getName());
                }
            }
        }
        return crew.toString();
    }

    /**
     * Returns a comma-separated string of cast members from the TMDb credits.
     * @param credits the TMDb credits
     * @return a comma-separated string of cast members from the TMDb credits
     */
    @NonNull
    private static String getCastCsv(@Nullable Credits credits) {
        StringBuilder cast = new StringBuilder();
        if (credits != null && credits.getCast() != null) {
            for (PersonCast personCast : credits.getCast()) {
                if (cast.length() > 0) {
                    cast.append(", ");
                }
                cast.append(personCast.getName());
            }
        }
        return cast.toString();
    }

    /**
     * Returns a comma-separated string of ISO language codes corresponding to a list of TMDb
     * production countries.
     * @param tmdbLanguages a list of TMDb production countries
     * @return a comma-separated string of ISO language codes corresponding to the list of TMDb
     *         production countries, e.g. "DA,EN"
     */
    @NonNull
    private static String getLanguageCsv(@Nullable List<Language> tmdbLanguages) {
        StringBuilder language = new StringBuilder();
        if (tmdbLanguages != null) {
            for (Language tmdbLanguage : tmdbLanguages) {
                if (language.length() > 0) {
                    language.append(",");
                }
                language.append(tmdbLanguage.getIsoCode());
            }
        }
        return language.toString();
    }

    /**
     * Returns a comma-separated string of ISO country codes corresponding to a list of TMDb
     * production countries.
     * @param tmdbCountries a list of TMDb production countries
     * @return a comma-separated string of ISO country codes corresponding to the list of TMDb
     *         production countries, e.g. "DK,FR,IT,SE,DE"
     */
    @NonNull
    private static String getCountryCsv(@Nullable List<ProductionCountry> tmdbCountries) {
        StringBuilder country = new StringBuilder();
        if (tmdbCountries != null) {
            for (ProductionCountry tmdbCountry : tmdbCountries) {
                if (country.length() > 0) {
                    country.append(",");
                }
                country.append(tmdbCountry.getIsoCode());
            }
        }
        return country.toString();
    }

    /**
     * Returns the URL for the largest available poster for a poster path.
     * @param tmdbConfiguration the TMDb configuration
     * @param posterPath the poster path, e.g. "/s1g3ffh.jpg"
     * @return the URL for the largest available poster for the poster path,
     *         e.g. "http://image.tmdb.org/t/p/w780/s1g3ffh.jpg"
     */
    @Nullable
    private static String getPosterUrl(@NonNull TmdbConfiguration tmdbConfiguration,
                                       @Nullable String posterPath) {
        if (posterPath == null) {
            return null;
        }
        String baseUrl = tmdbConfiguration.getBaseUrl();
        List<String> posterSizes = tmdbConfiguration.getPosterSizes();
        String posterSize = getImageLargestSize(posterSizes);
        return baseUrl + posterSize + posterPath;
    }

    /**
     * Returns the largest available size from a list of available sizes.
     * @param availableSizes The available sizes, each formatted as "wnnn", where "nnn" is the size
     * @return the largest available size, e.g. "w780"
     */
    @Nullable
    private static String getImageLargestSize(@Nullable List<String> availableSizes) {
        String largestSizeWithPrefix = null;
        if (availableSizes != null) {
            int largestSize = 0;
            for (String sizeWithPrefix : availableSizes) {
                if (sizeWithPrefix.length() > 1 && sizeWithPrefix.charAt(0) == 'w') {
                    int size;
                    try {
                        size = Integer.parseInt(sizeWithPrefix.substring(1));
                    } catch (Exception e) {
                        Timber.w("size was not numeric: " + sizeWithPrefix);
                        break;
                    }
                    if (size > largestSize) {
                        largestSize = size;
                        largestSizeWithPrefix = sizeWithPrefix;
                    }
                }
            }
        }
        return largestSizeWithPrefix;
    }

    //---------------------------------------------------------------------
    // Getters

    /**
     * Convenience method which returns a reference to a NetUtils object.
     * @return a reference to a NetUtils object
     */
    private NetUtils getNetUtils() {
        return ObjectFactory.getNetUtils();
    }

}
