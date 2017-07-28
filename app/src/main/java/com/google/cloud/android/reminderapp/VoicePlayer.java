package com.google.cloud.android.reminderapp;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 이 클래스는 DB로부터 녹음된 파일명을 받아와 해당 파일을 재생하는 역할을 수행한다.
 */

public class VoicePlayer {

    private static final int CHANNEL = AudioFormat.CHANNEL_OUT_MONO;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    Context context;
    boolean mIsPlaying  = false;
    DataBase db;

    AudioTrack audioTrack;
    private Thread mPlayingThread = null;
    int playCount;

    VoicePlayer(Context c)
    {
        context = c;
        db = MainActivity.getDBInstance();
    }

    //TODO : 피어리뷰에 적어야 할 것들 minBufferSize - 사용되지 않는 변수 제거, 불필요한 AudioTrack인스턴스 생성 없도록 개선 필요
    /**
     * 이 메소드는 새로운 thread를 생성하여 playWaveFile 메소드를 실행한다.
     * 음성 파일을 재생하기 위해 호출된다.
     *
     * @param SampleRate     녹음 시 사용된 sample rate(Hertz)
     * @param mBufferSize    재생 시 음성 파일에서 한 번에 읽어오는 음성 데이터의 최대 크기
     */
    public void startPlaying(final int SampleRate, final int mBufferSize) {
        // int minBufferSize = AudioTrack.getMinBufferSize(SampleRate, CHANNEL, ENCODING);

        mIsPlaying = true;
        mPlayingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                playWaveFile(SampleRate, mBufferSize);
            }
        }, "AudioRecorder Thread");
        mPlayingThread.start();
    }

    //TODO 변수 playing을 mIsplaying으로 바꾸기
    /**
     * 이 메소드는 변수 playing을 false로 설정하여 재생을 중지한다.
     */
    public void stopPlaying()
    {
        mIsPlaying = false;
    }

    /**
     * 이 메소드는 녹음 시 사용된 sample rate에 따라 audioTrack instance를 생성하고 음성 파일로부터 buffer size만큼 정보를 읽어와 재생한다.
     * 변수 playing의 값이 true일 때 재생한다.
     *
     * @param SampleRate     녹음 시 사용된 sample rate(Hertz)
     * @param mBufferSize    재생 시 음성 파일에서 한 번에 읽어오는 음성 데이터의 최대 크기
     *
     * @exception FileNotFoundException
     * @exeption IOException
     */
    public void playWaveFile(int SampleRate,int mBufferSize) {
        System.out.println("재생 시작");
        String fileName[] = db.getAllFileName();
        String alarmTime[] = db.getAllAlarmTime();
        playCount = fileName.length;
        int i;
        for(i=playCount-1;i>=0;i--){
            int count = 0;
            byte[] data = new byte[mBufferSize];


            Message message = MainActivity.vhandler.obtainMessage(1, alarmTime[i]);
            System.out.println("알람타임 테스트 : " + alarmTime[i]);

            MainActivity.vhandler.sendMessage(message);


            try {
                //Toast.makeText(context.getApplicationContext(),"현재 재생중인 파일 " + fileName[i] +"",Toast.LENGTH_SHORT).show();
                FileInputStream fis = context.openFileInput(fileName[i]);
                DataInputStream dis = new DataInputStream(fis);
                int minBufferSize = AudioTrack.getMinBufferSize(SampleRate, CHANNEL, ENCODING);
                audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SampleRate, CHANNEL, ENCODING, minBufferSize, AudioTrack.MODE_STREAM);
                audioTrack.play();
                while (((count = dis.read(data, 0, mBufferSize)) > -1)&&mIsPlaying) {
                    audioTrack.write(data, 0, count);
                }
                audioTrack.stop();
                audioTrack.release();
                dis.close();
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 이 메소드는 현재 재생 중인지 아닌지를 판단하기 위해 호출된다.
     *
     * @return boolean    현재 재생 중 여부 (True : 재생 중, False : 재생 중 아님)
     */
    public boolean isPlaying()
    {
        return mIsPlaying;
    }
}