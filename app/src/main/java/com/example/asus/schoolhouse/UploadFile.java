package com.example.asus.schoolhouse;

/**
 * Created by Asus on 27/11/2017.
 */

public class UploadFile {

    public String name;
    public String url;
    public String uid;
    public String date;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public UploadFile() {
    }

    public UploadFile(String name, String url, String uid, String date) {
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
