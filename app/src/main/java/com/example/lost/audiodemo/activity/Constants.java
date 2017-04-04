package com.example.lost.audiodemo.activity;

import android.os.Environment;

import java.io.File;

/**
 * Created by wuchanghe on 2017/3/29 16:08.
 */

public class Constants {

    /*录音文件保存路径*/
    public static final String audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CropVoiceRecord" + File.separator + "voiceList";

    /*录音文件json信息保存路径*/
    public static final String audioJsonFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +  "CropVoiceRecord" + File.separator + "voiceInfoList";
}
