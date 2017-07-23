
package com.google.cloud.android.reminderapp;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import android.support.annotation.NonNull;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Continuously records audio and notifies the {@link VoiceRecorder.Callback} when voice (or any
 * sound) is heard.
 *
 * <p>The recorded audio format is always {@link AudioFormat#ENCODING_PCM_16BIT} and
 * {@link AudioFormat#CHANNEL_IN_MONO}. This class will automatically pick the right sample rate
 * for the device. Use {@link #getSampleRate()} to get the selected value.</p>
 */

public class VoiceRecorder {

    private final int mBufferSize = 1024;
    private final int mBytesPerElement = 2;

    private int mSampleRate;
    private short mAudioFormat;
    private short mChannelConfig;

    private final Callback mCallback;

    private AudioRecord mRecorder = null;
    private Thread mRecordingThread = null;
    boolean mIsRecording = false;

    DataBase db;
    Context context;

    /**
     * Called when enter class first
     * get context, callback and db instance in main class.
     */
    public VoiceRecorder(Context c, @NonNull Callback callback)
    {
        context =c;
        mCallback = callback;
        db = MainActivity.getDBInstance();
    }

    /**
     * Called when the recording starts.
     * Setting Audio format, channel and make a Thread to record file.
     */
    public void startRecording() {
        System.out.println("녹음 시작");
        mRecorder = null;
        mRecorder = findAudioRecord();
        mRecorder.startRecording();
        mIsRecording = true;
        mRecordingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        mRecordingThread.start();
    }

    public static abstract class Callback {

        /**
         * Called when the recorder starts hearing voice.
         */
        public void onVoiceStart() {
        }

        /**
         * Called when the recorder is hearing voice.
         *
         * @param data The audio data in {@link AudioFormat#ENCODING_PCM_16BIT}.
         * @param size The size of the actual data in {@code data}.
         */
        public void onVoice(byte[] data, int size) {
        }

        /**
         * Called when the recorder stops hearing voice.
         */
        public void onVoiceEnd() {
        }
    }

    /**
     * Called to set sample rate, size of buffer, format, channel and AudioRecord object.
     *
     * @return recorder AudioRecord object recorder need to record file.
     */
    private AudioRecord findAudioRecord() {
        try {
            int rate = 16000;
            short channel = AudioFormat.CHANNEL_IN_MONO, format = AudioFormat.ENCODING_PCM_16BIT;
            int bufferSize = AudioRecord.getMinBufferSize(rate, channel, format);
            if(bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                mSampleRate = rate;
                mAudioFormat = format;
                mChannelConfig = channel;

                AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, mSampleRate, mChannelConfig, mAudioFormat, bufferSize);

                if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                    return recorder;    // 적당한 설정값들로 생성된 Recorder 반환
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;                     // 적당한 설정값들을 찾지 못한 경우 Recorder를 찾지 못하고 null 반환
    }

    /**
     * Called to write audio file.
     * the format of audio file is pcm.
     *
     * @exception FileNotFoundException
     * @exception IOException
     */
    //TODO 음성 녹음이 시작된 후, 중지 버튼을 누르지 않으면 음성 녹음이 끝나지 않는 문제 개선 필요
    private void writeAudioDataToFile() {

        short sData[] = new short[mBufferSize];
        FileOutputStream fos;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
        String tempTime = sdf.format(date);
        String fileName = tempTime + ".pcm";

        db.insert(fileName);
        try {
            fos = context.openFileOutput(fileName,context.MODE_PRIVATE);
            while (mIsRecording) {
                int size = mRecorder.read(sData, 0, mBufferSize); //7월 18일 commit에서 빠져서 녹음이 안됐음. 다시 추가.
                byte bData[] = short2byte(sData);
                fos.write(bData, 0, mBufferSize * mBytesPerElement);
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Called to converts short array data to byte array
     *
     * @return bytes which is converted short array to bytes data
     */
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    /**
     * Called to finish recording and send to google speech services by mCallback.
     * using mCallback in mainActivity because of use same Speech object in main.
     *
     * @return bytes which is converted short array to bytes data
     */

    public void stopRecording() {
        if (mRecorder != null) {
            mIsRecording = false;
            mRecorder.stop();
            mCallback.onVoiceEnd();
            mRecorder.release();
        }
    }

    /**
     * Called to get sample rate.
     *
     * @return sample rate  it needs in VoicePlayer class.
     **/

    public int getSampleRate() {
        return mRecorder.getSampleRate();
    }


    /**
     * Called to know is recording or not in the other class.
     *
     * @return is recording or not.
     **/
    public boolean isRecording()
    {
        return mIsRecording;
    }

}
