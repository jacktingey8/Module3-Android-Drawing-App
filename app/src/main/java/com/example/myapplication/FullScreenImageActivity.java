package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView imageView = findViewById(R.id.image_view_full);
        String uriString = getIntent().getStringExtra("image_uri");
        if (uriString != null) {
            imageView.setImageURI(Uri.parse(uriString));
        }

        imageView.setOnClickListener(v -> finish());
    }
}
