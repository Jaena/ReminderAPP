package com.google.cloud.android.reminderapp;

import android.content.Context;
import android.content.SharedPreferences;
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
    int playCount, i = 0;

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
    public void startPlaying(final int SampleRate, final int mBufferSize, int position) {
        // int minBufferSize = AudioTrack.getMinBufferSize(SampleRate, CHANNEL, ENCODING);
        playCount = position;
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
     *
     * @return int 현재 재생 중인 파일의 index
     */
    public int stopPlaying()
    {
        mIsPlaying = false;
        return i == -1 ? 0 : i;
    }

    /**
     * 이 메소드는 녹음 시 사용된 sample rate에 따라 audioTrack instance를 생성하고 음성 파일로부터 buffer size만큼 정보를 읽어와 재생한다.
     * 변수 playing의 값이 true일 때 재생한다.
     *
     * @param SampleRate     녹음 시 사용된 sample rate(Hertz)
     * @param mBufferSize    재생 시 음성 파일에서 한 번에 읽어오는 음성 데이터의 최대 크기
     *
     *
     * @exception FileNotFoundException
     * @exeption IOException
     */
    public void playWaveFile(int SampleRate,int mBufferSize) {
        String fileName[] = db.getAllFileName();
        String alarmTime[] = db.getAllAlarmTime();
        String contentValue[] = db.getAllContent();
        int cnt = fileName.length; //목록에서 선택 시 playCount값이 변하기 때문에... 이렇게 따로 cnt에 저장해놓자.

//       if(playCount == -1) //재생버튼을 눌러서 재생이 시작되는 경우 ( 이 외에는 목록의 파일을 클릭해서 재생 시작하는 경우임)
//            playCount = fileName.length;
//        int i; //전역변수로 선언하겠음 (stopPlayin에서 현재 재생 중인 파일의 위치를 return하기 위해서)
        for(i=playCount-1;i>=0;i--){
            int count = 0;
            byte[] data = new byte[mBufferSize];

            if(!mIsPlaying) {
                break; //추가했음. - 아래 while문에 mIsPlaying는 없어도 될듯. - 아 재생 중간에 정지되려면 while문 안에 있어야 할지도..?
            }

            Message message = MainActivity.vhandler.obtainMessage(1, alarmTime[i]+":"+contentValue[i]);
            MainActivity.vhandler.sendMessage(message);

//            Message message4 = MainActivity.phandler.obtainMessage(1, cnt - 1 - i);
//            MainActivity.phandler.sendMessage(message3);

            try {
                //Toast.makeText(context.getApplicationContext(),"현재 재생중인 파일 " + fileName[i] +"",Toast.LENGTH_SHORT).show();
                FileInputStream fis = context.openFileInput(fileName[i]);
                DataInputStream dis = new DataInputStream(fis);
                int minBufferSize = AudioTrack.getMinBufferSize(SampleRate, CHANNEL, ENCODING);
                audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SampleRate, CHANNEL, ENCODING, minBufferSize, AudioTrack.MODE_STREAM);
                audioTrack.play();

                while (((count = dis.read(data, 0, mBufferSize)) > -1)&&mIsPlaying) {
                    //재생 중인 파일 하이라이트하기 위해 position정보를 보낸다.(phandler이용)
                    //여기다가 쓴 이유는 파일이 실행중일 때 목록버튼을 누르는 경우에도 하이라이트가 되도록 하기 위함이다.
                    Message message3 = MainActivity.phandler.obtainMessage(1, cnt - 1 - i);
                    MainActivity.phandler.sendMessage(message3);

                    SharedPreferences preference = context.getSharedPreferences("volume", context.MODE_PRIVATE);
                    float volume = preference.getFloat("volume", 1f);
                    audioTrack.setVolume(volume);
                    audioTrack.write(data, 0, count);
                }
                audioTrack.stop();
                audioTrack.release();
                dis.close();
                fis.close();

                if(!mIsPlaying) break;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(i == -1) {
            //mIsPlaying = false;
            System.out.println("play count : " + playCount);
            Message message2 = MainActivity.vhandler.obtainMessage(1, "stop");
            MainActivity.vhandler.sendMessage(message2);
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