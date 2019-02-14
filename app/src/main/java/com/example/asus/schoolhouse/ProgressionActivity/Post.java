package com.example.asus.schoolhouse.ProgressionActivity;

/**
 * Created by Asus on 9/11/2017.
 */

public class Post {

    private String title;
    private String desc;
    private String image;
    private String username;
    private String date;
    private String uid;
    private String url;

    public Post(){

    }

    public Post(String title, String desc, String image, String username, String date, String uid, String url) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.username = username;
        this.date = date;
        this.uid = uid;
        this.url = url;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
