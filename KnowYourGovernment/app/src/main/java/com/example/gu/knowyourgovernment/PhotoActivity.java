package com.example.gu.knowyourgovernment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    private Official official;
    private TextView location;
    private TextView title;
    private TextView name;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        location = findViewById(R.id.location_photo);
        title = findViewById(R.id.title_photo);
        name = findViewById(R.id.name_photo);
        image = findViewById(R.id.image_photo);

        loadInfo();
    }

    private void loadInfo(){
        Intent intent = getIntent();
        if (intent.hasExtra("location")) {
            String text = intent.getStringExtra("location");
            location.setText(text);
        }
        if (intent.hasExtra("official")) {
            official = (Official) intent.getSerializableExtra("official");
            title.setText(official.getTitle());
            name.setText(official.getName());
            if ("Republican".equals(official.getParty()))
                findViewById(R.id.layout_photo).setBackgroundColor(Color.parseColor("Red"));
            if ("Democratic".equals(official.getParty()))
                findViewById(R.id.layout_photo).setBackgroundColor(Color.parseColor("Blue"));
        }
        if (official.getPhotoUrl() != null) {

            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {

                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {

                    // Here we try https if the http image attempt failed
                    String changedUrl = official.getPhotoUrl().replace("http:", "https:");
                    picasso.load(changedUrl).error(R.drawable.brokenimage).placeholder(R.drawable.placeholder).into(image);
                }
            }).build();
            picasso.load(official.getPhotoUrl()).error(R.drawable.brokenimage).placeholder(R.drawable.placeholder).into(image);
        }
    }
}
