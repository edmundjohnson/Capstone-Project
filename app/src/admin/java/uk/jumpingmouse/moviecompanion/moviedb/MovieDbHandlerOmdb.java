package uk.jumpingmouse.moviecompanion.moviedb;

/**
 * Class which handles fetching movie information using the OMDb API.
 * The class is commented off to avoid build errors.
 * @author Edmund Johnson
 * @deprecated Use MovieHandlerTmdb instead
 */
class MovieDbHandlerOmdb {
}

//public class MovieDbHandlerOmdb implements MovieDbHandler, OmdbHandler {
//
//    // This map contains a mapping between genres as stored in OMDb and their
//    // corresponding genre ids.
//    // The map keys must match the values stored in the remote database and hence
//    // MUST NOT be translated!
//    // The map values must match the values in arrays.xml "filter_genre_pref_key".
//    private static final Map<String, Integer> OMDB_GENRE_TO_GENRE_ID;
//    static {
//        Map<String, Integer> genresStoredModifiable = new HashMap<>();
//        genresStoredModifiable.put("Action", R.string.genre_id_action);
//        genresStoredModifiable.put("Adventure", R.string.genre_id_adventure);
//        genresStoredModifiable.put("Animation", R.string.genre_id_animation);
//        genresStoredModifiable.put("Comedy", R.string.genre_id_comedy);
//        genresStoredModifiable.put("Crime", R.string.genre_id_crime);
//        genresStoredModifiable.put("Documentary", R.string.genre_id_documentary);
//        genresStoredModifiable.put("Drama", R.string.genre_id_drama);
//        genresStoredModifiable.put("Family", R.string.genre_id_family);
//        genresStoredModifiable.put("Fantasy", R.string.genre_id_fantasy);
//        genresStoredModifiable.put("History", R.string.genre_id_history);
//        genresStoredModifiable.put("Horror", R.string.genre_id_horror);
//        genresStoredModifiable.put("Music", R.string.genre_id_music);
//        genresStoredModifiable.put("Mystery", R.string.genre_id_mystery);
//        genresStoredModifiable.put("Romance", R.string.genre_id_romance);
//        genresStoredModifiable.put("Sci-Fi", R.string.genre_id_sci_fi);
//        genresStoredModifiable.put("Thriller", R.string.genre_id_thriller);
//        genresStoredModifiable.put("War", R.string.genre_id_war);
//        genresStoredModifiable.put("Western", R.string.genre_id_western);
//        // Not on TMDb
//        //genresStoredModifiable.put("Biography", R.string.genre_id_biography);
//        //genresStoredModifiable.put("Film-Noir", R.string.genre_id_film_noir);
//        //genresStoredModifiable.put("Musical", R.string.genre_id_musical);
//        //genresStoredModifiable.put("Sport", R.string.genre_id_sport);
//        // On TMDb but not OMDb: "TV Movie"
//        OMDB_GENRE_TO_GENRE_ID = Collections.unmodifiableMap(genresStoredModifiable);
//    }
//
//    private MovieDbReceiver mMovieDbReceiver;
//
//    //---------------------------------------------------------------------
//    // Instance handling methods
//
//    /**
//     * Constructor.
//     * @param movieDbReceiver the receiver to be passed information fetched from
//     *                        the remote database.
//     */
//    private MovieDbHandlerOmdb(@NonNull MovieDbReceiver movieDbReceiver) {
//        mMovieDbReceiver = movieDbReceiver;
//    }
//
//    /**
//     * Returns a new instance of this class.
//     * A new instance of the class must be created for OMDb client class, otherwise
//     * the results of successive calls could be passed to the wrong MovieDbReceiver.
//     * @param movieDbReceiver the receiver which is to be passed the fetched data
//     * @return a new instance of this class
//     */
//    @NonNull
//    public static MovieDbHandler newInstance(@NonNull MovieDbReceiver movieDbReceiver) {
//        return new MovieDbHandlerOmdb(movieDbReceiver);
//    }
//
//    //---------------------------------------------------------------------
//    // Implementation of the MovieDbHandler interface
//
//    /**
//     * Fetches a movie from the remote database.
//     * @param imdbId the movie's IMDb id
//     * @return a status code, with STATUS_SUCCESS indicating that the operation can be attempted,
//     *         any other value indicating that an error has occurred, the operation cannot
//     *         be attempted, and movieDbReceiver.onFetchMovieCompleted(...) will not be called
//     */
//    @Override
//    public int fetchMovieByImdbId(@NonNull String imdbId) {
//        Context context = getContext();
//        if (context == null) {
//            return STATUS_RECEIVER_CONTEXT_IS_NULL;
//        }
//        if (!getNetUtils().isNetworkAvailable(context)) {
//            return STATUS_NETWORK_UNAVAILABLE;
//        }
//
//        // Attempt to get the OMDb API key, returning a failure status if unsuccessful
//        String apiKey = getApiKey(context);
//        if (apiKey == null) {
//            return STATUS_OMDB_API_KEY_KEY_NOT_FOUND;
//        }
//        // An API key was obtained, fetch the movie from the remote database
//        getOmdbApi().fetchMovie(apiKey, imdbId, this);
//
//        return STATUS_SUCCESS;
//    }
//
//    /**
//     * Returns a string resource for a message corresponding to a MovieDbHandler status code.
//     * @param status a MovieDbHandler status code
//     * @return a string resource for a message corresponding to status
//     */
//    @Override
//    public int getErrorMessageForStatus(int status) {
//        return MovieDbUtils.getErrorMessageForStatus(status);
//    }
//
//    //---------------------------------------------------------------------
//    // Implementation of the OmdbHandler interface
//
//    /**
//     * Handles the completion of a movie being fetched from the movie database,
//     * passing the movie to the MovieDbReceiver.
//     * This is called by the OmdbApi library.
//     * @param omdbMovie the movie that was fetched
//     */
//    @Override
//    public void onFetchMovieCompleted(@Nullable OmdbMovie omdbMovie) {
//        // Convert the received 'remote' movie into a 'local' movie
//        Movie movie = newMovie(omdbMovie);
//
//        // Pass the movie to the receiver, if the receiver still exists.
//        // Make the call to onFetchMovieCompleted() even if the movie is null,
//        // so the receiver can take action, e.g. display an error message.
//        if (mMovieDbReceiver != null) {
//            mMovieDbReceiver.onFetchMovieCompleted(movie);
//        }
//    }
//
//    //---------------------------------------------------------------------
//    // Private methods
//
//    /**
//     * Returns the API key for the OMDb interface.
//     * @param context the context
//     * @return the API key for the OMDb interface
//     */
//    @Nullable
//    private String getApiKey(@NonNull Context context) {
//        // Get the OMDb API key from the local.properties file
//        String apiKey = context.getString(R.string.omdbapi_key);
//        return apiKey.trim().isEmpty() ? null : apiKey.trim();
//    }
//
//    /**
//     * Return the context, obtained through the MovieDbReceiver.
//     * @return the context
//     */
//    @Nullable
//    private Context getContext() {
//        if (mMovieDbReceiver == null) {
//            return null;
//        }
//        return mMovieDbReceiver.getReceiverContext();
//    }
//
//    /**
//     * Creates a Movie from an OmdbMovie and returns it.
//     * @param omdbMovie the OmdbMovie
//     * @return a Movie corresponding to omdbMovie
//     */
//    @Nullable
//    private Movie newMovie(OmdbMovie omdbMovie) {
//        if (omdbMovie == null) {
//            Timber.w("newMovie: omdbMovie is null");
//            return null;
//        } else if (omdbMovie.getImdbID() == null) {
//            Timber.w("newMovie: omdbMovie.imdbId is null");
//            return null;
//        } else if (omdbMovie.getTitle() == null) {
//            Timber.w("newMovie: omdbMovie.title is null");
//            return null;
//        }
//
//        // Build and return the movie
//        String id = ModelUtils.imdbIdToMovieId(omdbMovie.getImdbID());
//        if (id == null) {
//            Timber.w("newMovie: could not obtain valid id from imdbID: " + omdbMovie.getImdbID());
//            return null;
//        }
//
//        int runtime = toIntOmdbRuntime(omdbMovie.getRuntime());
//        long released = toLongOmdbReleased(omdbMovie.getReleased());
//
//        String genre = getMovieGenreCsvFromOmdbMovieGenre(omdbMovie.getGenre());
//
//        return Movie.builder()
//                .id(id)
//                .imdbId(omdbMovie.getImdbID())
//                .title(omdbMovie.getTitle())
//                .certificate(omdbMovie.getRated())
//                .released(released)
//                .runtime(runtime)
//                .genre(genre)
//                .director(omdbMovie.getDirector())
//                .screenplay(omdbMovie.getWriter())
//                .cast(omdbMovie.getCast())
//                .plot(omdbMovie.getPlot())
//                .language(omdbMovie.getLanguage())
//                .country(omdbMovie.getCountry())
//                .poster(omdbMovie.getPoster())
//                .build();
//    }
//
//    /**
//     * Returns a long representing an OMDb-formatted released date as a number of milliseconds.
//     * @param omdbReleased an OMDb released date, formatted as "dd MMM yyyy"
//     * @return a long object representing omdbReleased as a number of milliseconds,
//     *         or Movie.RELEASED_UNKNOWN if omdbReleased could not be converted to a long
//     */
//    private static long toLongOmdbReleased(@Nullable final String omdbReleased) {
//        Date dateReleased = OmdbApi.toDateOmdbReleased(omdbReleased);
//        if (dateReleased == null) {
//            return Movie.RELEASED_UNKNOWN;
//        } else {
//            return dateReleased.getTime();
//        }
//    }
//
//    /**
//     * Returns an OMDb runtime as an int, e.g. returns "144 min" as 144
//     * @param omdbRuntime the OMDb runtime, e.g. "144 min"
//     * @return the runtime as an int, e.g. 144,
//     *         or Movie.RUNTIME_UNKNOWN if omdbRuntime could not be converted to an int
//     */
//    private static int toIntOmdbRuntime(@Nullable String omdbRuntime) {
//        if (omdbRuntime != null) {
//            String[] split = omdbRuntime.split(" ", 2);
//            // split.length is always at least 1
//            try {
//                return Integer.decode(split[0]);
//            } catch (NumberFormatException e) {
//                Timber.w(String.format(
//                        "NumberFormatException while attempting to decode OMDb runtime to int: \"%s\"",
//                        omdbRuntime));
//            }
//        }
//        return Movie.RUNTIME_UNKNOWN;
//    }
//
//    /**
//     * Returns a Movie genre CSV string corresponding to an OMDb genre CSV string.
//     * @param omdbGenreCsv an OMDb genre CSV string, e.g. "Comedy, Drama, Fantasy"
//     * @return the Movie genre CSV string corresponding to the OMDb genre CSV string, e.g. "35,18,14"
//     */
//    @Nullable
//    private String getMovieGenreCsvFromOmdbMovieGenre(@Nullable String omdbGenreCsv) {
//        if (omdbGenreCsv == null) {
//            return null;
//        }
//        StringBuilder movieGenreCsv = new StringBuilder();
//        String[] omdbGenreArray = omdbGenreCsv.split(",");
//        for (String omdbGenre : omdbGenreArray) {
//            String movieGenreId = getMovieGenreIdFromOmdbGenre(omdbGenre.trim());
//            if (movieGenreId != null) {
//                if (movieGenreCsv.length() > 0) {
//                    movieGenreCsv.append(",");
//                }
//                movieGenreCsv.append(movieGenreId);
//            }
//        }
//        return movieGenreCsv.toString();
//    }
//
//    /**
//     * Returns the Movie genre corresponding to an OMDb genre.
//     * @param omdbGenre the OMDb genre, e.g. "Comedy"
//     * @return the Movie genre corresponding to the OMDb genre, e.g. "35"
//     */
//    @Nullable
//    private String getMovieGenreIdFromOmdbGenre(@NonNull String omdbGenre) {
//        if (getContext() == null) {
//            return null;
//        }
//        Integer genreStringRes = OMDB_GENRE_TO_GENRE_ID.get(omdbGenre);
//        if (genreStringRes != null) {
//            return getContext().getString(genreStringRes);
//        }
//        return null;
//    }
//
//    //---------------------------------------------------------------------
//    // Getters
//
//    /**
//     * Convenience method which returns a reference to an OmdbApi object.
//     * @return a reference to an OmdbApi object
//     */
//    @NonNull
//    private static OmdbApi getOmdbApi() {
//        return OmdbApi.getInstance();
//    }
//
//    /**
//     * Convenience method which returns a reference to a NetUtils object.
//     * @return a reference to a NetUtils object
//     */
//    private NetUtils getNetUtils() {
//        return ObjectFactory.getNetUtils();
//    }
//
//}
