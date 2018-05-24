package com.example.muhammadkhan.ridersapp.Activities;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.example.muhammadkhan.ridersapp.Models.Driver;
import com.example.muhammadkhan.ridersapp.R;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivityForImage extends AppCompatActivity {
    String[] list;
    int index=0;
    ImageView imageView;
    Toolbar toolbar;
    Button button;
    Driver model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_for_image);
        //only portrait mode
        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);

        imageView=(ImageView) findViewById(R.id.image_id);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        button=(Button) findViewById(R.id.next);

        //getting data from previous activity
        Bundle bundle = getIntent().getExtras();
        model=(Driver) bundle.getSerializable("object");
        list=bundle.getStringArray("images");

        toolbar.setNavigationIcon(R.mipmap.ic_keyboard_backspace_white_24dp);
        toolbar.setTitle("Vehicle Pictures");
        toolbar.setTitleTextColor(getResources().getColor(R.color.cardview_light_background));

        Glide.with(this).load(Uri.parse(list[index])).into(imageView);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index++;
                if(index==4){
                    index=0;
                }
                Glide
                        .with(FullscreenActivityForImage.this)
                        .load(Uri.parse(list[index]))
                        .into(imageView);
            }
        });
    }

}
