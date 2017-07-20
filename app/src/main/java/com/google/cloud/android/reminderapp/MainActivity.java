/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.android.reminderapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class MainActivity extends AppCompatActivity implements MessageDialogFragment.Listener {

    private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";

    private static final String STATE_RESULTS = "results";

    static DataBase db;
    private SpeechService mSpeechService;

    private VoiceRecorder mVoiceRecorder;
    VoicePlayer voicePlayer;

    // View references
    private TextView mText;
    ImageView device;
    ImageButton record;
    ImageButton play;

    ImageView[] circles = new ImageView[5];
    Handler handler;

    boolean isEnd = false;
    int SampleRate = 16000;
    int BufferSize = 1024;

    TimeAnalysis timeAnalysis;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1; //추가
    boolean isButtonPushed = false; //추가

    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
            //showStatus(true);
            if (mSpeechService != null) {
                mSpeechService.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (mSpeechService != null) {
                mSpeechService.recognize(data, size);
            }
        }

        @Override
        public void onVoiceEnd() {
            //showStatus(false);
            if (mSpeechService != null) {
                mSpeechService.finishRecognizing();
            }
        }

    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }

    };
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        device = (ImageView) findViewById(R.id.backgound);
        mText = (TextView) findViewById(R.id.text);
        record = (ImageButton) findViewById(R.id.record);
        play = (ImageButton) findViewById(R.id.play);
        db = new DataBase(MainActivity.this);
        mVoiceRecorder = new VoiceRecorder(this, mVoiceCallback);
        voicePlayer = new VoicePlayer(this);
        timeAnalysis = new TimeAnalysis();
        device.setEnabled(false);
        mText.setVisibility(View.VISIBLE);

        circles[0] = (ImageView) (findViewById(R.id.circle1));
        circles[1] = (ImageView) (findViewById(R.id.circle2));
        circles[2] = (ImageView) (findViewById(R.id.circle3));
        circles[3] = (ImageView) (findViewById(R.id.circle4));
        circles[4] = (ImageView) (findViewById(R.id.circle5));

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mVoiceRecorder.isRecording()) {
                    record.setEnabled(false);
                    record.setVisibility(View.GONE);
                    play.setEnabled(false);
                    play.setVisibility(View.GONE);
                    mText.setText("녹음중 ");
                    mText.setVisibility(View.VISIBLE);
                    device.setEnabled(true);
//                    recordDisplay();
//                    NoticeDisplay();
                    startVoiceRecorder();
                }
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!voicePlayer.isPlaying()) {
                    record.setEnabled(false);
                    record.setVisibility(View.GONE);
                    play.setEnabled(false);
                    play.setVisibility(View.GONE);
                    mText.setText("재생중");
                    mText.setVisibility(View.VISIBLE);
                    device.setEnabled(true);
//                    playDisplay();
//                    NoticeDisplay();
                    voicePlayer.startPlaying(SampleRate, BufferSize);
                }
            }
        });


        device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("과연 : " + mVoiceRecorder.isRecording());
                if (mVoiceRecorder.isRecording()) {
                    stopVoiceRecorder();
                }
                if (voicePlayer.isPlaying()) {
                    voicePlayer.stopPlaying();
                    record.setEnabled(true);
                    record.setVisibility(View.VISIBLE);
                    play.setEnabled(true);
                    play.setVisibility(View.VISIBLE);
                    mText.setText("");
                    mText.setVisibility(View.GONE);
                    device.setEnabled(false);
                }
                if (isEnd) {
                    device.setEnabled(false);
                    record.setEnabled(true);
                    record.setVisibility(View.VISIBLE);
                    play.setEnabled(true);
                    play.setVisibility(View.VISIBLE);
                    mText.setText("");
                    isEnd = false;
                }
            }
        });

        handler = new Handler() {
            public void handleMessage(Message msg) {
                String returnedValue = (String) msg.obj;
                String extractValue = new String();

                //아무말 없이 취소했을 경우
                if(returnedValue.equals(""))
                {
                    //mText.setText("터치해주세요");
                    Toast.makeText(getApplicationContext(),"아무말도 안하셨습니다", Toast.LENGTH_LONG).show();
                    isEnd = true;
                    device.callOnClick();
                }
                //TODO 말이 있을 경우 (원하는 답을 찾지 못할때 인식 불가 기능을 추가할 예정)
                else {
                    mText.setText(timeAnalysis.Analysis(returnedValue));
                    Toast.makeText(getApplicationContext(), returnedValue, Toast.LENGTH_LONG).show();
                    isEnd = true;
                }
            }
        };

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                while(true) {
//                    if(mVoiceRecorder.isRecording() || voicePlayer.isPlaying()){
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                        }
//                        circles[index].setVisibility(View.VISIBLE);
//                        index++;
//                        if (index == 5) {
//                            for (int i = 0; i < 5; i++) {
//                                circles[i].setVisibility(View.INVISIBLE);
//                            }
//                            index = 0;
//                        }
//                    }
//
//
//
//                }
//            }
//        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        // Prepare Cloud Speech API
        bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);
        // Start listening to voices
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            showPermissionMessageDialog();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    @Override
    protected void onStop() {
        // Stop Cloud Speech API
        mSpeechService.removeListener(mSpeechServiceListener);
        unbindService(mServiceConnection);
        mSpeechService = null;

        super.onStop();
    }


    /**
     * Called to check permmision
     * if permission is not checked, show permmision dialog again.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (permissions.length == 1 && grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecorder();
            } else {
                showPermissionMessageDialog();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Called to start recorder.
     */
    private void startVoiceRecorder() {
//        if (mVoiceRecorder != null) {
//            mVoiceRecorder.stopRecording();
//        }
        mVoiceRecorder.startRecording();
    }

    /**
     * Called to stop voice recorder class and send to Speech service
     */
    private void stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stopRecording();
            FileInputStream fis = null;
            try {
                String fileName = db.getLastFileName();
                fis = openFileInput(fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            mSpeechService.recognizeInputStream(fis);
        }
    }

    /**
     * Called to show and confirm the permission.
     */
    private void showPermissionMessageDialog() {
        MessageDialogFragment
                .newInstance(getString(R.string.permission_message))
                .show(getSupportFragmentManager(), FRAGMENT_MESSAGE_DIALOG);
    }


    /**
     * Called to request permissions again.
     */
    @Override
    public void onMessageDialogDismissed() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_AUDIO_PERMISSION);
    }


    /**
     * Called to get string in Google Speech Server.
     *
     * @param text converted Voice to text.
     * @param isFinal tells analysis is over or not.
     */
    private final SpeechService.Listener mSpeechServiceListener =
            new SpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {

                    System.out.println("과연4 : " + mText);

                    if (mText != null) {
                        //if (isFinal) {
                        Message message = handler.obtainMessage(1, text);
                        handler.sendMessage(message);
                        //}
                    }
                }
            };


    public static DataBase getDBInstance() {
        return db;
    }


    /**
     * Called to change font in Activity.
     */

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

//    boolean RecRunning = true;
//    void recordDisplay(){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                while (RecRunning) {
//                    while (mVoiceRecorder.isRecording() == true) {
//                        TimerTask task = new TimerTask() {
//                            @Override
//                            public void run() {
//                                mText.setText("중지시 화면 클릭");
//                            }
//                        };
//                        Timer mTimer = new Timer();
//                        mTimer.schedule(task, 1000);
//                        mText.setText("녹음중");
//                        if (mVoiceRecorder.isRecording() == false)
//                            RecRunning = false;
//                    }
//                }
//            }
//        });
//
//        }
//
//    boolean playRunning =true;
//    void playDisplay() {
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                while(playRunning) {
//                    while (voicePlayer.isPlaying() == true) {
//                        TimerTask task = new TimerTask() {
//                            @Override
//                            public void run() {
//                                mText.setText("중지시 화면 클릭");
//                            }
//                        };
//                        Timer mTimer = new Timer();
//                        mTimer.schedule(task, 1000);
//                        mText.setText("재생중");
//                        if(!voicePlayer.isPlaying())
//                            playRunning = false;
//                    }
//                }
//            }
//        });
//
//    }
//
//    boolean noticeRunning = true;
//    void NoticeDisplay() {
//        int duration = 100;
//        runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                while (noticeRunning) {
//                    while (mVoiceRecorder.isRecording() || voicePlayer.isPlaying()) {
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                        }
//                        circles[index].setVisibility(View.VISIBLE);
//                        index++;
//                        if (index == 5) {
//                            for (int i = 0; i < 5; i++) {
//                                circles[i].setVisibility(View.INVISIBLE);
//                            }
//                            index = 0;
//                        }
//                        if (!(mVoiceRecorder.isRecording() || voicePlayer.isPlaying()))
//                            noticeRunning = false;
//                    }
//                }
//            }
//        });
//    }
}