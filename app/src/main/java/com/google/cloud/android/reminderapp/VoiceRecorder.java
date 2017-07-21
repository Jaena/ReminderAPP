
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

//   private final Callback mCallback;
//
    private AudioRecord mRecorder = null;
    private Thread mRecordingThread = null;
    boolean mIsRecording = false;

    DataBase db;
    Context context;

    /**
     * 디비와 레코딩 설정 등 Context가 필요한 부분을 위해 context를 Main으로 부터 받아오고
     * 디비 변수를 Main으로 부터 받아온다.
     */
    public VoiceRecorder(Context c, @NonNull Callback callback)
    {
        context =c;
//        mCallback = callback;
      db = MainActivity.getDBInstance();
    }

    /**
     * 녹음을 시작할 때 불려지며
     * 녹음을 시작하고 WriteAudioDataToFile 함수로 가 녹음파일을 즉시 저장한다.
     */
    public void startRecording() {
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
//
//        /**
//         * Called when the recorder starts hearing voice.
//         */
//        public void onVoiceStart() {
//        }
//
//        /**
//         * Called when the recorder is hearing voice.
//         *
//         * @param data The audio data in {@link AudioFormat#ENCODING_PCM_16BIT}.
//         * @param size The size of the actual data in {@code data}.
//         */
//        public void onVoice(byte[] data, int size) {
//        }
//
//        /**
//         * Called when the recorder stops hearing voice.
//         */
//        public void onVoiceEnd() {
//        }
    }

    /**
     * 샘플 레이트, 버퍼의 크기, 오디오의 포맷, 채널 그리고 오디오 레코드 객체를 세팅하기 위해 불려진다.
     *
     * @return 위의 사항을 세팅한 AudioRecord 객체를 리턴한다.
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
     * 음성을 녹음 파일로 저장시켜주는 일을 한다.
     * 이곳에서 파일이 다 저장되기 전에 DB에 파일 이름이 들어간다.
     *
     * @exception FileNotFoundException 저장된 파일을 찾지 못할 수 있으므로 발생 가능하다.
     * @exception IOException
     */

    private void writeAudioDataToFile() {

        short sData[] = new short[mBufferSize];
        FileOutputStream fos;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
        String tempTime = sdf.format(date);
        String fileName = tempTime + ".pcm";
        //TODO 음성파일도 데이터베이스에 저장되도록 개선 필요
        db.insert(fileName);
        try {
            fos = context.openFileOutput(fileName,context.MODE_PRIVATE);
            while (mIsRecording) {
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
     * short 어레이를 바이트 어레이로 고치기 위해 필요하다.
     *
     * @return 바이트 어레이로 고친 값이 리턴된다.
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
     * 녹음을 멈추고 녹음 중 상태 값을 false로 변경한다.
     *
     */

    public void stopRecording() {
        if (mRecorder != null) {
            mIsRecording = false;
            mRecorder.stop();
//            mCallback.onVoiceEnd();
         mRecorder.release();
        }
    }

    /**
     * 샘플레이트를 main에서 가져오기 위해 사용된다.
     * 메인에서 샘플 값은 보이스 플레이어 클래스에서 재생 SampleRate로 사용되며
     * 녹음과 재생의 샘플 값이 일치하지 않는 경우
     * 재생될 때 빠르게, 느리게 들리거나 음원 파일이 손상된 것 처럼 들린다.
     *
     * @return int로 된 샘플 값을 리턴한다.
     **/

    public int getSampleRate() {
        return mRecorder.getSampleRate();
    }


    /**
     * 다른 클래스(ex) MainActivity) 에서 레코딩 중인지 여부를 알기 위해 사용된다.
     *
     * @return 레코딩 상황인지 아닌지를 boolean 변수로 리턴한다.
     **/
    public boolean isRecording()
    {
        return mIsRecording;
    }

}
