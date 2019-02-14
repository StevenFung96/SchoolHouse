package com.example.asus.schoolhouse.EventActivity;

/**
 * Created by Asus on 21/11/2017.
 */

public class Event {

    private String title;
    private String desc;
    private String location;
    private String postDate;
    private String date;
    private String time;
    private String dateTime;
    private String uid;
    private String username;
    private String type;

    public Event(){

    }

    public Event(String title, String desc, String location, String postDate, String date, String time, String dateTime, String uid, String username, String type) {
        this.title = title;
        this.desc = desc;
        this.location = location;
        this.postDate = postDate;
        this.date = date;
        this.time = time;
        this.dateTime = dateTime;
        this.uid = uid;
        this.username = username;
        this.type = type;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
