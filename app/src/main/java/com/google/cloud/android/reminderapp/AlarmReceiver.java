package com.google.cloud.android.reminderapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by 이상원 on 2017-08-06.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent mServiceintent = new Intent(context, AlarmSoundService.class);

        String temp = intent.getStringExtra("filename");
//        VoicePlayer vp = (VoicePlayer) intent.getSerializableExtra("OBJECT");

        mServiceintent.putExtra("filename", temp);
//        mServiceintent.putExtra("OBJECT", vp);

        context.startService(mServiceintent);
    }
}