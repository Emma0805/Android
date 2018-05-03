package com.example.gu.knowyourgovernment;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gu on 2018/3/18.
 */

public class Official implements Serializable {
    private String name;
    private String title;
    private String party;
    private Address address;
    private List phones;
    private List urls;
    private List emails;
    private String photoUrl;
    private Channel channel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(String line, String city, String state, String zip) {
        this.address = new Address(line, city, state, zip);
    }

    public List getPhones() {
        return phones;
    }

    public void setPhones(JSONArray phones) {
        this.phones = new ArrayList();
        try {
            for (int i = 0; i < phones.length(); i++) {
                this.phones.add(phones.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List getUrls() {
        return urls;
    }

    public void setUrls(JSONArray urls) {
        this.urls = new ArrayList();
        try {
            for (int i = 0; i < urls.length(); i++) {
                this.urls.add(urls.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List getEmails() {
        return emails;
    }

    public void setEmails(JSONArray emails) {
        this.emails = new ArrayList();
        try {
            for (int i = 0; i < emails.length(); i++) {
                this.emails.add(emails.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
