package com.example.asus.schoolhouse.ProgressionActivity;

/**
 * Created by Asus on 18/11/2017.
 */

public class Comment {

    private String comment_detail;
    private String uid;
    private String username;
    private String date;

    public Comment(){
    }

    public Comment(String comment_detail, String uid, String username, String date) {
        this.comment_detail = comment_detail;
        this.uid = uid;
        this.username = username;
        this.date = date;
    }

    public String getComment_detail() {
        return comment_detail;
    }

    public void setComment_detail(String comment_detail) {
        this.comment_detail = comment_detail;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
