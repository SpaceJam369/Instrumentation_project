package com.aimconsulting.gettyexample.http.getty;

import android.support.annotation.NonNull;

import com.aimconsulting.gettyexample.http.ImageMetadata;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Model object for Getty API HTTP response.
 *
 * Created by yuribrigance on 5/3/17.
 */
class GettyResponse {

    private class DisplaySize {

        @SerializedName("uri")
        private String url;
    }

    private class ImageResult {

        @SerializedName("title")
        private String title;

        @SerializedName("display_sizes")
        private DisplaySize[] displaySizes;
    }

    @SerializedName("images")
    private ImageResult[] imageResults;

    /**
     * @return Returns a list of parsed ImageMetadata objects.
     */
    @NonNull ArrayList<ImageMetadata> getImageMetadata() {
        ArrayList<ImageMetadata> results = new ArrayList<>();

        for (ImageResult result : imageResults) {
            ImageMetadata metadata = new ImageMetadata();
            metadata.title = result.title;
            if (result.displaySizes.length > 0) {
                metadata.url = result.displaySizes[0].url;
            }
            results.add(metadata);
        }

        return results;
    }

}
