package com.example.asus.schoolhouse;

/**
 * Created by Asus on 26/11/2017.
 */

public class UploadImage {

    public String name;
    public String url;
    public String uid;
    public String date;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public UploadImage() {
    }

    public UploadImage(String name, String url, String uid, String date) {
        this.name = name;
        this.url= url;
        this.uid = uid;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getUid(){
        return uid;
    }

    public String getDate(){
        return date;
    }
}
