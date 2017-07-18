package com.google.cloud.android.reminderapp;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class receives the recorded file name from DB and plays the file.
 */

public class VoicePlayer {

    private static final int CHANNEL = AudioFormat.CHANNEL_OUT_MONO;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    Context context;
    boolean playing  = false;
    DataBase db;

    AudioTrack audioTrack;
    private Thread mPlayingThread = null;
    int playCount;

    /**
     * Constructor of the VoicePlayer class.
     * Get context and db instance from MainActivity class.
     *
     * @param c    context(the information of this program)
     */
    VoicePlayer(Context c)
    {
        context = c;
        db = MainActivity.getDBInstance();
    }

    /**
     * Called to call the playWaveFile method on a thread for playing audio files.
     * This method creates a new thread and plays the playWaveFile method on the thread.
     *
     * @param SampleRate    sample rate expressed in Hertz.
     * @param mBufferSize    total size of the buffer where audio data is written to during the recording.
     */
    public void startPlaying(final int SampleRate, final int mBufferSize) {
        int minBufferSize = AudioTrack.getMinBufferSize(SampleRate, CHANNEL, ENCODING);
        //audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SampleRate, CHANNEL, ENCODING, minBufferSize, AudioTrack.MODE_STREAM); // 어차피 playWaveFile에 있으니 없애도 될듯.
        playing = true;
        mPlayingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                playWaveFile(SampleRate, mBufferSize);
            }
        }, "AudioRecorder Thread");
        mPlayingThread.start();
    }

    /**
     * Called to stop playing audio files.
     * This method stops playing audio by initializing variable playing to false.
     */
    public void stopPlaying()
    {
        playing = false;
    }

    /**
     * Called to playback audio files.
     * This method creates an AudioTrack instance using the sample rate used when recording and playbacks the data from the audio file by the buffer size.
     *
     * @param SampleRate    sample rate expressed in Hertz.
     * @param mBufferSize    total size of the buffer where audio data is written to during the recording
     *
     * @exception FileNotFoundException
     * @exeption IOException
     */
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

    /**
     * Called to check whether audio is being played now.
     * This method returns whether audio is being played now.
     *
     * @return boolean    whether audio is being played now.
     */
    public boolean isPlaying()
    {
        return playing;
    }
}