package com.aimconsulting.gettyexample.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.aimconsulting.gettyexample.R;
import com.aimconsulting.gettyexample.grid.ImageGridViewAdapter;
import com.aimconsulting.gettyexample.http.ImageMetadata;
import com.aimconsulting.gettyexample.http.ImageProvider;
import com.aimconsulting.gettyexample.http.getty.GettyImageProvider;
import com.aimconsulting.gettyexample.http.getty.TestGettyImageProvider;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageSearchActivity extends AppCompatActivity {

    /** The scroll threshold for downloading new data. */
    private static final float SCROLL_THRESHOLD = .75f;

    /** The simulated progress bar will be incremented at most by this interval */
    private static final int SIMULATED_PROGRESS_MAX_INTERVAL_MS = 50;
    private static final int HUNDRED_PERCENT = 100;

    /** This is a simulated progress bar. */
    @BindView(R.id.progressBar) ProgressBar progressBar;

    /** The search text entry view. */
    @BindView(R.id.searchView) SearchView searchView;

    /** Provides images for a specific query. */
    private ImageProvider imageProvider;

    /** Displays provided images in a grid. */
    private ImageGridViewAdapter gridViewAdapter;

    /** Previous query. */
    private String prevQuery = null;

    /** Keeps track of the current page. Used by continuous scroller. */
    private int currentPage = 0;

    /** This flag is set to true when data is being downloaded. */
    private boolean requestInProgress = false;

    /** This is executed whenever the user searches for new images. */
    private ImageProvider.ImageProviderCallback imageProviderCallback = new ImageProvider.ImageProviderCallback() {
        @Override
        public void onSuccess(final ArrayList<ImageMetadata> metadata) {

            // Update the grid view.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (currentPage == 1) {
                        gridViewAdapter.getData().clear();
                    }
                    gridViewAdapter.getData().addAll(metadata);
                    gridViewAdapter.notifyDataSetChanged();
                }
            });

            // Download complete, hide the progress indicator.
            showDownloadProgress(false);
        }

        @Override
        public void onFailure(Exception exception) {

            // Hide the progress indicator.
            showDownloadProgress(false);

            // Display the error.
            AlertDialog alertDialog = new AlertDialog.Builder(ImageSearchActivity.this).create();
            alertDialog.setTitle("Error!");
            alertDialog.setMessage(exception != null ? exception.getLocalizedMessage() : "Request failed.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    };

    /** This class just makes the progress bar simulated so that it looks like stuff is loading. */
    private FakeProgressRunnable fakeProgressRunnable = new FakeProgressRunnable();
    class FakeProgressRunnable implements Runnable {

        private boolean stop = false;

        @Override
        public void run() {
            while (!stop) {

                // Set progress bar progress.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int progress = progressBar.getProgress();
                        if (progress < HUNDRED_PERCENT) {
                            progress++;
                        } else {
                            stop = true;
                            return;
                        }
                        progressBar.setProgress(progress);
                    }
                });

                try {
                    Thread.sleep((long)(Math.random() * SIMULATED_PROGRESS_MAX_INTERVAL_MS));
                } catch (Exception ignored) {}

            }
            stop = false;
        }
    }

    /** This is executed when the user interacts with the search view. */
    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            showDownloadProgress(true);

            // Make sure that the query is not the same.
            if (prevQuery == null || !query.equals(prevQuery)) {

                // Reset scroll point to top.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gridViewAdapter.getData().clear();
                        gridViewAdapter.notifyDataSetChanged();

                        // Hide the keyboard.
                        searchView.clearFocus();
                    }
                });
                prevQuery = query;
                currentPage = 1;

                // Make the request.
                imageProvider.getImagesForQuery(query, currentPage, imageProviderCallback);
            }

            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return true;
        }
    };

    /**
     * This monitors the grid view's scroll position and downloads more images when
     * the grid view is X% near the bottom. Allows for continuous scrolling.
     */
    private GridView.OnScrollListener onScrollListener = new GridView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {}

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            float scrolledItems = firstVisibleItem + visibleItemCount;
            float percentScrolled = (scrolledItems / totalItemCount);

            // Request the next page if there 75% scrolled.
            if (percentScrolled > SCROLL_THRESHOLD && !requestInProgress) {
                currentPage++;
                showDownloadProgress(true);
                imageProvider.getImagesForQuery(prevQuery, currentPage, imageProviderCallback);
            }
        }
    };

    /**
     * Starts an image view activity when a grid item is clicked.
     */
    private GridView.OnItemClickListener onItemClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(ImageSearchActivity.this, ImageViewActivity.class);
            ImageMetadata metadata = gridViewAdapter.getData().get(position);
            intent.putExtra("url", metadata.url);
            intent.putExtra("title", metadata.title);
            startActivity(intent);
        }
    };

    /**
     * Shows or hides the progress bad.
     * @param show Set to true to show, false to hide.
     */
    private void showDownloadProgress(final boolean show) {
        requestInProgress = show;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.VISIBLE);
                    new Thread(fakeProgressRunnable).start();
                } else {
                    progressBar.setVisibility(View.GONE);
                    fakeProgressRunnable.stop = true;
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);

        ButterKnife.bind(this);

        //FIXME: For instrumentation test purpose i used test image provider..
        imageProvider = new TestGettyImageProvider();

        GridView gridView = findViewById(R.id.gridView);
        gridViewAdapter = new ImageGridViewAdapter(this, R.layout.grid_item_layout, new ArrayList<ImageMetadata>());
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnScrollListener(onScrollListener);
        gridView.setOnItemClickListener(onItemClickListener);

        searchView.setOnQueryTextListener(onQueryTextListener);

        progressBar.setVisibility(View.GONE);
    }
}
