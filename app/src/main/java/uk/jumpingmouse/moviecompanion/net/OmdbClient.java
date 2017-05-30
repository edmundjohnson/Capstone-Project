package uk.jumpingmouse.moviecompanion.net;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * A client interface for Retrofit calls which read the OMDb API data.
 * @author Edmund Johnson
 */
public interface OmdbClient {
    // URL to test a non-XML response (FEED_STATUS_SERVER_DATA_INVALID)
    //"http://www.google.com";
    // URL to test an error 400 response (FEED_STATUS_SERVER_INVALID)
    //"http://www.google.com/ping";
    // URL to test an error 404 response (FEED_STATUS_SERVER_INVALID)
    //"http://jumpingmouse.uk/nosuchpage";

    @GET("/")
    Call<OmdbMovie> movieByImdbIdCall(
            @SuppressWarnings("SameParameterValue") @Query("apikey") String omdbApiKey,
            @SuppressWarnings("SameParameterValue") @Query("i") String imdbId
    );
}
