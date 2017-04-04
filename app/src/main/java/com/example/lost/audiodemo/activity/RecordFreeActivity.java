package com.example.lost.audiodemo.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lost.audiodemo.AudioRecord;
import com.example.lost.audiodemo.R;
import com.example.lost.audiodemo.bean.AudioInfoBean;
import com.example.lost.audiodemo.utils.DensityUtil;
import com.example.lost.audiodemo.utils.FileUtils;
import com.example.lost.audiodemo.utils.PermissionHelper;
import com.example.lost.audiodemo.utils.ScreenUtils;
import com.example.lost.audiodemo.views.RecordHeadView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordFreeActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Button btn_record;
    private LinearLayout ll_container;
    private LinearLayout ll_circle_container;
    private Button btn_next;
    private TextView tv_record_start;
    private TextView tv_record_end;
    private SeekBar seek_record;
    private Button btn_record_complete;
    private TextView tv_record_name;
    private TextView tv_record_time;// 记录当前录音的时间
    private Button btn_record_list;// 录音列表
    private LinearLayout ll_head_lines_container;
    private View seek_cover;// 覆盖seekBar


    private boolean isRecord = true;
    private String recordPath = "";
    private String audioFolderPath = "";
    private String audioJsonFolderPath = "";
    private String splitFilePath = "";
    private String waitSplitPath = "";
    private int cutSecond;


    private AudioRecord audioRecord;
    private PermissionHelper mHelper;

    private List<View> views = new ArrayList<>();
    private List<View> circleList = new ArrayList<>();
    private List<String> amrList = new ArrayList<>();// 路径集合
    private ArrayList<Integer> waveList = new ArrayList<>();// 保存录音的波形集合

    private String TAG = this.getClass().getSimpleName();

    private ImageView iv_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_free);

        clearVoiceCache();// 进来先清除录音缓存

        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        ll_circle_container = (LinearLayout) findViewById(R.id.ll_circle_container);
        tv_record_start = (TextView) findViewById(R.id.tv_record_start);
        tv_record_end = (TextView) findViewById(R.id.tv_record_end);
        seek_record = (SeekBar) findViewById(R.id.seek_record);
        btn_record_complete = (Button) findViewById(R.id.btn_record_complete);
        ll_head_lines_container = (LinearLayout) findViewById(R.id.ll_head_lines_container);
        tv_record_name = (TextView) findViewById(R.id.tv_record_name);
        seek_cover = findViewById(R.id.seek_cover);
        tv_record_time = (TextView) findViewById(R.id.tv_record_time);
        tv_record_time.setText(getCurrentTime());
        btn_record_complete.setOnClickListener(this);
        btn_record_complete.setClickable(false);
        btn_record_list = (Button) findViewById(R.id.btn_record_list);
        btn_record_list.setOnClickListener(this);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_record = (Button) findViewById(R.id.btn_record);
        btn_record.setOnClickListener(this);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);

        audioFolderPath = Constants.audioFilePath;
        audioJsonFolderPath = Constants.audioJsonFilePath;
        audioRecord = AudioRecord.getInstance(this, audioFolderPath);
        tv_record_name.setText(checkLocalFileName(audioFolderPath));

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(RecordFreeActivity.this, AudioPlayActivity.class);
                startActivity(playIntent);

            }
        });

        addView(60);
        mHelper = new PermissionHelper(this);
        mHelper.requestPermissions("请授予[录音]，[读写]权限，否则无法录音",
                new PermissionHelper.PermissionListener() {
                    @Override
                    public void doAfterGrand(String... permission) {

                    }

                    @Override
                    public void doAfterDenied(String... permission) {
                        Toast.makeText(RecordFreeActivity.this, "请授权,否则无法录音", Toast.LENGTH_SHORT).show();
                    }
                }, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        /**
         * 录音监听
         */
        audioRecord.setAudioRecordListener(new AudioRecord.AudioRecordListener() {
            @Override
            public void onAudioStartRecordListener(String path) {
                recordPath = path;// 获取录音的路径
            }

            @Override
            public void onAudioRecordingListener(String time, int maxTime) {

                tv_record_end.setText(time);
                if (maxTime != 0) {
                    seek_record.setMax(maxTime);
                    seek_record.setProgress(maxTime);
                }
                int level = audioRecord.getVoiceLevel();
                waveList.add(level);
                waveList.add((int) (Math.random() * level + 1));
                waveList.add((int) (Math.random() * level + 1));
                waveList.add((int) (Math.random() * level + 1));
                viewAnimation(level);
            }

            @Override
            public void onAudioRecordingComplete() {
                Log.i(TAG, "录音完成");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mHelper.handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_record:
                Log.i(TAG, "录音文件波形图的大小为：" + waveList.size());
                if (isRecord) {
                    startRecord();

                } else {
                    stopRecord();
                }
                break;
            case R.id.btn_record_complete:

                if (seek_record.getMax() < 10) {
                    Toast.makeText(RecordFreeActivity.this, "录音时间不足10s，请继续录音。", Toast.LENGTH_SHORT).show();
                    return;
                }


                Bundle bundle = new Bundle();
                bundle.putString("recordName", tv_record_name.getText().toString());
                bundle.putString("title", "存储录音文件");
                bundle.putString("leftText", "删除");
                bundle.putString("rightText", "存储");
                final EditDialogFragment dialogFragment = EditDialogFragment.getInstance(bundle);
                dialogFragment.show(getFragmentManager(), "");
                dialogFragment.setOnSaveClickListener(new EditDialogFragment.OnSaveClickListener() {
                    @Override
                    public void saveClick(String recordName) {
                        if (completeRecord(recordName)) {
                            dialogFragment.dismiss();
                        }
                    }

                    @Override
                    public void delete(String recordName) {
                        Bundle deleteBundle = new Bundle();
                        deleteBundle.putString("deleteTip", "确定要删除" + recordName + "吗？");
                        final DeleteDialogFragment deleteDialogFragment = DeleteDialogFragment.getInstance(deleteBundle);
                        deleteDialogFragment.show(getFragmentManager(), "deleteDialog");
                        deleteDialogFragment.setDeleteDialogClickListener(new DeleteDialogFragment.DeleteDialogClickListener() {
                            @Override
                            public void delete() {
                                Log.i(TAG, "删除文件");
                                for (String path : amrList) {
                                    FileUtils.deleteFile(path);
                                }
                                resetView();
                                deleteDialogFragment.dismiss();
                                dialogFragment.dismiss();
                            }
                        });
                    }
                });
                break;

            case R.id.btn_record_list:
                Intent listIntent = new Intent(RecordFreeActivity.this, AudioListActivity.class);
                startActivity(listIntent);
                this.overridePendingTransition(R.anim.activity_open,0);
                break;

            case R.id.iv_back:
                finish();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.setRecordTime(0);
        }
        amrList.add(recordPath);
        for (String path : amrList) { // 页面销毁时删除多余录音文件
            FileUtils.deleteFile(path);
        }
        super.onDestroy();
    }


    // 添加录音动画界面
    private void addView(int count) {

        RecordHeadView recordHeadView = new RecordHeadView(this, ScreenUtils.getScreenWidth(this));// 添加头部自定义view
        ll_head_lines_container.addView(recordHeadView);

        for (int i = 0; i < count; i++) {
            int width = (ScreenUtils.getScreenWidth(this) - (2 * DensityUtil.dip2px(this, 16))) / count - 5;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, 0);
            layoutParams.setMargins(0, 0, 5, 0);
            View view = new View(this);
            view.setBackgroundColor(Color.WHITE);
            view.setLayoutParams(layoutParams);
            views.add(view);
            ll_container.addView(view);

        }

        for (int i = 0; i < count; i++) {
            int width = (ScreenUtils.getScreenWidth(this) - (2 * DensityUtil.dip2px(this, 16))) / count - 5;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
            layoutParams.setMargins(0, 0, 5, 2);
            View view = new View(this);
            view.setBackgroundColor(ContextCompat.getColor(RecordFreeActivity.this, R.color.circle_color));
            view.setLayoutParams(layoutParams);
            circleList.add(view);
            ll_circle_container.addView(view);
        }


    }

    /**
     * 跳动的小动画
     *
     * @param level
     */
    private void viewAnimation(int level) {
        for (int i = 0; i < ll_container.getChildCount(); i++) {
            final View view = ll_container.getChildAt(i);
            final View circleView = ll_circle_container.getChildAt(i);
            int randomHeight = (int) (Math.random() * (level));
            int columnHeight = randomHeight;
            int circleHeight = randomHeight + 1;
            ValueAnimator va;
            //显示view，高度从0变到height值
            va = ValueAnimator.ofInt(view.getHeight(), columnHeight);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    //获取当前的height值
                    int h = (Integer) valueAnimator.getAnimatedValue();
                    //动态更新view的高度
                    view.getLayoutParams().height = h;
                    view.invalidate();
                    ll_container.requestLayout();
                }

            });


            va.setDuration(100);
            va.setInterpolator(new LinearInterpolator());
            //开始动画
            va.start();

            ObjectAnimator animator = ObjectAnimator.ofFloat(circleView, "translationY", circleView.getTranslationY(), -(circleHeight));
            if (-circleView.getTranslationY() > circleHeight) {
                animator.setDuration(110);
            } else {
                animator.setDuration(90);
            }
            animator.setInterpolator(new LinearInterpolator());
            animator.start();

        }
    }


    /**
     * 获取00:00:00类型的时间
     *
     * @param cnt
     * @return
     */
    private String getStringTime(int cnt) {
        int hour = cnt / 3600;
        int min = cnt % 3600 / 60;
        int second = cnt % 60;
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, second);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tv_record_start.setText(getStringTime(progress));
        cutSecond = progress;
        Log.i(TAG, "切割秒：" + cutSecond);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    /**
     * 合并多个amr音频文件
     *
     * @param recordFiles amr文件路径集合
     * @throws IOException
     */
    public void amrFileMerge(List<String> recordFiles, String path) throws IOException {
        Log.i(TAG, "开始合并......");
        OutputStream os;
        if (recordFiles.size() >= 1) {
            File file = new File(path);
            if (file.exists()) { // 如果存在该文件则先删除后创建
                file.delete();
            }
            file.createNewFile();
            os = new FileOutputStream(file);
            for (int i = 0; i < recordFiles.size(); i++) {
                File f = new File(recordFiles.get(i));
                if (!f.exists()) {
                    continue;
                }
                FileInputStream fis = new FileInputStream(f);
                byte[] b = new byte[fis.available()];
                int length = b.length;
                // 头文件
                if (i == 0) {
                    if (length > 0) {
                        while (fis.read(b) != -1) {
                            os.write(b, 0, length);
                        }
                    }
                } else {
                    // 之后的文件，去掉头文件就可以了
                    while (fis.read(b) != -1) {
                        // amr_wb格式的头文件长度
                        os.write(b, 6, length - 6);
                    }
                }
                StringBuffer sb = new StringBuffer();
                for (byte c : b) {
                    sb.append(c);
                }
                fis.close();
                f.delete();
            }
            os.flush();
            os.close();
            int second = (int) ((file.length() - 6) / 32 / 50);
            Log.i(TAG, "合并结束...... 大小为：" + second);
            tv_record_end.setText(getStringTime(second));// 重新计算音频大小
            audioRecord.setRecordTime(second);

        }
    }


    /**
     * 获取文件的byte[] 数组
     *
     * @param f
     * @return
     * @throws IOException
     */
    public byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        try {
            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }


    /**
     * 文件切割
     *
     * @param oldFilePath
     * @param newFilePath
     * @param endSecond   截取到最后的秒数
     */
    private void cutByTimeDuration(String oldFilePath, String newFilePath, int endSecond) throws IOException {
        Log.i(TAG, "剪切的路径为：" + oldFilePath + " 新文件路径为：" + newFilePath);
        File originalFile = new File(oldFilePath);
        byte[] originalByte = fullyReadFileToBytes(originalFile);// 读取原始文件的字节数组
        long noBytesStart = 0;// 默认开始时间是0，如果有从中间切割的需求需要替换成参数值
        long noBytesEnd = endSecond * 50 * 32 + 6; // 一秒 50帧 一帧33字节 9为amr-wb文件头部信息的字节个数
        byte[] targetByte = new byte[(int) (noBytesEnd - noBytesStart)];
        System.arraycopy(originalByte, (int) noBytesStart, targetByte, 0,
                (int) (noBytesEnd - noBytesStart));// 复制字节数组
        try {
            File file = new File(newFilePath);
            {
                OutputStream out = new FileOutputStream(file);
                int length = targetByte.length;// -1;
                out.write(targetByte, 0, length);
                Thread.yield();
                out.flush();
                out.close();
                originalFile.delete();// 删除原始文件
                Log.i(TAG, "切割完成！" + "字节的长度为：" + targetByte.length + "  原始字节长度:" + originalByte.length);

                float percent = (float) (originalByte.length - noBytesEnd) / (float) originalByte.length;
                Log.i(TAG, "切割的百分比为：" + (int) (waveList.size() * percent));
                int cutCount = (int) (waveList.size() * percent);
                cutWaveList(cutCount);// 切割波形图数据
            }
        } catch (Exception e) {
        }
    }


    /**
     * 开始录音
     */
    private void startRecord() {
        btn_record_complete.setTextColor(Color.GRAY);
        btn_record_complete.setClickable(false);
        btn_record_list.setClickable(false);
        btn_record_list.setTextColor(Color.GRAY);
        if (cutSecond != 0) {
            Log.i(TAG, "开始录音时候进行裁剪的秒数：" + cutSecond);
            splitFilePath = audioFolderPath + File.separator + "split.temp";
            try {
                cutByTimeDuration(waitSplitPath, splitFilePath, cutSecond);
            } catch (IOException e) {
                e.printStackTrace();
            }
            audioRecord.setRecordTime(cutSecond);
            amrList.clear();
            amrList.add(splitFilePath);
        }


        tv_record_start.setText("00:00:00");// 重置裁剪时间
        seek_cover.setVisibility(View.VISIBLE);
        seek_cover.setOnClickListener(this);
        audioRecord.startRecord("ap-record" + System.currentTimeMillis());// 开始录音
        btn_record.setBackgroundResource(R.mipmap.recording);
        isRecord = false;
        seek_record.setOnSeekBarChangeListener(null);
        cutSecond = 0;// 重置裁剪时间
    }


    /**
     * 暂停录音
     */
    private void stopRecord() {


        btn_record_complete.setTextColor(Color.WHITE);
        btn_record_complete.setClickable(true);
        seek_cover.setVisibility(View.INVISIBLE);
        isRecord = true;
        btn_record.setBackgroundResource(R.mipmap.ready_record);
        amrList.add(recordPath);
        audioRecord.stop();
        String mergePath = audioFolderPath + File.separator + "non_finish" + System.currentTimeMillis() + "merge.temp";
        waitSplitPath = mergePath;
        try {
            amrFileMerge(amrList, mergePath);
            amrList.clear();// 清空合并列表
            amrList.add(mergePath);// 添加合并之后的路径到待合并列表中
        } catch (IOException e) {
            e.printStackTrace();
        }
        seek_record.setOnSeekBarChangeListener(this);
    }

    /**
     * 结束录音
     */
    private boolean completeRecord(String completeName) {

        try {
            String audioSavePath = audioFolderPath + File.separator + completeName + ".amr";
            File file = new File(audioSavePath);

            if (file.exists()) {
                Toast.makeText(RecordFreeActivity.this, "已存在同名文件，请换个名称", Toast.LENGTH_SHORT).show();
                return false;
            }
            amrFileMerge(amrList, audioSavePath);
            AudioInfoBean audioInfoBean = new AudioInfoBean();
            audioInfoBean.setAudioName(completeName);
            audioInfoBean.setAudioJsonSaveName(completeName + ".json");
            audioInfoBean.setAudioWaveList(waveList);
            audioInfoBean.setAudioPath(audioSavePath);
            audioInfoBean.setAudioLength(tv_record_end.getText().toString());
            audioInfoBean.setAudioJsonFolderPath(audioJsonFolderPath);
            audioInfoBean.setAudioFolderPath(audioFolderPath);
            audioInfoBean.setRecordTime(getCurrentTime());
            FileUtils.saveAudioToJson(audioInfoBean);
            resetView();
            amrList.clear();// 清空amr列表
            btn_record_complete.setTextColor(Color.GRAY);
            btn_record_complete.setClickable(false);
            tv_record_name.setText(checkLocalFileName(audioFolderPath));
            btn_record_list.setClickable(true);
            btn_record_list.setTextColor(Color.WHITE);
            audioRecord.setRecordTime(0);// 重置录音时间
            seek_cover.setVisibility(View.VISIBLE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 重置界面
     */
    private void resetView() {
        cutSecond = 0;
        amrList.clear();
        audioRecord.setRecordTime(0);
        audioRecord.release();
        tv_record_start.setText("00:00:00");
        tv_record_end.setText("00:00:00");
        viewAnimation(1);
    }


    /**
     * 裁剪波形图数据
     *
     * @param count
     */
    private void cutWaveList(int count) {
        Log.i(TAG, "波形图剪切之前大小为：" + waveList.size());

        int waveSize = waveList.size();
        for (int i = 0; i < count; i++) {
            waveList.remove(waveSize - i - 1);
        }
        Log.i(TAG, "波形图剪切之后大小为：" + waveList.size());
    }


    /**
     * 获取当前年月日
     *
     * @return
     */
    private String getCurrentTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String dateNowStr = sdf.format(d);
        return dateNowStr;
    }


    /**
     * 返回本地录音名称
     *
     * @param audioFolder
     * @return
     */
    private String checkLocalFileName(String audioFolder) {
        String audioName = "新录音";
        int index = 1;
        while (true) {
            File file = new File(audioFolder + File.separator + audioName + ".amr");
            if (file.exists()) {
                audioName = "新录音" + index;
                index++;
                continue;
            } else {
                break;
            }
        }
        return audioName;
    }

    /**
     * 清除录音缓存
     */
    private void clearVoiceCache() {
        File dir = new File(Constants.audioFilePath);
        if (!dir.exists()) {
            return;
        }
        if (!dir.isFile() && dir.list() != null) {
            String[] pathList = dir.list();

            for (String path : pathList) {
                if (path.contains(".temp")) {
                    String tempPath = Constants.audioFilePath + File.separator + path;
                    File tempFile = new File(tempPath);
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }
                }
            }
        }
    }

}
