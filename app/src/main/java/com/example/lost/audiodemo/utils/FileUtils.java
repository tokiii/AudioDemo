package com.example.lost.audiodemo.utils;

import android.util.Log;


import com.example.lost.audiodemo.activity.Constants;
import com.example.lost.audiodemo.bean.AudioInfoBean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuchanghe on 2017/3/28 15:02.
 */

public class FileUtils {

    private static final String TAG = "FileUtils";

    /**
     * 删除文件
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }


    /**
     * 删除单个音乐文件
     *
     * @param audioInfoBean
     */
    public static void deleteAudio(AudioInfoBean audioInfoBean) {
        String jsonPath = audioInfoBean.getAudioJsonFolderPath() + File.separator + audioInfoBean.getAudioJsonSaveName();
        String audioPath = audioInfoBean.getAudioPath();
        deleteFile(jsonPath);
        deleteFile(audioPath);
    }


    /**
     * 删除音乐文件集合
     *
     * @param audioInfoBeen
     */
    public static void deleteAudioList(List<AudioInfoBean> audioInfoBeen) {
        for (AudioInfoBean audioInfoBean : audioInfoBeen) {
            deleteAudio(audioInfoBean);
        }
    }


    /**
     * 重命名文件
     * @param audioInfoBean
     * @param newName
     * @return
     */
    public static AudioInfoBean reNameAudio(AudioInfoBean audioInfoBean, String newName) {

        // 创建新的AudioInfoBean
        String audioFolderPath = audioInfoBean.getAudioFolderPath();// 音乐文件路径
        String oldAudioName = audioInfoBean.getAudioName();
        if (newName.equals(oldAudioName)) {// 如果新文件名称和原文件名称一致，不进行操作
            return audioInfoBean;
        }
        String newAudioPath = audioFolderPath + File.separator + newName + ".amr";
        Log.i("FileUtils", "新录音的新路径为：" + newAudioPath);
        File newFile = new File(newAudioPath);
        if (newFile.exists()) {
            return null;// 文件名冲突
        }

        File oldFile = new File(audioInfoBean.getAudioPath());
        boolean isRename = oldFile.renameTo(newFile);

        if (isRename) {
            audioInfoBean.setAudioName(newName);
            audioInfoBean.setAudioPath(newAudioPath);
            audioInfoBean.setAudioJsonSaveName(newName + ".json");
            saveAudioToJson(audioInfoBean);
            deleteFile(audioInfoBean.getAudioJsonFolderPath() + File.separator + oldAudioName  + ".json");
        }

        return audioInfoBean;
    }


    /**
     * 保存音频信息到.json文件中
     * @param audioInfoBean
     */
    public static void saveAudioToJson(AudioInfoBean audioInfoBean) {
        String outPath = audioInfoBean.getAudioJsonFolderPath();
        File dir = new File(outPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(outPath + File.separator + audioInfoBean.getAudioJsonSaveName());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Writer output = null;
        try {
            output = new BufferedWriter(new FileWriter(file));
            output.write(JsonUtils.objectToJson(audioInfoBean));
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    /**
     * 扫描本地文件夹下面所有的文件
     *
     * @param fileRootPath
     */
    public static List<String> searchLocalFileList(String fileRootPath) {
        List<String> pathList = new ArrayList<>();
        File file = new File(fileRootPath);
        if (!file.isFile() && file.list() != null) {
            String[] fileList = file.list();
            for (int i = 0; i < fileList.length; i++) {

                Log.i(TAG, "文件名称为：" + (fileRootPath + File.separator + fileList[i]));
                pathList.add(fileRootPath + File.pathSeparator + fileList[i]);
            }
        }
        Log.i(TAG, "获得文件的列表数量为：" + pathList.size());

        return pathList;
    }



    /**
     * 匹配json文件数组
     */
    public static List<AudioInfoBean> matchJsonFile() {
        List<AudioInfoBean> audioInfoBeanList = new ArrayList<>();
        audioInfoBeanList.clear();
        String jsonFile = Constants.audioJsonFilePath;
        File file = new File(jsonFile);
        if (!file.isFile() && file.list() != null) {
            String[] fileList = file.list();

            for (int i = 0; i < fileList.length; i++) {
                String filePath = jsonFile + File.separator + fileList[i];
                try {
                    File yourFile = new File(filePath);
                    FileInputStream stream = new FileInputStream(yourFile);
                    String jsonStr = null;
                    try {
                        FileChannel fc = stream.getChannel();
                        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                        jsonStr = Charset.defaultCharset().decode(bb).toString();
                        AudioInfoBean audioInfoBean = (AudioInfoBean) JsonUtils.jsonToBean(jsonStr, AudioInfoBean.class);
                        audioInfoBeanList.add(audioInfoBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        stream.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return audioInfoBeanList;

    }

}
