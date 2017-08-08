package com.google.cloud.android.reminderapp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.Toast;

import static com.google.cloud.android.reminderapp.MainActivity.play;
import static com.google.cloud.android.reminderapp.MainActivity.playingPos;
import static com.google.cloud.android.reminderapp.MainActivity.value;

public class AlarmSoundService extends Service {
    public AlarmSoundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void recordUIInvisible() {
        MainActivity.mText2.setVisibility(View.INVISIBLE);
        MainActivity.recordresult.setVisibility(View.INVISIBLE);
        MainActivity.resulttitle.setVisibility(View.INVISIBLE);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(MainActivity.mVoiceRecorder.mIsRecording) {
            MainActivity.mVoiceRecorder.stopRecording();
            MainActivity.timer.cancel(); //** 잊기 쉬움. 주의!

            recordUIInvisible();
//            MainActivity.mText2.setVisibility(View.INVISIBLE);
//            MainActivity.recordresult.setVisibility(View.INVISIBLE);
//            MainActivity.resulttitle.setVisibility(View.INVISIBLE);
            System.out.println("녹음 중에 알람이 울린다");
            //db에서 해당 음성 파일을 삭제하는 작업이 필요하지만, 현재 구현상 STT전에 멈추면,
            //db에 파일이름이 저장되지 않아서 큰 상관은 없어보인다. 단, 내장 메모리에는 음성 파일이 저장돼 있다.
            //그래서 녹음 중 알람이 울릴 때가 되면 녹음을 중지시키고 알람을 울리게 하는 기능이 겉으로 볼 땐 큰 문제 없을 것 같다.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String temp = intent.getStringExtra("filename");

        if(!MainActivity.powerOn) {
            Toast.makeText(this, "알람이 울립니다. " + temp, Toast.LENGTH_LONG).show();
            return START_NOT_STICKY;
        }

//        VoicePlayer vp = (VoicePlayer)intent.getSerializableExtra("OBJECT");

        Toast.makeText(this, "알람이 울립니다. " + temp, Toast.LENGTH_LONG).show();

        //녹음이 종료된 직후 녹음 결과 화면이 나올 때 알람이 울릴 경우에 UI처리 필요.
        //하지만 결과 화면이 알람이 실행된 직후에 출력되는 타이밍이라면... 이 방법으로는 처리가 안된다...
        recordUIInvisible();

        MainActivity.device.setVisibility(View.VISIBLE);
        MainActivity.alram.setVisibility(View.VISIBLE);
        MainActivity.list.setVisibility(View.GONE);
        MainActivity.information.setVisibility(View.GONE);
        MainActivity.deleteButton.setVisibility(View.GONE);
        MainActivity.record.setEnabled(false);
        MainActivity.record.setVisibility(View.GONE);
        MainActivity.play.setEnabled(false);
        MainActivity.play.setVisibility(View.GONE);
        MainActivity.mText2.setText("       알람 중");
        MainActivity.mText2.setVisibility(View.VISIBLE);
        MainActivity.alramtext.setText("화면을 클릭시\n알람이 종료 됩니다.");
        MainActivity.alramtext.setVisibility(View.VISIBLE);
        MainActivity.mText.setVisibility(View.INVISIBLE);
        MainActivity.numPlayList.setVisibility(View.INVISIBLE);
        //목록
        MainActivity.listView.setVisibility(View.INVISIBLE);
        //삭제 화면
        MainActivity.whetherDelete.setVisibility(View.INVISIBLE);
        MainActivity.deleteButton.setVisibility(View.INVISIBLE);
        MainActivity.yesButton.setVisibility(View.INVISIBLE);
        MainActivity.noButton.setVisibility(View.INVISIBLE);
//        try {
//            Thread.sleep(1000);
//        }catch(InterruptedException e) {
//            e.printStackTrace();
//        }
//        //응답하지 않습니다 문제 - 별도의 쓰레드 사용해보기
//        MainActivity.voicePlayer.playWaveFileAlarm(16000, 1024, temp);

        class BackgroundTask extends AsyncTask<String, String, String> {
            protected void onPreExecute() {
                MainActivity.voicePlayer.stopPlaying();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                    System.out.println("여기는 안들어올걸? " + playingPos);
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
