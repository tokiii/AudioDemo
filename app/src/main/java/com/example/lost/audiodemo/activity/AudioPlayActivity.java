package com.example.lost.audiodemo.activity;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lost.audiodemo.AudioPlay;
import com.example.lost.audiodemo.R;
import com.example.lost.audiodemo.bean.AudioInfoBean;
import com.example.lost.audiodemo.utils.DensityUtil;
import com.example.lost.audiodemo.utils.FileUtils;
import com.example.lost.audiodemo.utils.JsonUtils;
import com.example.lost.audiodemo.utils.ScreenUtils;
import com.example.lost.audiodemo.views.AudioPlayHeadView;
import com.example.lost.audiodemo.views.FingerView;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.List;

/**
 * 音乐播放界面
 * Created by wuchanghe on 2017/3/21 13:27.
 */

public class AudioPlayActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout ll_container;
    private AudioInfoBean audioInfoBean;
    private TextView tv_play_start;
    private int audioLong = 0;
    private int screenWidth;
    private String TAG = this.getClass().getSimpleName();
    private LinearLayout ll_wave_container;
    private RelativeLayout rl_head_container;
    AudioPlay audioPlay;
    private Button btn_play;
    private FingerView fingerView;
    private Button btn_refactor;// 重命名
    private Button btn_delete; // 删除
    private TextView tv_record_time;
    private TextView tv_music_name;
    private ImageView iv_back;// 返回


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_play);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        rl_head_container = (RelativeLayout) findViewById(R.id.rl_head_container);
        btn_refactor = (Button) findViewById(R.id.btn_refactor);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(this);
        btn_refactor.setOnClickListener(this);
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_play.setOnClickListener(this);
        fingerView = new FingerView(this);
        tv_record_time = (TextView) findViewById(R.id.tv_record_time);
        tv_music_name = (TextView) findViewById(R.id.tv_music_name);
        fingerView.onPlay(DensityUtil.dip2px(this, 25));
        ll_container.addView(fingerView);
        tv_play_start = (TextView) findViewById(R.id.tv_play_start);
        audioInfoBean = getIntent().getParcelableExtra("audioInfo");
        tv_music_name.setText(audioInfoBean.getAudioName());
        tv_record_time.setText(audioInfoBean.getRecordTime());
        Log.i(TAG, "播放界面得到的json数据为：" + JsonUtils.objectToJson(audioInfoBean));
        audioPlay = AudioPlay.getInstance();
        screenWidth = ScreenUtils.getScreenWidth(this);
        ll_wave_container = (LinearLayout) findViewById(R.id.ll_wave_container);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        audioPlay.setPlayStatusListener(new AudioPlay.OnMediaPlayStatusListener() {
            @Override
            public void onPlayComplete(MediaPlayer mediaPlayer, String musicLength) {
                audioPlay.release();
                fingerView.onPlay(DensityUtil.dip2px(AudioPlayActivity.this, 25) + (screenWidth - (2 * DensityUtil.dip2px(AudioPlayActivity.this, 25))));
                // 播放完成恢复到初始状态
                resetView();
            }

            @Override
            public void onPlayDuration(float percent, String playTime) {
                tv_play_start.setText(playTime);
                fingerView.onPlay((int) (DensityUtil.dip2px(AudioPlayActivity.this, 25) + (screenWidth - (2 * DensityUtil.dip2px(AudioPlayActivity.this, 25))) * percent));
            }

            @Override
            public void onPlayStart(int audioTime, String audioLength) {
                audioLong = audioTime;
            }
        });

        audioPlay.playSound(audioInfoBean.getAudioPath());

        addView(audioLong);

        fingerView.setTouchListener(new FingerView.OnFingerTouchListener() {
            @Override
            public void onMove() {
                Log.i(TAG, "move");
            }

            @Override
            public void onFingerUp(float percent) {
                Log.i(TAG, "onFingerUp");
                audioPlay.playPosition((int) (audioLong * percent));
            }

            @Override
            public void onFingerDown() {
                Log.i(TAG, "onFingerDown");
                audioPlay.pause();
            }
        });

    }

    private void addView(int audioLong) {

        // 计算时间平均值
        int averageTime = audioLong / 6;
        Log.i(TAG, "一份时间的平均值：" + averageTime);

        List<String> timeList = new ArrayList<>();
        timeList.add("00:00:00");
        timeList.add(AudioPlay.calculateMusicLength(averageTime));
        timeList.add(AudioPlay.calculateMusicLength(averageTime * 2));
        timeList.add(AudioPlay.calculateMusicLength(averageTime * 3));
        timeList.add(AudioPlay.calculateMusicLength(averageTime * 4));
        timeList.add(AudioPlay.calculateMusicLength(averageTime * 5));
        timeList.add(AudioPlay.calculateMusicLength(audioLong));


        AudioPlayHeadView recordHeadView = new AudioPlayHeadView(this, ScreenUtils.getScreenWidth(this), timeList);// 添加头部自定义view
        RelativeLayout.LayoutParams headLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        headLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        recordHeadView.setLayoutParams(headLayoutParams);
        rl_head_container.addView(recordHeadView);


        List<Integer> audioWaveList = audioInfoBean.getAudioWaveList();

        int waveSize = (ScreenUtils.getScreenWidth(this) - (2 * DensityUtil.dip2px(this, 16))) / DensityUtil.dip2px(this, 3);

        int elementNum = (int) Math.round((double) audioWaveList.size() / waveSize);
        if (elementNum == 0) {
            elementNum = 1;
        }
        Log.i(TAG, "元素的数量为：" + elementNum);
        List<List<Integer>> groupList = groupListByQuantity(audioWaveList, elementNum);
        Log.i(TAG, "分组得到的数据为：" + new Gson().toJson(groupList));
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < groupList.size(); i++) {
            int average;
            int size = groupList.get(i).size();
            int sum = 0;
            for (Integer integer : groupList.get(i)) {
                sum = sum + integer;
            }
            average = sum / size;
            integers.add(average);
        }

        Log.i(TAG, "处理得到的结果为：" + new Gson().toJson(integers) + "   数组的大小为：" + integers.size());
        Log.i(TAG, "处理得到的结果为：可用宽度" + (ScreenUtils.getScreenWidth(this) - (2 * DensityUtil.dip2px(this, 16))));
        int usefulWidth = ScreenUtils.getScreenWidth(this) - (2 * DensityUtil.dip2px(this, 16));
        double waveDoubleWidth = ((float)usefulWidth) / integers.size();
        int waveWidth = (int) Math.round(waveDoubleWidth);
        Log.i(TAG, "处理得到的结果为：单个宽度" + waveWidth);

        int waveSumWidth = 0;
        for (int i = 0; i < integers.size(); i++) {
            int width = waveWidth;
            waveSumWidth = width + waveSumWidth;
            int height = integers.get(i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width / 2, height);
            layoutParams.setMargins((int) Math.round((double)width / 4), 0, (int) Math.round((double)width / 4), 0);
            View view = new View(this);
            view.setBackgroundColor(Color.WHITE);
            view.setLayoutParams(layoutParams);
            if (waveSumWidth <= usefulWidth - 2 * width) {
                ll_wave_container.addView(view);
            } else {
                Log.i(TAG, "处理得到的结果为：已经用的宽度" + waveSumWidth);
                break;
            }
        }

        for (int i = 0; i < 50; i++) { // 补位的view
            int width = (ScreenUtils.getScreenWidth(this) - (2 * DensityUtil.dip2px(this, 16))) / integers.size();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width / 2, (int) (Math.random() * 10 + 1));
            layoutParams.setMargins(width / 4, 0, width / 4, 0);
            View view = new View(this);
            view.setBackgroundColor(Color.WHITE);
            view.setLayoutParams(layoutParams);
            ll_wave_container.addView(view);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioPlay != null) {
            audioPlay.release();
        }
    }

    private boolean isPause;
    private boolean isStarted;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                if (!isPause) {
                    btn_play.setBackgroundResource(R.mipmap.record_play_stop);
                    if (!isStarted) {
                        audioPlay.start();
                        isStarted = true;
                    } else {
                        audioPlay.resume();
                    }
                    isPause = true;
                } else {
                    btn_play.setBackgroundResource(R.mipmap.record_play);
                    audioPlay.pause();
                    isPause = false;
                }
                break;

            case R.id.iv_back:
                finish();
                break;

            case R.id.btn_refactor:
                resetView();
                Bundle bundle = new Bundle();
                bundle.putString("recordName", audioInfoBean.getAudioName());
                bundle.putString("title", "请重新命名");
                bundle.putString("leftText", "取消");
                bundle.putString("rightText", "存储");
                final EditDialogFragment editDialogFragment = EditDialogFragment.getInstance(bundle);
                editDialogFragment.show(getFragmentManager(), "edit");
                editDialogFragment.setOnSaveClickListener(new EditDialogFragment.OnSaveClickListener() {
                    @Override
                    public void saveClick(String recordName) {
                        AudioInfoBean newAudioInfoBean = FileUtils.reNameAudio(audioInfoBean, recordName);
                        if (newAudioInfoBean != null) {
                            editDialogFragment.dismiss();
                            audioInfoBean = newAudioInfoBean;
                            tv_music_name.setText(audioInfoBean.getAudioName());
                        } else {
                            Toast.makeText(AudioPlayActivity.this, "文件名称与本地已存在文件冲突！", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void delete(String recordName) {
                        editDialogFragment.dismiss();
                    }
                });
                break;

            case R.id.btn_delete:
                resetView();
                Bundle deleteBundle = new Bundle();
                deleteBundle.putString("deleteTip", "确定要删除\"" + tv_music_name.getText().toString() + "\"吗？");
                final DeleteDialogFragment deleteDialogFragment = DeleteDialogFragment.getInstance(deleteBundle);
                deleteDialogFragment.show(getFragmentManager(), "delete");
                deleteDialogFragment.setDeleteDialogClickListener(new DeleteDialogFragment.DeleteDialogClickListener() {
                    @Override
                    public void delete() {
                        FileUtils.deleteAudio(audioInfoBean);
                        deleteDialogFragment.dismiss();
                        finish();
                    }
                });
                break;
        }
    }


    /**
     * 对集合按照数量分组
     *
     * @param list
     * @param quantity
     * @return
     */
    public static List groupListByQuantity(List list, int quantity) {
        if (list == null || list.size() == 0) {
            return list;
        }

        if (quantity <= 0) {
            new IllegalArgumentException("Wrong quantity.");
        }

        List wrapList = new ArrayList();
        int count = 0;
        while (count < list.size()) {
            wrapList.add(list.subList(count, (count + quantity) > list.size() ? list.size() : count + quantity));
            count += quantity;
        }

        return wrapList;
    }


    /**
     * 恢复到初始状态
     */
    private void resetView() {
        audioPlay.pause();
        fingerView.onPlay(DensityUtil.dip2px(AudioPlayActivity.this, 25));
        tv_play_start.setText("00:00:00 ");
        isStarted = false;
        isPause = false;
        audioPlay.playSound(audioInfoBean.getAudioPath());
        btn_play.setBackgroundResource(R.mipmap.record_play);
    }
}
