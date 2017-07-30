package com.google.cloud.android.reminderapp;

/**
 * Created by 이상원 on 2017-07-27.
 */

public class Playlist {
    String name;
    String mobile;
    int age;
//    int resId;

    public Playlist(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}