package com.google.cloud.android.reminderapp;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by 이상원 on 2017-07-27.
 */

public class PlaylistView extends LinearLayout {
    TextView textView;
    public PlaylistView(Context context) {
        super(context);
        init(context);
    }

    public PlaylistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.play_list, this, true);

        textView = (TextView) findViewById(R.id.textView);
    }

    public void setName(String name) {
        textView.setText(name);
    }
}
