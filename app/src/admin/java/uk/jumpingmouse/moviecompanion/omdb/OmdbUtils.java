package uk.jumpingmouse.moviecompanion.omdb;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;
import uk.jumpingmouse.moviecompanion.data.Movie;

/**
 * Class containing utility methods related to the open movie database API.
 * @author Edmund Johnson
 */
public class OmdbUtils {
    /** The date format used by the OMDb API, e.g. "01 Jun 2016". */
    private static final String DATE_FORMAT_OMDB_RELEASED = "dd MMM yyyy";

    /** The format for converting between OMDb-formatted date Strings and Dates. */
    private static SimpleDateFormat sDateFormatOmdbReleased = null;

    /** The singleton instance of this class. */
    private static OmdbUtils sOmdbUtils = null;

    //---------------------------------------------------------------------
    // Instance handling methods

    /**
     * Returns an instance of this class.
     * @return an instance of this class
     */
    @NonNull
    public static OmdbUtils getInstance() {
        if (sOmdbUtils == null) {
            sOmdbUtils = new OmdbUtils();
        }
        return sOmdbUtils;
    }

    /** Private default constructor, to prevent instantiation from outside this class. */
    private OmdbUtils() {
    }

    //---------------------------------------------------------------------
    // Utility methods

//    /**
//     * Fetches and returns a movie from the Omdb.
//     * @param omdbApiKey the OMDb API key
//     * @param imdbId the movie's IMDb id
//     * @return the movie with the specified IMDb id.
//     */
//    @Nullable
//    @WorkerThread
//    Movie fetchMovie(@Nullable String omdbApiKey, @Nullable String imdbId) {
//        if (omdbApiKey == null || imdbId == null || imdbId.isEmpty()) {
//            return null;
//        }
//        // Create a client to read the OMDb API
//        OmdbClient client = OmdbServiceGenerator.createService(OmdbClient.class);
//        // Create a call to read the JSON
//        Call<OmdbMovie> call = client.movieByImdbIdCall(omdbApiKey, imdbId);
//
//        try {
//            // Execute the call to obtain the data from the server
//            Response response = call.execute();
//
//            if (!response.isSuccessful()) {
//                // Handle any connection errors
//                Timber.w("movieByImdbIdCall: response.code(): " + response.code());
//                switch (response.code()) {
//                    case HttpURLConnection.HTTP_BAD_REQUEST:
//                    case HttpURLConnection.HTTP_NOT_FOUND:
//                        //getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_SERVER_INVALID);
//                        return null;
//                    default:
//                        //getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_SERVER_ERROR);
//                        return null;
//                }
//            } else {
//                // Response received successfully, parse it to get the OMDb API data
//                OmdbMovie omdbMovie = (OmdbMovie) response.body();
//                if (omdbMovie == null) {
//                    Timber.e("movieByImdbIdCall: response body does not contain OmdbMovie");
//                    //getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_SERVER_NO_DATA);
//                    return null;
//                } else if (omdbMovie.getImdbID() == null) {
//                    Timber.e("movieByImdbIdCall: response body does not contain OmdbMovie.imdbID");
//                    //getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_SERVER_NO_DATA);
//                    return null;
//                } else if (omdbMovie.getTitle() == null) {
//                    Timber.e("movieByImdbIdCall: response body does not contain OmdbMovie.title");
//                    //getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_SERVER_NO_DATA);
//                    return null;
//                } else {
//                    @SuppressWarnings("UnnecessaryLocalVariable")
//                    Movie movie = Movie.builder()
//                            .id(omdbMovie.getImdbID())
//                            .imdbId(omdbMovie.getImdbID())
//                            .title(omdbMovie.getTitle())
//                            .genre(omdbMovie.getGenre())
//                            .runtime(toIntOmdbRuntime(omdbMovie.getRuntime()))
//                            .poster(omdbMovie.getPoster())
//                            .year(omdbMovie.getYear())
//                            .released(toLongOmdbReleased(omdbMovie.getReleased()))
//                            .build();
//                    // If the movie data has been obtained, return the movie
//                    //getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_OK);
//                    return movie;
//                }
//            }
//        } catch (Exception e) {
//            Timber.e("movieByImdbIdCall: Exception: ", e);
//            // This is probably an error parsing the JSON into the Movie object
////            getPrefUtils().setOmdbApiStatus(context, PrefUtils.STATUS_SERVER_DATA_INVALID);
//            return null;
//        }
//    }

    //---------------------------------------------------------------------
    // Movie runtime field

    /**
     * Returns an OMDb runtime as an int, e.g. returns "144 min" as 144
     * @param omdbRuntime the OMDb runtime, e.g. "144 min"
     * @return the runtime as an int, e.g. 144
     */
    int toIntOmdbRuntime(@Nullable String omdbRuntime) {
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

    /**
     * Returns an OMDb-formatted runtime string representing a number of minutes.
     * @param runtime the runtime in minutes
     * @return an OMDb-formatted runtime string corresponding to the runtime,
     *         or an empty String if runtime indicates an unknown runtime
     */
    @NonNull
    String toStringOmdbRuntime(final int runtime) {
        if (runtime == Movie.RUNTIME_UNKNOWN) {
            return "";
        }
        return runtime + " min";
    }

    //---------------------------------------------------------------------
    // Movie released field

    /**
     * Returns an OMDb-formatted released date string representing a number of milliseconds.
     * @param released a release date as a number of milliseconds
     * @return an OMDb-formatted released date string corresponding to released,
     *         or an empty String if released indicates an unknown release date
     */
    @NonNull
    String toStringOmdbReleased(final long released) {
        if (released == Movie.RELEASED_UNKNOWN || released < 0) {
            return "";
        }
        Date dateReleased = new Date(released);
        return toStringOmdbReleased(dateReleased);
    }

    /**
     * Returns a Date as a String in the OMDb released date format, e.g. "12 Jun 2017".
     * @param date the date
     * @return the date in the OMDb released date string format
     */
    @NonNull
    private String toStringOmdbReleased(@NonNull final Date date) {
        return toString(getDateFormatOmdbReleased(), date);
    }

    /**
     * Returns a long representing an OMDb-formatted released date as a number of milliseconds.
     * @param omdbReleased an OMDb released date, formatted as "dd MMM yyyy"
     * @return a long object representing omdbReleased as a number of milliseconds,
     *         or RELEASED_UNKNOWN if omdbReleased could not be converted to a long
     */
    long toLongOmdbReleased(@Nullable final String omdbReleased) {
        Date dateReleased = toDateOmdbReleased(omdbReleased);
        if (dateReleased == null) {
            return Movie.RELEASED_UNKNOWN;
        } else {
            return dateReleased.getTime();
        }
    }

    /**
     * Returns a Date object representing an OMDb-formatted date string, e.g. "11 Jun 2016".
     * @param strDate an OMDb-formatted date string, e.g. "11 Jun 2016"
     * @return a Date object representing the supplied String,
     *         or null if the String could not be parsed as a date
     */
    @Nullable
    private Date toDateOmdbReleased(@Nullable final String strDate) {
        return toDate(getDateFormatOmdbReleased(), strDate);
    }

    /**
     * Returns a SimpleDateFormat object for OMDb released dates.
     * @return a SimpleDateFormat object for OMDb released dates
     */
    @NonNull
    private static SimpleDateFormat getDateFormatOmdbReleased() {
        if (sDateFormatOmdbReleased == null) {
            sDateFormatOmdbReleased = new SimpleDateFormat(DATE_FORMAT_OMDB_RELEASED, Locale.US);
        }
        return sDateFormatOmdbReleased;
    }

    //---------------------------------------------------------------------
    // Date utilities

    /**
     * Returns a Date object representing a supplied String.
     * @param format the SimpleDateFormat used for strDate, e.g. "dd MMM yyyy"
     * @param strDate a String representing a date
     * @return a Date object representing the supplied String,
     *         or null if the String could not be parsed as a date
     */
    @Nullable
    private static Date toDate(@NonNull SimpleDateFormat format, @Nullable final String strDate) {
        if (strDate == null) {
            return null;
        }
        try {
            return format.parse(strDate);
        } catch (ParseException e) {
            Timber.e("toDate: Cannot parse String \"" + strDate + "\" to a Date using format: \""
                    + format + "\"", e);
            return null;
        }
    }

    /**
     * Returns a Date as a String.
     * @param format the SimpleDateFormat used for returned String, e.g. "dd MMM yyyy"
     * @param date the Date
     * @return the Date as a String
     */
    @NonNull
    private static String toString(@NonNull SimpleDateFormat format, @NonNull final Date date) {
        return format.format(date);
    }

}
