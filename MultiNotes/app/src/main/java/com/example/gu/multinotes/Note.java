package com.example.gu.multinotes;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gu on 2018/1/31.
 */

public class Note {
    private String title;
    private String content;
    private String date;

    public Note(String title, String content){
        this.title = title;
        this.content = content;
        this.date = getCurrentDate();
    }

    public Note() {
    }

    public String getCurrentDate(){
        SimpleDateFormat df = new SimpleDateFormat("E MMM dd, hh:mm a");
        return df.format(new Date());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
