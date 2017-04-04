package com.example.lost.audiodemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 音频信息
 * Created by wuchanghe on 2017/3/27 10:15.
 */

public class AudioInfoBean implements Parcelable {

    private String audioName; // 音频名称，不带.amr
    private String audioLength; // 音频长度
    private String audioPath;// 文件的路径
    private ArrayList<Integer> audioWaveList;// 波形图集合
    private String audioJsonSaveName; // Json的文件名 带.json字样
    private String audioJsonFolderPath;// json文件文件夹路径
    private String audioFolderPath; // 录音文件文件夹路径
    private String recordTime;// 录音时间

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getAudioFolderPath() {
        return audioFolderPath;
    }

    public void setAudioFolderPath(String audioFolderPath) {
        this.audioFolderPath = audioFolderPath;
    }

    public String getAudioJsonFolderPath() {
        return audioJsonFolderPath;
    }

    public void setAudioJsonFolderPath(String audioJsonFolderPath) {
        this.audioJsonFolderPath = audioJsonFolderPath;
    }

    public AudioInfoBean() {

    }


    protected AudioInfoBean(Parcel in) {
        audioName = in.readString();
        audioLength = in.readString();
        audioPath = in.readString();
        audioJsonSaveName = in.readString();
        audioWaveList = (ArrayList<Integer>) in.readSerializable();
        audioJsonFolderPath = in.readString();
        audioFolderPath = in.readString();
        recordTime = in.readString();
    }

    public static final Creator<AudioInfoBean> CREATOR = new Creator<AudioInfoBean>() {
        @Override
        public AudioInfoBean createFromParcel(Parcel in) {
            return new AudioInfoBean(in);
        }

        @Override
        public AudioInfoBean[] newArray(int size) {
            return new AudioInfoBean[size];
        }
    };

    public String getAudioJsonSaveName() {
        return audioJsonSaveName;
    }

    public void setAudioJsonSaveName(String audioJsonSaveName) {
        this.audioJsonSaveName = audioJsonSaveName;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public String getAudioLength() {
        return audioLength;
    }

    public void setAudioLength(String audioLength) {
        this.audioLength = audioLength;
    }

    public ArrayList<Integer> getAudioWaveList() {
        return audioWaveList;
    }

    public void setAudioWaveList(ArrayList<Integer> audioWaveList) {
        this.audioWaveList = audioWaveList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(audioName);
        dest.writeString(audioLength);
        dest.writeString(audioPath);
        dest.writeString(audioJsonSaveName);
        dest.writeSerializable(audioWaveList);
        dest.writeString(audioJsonFolderPath);
        dest.writeString(audioFolderPath);
        dest.writeString(recordTime);
    }
}
