package com.example.lost.audiodemo;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;


import com.example.lost.audiodemo.utils.DensityUtil;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 音频录制工具类
 * Created by wuchanghe on 2017/3/13 9:15.
 */

public class AudioRecord {

    private MediaRecorder mRecorder;
    //文件夹位置
    private String mAudioPath;
    //录音文件保存路径
    private String mCurrentFilePathString;
    //是否准备好开始录音
    private boolean isPrepared;
    private int startTime = 0;
    private Timer timer;
    private final int RECORDING = 11;
    private AudioRecordListener audioRecordListener;
    private Context context;


    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RECORDING:
                    if (audioRecordListener != null) {
                        audioRecordListener.onAudioRecordingListener(String.valueOf(msg.obj), msg.arg1);
                    }
                    break;
            }
        }
    };


    public void setAudioRecordListener(AudioRecordListener audioRecordListener) {
        this.audioRecordListener = audioRecordListener;
    }


    /**
     * 单例化这个类
     */
    private static AudioRecord mInstance;

    private AudioRecord(Context context, String dir) {
        this.context = context.getApplicationContext();
        mAudioPath = dir;
    }

    /**
     * 单例模式创建Audio对象
     * @param context
     * @param dir
     * @return
     */
    public static AudioRecord getInstance(Context context, String dir) {
        if (mInstance == null) {
                if (mInstance == null) {
                    mInstance = new AudioRecord(context, dir);
                }
        }
        return mInstance;

    }


    /**
     * 开始录音
     */
    public void startRecord(String amrName) {
        try {
            isPrepared = false;
            File dir = new File(mAudioPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileNameString = generalFileName(amrName);
            File file = new File(dir, fileNameString);
            mCurrentFilePathString = file.getAbsolutePath();
            mRecorder = new MediaRecorder();
            mRecorder.setOutputFile(file.getAbsolutePath());
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.prepare();
            mRecorder.start();
            timer = new Timer();
            startClick();
            // 准备结束
            isPrepared = true;
            // 已经准备好了，可以录制了
            if (audioRecordListener != null) {
                audioRecordListener.onAudioStartRecordListener(mCurrentFilePathString);
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 随机生成文件的名称
     *
     * @return
     */
    private String generalFileName(String amrName) {
        return amrName + ".temp";
    }

    // 获得声音的level
    public int getVoiceLevel() {
        // mRecorder.getMaxAmplitude()这个是音频的振幅范围，值域是1-32767
        if (isPrepared) {
            try {

                // 取证+1，否则去不到7
                return DensityUtil.dip2px(context, 200) * mRecorder.getMaxAmplitude() / 32768 + 10;
              /*  double ratio = (double) mRecorder.getMaxAmplitude() / 7000;
//                double db = 0;// 分贝
//                if (ratio > 1)
//                    db = 20 * Math.log10(ratio);
                double m = 20 * Math.log10((double) Math.abs(mRecorder.getMaxAmplitude()));
                Log.d(TAG, "分贝值：" + mRecorder.getMaxAmplitude());
                return (int) ratio + 1;*/
            } catch (Exception e) {

            }
        }

        return 1;
    }

    // 释放资源
    public void release() {
        if (audioRecordListener != null) {
            audioRecordListener.onAudioRecordingComplete();
        }
        if (timer != null && timerTask != null) {
            timer.cancel();
            timer = null;
            if (!timerTask.cancel()) {
                timerTask.cancel();
                timerTask = null;
            }


        }
        // 严格按照api流程进行
        if (mRecorder == null) return;
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }





    /**
     * 停止录音
     */
    public void stop() {
        release();
    }

    // 取消,因为prepare时产生了一个文件，所以cancel方法应该要删除这个文件，
    // 这是与release的方法的区别
    public void cancel() {
        release();
        if (mCurrentFilePathString != null) {
            File file = new File(mCurrentFilePathString);
            file.delete();
            mCurrentFilePathString = null;
        }

    }

    public String getCurrentFilePath() {
        return mCurrentFilePathString;
    }

    public void startClick() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                startTime++;
                Message message = new Message();
                    message.obj = getStringTime(startTime / 10);
                    message.arg1 = startTime / 10;
                    message.what = RECORDING;
                    timeHandler.sendMessage(message);
            }
        };
        timer.schedule(timerTask, 0, 100);
    }

    private TimerTask timerTask;


    private String getStringTime(int cnt) {
        int hour = cnt / 3600;
        int min = cnt % 3600 / 60;
        int second = cnt % 60;
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, second);
    }

    public interface AudioRecordListener {
        void onAudioStartRecordListener(String path);

        void onAudioRecordingListener(String time, int maxTime);

        void onAudioRecordingComplete();

    }


    /**
     * 录音完成
     */
    public void completeRecord() {
        startTime = -1;
    }


    public void setRecordTime(int time) {
        this.startTime = time * 10;
    }


}
