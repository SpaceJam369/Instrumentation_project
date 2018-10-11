package com.aimconsulting.gettyexample.grid;

import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Contains all the UI elements needed to render a grid view item.
 *
 * Created by yuribrigance on 5/3/17.
 */
class ImageGridViewItem {

    private ImageView imageView;
    private TextView textView;

    /**
     * Default constructor.
     * @param imageView ImageView instance.
     * @param textView TextView instance.
     */
    protected ImageGridViewItem(@NonNull ImageView imageView, @NonNull TextView textView) {
        this.imageView = imageView;
        this.textView = textView;
    }

    @NonNull ImageView getImageView() {
        return imageView;
    }

    @NonNull TextView getTextView() {
        return textView;
    }
    
}
