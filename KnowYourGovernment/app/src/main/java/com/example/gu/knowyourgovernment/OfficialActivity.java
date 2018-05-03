package com.example.gu.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by gu on 2018/3/19.
 */

public class OfficialActivity extends AppCompatActivity {

    private Official official;
    private TextView location;
    private TextView title;
    private TextView name;
    private TextView party;
    private ImageView image;
    private TextView address;
    private TextView phone;
    private TextView email;
    private TextView website;
    private ImageView youtube;
    private ImageView google;
    private ImageView twitter;
    private ImageView facebook;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        location = findViewById(R.id.location_official);
        title = findViewById(R.id.title_official);
        name = findViewById(R.id.name_official);
        party = findViewById(R.id.party_official);
        image = findViewById(R.id.image_official);
        address = findViewById(R.id.address_official);
        phone = findViewById(R.id.phone_official);
        email = findViewById(R.id.email_official);
        website = findViewById(R.id.website_official);
        youtube = findViewById(R.id.youtube);
        google = findViewById(R.id.google);
        twitter = findViewById(R.id.twitter);
        facebook = findViewById(R.id.facebook);
        loadInfo();
    }

    private void loadInfo() {
        Intent intent = getIntent();
        if (intent.hasExtra("location")) {
            String text = intent.getStringExtra("location");
            location.setText(text);
        }
        if (intent.hasExtra("official")) {
            official = (Official) intent.getSerializableExtra("official");
            title.setText(official.getTitle());
            name.setText(official.getName());
            party.setText("(" + (official.getParty()==null?"Unknown":official.getParty()) + ")");
            if ("Republican".equals(official.getParty()))
                findViewById(R.id.layout_official).setBackgroundColor(Color.parseColor("Red"));
            if ("Democratic".equals(official.getParty()))
                findViewById(R.id.layout_official).setBackgroundColor(Color.parseColor("Blue"));
            if (official.getAddress() != null) {
                address.setText(official.getAddress().getLine() + " " + official.getAddress().getCity() + ", " + official.getAddress().getState() + " " + official.getAddress().getZip());
                Linkify.addLinks((address), Linkify.MAP_ADDRESSES);
            }
            if (official.getPhones() != null) {
                if (official.getPhones().size() > 0) {
                    phone.setText(official.getPhones().get(0).toString());
                    Linkify.addLinks((phone), Linkify.PHONE_NUMBERS);
                }
            }
            if (official.getEmails() != null) {
                if (official.getEmails().size() > 0) {
                    email.setText(official.getEmails().get(0).toString());
                    Linkify.addLinks((email), Linkify.EMAIL_ADDRESSES);
                }
            }
            if (official.getUrls() != null) {
                if (official.getUrls().size() > 0) {
                    website.setText(official.getUrls().get(0).toString());
                    Linkify.addLinks((website), Linkify.WEB_URLS);
                }
            }
            Channel channel = official.getChannel();
            if (channel != null) {
                if(channel.getYouTubeId()!=null) youtube.setVisibility(View.VISIBLE);
                if(channel.getGooglePlusId()!=null) google.setVisibility(View.VISIBLE);
                if(channel.getTwitterId()!=null) twitter.setVisibility(View.VISIBLE);
                if(channel.getFacebookId()!=null) facebook.setVisibility(View.VISIBLE);
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

    public void clickGoogle(View v) {
        String name = official.getChannel().getGooglePlusId();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/" + name)));
        }
    }

    public void clickYoutube(View v) {
        String name = official.getChannel().getYouTubeId();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);     }
            catch (ActivityNotFoundException e)
            {         startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
            }
    }

    public void clickTwitter(View v) {
        String user = official.getChannel().getTwitterId();
        String twitterAppUrl = "twitter://user?screen_name=" + user;
        String twitterWebUrl = "https://twitter.com/" + user;

        Intent intent = null;
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
        }
        startActivity(intent);
    }

    public void clickFacebook(View v) {
        String fbName = official.getChannel().getFacebookId();
        String FACEBOOK_URL = "https://www.facebook.com/" + fbName;

        Intent intent = null;
        String urlToUse;
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);

            int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + fbName;
            }
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL));
        }
        startActivity(intent);
    }

    public void openPhotoActivity(View v){
        if (official.getPhotoUrl() == null) {return;}
        Intent intentShowPhoto = new Intent(this, PhotoActivity.class);
        intentShowPhoto.putExtra("location",location.getText().toString());
        intentShowPhoto.putExtra("official",official);
        startActivity(intentShowPhoto);
    }
}
