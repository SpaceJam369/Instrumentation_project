package com.aimconsulting.gettyexample.http.getty;

import android.support.annotation.NonNull;
import android.util.Log;

import com.aimconsulting.gettyexample.http.ImageProvider;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class TestGettyImageProvider implements ImageProvider{


    /**
     * API Key and header values.
     */
    private static final String HEADER_API_KEY = "Api-Key";
    private static final String GETTY_API_KEY = "9yh55f5xa2mrcduqdtr3qjkb";

    /**
     * Getty URL format.
     */
    private static final String URL_FORMAT = "https://api.gettyimages.com/v3/search/images?phrase=%s&page=%d";

    /**
     * HTTP client which handles all communications.
     */
//    private OkHttpClient httpClient = new OkHttpClient();

    /**
     * Used to serialize JSON strings into objects.
     */
    private Gson gson = new Gson();

    private static String BASE_URl = "";

    /**
     * Given a query string and page number, returns the complete URL of the request.
     * @param query Query string. For example: "cat".
     * @param pageNumber Page number of the request.
     * @return Request URL.
     */
    private String getUrlForQuery(String query, int pageNumber) {
        return BASE_URl + "search/" + "images?phrase=" + query + "page" + pageNumber;
    }

    /**
     * Instrumentation tests runs in local host.. instead of the macking the actual network call
     * @param baseUrl mock web server url
     */
    public static void setBaseUrl(String baseUrl){
            BASE_URl = baseUrl;
    }


    @Override
    public void getImagesForQuery(@NonNull String query,
                                  int pageNumber,
                                  @NonNull final ImageProvider.ImageProviderCallback callback) {
        String url = getUrlForQuery(query, pageNumber);

        //Logging interceptor added for testing purpose... to get the json data for testing..
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .build();

        Request request = new Request.Builder().url(url).addHeader(HEADER_API_KEY, GETTY_API_KEY).build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException exception) {
                callback.onFailure(exception);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    @SuppressWarnings("ConstantConditions") String jsonString = response.body().string();
                    GettyResponse gettyResponse = gson.fromJson(jsonString, GettyResponse.class);
                    callback.onSuccess(gettyResponse.getImageMetadata());
                } else {
                    callback.onFailure(new Exception("Response was empty"));
                }
            }
        });
    }
}
