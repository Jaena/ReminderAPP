package com.google.cloud.android.reminderapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by 이상원 on 2017-07-27.
 */

public class PlaylistView extends LinearLayout{
    TextView textView;
    TextView textView2;
    TextView textView3;
    ImageView imageView;

    public PlaylistView(Context context) {
        super(context);
        init(context);
    }

    public PlaylistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.play_list, this, true);

        textView = (TextView) findViewById(R.id.textView);
//        textView2 = (TextView) findViewById(R.id.textView2);
//        textView3 = (TextView) findViewById(R.id.textView3);
        //imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void setName(String name) {
        textView.setText(name);
    }

    public void setMobile(String mobile) {
        textView2.setText(mobile);
    }

    public void setAge(int age) {
        System.out.println("나이 : " + age);
        textView3.setText(""+age);
    }

    public void setImage(int resId) {
        imageView.setImageResource(resId);
    }
}
