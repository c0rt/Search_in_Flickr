package com.example.searchinflickr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.URL;

public class ImageActivity extends AppCompatActivity {

    public static final String PHOTO_URL_KEY = "photo_url_key";

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra(PHOTO_URL_KEY, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent intent = this.getIntent();
        final String photoUrl = intent.getStringExtra(PHOTO_URL_KEY);
        final ImageView iv = findViewById(R.id.imageView);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                Bitmap bmp = null;
                try {
                    url = new URL(photoUrl);
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(bmp != null)
                    iv.setImageBitmap(bmp);
                else
                    Snackbar.make(iv, "URL картинки не найден, проблема со стороны Flickr", Snackbar.LENGTH_LONG).show();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
