package com.example.gu.knowyourgovernment;

import java.io.Serializable;

/**
 * Created by gu on 2018/3/19.
 */

public class Address implements Serializable {
    private String Line;
    private String city;
    private String state;
    private String zip;

    public Address() {
    }

    public Address(String line, String city, String state, String zip) {
        Line = line;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public String getLine() {
        return Line;
    }

    public void setLine(String line) {
        Line = line;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
