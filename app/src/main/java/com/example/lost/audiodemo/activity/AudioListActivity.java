package com.example.lost.audiodemo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.lost.audiodemo.R;
import com.example.lost.audiodemo.adapter.AudioListAdapter;
import com.example.lost.audiodemo.bean.AudioInfoBean;
import com.example.lost.audiodemo.utils.FileUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 音乐播放列表
 * Created by wuchanghe on 2017/3/23 17:00.
 */

public class AudioListActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView rv_audio_list;
    private String TAG = this.getClass().getSimpleName();
    private List<AudioInfoBean> audioInfoBeanList = new ArrayList<>();
    private TextView tv_edit;// 编辑
    AudioListAdapter audioListAdapter;
    private TextView tv_delete;// 删除
    private RelativeLayout rl_delete_container;// 包含删除字样的布局

    private List<AudioInfoBean> deleteAudioList = new ArrayList<>();
    private ImageView iv_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);
        rv_audio_list = (RecyclerView) findViewById(R.id.rv_audio_list);
        rv_audio_list.setLayoutManager(new LinearLayoutManager(this));
        tv_edit = (TextView) findViewById(R.id.tv_edit);
        tv_edit.setOnClickListener(this);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        rl_delete_container = (RelativeLayout) findViewById(R.id.rl_delete_container);
        tv_delete.setOnClickListener(this);
        tv_delete.setTextColor(Color.GRAY);
        tv_delete.setClickable(false);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        audioInfoBeanList.clear();
        audioInfoBeanList.addAll(FileUtils.matchJsonFile());
        Log.i(TAG, " 获取的本地json文件为：" + new Gson().toJson(audioInfoBeanList));
        audioListAdapter = new AudioListAdapter(this, audioInfoBeanList);
        rv_audio_list.setAdapter(audioListAdapter);
        audioListAdapter.setSelectedListener(new AudioListAdapter.OnAudioSelectedListener() {
            @Override
            public void onAudioSelect(HashMap<Integer, Boolean> audioInfoBeanHashMap, List<AudioInfoBean> audioInfoBeenList) {
                deleteAudioList.clear();
                for (int key : audioInfoBeanHashMap.keySet()) {
                    if (audioInfoBeanHashMap.get(key)) {
                        deleteAudioList.add(audioInfoBeenList.get(key));
                    }
                }

                Log.i(TAG, "选择的删除list大小为：" + deleteAudioList.size());

                if (deleteAudioList.size() == 0) {
                    tv_delete.setTextColor(Color.GRAY);
                    tv_delete.setClickable(false);
                } else {
                    tv_delete.setTextColor(ContextCompat.getColor(AudioListActivity.this, R.color.circle_color));
                    tv_delete.setClickable(true);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        audioInfoBeanList.clear();
        audioInfoBeanList.addAll(FileUtils.matchJsonFile());
        audioListAdapter.notifyDataSetChanged();
    }

    private boolean checkShow;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit:
                if (!checkShow) {
                    audioListAdapter.showCheckBox(true);
                    checkShow = true;
                    tv_edit.setText("取消");
                    rl_delete_container.setVisibility(View.VISIBLE);
                } else {
                    audioListAdapter.showCheckBox(false);
                    checkShow = false;
                    tv_edit.setText("编辑");
                    rl_delete_container.setVisibility(View.GONE);

                }
                break;

            case R.id.tv_delete:
                for (AudioInfoBean audioInfoBean : deleteAudioList) {
                    audioInfoBeanList.remove(audioInfoBean);
                }
                FileUtils.deleteAudioList(deleteAudioList);
                audioListAdapter.clearCheckStatus();
                audioListAdapter.showCheckBox(false);
                tv_edit.setText("编辑");
                rl_delete_container.setVisibility(View.GONE);
                checkShow = false;
                audioListAdapter.notifyDataSetChanged();
                Log.i(TAG, "要删除的文件为：" + new Gson().toJson(deleteAudioList));
                tv_delete.setTextColor(Color.GRAY);
                tv_delete.setClickable(false);
                break;

            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0,R.anim.activity_close);

    }
}
