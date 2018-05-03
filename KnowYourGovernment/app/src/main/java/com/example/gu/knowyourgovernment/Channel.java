package com.example.gu.knowyourgovernment;

import java.io.Serializable;

/**
 * Created by gu on 2018/3/19.
 */

public class Channel implements Serializable {
    private String GooglePlusId;
    private String FacebookId;
    private String TwitterId;
    private String YouTubeId;

    public Channel() {
    }

    public Channel(String GooglePlusId, String FacebookId, String TwitterId, String YouTubeId) {
        this.GooglePlusId = GooglePlusId;
        this.FacebookId = FacebookId;
        this.TwitterId = TwitterId;
        this.YouTubeId = YouTubeId;
    }

    public String getGooglePlusId() {
        return GooglePlusId;
    }

    public void setGooglePlusId(String googlePlusId) {
        GooglePlusId = googlePlusId;
    }

    public String getFacebookId() {
        return FacebookId;
    }

    public void setFacebookId(String facebookId) {
        FacebookId = facebookId;
    }

    public String getTwitterId() {
        return TwitterId;
    }

    public void setTwitterId(String twitterId) {
        TwitterId = twitterId;
    }

    public String getYouTubeId() {
        return YouTubeId;
    }

    public void setYouTubeId(String youTubeId) {
        YouTubeId = youTubeId;
    }
}
