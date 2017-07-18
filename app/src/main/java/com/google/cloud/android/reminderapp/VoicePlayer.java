package com.google.cloud.android.reminderapp;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class VoicePlayer {

    private static final int CHANNEL = AudioFormat.CHANNEL_OUT_MONO;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    Context context;
    boolean playing  = false;
    DataBase db;

    AudioTrack audioTrack;
    private Thread mPlayingThread = null;
    int playCount;

    VoicePlayer(Context c)
    {
        context = c;
        db = MainActivity.getDBInstance();
    }
    public void startPlaying(final int SampleRate, final int mBufferSize) {
        int minBufferSize = AudioTrack.getMinBufferSize(SampleRate, CHANNEL, ENCODING);
        audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SampleRate, CHANNEL, ENCODING, minBufferSize, AudioTrack.MODE_STREAM);
        playing = true;
        mPlayingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                playWaveFile(SampleRate, mBufferSize);
            }
        }, "AudioRecorder Thread");
        mPlayingThread.start();
    }

    public void stopPlaying()
    {
        playing = false;
    }

    public void playWaveFile(int SampleRate,int mBufferSize) {

        String fileName[] = db.getAllFileName();
        playCount = fileName.length;
        int i;
        for(i=playCount-1;i>=0;i--){
            int count = 0;
            byte[] data = new byte[mBufferSize];
            try {
                FileInputStream fis = context.openFileInput(fileName[i]);
                DataInputStream dis = new DataInputStream(fis);
                int minBufferSize = AudioTrack.getMinBufferSize(SampleRate, CHANNEL, ENCODING);
                audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SampleRate, CHANNEL, ENCODING, minBufferSize, AudioTrack.MODE_STREAM);
                audioTrack.play();
                while (((count = dis.read(data, 0, mBufferSize)) > -1)&&playing) {
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
    public boolean isPlaying()
    {
        return playing;
    }
}