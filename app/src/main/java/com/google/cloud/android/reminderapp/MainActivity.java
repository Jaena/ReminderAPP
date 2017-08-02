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
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Timer;


public class MainActivity extends AppCompatActivity implements MessageDialogFragment.Listener {

    private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";
    private static final String STATE_RESULTS = "results";

    Handler mHandler = new Handler();
    static DataBase db;
    private SpeechService mSpeechService;

    private VoiceRecorder mVoiceRecorder;
    VoicePlayer voicePlayer;

    boolean recRunning = false;
    boolean playRunning = false;

    // View references
    public static TextView mText;

    //EPD timer
    public static int value = 0;

    ImageSwitcher device;
    ImageButton record;
    ImageButton play;
    ImageButton list;
    ImageButton deleteButton;
    TextView whetherDelete;
    Button yesButton, noButton;
    ImageSwitcher deviceOn;
    ImageSwitcher deviceOff;

    RecordingSwtich rec = new RecordingSwtich();
    PlayingSwtich playS = new PlayingSwtich();
    DeviceOn deviceON= new DeviceOn();
    DeviceOff deviceOFF= new DeviceOff();

    SeekBar settingBar;

    ImageView[] circles = new ImageView[5];

    //핸들러
    Handler handler; //화면 클릭했을때 녹음 처리 핸들러
    public static Handler vhandler; // 재생중인 화면 mtext 처리 핸들러
    public static Handler phandler; // 재생중인 리스터 처리 핸들러
//    Handler dhandler; // EPD 핸들러

    boolean isEnd = false;
    int SampleRate = 16000;
    int BufferSize = 1024;
    private SoundPool sound;
    private int soundbeep;

    TimeAnalysis timeAnalysis;
    ContentAnalysis contentAnalysis;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1; //추가
    boolean isButtonPushed = false; //추가

    public static String fileName;
    String alarmTimeArr[], fileNameArr[];
    int playCount, playingPos;

    ListView listView;
    PlaylistAdapter adapter;

    TextView numPlayList;

    PlaylistView viewArr[] = new PlaylistView[100]; //list의 각 아이템들의 view값을 담고 있다. 일단 최대 100개로 해보자.
    int tempPos = -1, tempPos2;

    //Timer
    CountDownTimer timer;

    /**
     * @TODO mVoiceCallback 지우기. Main과 Recorder에 있으며 스트리밍을 위한 함수로 보여짐
     */
    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

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

    /**
     * 텍스트, 버튼 등 UI 요소의 클릭 등을 관리하고
     * 핸들러를 통해 Service Search Listener로 부터 받아온 텍스트를 TimeAnalysis로 보내어 분석한다.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numPlayList = (TextView)findViewById(R.id.numPlayList);
        device = (ImageSwitcher) findViewById(R.id.backgound);
        mText = (TextView) findViewById(R.id.text);
        record = (ImageButton) findViewById(R.id.record);
        play = (ImageButton) findViewById(R.id.play);
        settingBar = (SeekBar) findViewById(R.id.seekBar);
        settingBar.setMax(4);


        list = (ImageButton) findViewById(R.id.list);
        deleteButton = (ImageButton) findViewById(R.id.deleteButton);
        whetherDelete = (TextView) findViewById(R.id.whetherDelete);
        yesButton = (Button) findViewById(R.id.yesButton);
        noButton = (Button) findViewById(R.id.noButton);

        db = new DataBase(MainActivity.this);
        mVoiceRecorder = new VoiceRecorder(this, mVoiceCallback);
        voicePlayer = new VoicePlayer(this);
        timeAnalysis = new TimeAnalysis();
        contentAnalysis = new ContentAnalysis();
        mText.setVisibility(View.VISIBLE);


        deviceOn = (ImageSwitcher) findViewById(R.id.device_on);
        deviceOff = (ImageSwitcher)findViewById(R.id.device_off);

        sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundbeep = sound.load(getApplicationContext(), R.raw.rec_start, 1);


        //listing
        listView = (ListView) findViewById(R.id.listView);
        makeList();

        record.setEnabled(false);
        record.setVisibility(View.GONE);
        play.setEnabled(false);
        play.setVisibility(View.GONE);
        numPlayList.setVisibility(View.GONE);


        //timer - 시간 제한 7초.
        timer =  new CountDownTimer(7000, 880) {
            @Override
            public void onTick(long millisUntilFinished) {
                System.out.println("value값 계속 ++됨? : " + value);
                mText.setText("녹음중\n" + (7-value) + "초 후 종료");
                value++;
            }

            @Override
            public void onFinish() {
                value = 0;
                mText.setText("녹음 종료");
                device.callOnClick();
                //    device.setVisibility(View.INVISIBLE);
            }
        };

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("여기까지" + mVoiceRecorder.isRecording());
                if (!mVoiceRecorder.isRecording()) {
                    device.setVisibility(View.VISIBLE);
                    record.setEnabled(false);
                    record.setVisibility(View.GONE);
                    play.setEnabled(false);
                    play.setVisibility(View.GONE);
                    mText.setText("녹음중 ");
                    mText.setVisibility(View.VISIBLE);
                    if (!rRunning) {
                        recRunning = true;
                        rec.start();
                    } else {
                        recRunning = true;
                    }
                    SharedPreferences preference = getSharedPreferences("volume", MODE_PRIVATE);
                    float volume = preference.getFloat("volume", 1f);
                    sound.play(soundbeep, volume, volume, 0, 0, 1);
                    startVoiceRecorder();

                    //TimeLimit 구현
                    timer.cancel();
                    timer.start(); //타이머 시작
//                    CountDownTimer timer;
//                    timer =  new CountDownTimer(7000, 980) {
//                        @Override
//                        public void onTick(long millisUntilFinished) {
//                            value++;
//                            if (value == 7) {
//                                onFinish();
//                            } else if (!mVoiceRecorder.isRecording()) {
//                                onFinish();
//                            }
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            if (value == 7) {
//                                Message message = dhandler.obtainMessage(1, 1);
//                                dhandler.sendMessage(message);
//                                value = 0;
//                            } else {
//                                Message message = dhandler.obtainMessage(1, 2);
//                                dhandler.sendMessage(message);
//                                value = 0;
//                            }
//                        }
//                      }.start();  // 타이머 시작
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeList();
                device.setVisibility(View.VISIBLE);
                numPlayList.setVisibility(View.INVISIBLE);
                if (playCount == 0) {
                    Toast.makeText(getApplicationContext(), "재생할 목록이 비어있습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!voicePlayer.isPlaying()) {
                    record.setEnabled(false);
                    record.setVisibility(View.GONE);
                    play.setEnabled(false);
                    play.setVisibility(View.GONE);
                    if (!pRunning) {
                        playRunning = true;
                        playS.start();
                    } else {
                        playRunning = true;
                    }
                    mText.setText("재생중");
                    mText.setVisibility(View.VISIBLE);
                    list.setVisibility(View.VISIBLE);
                    deleteButton.setVisibility(View.VISIBLE);
//                    playDisplay();
//                    NoticeDisplay();
                    // 중간에 녹음한 것에 대해서 playCount값이 여기서 갱신되지 않은 상태일 수 있으므로, 대신 -1을 전달하고
                    // -1인 경우 voicePlayer에서 갱신된 디비로부터 playCount값을 얻는 식으로 처리할 것이다.
                    voicePlayer.startPlaying(SampleRate, BufferSize, playCount);
//                    voicePlayer.startPlaying(SampleRate, BufferSize, -1);
                    //TODO 모든 파일의 재생이 완료된 후, 시작 화면으로 전환되도록 개선 필요
                }
            }
        });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeList();
                device.setVisibility(View.INVISIBLE);
                listView.setAdapter(adapter);
                listView.setVisibility(View.VISIBLE);
                playRunning = false;
                mText.setVisibility(View.GONE);
                list.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
            }
        });
//삭제 버튼을 누르면 재생이 중지가 되고, 삭제 여부를 물어보는 화면이 뜬다. 거기서 yes를 누르면 삭제가 되고, 다음 파일부터 재생.
//거기서 no를 누르면 다음 파일부터 재생한다.
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //재생 중지
                playingPos = voicePlayer.stopPlaying();

                playRunning = false;
                mText.setVisibility(View.GONE);
                list.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);

                whetherDelete.setVisibility(View.VISIBLE);
                yesButton.setVisibility(View.VISIBLE);
                noButton.setVisibility(View.VISIBLE);
                device.setVisibility(View.INVISIBLE);
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileNameArr[] = db.getAllFileName();
                alarmTimeArr = db.getAllAlarmTime();
                db.delete(fileNameArr[playingPos]);

                whetherDelete.setVisibility(View.GONE);
                yesButton.setVisibility(View.GONE);
                noButton.setVisibility(View.GONE);
                device.setVisibility(View.VISIBLE);
                if (playingPos == 0) {
                    System.out.println("삭제 시 출력1");
                    isEnd = true;
                    device.callOnClick();
                } else {

                    if (alarmTimeArr[playingPos - 1].equals("일반 메모")) {
                        mText.setText("일반 메모");
                    } else {
                        System.out.println("삭제 시 출력2");
                        String[] words = alarmTimeArr[playingPos - 1].split(":");
                        if (Integer.parseInt(words[3]) < 10) words[3] = '0' + words[3];
                        if (Integer.parseInt(words[4]) < 10) words[4] = '0' + words[4];
                        String timeRegistered = words[3] + ":" + words[4] + "(" + words[1] + "월" + words[2] + "일" + ")";
                        System.out.println("재성 " + timeRegistered);
                        mText.setText(timeRegistered);
                    }
                    mText.setVisibility(View.VISIBLE);
                    list.setVisibility(View.VISIBLE);
                    deleteButton.setVisibility(View.VISIBLE);
                    device.setVisibility(View.VISIBLE);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    voicePlayer.startPlaying(SampleRate, BufferSize, playingPos); //다음 파일부터 재생
                }


            }
        });

        noButton.setOnClickListener(new View.OnClickListener() { //삭제 안함 -> 다음 파일부터 재생
            @Override
            public void onClick(View v) {
                fileNameArr = db.getAllFileName();
                alarmTimeArr = db.getAllAlarmTime();

                whetherDelete.setVisibility(View.GONE);
                yesButton.setVisibility(View.GONE);
                noButton.setVisibility(View.GONE);

                if ((alarmTimeArr[playingPos > 0 ? (playingPos - 1) : 0]).equals("일반 메모")) {
                    mText.setText("일반 메모");
                } else {
                    String[] words = alarmTimeArr[playingPos > 0 ? (playingPos - 1) : 0].split(":");
                    if (Integer.parseInt(words[3]) < 10) words[3] = '0' + words[3];

                    if (Integer.parseInt(words[4]) < 10) words[4] = '0' + words[4];
                    String timeRegistered = words[3] + ":" + words[4] + "(" + words[1] + "월" + words[2] + "일" + ")";
                    System.out.println("재성 " + timeRegistered);
                    mText.setText(timeRegistered);
                }
                mText.setVisibility(View.VISIBLE);
                list.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                device.setVisibility(View.VISIBLE);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                voicePlayer.startPlaying(SampleRate, BufferSize, playingPos); //다음 파일부터 재생
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                voicePlayer.stopPlaying();
                listView.setVisibility(View.GONE);

                if (alarmTimeArr[(playCount - 1) - position].equals("일반 메모")) {
                    mText.setText("일반 메모");
                } else {
                    String[] words = alarmTimeArr[(playCount - 1) - position].split(":");
                    if (Integer.parseInt(words[3]) < 10) words[3] = '0' + words[3];
                    if (Integer.parseInt(words[4]) < 10) words[4] = '0' + words[4];
                    String timeRegistered = words[3] + ":" + words[4] + "(" + words[1] + "월" + words[2] + "일" + ")";
                    mText.setText(timeRegistered);
                }
                mText.setVisibility(View.VISIBLE);
                list.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                device.setVisibility(View.VISIBLE);


                Toast.makeText(getApplicationContext(), (playCount - 1) - position + " " + position, Toast.LENGTH_SHORT).show();
                System.out.println("재성 " + ((playCount - 1) - position) + " " + position);
                //voicePlayer stop이후, 바로 startPlaying시 문제가 발생하여, stop이 완료될 때까지 좀 기다린 후 start한다.
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //isEnd = true;
                voicePlayer.startPlaying(SampleRate, BufferSize, playCount - position);
            }
        });

        device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("MainActivity에서 mVoiceRecorder.isRecording 확인 : " + mVoiceRecorder.isRecording());
                if (mVoiceRecorder.isRecording()) {
                    System.out.println("in device 1");
                    System.out.println("stop Voice Recorder");
                    SharedPreferences preference = getSharedPreferences("volume", MODE_PRIVATE);
                    float volume = preference.getFloat("volume", 1f);
                    sound.play(soundbeep, volume, volume, 0, 0, 1);
                    recRunning = false;
                    stopVoiceRecorder();
                    timer.cancel();
                }
                if (voicePlayer.isPlaying()) {
                    System.out.println("in device 2");
                    voicePlayer.stopPlaying();
                    record.setEnabled(true);
                    numPlayList.setVisibility(View.VISIBLE);
                    numPlayList.setText(db.getAllPlayListNum()+"");
                    record.setVisibility(View.VISIBLE);
                    play.setEnabled(true);
                    play.setVisibility(View.VISIBLE);
                    playRunning = false;
                    mText.setText("");
                    mText.setVisibility(View.GONE);
                    list.setVisibility(View.GONE);
                    listView.setVisibility((View.GONE));
                    deleteButton.setVisibility(View.GONE);
                }
                value = 0;
                if (isEnd) {
                    System.out.println("in device 3");
                    numPlayList.setVisibility(View.VISIBLE);
                    record.setEnabled(true);
                    record.setVisibility(View.VISIBLE);
                    play.setEnabled(true);
                    play.setVisibility(View.VISIBLE);
                    numPlayList.setText(db.getAllPlayListNum()+"");
                    mText.setVisibility(View.GONE);
                    list.setVisibility(View.GONE);
                    listView.setVisibility((View.GONE));
                    deleteButton.setVisibility(View.GONE);
                    mText.setText("");
                    SharedPreferences preference = getSharedPreferences("volume", MODE_PRIVATE);
                    float volume = preference.getFloat("volume", 1f);
                    sound.play(soundbeep, volume, volume, 0, 0, 1);
                    isEnd = false;
                }
            }
        });
        //TODO 음성 정보가 없는 경우(아무 말도 하지 않을 때, 녹음이 되지 않는 경우 등)와 시간 인식이 불가능한 음성정보에 대해서 따로 처리하는 코드가 없고, 현재 시간으로 처리됨. 각각에 대해 따로 처리하여 사용자에게 알려주도로 개선 필요
        handler = new Handler() {
            public void handleMessage(Message msg) {
                String returnedValue = (String) msg.obj;
                String extractValue = new String();

                //아무말 없이 취소했을 경우
                if (returnedValue.equals("")) {
                    //mText.setText("터치해주세요");
                    Toast.makeText(getApplicationContext(), "아무말도 안하셨습니다", Toast.LENGTH_LONG).show();

                    if(powerOn==true) {
                        isEnd = true;
                        mText.setText("음성인식 실패");
                        //device.callOnClick();
                    }
                }

                else {
                    String alarmTime = timeAnalysis.Analysis(returnedValue);
                    String contentValue = contentAnalysis.Analysis(returnedValue);

                    if (alarmTime.equals("note")) {
                        db.insert(fileName, "일반 메모", contentValue);
                        mText.setText("<일반메모>\n"+ contentValue);
                        Toast.makeText(getApplicationContext(), returnedValue, Toast.LENGTH_LONG).show();
                    } else {
                        String[] words = alarmTime.split(":");
                        if (Integer.parseInt(words[3]) < 10) words[3] = '0' + words[3];
                        if (Integer.parseInt(words[4]) < 10) words[4] = '0' + words[4];

                        String timeRegistered = words[3] + ":" + words[4] + "(" + words[1] + "월" + words[2] + "일" + ")";
                        mText.setText("<알람시간>\n" +timeRegistered + "\n" + contentValue);

                        db.insert(fileName, alarmTime, contentValue);

                        Toast.makeText(getApplicationContext(), returnedValue, Toast.LENGTH_LONG).show();
                    }
                    isEnd = true;
                }
            }
        };


        vhandler = new Handler() {
            public void handleMessage(Message msg) {
                if (((String) msg.obj).equals("stop")) {
                    isEnd = true;
                    device.callOnClick();
                    System.out.println("device call on click");
                } else if (voicePlayer.isPlaying()) {
                    String alarmTime = (String) msg.obj;
                    String[] words = alarmTime.split(":");

                    if (words[0].equals("일반 메모")) {
                        mText.setText("<일반 메모>\n" + words[1]);
                    } else {

                        if (Integer.parseInt(words[3]) < 10) words[3] = '0' + words[3];
                        if (Integer.parseInt(words[4]) < 10) words[4] = '0' + words[4];

                        String timeRegistered = words[3] + ":" + words[4] + "(" + words[1] + "월" + words[2] + "일" + ")";
                        System.out.println("재성(vhandler) " + timeRegistered);
                        mText.setText(timeRegistered +"\n" + words[5]);
                    }
                }
            }
        };

        phandler = new Handler() {
            public void handleMessage(Message msg) {
                //재생 중이고, 현재 목록이 보이는 상태(View.VISIBLE)이면
                if (voicePlayer.isPlaying() && listView.getVisibility() == View.VISIBLE) {
                    int position = (int) msg.obj;
                    tempPos2 = position;
                    //같은 position이 여러번 들어오면 한 번만 색깔을 바꾸도록 하기 위함.
                    if (tempPos != position) {
                        tempPos = position;
                    } else {
                        return;
                    }

                    System.out.println("phandler position : " + position);
                    for (int i = 0; i < 10; i++) {
                        System.out.println("viewarr : " + i + " - " + viewArr[i]);
                    }

//                   if(position != 0) {
//                       System.out.println("하얀색으로 바뀌는 것 - " + (position - 1));
//                   //    viewArr[position-1].setBackgroundColor(Color.WHITE);
//                       viewArr[position-1].setBackgroundColor_white();
//                   }
//
//                   System.out.println("초록으로 바뀌는 것 - " + position);
//                   //viewArr[position].setBackgroundColor(Color.GREEN);
//                   viewArr[position].setBackgroundColor_green();
                    //listView.setVisibility(View.GONE);
                    // makeList();


                    listView.setAdapter(adapter);
                    if(position != 0) {
                        listView.setSelection(position-1);
                    }
//                   listView.setVisibility(View.VISIBLE);
                }
            }
        };

//        dhandler = new Handler() {
//            public void handleMessage(Message msg) {
//                System.out.println("dhandler 결과값 : " + (int)(msg.obj));
//                if((int)(msg.obj) == 1) {
//                    device.callOnClick();
//                }
//            }
//        };

        device.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setBackgroundColor(0x00000000);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });

        settingBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (progress > 0) {
                    if(powerOn==false) {
                        Toast.makeText(getApplicationContext(), "전원 켜짐", Toast.LENGTH_SHORT).show();
                        powerOn = true;
                        numPlayList.setVisibility(View.VISIBLE);
                        record.setEnabled(true);
                        record.setVisibility(View.VISIBLE);
                        play.setEnabled(true);
                        play.setVisibility(View.VISIBLE);
                        numPlayList.setText(db.getAllPlayListNum()+"");
                    }
                    if(progress==1){
                        SharedPreferences a = getSharedPreferences("volume", MODE_PRIVATE);
                        SharedPreferences.Editor editor = a.edit();
                        editor.putFloat("volume", 0);
                        editor.commit();}
                    else{
                        progress--;
                        SharedPreferences a = getSharedPreferences("volume", MODE_PRIVATE);
                        SharedPreferences.Editor editor = a.edit();
                        editor.putFloat("volume", (float) (progress * 0.3));
                        Log.d("volume setting", "" + progress * 0.3);
                        editor.commit();
                    }
                }
                else {
                    if(mVoiceRecorder.isRecording())
                    {
                        stopVoiceRecorder();
                        recRunning = false;
                        deleteInternalFile();
                        fileName = "";
                        record.setEnabled(false);
                        record.setVisibility(View.GONE);
                        play.setEnabled(false);
                        play.setVisibility(View.GONE);
                        mText.setVisibility(View.GONE);
                        list.setVisibility(View.GONE);
                        listView.setVisibility((View.GONE));
                        deleteButton.setVisibility(View.GONE);
                        numPlayList.setVisibility(View.GONE);
                        mText.setText("");
                    }
                    if(voicePlayer.isPlaying())
                    {
                        System.out.println("in device 2");
                        voicePlayer.stopPlaying();
                        playRunning = false;
                        mText.setText("");
                        mText.setVisibility(View.GONE);
                        list.setVisibility(View.GONE);
                        listView.setVisibility((View.GONE));
                        deleteButton.setVisibility(View.GONE);
                        numPlayList.setVisibility(View.GONE);
                    }
                    if(powerOn==true)
                    {
                        powerOn = false;
                        Toast.makeText(getApplicationContext(), "전원 꺼짐", Toast.LENGTH_SHORT).show();
                        record.setEnabled(false);
                        record.setVisibility(View.GONE);
                        play.setEnabled(false);
                        play.setVisibility(View.GONE);
                        mText.setText("");
                        device.setVisibility(View.INVISIBLE);
                        numPlayList.setVisibility(View.INVISIBLE);
                        //삭제 화면일 경우 전원 꺼지면 다 초기화
                        whetherDelete.setVisibility(View.GONE);
                        yesButton.setVisibility(View.GONE);
                        noButton.setVisibility(View.GONE);

                        //전원 꺼지면 카운트 다운 초기화
                        value = 0;
                        timer.cancel();

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    /**
     * MainActivity가 시작할 때, 녹음에 대한 Permission이 있는지 확인하고
     * Permission이 제대로 되어있지 않을 경우 다시 Permission을 받는다.
     */
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

    /**
     * MainActivity가 멈출 때, SpeechService를 종료한다.
     */
    @Override
    protected void onStop() {
        // Stop Cloud Speech API
        mSpeechService.removeListener(mSpeechServiceListener);
        unbindService(mServiceConnection);
        mSpeechService = null;

        super.onStop();
    }


    /**
     * Permission을 체크한다. Permission 되어있지 않을 경우 다이얼로그를 통해
     * 유저에게 이를 알린다.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (permissions.length == 1 && grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startVoiceRecorder();
//                stopVoiceRecorder();
            } else {
                showPermissionMessageDialog();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 보이스 레코더를 호출하기 위해 사용되는 메소드이다.
     */
    private void startVoiceRecorder() {
//        if (mVoiceRecorder != null) {
//            mVoiceRecorder.stopRecording();
//        }
        mVoiceRecorder.startRecording();
    }

    /**
     * 음성 녹음을 멈춘뒤, 스피치 서비스를 통해 구글 STT 서버로 파일을 전송시킨다.
     */
    boolean powerOn;
    private void stopVoiceRecorder() {
        if(!powerOn){
            mVoiceRecorder.stopRecording();
        }
        else {
            if (mVoiceRecorder != null) {
                System.out.println("녹음을 중지하자.");
                mVoiceRecorder.stopRecording();
                FileInputStream fis = null;
                try {
                    //    String fileName = db.getLastFileName(); //전역변수 fileNme에 현재 녹음한 파일이름이 저장돼있음.
                    fis = openFileInput(fileName);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println("녹음 완료 후, 구글 STT서버로 보내기");
                System.out.println("테스트 파일이름 : " + fileName);
                mSpeechService.recognizeInputStream(fis);
                System.out.println("구글 STT서버로 잘 보내진건가...?");
            }
        }
    }

    /**
     * 녹음과 관련된 Permission을 유저에게 확인받기 위해 다이얼로그를 띄운다.
     */
    private void showPermissionMessageDialog() {
        MessageDialogFragment
                .newInstance(getString(R.string.permission_message))
                .show(getSupportFragmentManager(), FRAGMENT_MESSAGE_DIALOG);
    }


    /**
     * 다이얼로그가 무시되었을 경우 다시 다이얼로그를 띄운다.
     */
    @Override
    public void onMessageDialogDismissed() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_AUDIO_PERMISSION);
    }


    /**
     * 구글 STT 서버로 부터 분석된 텍스트를 받아 onCreate에 있는 handler로 전송시킨다.
     *
     * @param text 파일의 음성을 텍스트로 변경한 값
     * @param isFinal 구글 STT 서버로부터 텍스트를 받았는지 아닌지 확인하는 값
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

    /**
     * DBInstance를 다른 Class에서도 사용할 수 있도록 하기 위해 사용한다.
     */
    public static DataBase getDBInstance() {
        return db;
    }


    /**
     * 액티비티의 글꼴을 바꾸기 위해 불러지는 함수이다.
     * CustomStartApp과 연결되어 있다.
     */

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    boolean rRunning = false;

    class RecordingSwtich extends Thread {

        int m_duration;
        final int image_m_Id[] = {R.drawable.display_off1, R.drawable.display_off2, R.drawable.display_off3, R.drawable.display_off4, R.drawable.display_off5, R.drawable.display_off};
        int m_currentIndex = 0;

        @Override
        public void run() {
            rRunning = true;
            while (rRunning) {
                synchronized (this) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (recRunning)
                                device.setImageResource(image_m_Id[m_currentIndex]);
                        }
                    });
                    m_currentIndex++;
                    if (m_currentIndex == image_m_Id.length) {
                        m_currentIndex = 0;
                    }
                    try {
                        m_duration = 600;
                        Thread.sleep(m_duration);
                    } catch (InterruptedException e) {
                    }
                }
            }   //while end

        }
    }

    boolean pRunning = false;

    class PlayingSwtich extends Thread {

        int m_duration;
        final int image_m_Id[] = { R.drawable.display_off1, R.drawable.display_off2, R.drawable.display_off3, R.drawable.display_off4, R.drawable.display_off5, R.drawable.display_off};
        int m_currentIndex = 0;

        @Override
        public void run() {
            pRunning = true;
            while (pRunning) {
                synchronized (this) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (playRunning)
                                device.setImageResource(image_m_Id[m_currentIndex]);
                        }
                    });
                    m_currentIndex++;
                    if (m_currentIndex == image_m_Id.length) {
                        m_currentIndex = 0;
                    }
                    try {
                        m_duration = 600;
                        Thread.sleep(m_duration);
                    } catch (InterruptedException e) {
                    }
                }
            }   //while end
        }
    }

    class DeviceOn extends Thread {

        int m_duration;
        final int image_m_Id[] = {R.drawable.display_off,R.drawable.display_on};
        int m_currentIndex = 0;

        @Override
        public void run() {
            while (m_currentIndex != image_m_Id.length) {
                synchronized (this) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            device.setImageResource(image_m_Id[m_currentIndex]);
                        }
                    });
                    m_currentIndex++;
                    try {
                        m_duration = 600;
                        Thread.sleep(m_duration);
                    } catch (InterruptedException e) {
                    }
                }
            }   //while end
        }
    }

    class DeviceOff extends Thread {

        int m_duration;
        final int image_m_Id[] = {R.drawable.display_off,R.drawable.display_off_1};
        int m_currentIndex = 0;

        @Override
        public void run() {
            while (m_currentIndex != image_m_Id.length) {
                synchronized (this) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            device.setImageResource(image_m_Id[m_currentIndex]);
                        }
                    });
                    m_currentIndex++;
                    try {
                        m_duration = 600;
                        Thread.sleep(m_duration);
                    } catch (InterruptedException e) {
                    }
                }
            }   //while end
        }
    }

    // 목록을 관리해주는 adapter
    class PlaylistAdapter extends BaseAdapter {
        ArrayList<Playlist> items = new ArrayList<Playlist>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(Playlist item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            PlaylistView view = new PlaylistView(getApplicationContext());
            Playlist item = items.get(position);
            view.setName(item.getName());

            if (position == tempPos2) {
                view.setBackgroundColor(Color.YELLOW);
            } else {
                view.setBackgroundColor(Color.BLACK);
            }
            viewArr[position] = view;

            return view;
        }
    }

    /**
     * 재생 목록을 만드는 메소드, 재생 목록에는 각 녹음파일의 녹음한 시각이 보여진다.
     */
    public void makeList() {
        adapter = new PlaylistAdapter();
        fileNameArr = db.getAllFileName();
        alarmTimeArr = db.getAllAlarmTime();
        playCount = alarmTimeArr.length;
        System.out.println("Play Count : " + playCount);
        for (int i = playCount - 1; i >= 0; i--) {
            //기존의 알림 예정 시간 혹은 일반 메모를 출력하는 코드
            /*if (alarmTimeArr[i].equals("일반 메모")) {
                adapter.addItem(new Playlist("일반 메모"));
            } else {
                String[] words = alarmTimeArr[i].split(":");
                if (Integer.parseInt(words[3]) < 10) words[3] = '0' + words[3];
                if (Integer.parseInt(words[4]) < 10) words[4] = '0' + words[4];

                String timeRegistered = words[3] + ":" + words[4] + "(" + words[1] + "월" + words[2] + "일" + ")";
                adapter.addItem(new Playlist(timeRegistered));
            }*/
            //각 녹음 파일의 녹음한 시각을 목록에 출력하는 코드.
            adapter.addItem(new Playlist(currentTime(fileNameArr[i])));
        }
    }

    /**
     * file name이 가지고 있는 현재 시간 정보(yy-MM-dd hh:mm:ss)를  시:분(-월-일) 형식으로 변환하는 메소드
     * @param fileName 파일 이름(시간 정보로 되어있음) String값
     * @return 변환된 형식의 시간정보(시:분(-월-일)) String값
     */
    public String currentTime(String fileName) {
        String retStr = fileName.substring(3, fileName.length() - 7);
        return retStr;
    }

    /**
     * 내부저장소에 저장된 파일을 지우기 위한 메소드이다
     *
     */
    private void deleteInternalFile()
    {
        File tempFile = new File(getFilesDir(),fileName);
        tempFile.delete();
    }

    //뒤로 가기 버튼 눌렀을 경우 모든 프로세스 종료
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onBackPressed();
    }
}