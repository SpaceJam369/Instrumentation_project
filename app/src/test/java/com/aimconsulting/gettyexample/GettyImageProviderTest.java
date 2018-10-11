package com.aimconsulting.gettyexample;

import com.aimconsulting.gettyexample.http.ImageMetadata;
import com.aimconsulting.gettyexample.http.ImageProvider;
import com.aimconsulting.gettyexample.http.getty.GettyImageProvider;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.*;

public class GettyImageProviderTest {

    private  CountDownLatch latch = new CountDownLatch(1);
    private GettyImageProvider provider = new GettyImageProvider();

    @Test
    public void testImageMetadata() throws InterruptedException {
        provider.getImagesForQuery("cat", 1, new ImageProvider.ImageProviderCallback() {

            @Override
            public void onSuccess(ArrayList<ImageMetadata> metadata) {
                assertNotNull(metadata);
                assertFalse(metadata.isEmpty());
                for (ImageMetadata item : metadata) {
                    assertNotNull(item.title);
                    assertNotNull(item.url);
                }
                latch.countDown();
            }

            @Override
            public void onFailure(Exception exception) {
                assertFalse(true);
                latch.countDown();
            }
        });
        latch.await(150, TimeUnit.SECONDS);
    }
}
