package com.google.cloud.android.reminderapp;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * 다른 모든 클래스보다 먼저 시작하여 글꼴을 변경한다.
 * Fragment는 이 방식을 사용할 수 없는 것으로 알고 있으며
 * Activity일 경우 attatchBaseContext라는 메소드를 통해 글꼴을 적용할 수 있다.
 * 글꼴은 asset파일에 저장 하면 이용할 수 있다.
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
