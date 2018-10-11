package com.aimconsulting.gettyexample.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.aimconsulting.gettyexample.R;
import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        if (url != null) {
            url = url.split("\\?")[0];
        }
        String title = intent.getStringExtra("title");

        ImageView imageView = findViewById(R.id.largeImageView);
        Picasso.with(this).load(url).placeholder(R.drawable.ic_image_load).into(imageView);

        TextView textView = findViewById(R.id.largeTextView);
        textView.setText(title);
    }
}
