package com.aimconsulting.gettyexample.grid;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aimconsulting.gettyexample.R;
import com.aimconsulting.gettyexample.http.ImageMetadata;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageGridViewAdapter extends ArrayAdapter<ImageMetadata> {

    private Context context;
    private int layoutResId;
    private ArrayList<ImageMetadata> data;

    public ImageGridViewAdapter(@NonNull Context context,
                                int layoutResId,
                                @NonNull ArrayList<ImageMetadata> data) {
        super(context, layoutResId, data);
        this.context = context;
        this.layoutResId = layoutResId;
        this.data = data;
    }

    public @NonNull ArrayList<ImageMetadata> getData() {
        return this.data;
    }


    @Override
    public @NonNull View getView(int position,
                                 View convertView,
                                 @NonNull ViewGroup parent) {
        View gridItem = convertView;
        ImageGridViewItem model;

        if (gridItem == null) {
            // Get the layout inflater, this will be used to get the grid item's layout.
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();

            // Inflate the layout into a grid item view.
            gridItem = inflater.inflate(layoutResId, parent, false);

            // Initialize the view's model.
            TextView textView = gridItem.findViewById(R.id.textView);
            ImageView imageView = gridItem.findViewById(R.id.imageView);
            model = new ImageGridViewItem(imageView, textView);
            gridItem.setTag(model);
        } else {
            model = (ImageGridViewItem) gridItem.getTag();
        }

        // Set the image name.
        ImageMetadata viewData = data.get(position);
        if (viewData.title != null) {
            model.getTextView().setText(viewData.title);
        }

        // Download the image bitmap.
        if (viewData.url != null) {
            Picasso.with(context).load(viewData.url).placeholder(R.drawable.ic_image_load).into(model.getImageView());
        }

        return gridItem;
    }
}