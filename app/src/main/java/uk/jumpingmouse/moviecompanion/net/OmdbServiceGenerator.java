package uk.jumpingmouse.moviecompanion.net;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A factory class which generates Retrofit service objects for creating connections
 * to the OMDb API server.
 * @author Edmund Johnson
 */
public class OmdbServiceGenerator {
    /** The base URL. */
    private static final String API_BASE_URL = "https://www.omdbapi.com";
    // URL to test a non-XML response (FEED_STATUS_SERVER_DATA_INVALID)
    //"http://www.google.com";
    // URL to test an error 400 response (FEED_STATUS_SERVER_INVALID)
    //"http://www.google.com/ping";
    // URL to test an error 404 response (FEED_STATUS_SERVER_INVALID)
    //"http://jumpingmouse.uk/nosuchpage";

    public static <S> S createService(Class<S> serviceClass) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        return retrofit.create(serviceClass);
    }
}
