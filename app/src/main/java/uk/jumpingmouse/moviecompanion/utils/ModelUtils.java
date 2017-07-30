package uk.jumpingmouse.moviecompanion.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.R;
import uk.jumpingmouse.moviecompanion.data.Award;
import uk.jumpingmouse.moviecompanion.data.Movie;
import uk.jumpingmouse.moviecompanion.data.UserMovie;
import uk.jumpingmouse.moviecompanion.data.ViewAward;
import uk.jumpingmouse.moviecompanion.model.DataContract;

/**
 * Class for model utilities.
 * @author Edmund Johnson
 */
public final class ModelUtils {

    // This map contains a mapping between genre ids and their corresponding genre names.
    // The map keys are the genre ids stored in the database and match "@string/genre_id...".
    // The map values are the string resource ids of the displayable genre names.
    private static final Map<String, Integer> GENRES;
    static {
        Map<String, Integer> genresStoredModifiable = new HashMap<>();
        genresStoredModifiable.put(Movie.GENRE_ID_ACTION, R.string.genre_name_action);
        genresStoredModifiable.put(Movie.GENRE_ID_ADVENTURE, R.string.genre_name_adventure);
        genresStoredModifiable.put(Movie.GENRE_ID_ANIMATION, R.string.genre_name_animation);
        genresStoredModifiable.put(Movie.GENRE_ID_COMEDY, R.string.genre_name_comedy);
        genresStoredModifiable.put(Movie.GENRE_ID_CRIME, R.string.genre_name_crime);
        genresStoredModifiable.put(Movie.GENRE_ID_DOCUMENTARY, R.string.genre_name_documentary);
        genresStoredModifiable.put(Movie.GENRE_ID_DRAMA, R.string.genre_name_drama);
        genresStoredModifiable.put(Movie.GENRE_ID_FAMILY, R.string.genre_name_family);
        genresStoredModifiable.put(Movie.GENRE_ID_FANTASY, R.string.genre_name_fantasy);
        genresStoredModifiable.put(Movie.GENRE_ID_HISTORY, R.string.genre_name_history);
        genresStoredModifiable.put(Movie.GENRE_ID_HORROR, R.string.genre_name_horror);
        genresStoredModifiable.put(Movie.GENRE_ID_MUSIC, R.string.genre_name_music);
        genresStoredModifiable.put(Movie.GENRE_ID_MYSTERY, R.string.genre_name_mystery);
        genresStoredModifiable.put(Movie.GENRE_ID_ROMANCE, R.string.genre_name_romance);
        genresStoredModifiable.put(Movie.GENRE_ID_SCI_FI, R.string.genre_name_sci_fi);
        genresStoredModifiable.put(Movie.GENRE_ID_THRILLER, R.string.genre_name_thriller);
        genresStoredModifiable.put(Movie.GENRE_ID_WAR, R.string.genre_name_war);
        genresStoredModifiable.put(Movie.GENRE_ID_WESTERN, R.string.genre_name_western);
        // Not on TMDb:
        //genresStoredModifiable.put(Movie.GENRE_ID_BIOGRAPHY, R.string.genre_name_biography);
        //genresStoredModifiable.put(Movie.GENRE_ID_FILM_NOIR, R.string.genre_name_film_noir);
        //genresStoredModifiable.put(Movie.GENRE_ID_MUSICAL, R.string.genre_name_musical);
        //genresStoredModifiable.put(Movie.GENRE_ID_SPORT, R.string.genre_name_sport);
        // On TMDb but not used:
        //genresStoredModifiable.put(Movie.GENRE_ID_TV_MOVIE, R.string.genre_name_tv_movie);
        GENRES = Collections.unmodifiableMap(genresStoredModifiable);
    }

    /**
     * Private default constructor to prevent instantiation.
     */
    private ModelUtils() {
    }

    //---------------------------------------------------------------------
    // Movie methods

    /**
     * Converts an IMDb id to a movie id and returns it.
     * @param imdbId the IMDb id, e.g. "tt4061234"
     * @return the movie id, e.g. "4061234"
     */
    @Nullable
    public static String imdbIdToMovieId(String imdbId) {
        if (imdbId != null && imdbId.length() > 2) {
            return imdbId.substring(2);
        }
        return null;
    }

    /**
     * Creates and returns a Movie based on a set of ContentValues.
     * @param values the ContentValues
     * @return a Movie corresponding to the ContentValues, or null if the ContentValues
     *         do not contain values for any of the fields which are mandatory for Movie
     */
    @Nullable
    public static Movie newMovie(@NonNull final ContentValues values) {
        // if any mandatory attribute is missing, return null

        // id
        String id = values.getAsString(DataContract.MovieEntry.COLUMN_ID);
        try {
            if (id == null) {
                Timber.e("newMovie: missing id");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("newMovie: id is null ContentValues", e);
            return null;
        }

        // imdbId
        String imdbId;
        try {
            imdbId = values.getAsString(DataContract.MovieEntry.COLUMN_IMDB_ID);
            if (imdbId == null) {
                Timber.e("newMovie: missing imdbId");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("newMovie: imdbId is null ContentValues", e);
            return null;
        }

        // title
        String title;
        try {
            title = values.getAsString(DataContract.MovieEntry.COLUMN_TITLE);
            if (title == null) {
                Timber.e("newMovie: missing title");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("newMovie: title is null ContentValues", e);
            return null;
        }

        return Movie.builder()
                .id(id)
                .imdbId(imdbId)
                .title(title)
                .certificate(values.getAsString(DataContract.MovieEntry.COLUMN_CERTIFICATE))
                .released(values.getAsLong(DataContract.MovieEntry.COLUMN_RELEASED))
                .runtime(values.getAsInteger(DataContract.MovieEntry.COLUMN_RUNTIME))
                .genre(values.getAsString(DataContract.MovieEntry.COLUMN_GENRE))
                .director(values.getAsString(DataContract.MovieEntry.COLUMN_DIRECTOR))
                .screenplay(values.getAsString(DataContract.MovieEntry.COLUMN_SCREENPLAY))
                .cast(values.getAsString(DataContract.MovieEntry.COLUMN_CAST))
                .plot(values.getAsString(DataContract.MovieEntry.COLUMN_PLOT))
                .language(values.getAsString(DataContract.MovieEntry.COLUMN_LANGUAGE))
                .country(values.getAsString(DataContract.MovieEntry.COLUMN_COUNTRY))
                .poster(values.getAsString(DataContract.MovieEntry.COLUMN_POSTER))
                .build();
    }

    /**
     * Creates and returns a Movie based on the values of a cursor row.
     * @param cursor a cursor positioned at the required row
     * @return a Movie based on the values of the cursor row, or null if the cursor row
     *         does not contain values for any of the fields which are mandatory for Movie
     */
    @Nullable
    public static Movie newMovie(@NonNull Cursor cursor) {
        final String id = cursor.getString(DataContract.MovieEntry.COL_ID);
        final String imdbId = cursor.getString(DataContract.MovieEntry.COL_IMDB_ID);
        final String title = cursor.getString(DataContract.MovieEntry.COL_TITLE);
        final String certificate = cursor.getString(DataContract.MovieEntry.COL_CERTIFICATE);
        final String genre = cursor.getString(DataContract.MovieEntry.COL_GENRE);
        final String director = cursor.getString(DataContract.MovieEntry.COL_DIRECTOR);
        final String screenplay = cursor.getString(DataContract.MovieEntry.COL_SCREENPLAY);
        final String cast = cursor.getString(DataContract.MovieEntry.COL_CAST);
        final String plot = cursor.getString(DataContract.MovieEntry.COL_PLOT);
        final String language = cursor.getString(DataContract.MovieEntry.COL_LANGUAGE);
        final String country = cursor.getString(DataContract.MovieEntry.COL_COUNTRY);
        final String poster = cursor.getString(DataContract.MovieEntry.COL_POSTER);

        // if the id mandatory attribute is missing, return null
        if (id == null) {
            Timber.e("newMovie(Cursor): missing id");
            return null;
        }
        // if the imdbId mandatory attribute is missing, return null
        if (imdbId == null) {
            Timber.e("newMovie(Cursor): missing imdbId");
            return null;
        }
        // if the title mandatory attribute is missing, return null
        if (title == null) {
            Timber.e("newMovie(Cursor): missing title");
            return null;
        }
        // if the released date is invalid set it to unknown
        long released = cursor.getLong(DataContract.MovieEntry.COL_RELEASED);
        if (released <= 0 && released != Movie.RELEASED_UNKNOWN) {
            Timber.w("newMovie(Cursor): invalid released");
            released = Movie.RELEASED_UNKNOWN;
        }
        // if the runtime is invalid set it to unknown
        int runtime = cursor.getInt(DataContract.MovieEntry.COL_RUNTIME);
        if (runtime < 1 && runtime != Movie.RUNTIME_UNKNOWN) {
            Timber.w("newMovie(Cursor): invalid runtime");
            runtime = Movie.RUNTIME_UNKNOWN;
        }

        return Movie.builder()
                .id(id)
                .imdbId(imdbId)
                .title(title)
                .certificate(certificate)
                .released(released)
                .runtime(runtime)
                .genre(genre)
                .director(director)
                .screenplay(screenplay)
                .cast(cast)
                .plot(plot)
                .language(language)
                .country(country)
                .poster(poster)
                .build();
    }

    /**
     * Returns the list of movies represented by a cursor.
     * @param cursor the cursor
     * @return the list of movies represented by the cursor
     */
    @Nullable
    public static List<Movie> toMovieList(@NonNull Cursor cursor) {
        if (cursor.getCount() == 0) {
            return null;
        }
        List<Movie> movieList = new ArrayList<>();
        // Assume that the cursor is in its initial position before the first row.
        // The next line is safer if the initial position of the cursor is unknown:
        //for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
        while (cursor.moveToNext()) {
            Movie movie = newMovie(cursor);
            if (movie != null) {
                movieList.add(movie);
            }
        }
        return movieList;
    }

    /**
     * Returns a set of ContentValues corresponding to a movie.
     * @param movie the movie
     * @return the set of ContentValues corresponding to the movie
     */
    @NonNull
    private static ContentValues toContentValues(@NonNull Movie movie) {
        ContentValues values = new ContentValues();

        values.put(DataContract.MovieEntry.COLUMN_ID, movie.getId());
        values.put(DataContract.MovieEntry.COLUMN_IMDB_ID, movie.getImdbId());
        values.put(DataContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(DataContract.MovieEntry.COLUMN_CERTIFICATE, movie.getCertificate());
        values.put(DataContract.MovieEntry.COLUMN_RELEASED, movie.getReleased());
        values.put(DataContract.MovieEntry.COLUMN_RUNTIME, movie.getRuntime());
        values.put(DataContract.MovieEntry.COLUMN_GENRE, movie.getGenre());
        values.put(DataContract.MovieEntry.COLUMN_DIRECTOR, movie.getDirector());
        values.put(DataContract.MovieEntry.COLUMN_SCREENPLAY, movie.getScreenplay());
        values.put(DataContract.MovieEntry.COLUMN_CAST, movie.getCast());
        values.put(DataContract.MovieEntry.COLUMN_PLOT, movie.getPlot());
        values.put(DataContract.MovieEntry.COLUMN_LANGUAGE, movie.getLanguage());
        values.put(DataContract.MovieEntry.COLUMN_COUNTRY, movie.getCountry());
        values.put(DataContract.MovieEntry.COLUMN_POSTER, movie.getPoster());

        return values;
    }

    //---------------------------------------------------------------------
    // Award methods

    /**
     * Creates and returns an Award object based on a set of ContentValues.
     * @param values the ContentValues
     * @return an Award object corresponding to the ContentValues, or null if the ContentValues
     *         do not contain values for any of the fields which are mandatory for Award
     */
    @Nullable
    public static Award newAward(@NonNull final ContentValues values) {
        // if any mandatory attribute is missing, return null

        // id
        String id;
        try {
            id = values.getAsString(DataContract.AwardEntry.COLUMN_ID);
            if (id == null) {
                Timber.e("newAward: id is null in ContentValues");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("newAward: id is missing from ContentValues", e);
            return null;
        }

        // movieId
        String movieId;
        try {
            movieId = values.getAsString(DataContract.AwardEntry.COLUMN_MOVIE_ID);
            if (movieId == null) {
                Timber.e("newAward: movieId is null in ContentValues");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("newAward: movieId is missing from ContentValues", e);
            return null;
        }

        // awardDate
        String awardDate;
        try {
            awardDate = values.getAsString(DataContract.AwardEntry.COLUMN_AWARD_DATE);
            if (awardDate == null) {
                Timber.e("newAward: awardDate is null in ContentValues");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("newAward: awardDate is missing from ContentValues", e);
            return null;
        }

        // category
        String category;
        try {
            category = values.getAsString(DataContract.AwardEntry.COLUMN_CATEGORY);
            if (category == null) {
                Timber.e("newAward: category is null in ContentValues");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("newAward: category is missing from ContentValues", e);
            return null;
        }

        // review
        String review;
        try {
            review = values.getAsString(DataContract.AwardEntry.COLUMN_REVIEW);
            if (review == null) {
                Timber.e("newAward: review is null in ContentValues");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("newAward: review is missing from ContentValues", e);
            return null;
        }

        // displayOrder
        int displayOrder;
        try {
            displayOrder = values.getAsInteger(DataContract.AwardEntry.COLUMN_DISPLAY_ORDER);
            if (displayOrder <= 0) {
                Timber.e("newAward: invalid displayOrder in ContentValues");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("newAward: displayOrder is missing from ContentValues", e);
            return null;
        }

        return Award.builder()
                .id(id)
                .movieId(movieId)
                .awardDate(awardDate)
                .category(category)
                .review(review)
                .displayOrder(displayOrder)
                .build();
    }

    //---------------------------------------------------------------------
    // UserMovie methods

    /**
     * Creates and returns a UserMovie based on a set of ContentValues.
     * @param values the ContentValues
     * @return a UserMovie corresponding to the ContentValues, or null if the ContentValues
     *         do not contain values for any of the fields which are mandatory for UserMovie
     */
    @Nullable
    public static UserMovie newUserMovie(@NonNull final ContentValues values) {
        // if any mandatory attribute is missing, return null

        // id
        String id;
        try {
            id = values.getAsString(DataContract.UserMovieEntry.COLUMN_ID);
            if (id == null) {
                Timber.e("newUserMovie: missing id");
                return null;
            }
        } catch (NullPointerException e) {
            Timber.e("newUserMovie: id is null ContentValues", e);
            return null;
        }

        boolean onWishlist = values.getAsBoolean(DataContract.UserMovieEntry.COLUMN_ON_WISHLIST);
        boolean watched = values.getAsBoolean(DataContract.UserMovieEntry.COLUMN_WATCHED);
        boolean favourite = values.getAsBoolean(DataContract.UserMovieEntry.COLUMN_FAVOURITE);

        return UserMovie.builder()
                .id(id)
                .onWishlist(onWishlist)
                .watched(watched)
                .favourite(favourite)
                .build();
    }

    /**
     * Construct and return a UserMovie based on the values in a ViewAward.
     * @param viewAward the ViewAward
     * @return a UserMovie based on the values in the ViewAward
     */
    public static UserMovie newUserMovie(@Nullable ViewAward viewAward) {
        return viewAward == null ? null
                : UserMovie.builder()
                        .id(viewAward.getMovieId())
                        .onWishlist(viewAward.isOnWishlist())
                        .watched(viewAward.isWatched())
                        .favourite(viewAward.isFavourite())
                        .build();
    }

    //---------------------------------------------------------------------
    // ViewAward methods

    /**
     * Creates and returns a ViewAward based on the values of a cursor row.
     * @param cursor a cursor positioned at the required row
     * @return a ViewAward based on the values of the cursor row, or null if the cursor row
     *         does not contain values for any of the fields which are mandatory for ViewAward
     */
    @Nullable
    public static ViewAward newViewAward(@NonNull Cursor cursor) {
        final String id = cursor.getString(DataContract.ViewAwardEntry.COL_ID);
        final String movieId = cursor.getString(DataContract.ViewAwardEntry.COL_MOVIE_ID);
        final String imdbId = cursor.getString(DataContract.ViewAwardEntry.COL_IMDB_ID);
        final String awardDate = cursor.getString(DataContract.ViewAwardEntry.COL_AWARD_DATE);
        final String category = cursor.getString(DataContract.ViewAwardEntry.COL_CATEGORY);
        final String review = cursor.getString(DataContract.ViewAwardEntry.COL_REVIEW);
        final int displayOrder = cursor.getInt(DataContract.ViewAwardEntry.COL_DISPLAY_ORDER);
        final String title = cursor.getString(DataContract.ViewAwardEntry.COL_TITLE);
        int runtime = cursor.getInt(DataContract.ViewAwardEntry.COL_RUNTIME);
        final String genre = cursor.getString(DataContract.ViewAwardEntry.COL_GENRE);
        final String poster = cursor.getString(DataContract.ViewAwardEntry.COL_POSTER);
        final boolean onWishlist = cursor.getInt(DataContract.ViewAwardEntry.COL_ON_WISHLIST) == 1;
        final boolean watched = cursor.getInt(DataContract.ViewAwardEntry.COL_WATCHED) == 1;
        final boolean favourite = cursor.getInt(DataContract.ViewAwardEntry.COL_FAVOURITE) == 1;

        // if the runtime is invalid, set it to unknown
        if (runtime < 1 && runtime != Movie.RUNTIME_UNKNOWN) {
            Timber.w("newViewAward(Cursor): invalid runtime: " + runtime);
            runtime = Movie.RUNTIME_UNKNOWN;
        }
        // if the id mandatory attribute is missing, return null
        if (id == null) {
            Timber.e("newViewAward(Cursor): missing id");
            return null;
        }
        // if the movieId mandatory attribute is missing, return null
        if (movieId == null) {
            Timber.e("newViewAward(Cursor): missing movieId");
            return null;
        }
        // if the imdbId mandatory attribute is missing, return null
        if (imdbId == null) {
            Timber.e("newViewAward(Cursor): missing imdbId");
            return null;
        }
        // if the title mandatory attribute is missing, return null
        if (title == null) {
            Timber.e("newViewAward(Cursor): missing title");
            return null;
        }

        return ViewAward.builder()
                .id(id)
                .movieId(movieId)
                .imdbId(imdbId)
                .awardDate(awardDate)
                .category(category)
                .review(review)
                .displayOrder(displayOrder)
                .title(title)
                .runtime(runtime)
                .genre(genre)
                .poster(poster)
                .onWishlist(onWishlist)
                .watched(watched)
                .favourite(favourite)
                .build();
    }


    @Nullable
    public static String toGenreNameCsv(@Nullable Context context, @Nullable String genreIdCsv) {
        if (context == null || genreIdCsv == null) {
            return null;
        }
        StringBuilder genreNameCsv = new StringBuilder();
        String[] genreIdArray = genreIdCsv.split(",");
        for (String genreId : genreIdArray) {
            String genreName = toGenreName(context, genreId);
            if (genreName != null) {
                if (genreNameCsv.length() > 0) {
                    genreNameCsv.append(", ");
                }
                genreNameCsv.append(genreName);
            }
        }
        return genreNameCsv.toString();
    }

    /**
     * Returns the genre name corresponding to a genre id.
     * @param context the context
     * @param genreId the genre id, e.g. "35"
     * @return the genre name, e.g. "Comedy"
     */
    @Nullable
    private static String toGenreName(@NonNull Context context, @NonNull String genreId) {
        Integer genreNameStringRes = GENRES.get(genreId);
        if (genreNameStringRes != null) {
            return context.getString(genreNameStringRes);
        }
        return null;
    }

}
