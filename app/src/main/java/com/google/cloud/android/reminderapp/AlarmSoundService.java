package com.google.cloud.android.reminderapp;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.Toast;

import static com.google.cloud.android.reminderapp.MainActivity.value;

public class AlarmSoundService extends Service {
    public AlarmSoundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String temp = intent.getStringExtra("filename");
//        VoicePlayer vp = (VoicePlayer)intent.getSerializableExtra("OBJECT");

        Toast.makeText(this, "알람이 울립니다. " + temp, Toast.LENGTH_LONG).show();

        MainActivity.device.setVisibility(View.VISIBLE);
        MainActivity.record.setEnabled(false);
        MainActivity.record.setVisibility(View.GONE);
        MainActivity.play.setEnabled(false);
        MainActivity.play.setVisibility(View.GONE);
        MainActivity.mText.setText("알람 중");
        MainActivity.mText.setVisibility(View.VISIBLE);
        MainActivity.numPlayList.setVisibility(View.INVISIBLE);
//        try {
//            Thread.sleep(1000);
//        }catch(InterruptedException e) {
//            e.printStackTrace();
//        }
//        //응답하지 않습니다 문제 - 별도의 쓰레드 사용해보기
//        MainActivity.voicePlayer.playWaveFileAlarm(16000, 1024, temp);

        class BackgroundTask extends AsyncTask<String, String, String> {
            protected void onPreExecute() {

            }

            protected String doInBackground(String ... values) {
                //응답하지 않습니다 문제 - 별도의 쓰레드 사용해보기
                MainActivity.voicePlayer.playWaveFileAlarm(16000, 1024, values[0]);
//                publishProgress(values[0]);
                return values[0];
            }

            protected void onProgressUpdate(String values) {
//                Toast.makeText(getApplicationContext(), "알람이 울립니다. ", Toast.LENGTH_LONG).show();
//                MainActivity.device.callOnClick();
            }

            protected void onPostExecute(String result) {
                MainActivity.device.callOnClick();
            }

            protected void onCancelled() {

            }
        }

        BackgroundTask task = new BackgroundTask();
        task.execute(temp);

        return START_NOT_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
