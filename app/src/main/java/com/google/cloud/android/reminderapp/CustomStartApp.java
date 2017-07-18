package com.google.cloud.android.reminderapp;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * It starts first among any other class to change font.
 * it get a font in asset folder.
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
