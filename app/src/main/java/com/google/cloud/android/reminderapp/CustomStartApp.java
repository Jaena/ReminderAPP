package com.google.cloud.android.reminderapp;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * Created by jaena on 2017-06-29.
 */

public class CustomStartApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "font.ttf"))
                .addBold(Typekit.createFromAsset(this, "font.ttf"))
                .addCustom1(Typekit.createFromAsset(this, "font.ttf"));// "fonts/폰트.ttf"
    }
}
