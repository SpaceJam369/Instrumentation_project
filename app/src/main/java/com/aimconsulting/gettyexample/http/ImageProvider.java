package com.aimconsulting.gettyexample.http;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Provides a list of image metadata.
 *
 * Created by yuribrigance on 5/3/17.
 */
public interface ImageProvider {

    /**
     * Callback interface for receiving an HTTP response.
     *
     * Created by yuribrigance on 5/3/17.
     */
     interface ImageProviderCallback {

        /**
         * Called when the image metadata has been successfully retrieved.
         * @param metadata List of image metadata objects.
         */
        void onSuccess(ArrayList<ImageMetadata> metadata);

        /**
         * Called if there was an error.
         * @param exception Exception object.
         */
        void onFailure(Exception exception);
    }

    /**
     * Given a query string, returns image metadata results.
     * @param query Query string. For example: "cat".
     * @param pageNumber Current page number, starting with 1.
     * @param callback Callback which gets called when results are fetched.
     */
    void getImagesForQuery(@NonNull  String query,
                           int pageNumber,
                           @NonNull ImageProviderCallback callback);
}
