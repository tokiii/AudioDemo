package com.example.lost.audiodemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lost.audiodemo.R;
import com.example.lost.audiodemo.activity.AudioPlayActivity;
import com.example.lost.audiodemo.bean.AudioInfoBean;
import com.example.lost.audiodemo.holder.AudioListHolder;

import java.util.HashMap;
import java.util.List;

/**
 * 音乐列表界面适配器
 * Created by wuchanghe on 2017/3/23 17:02.
 */

public class AudioListAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<AudioInfoBean> audioInfoBeen;
    private boolean checkShow;
    private HashMap<Integer, Boolean> selectMap = new HashMap<>();
    private OnAudioSelectedListener selectedListener;

    public AudioListAdapter(Context context, List<AudioInfoBean> infoBeanList) {
        this.context = context;
        this.audioInfoBeen = infoBeanList;
        for (int i = 0; i < infoBeanList.size(); i++) {
            selectMap.put(i, false);
        }
    }


    public void setSelectedListener(OnAudioSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_audio, parent, false);
        return new AudioListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AudioListHolder) {
            final AudioListHolder audioListHolder = (AudioListHolder) holder;
            audioListHolder.tv_name.setText(audioInfoBeen.get(position).getAudioName());
            audioListHolder.tv_audio_length.setText(audioInfoBeen.get(position).getAudioLength());
            audioListHolder.tv_record_time.setText(audioInfoBeen.get(position).getRecordTime());
            if (checkShow) {
                audioListHolder.checkBox.setVisibility(View.VISIBLE);
            } else {
                audioListHolder.checkBox.setVisibility(View.GONE);
            }

            audioListHolder.checkBox.setChecked(selectMap.get(position));

            audioListHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectMap.put(position, audioListHolder.checkBox.isChecked());
                    if (selectedListener != null) {
                        selectedListener.onAudioSelect(selectMap, audioInfoBeen);
                    }
                }
            });
            audioListHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkShow) {

                    } else {
                        Intent playIntent = new Intent(context, AudioPlayActivity.class);
                        playIntent.putExtra("audioInfo", audioInfoBeen.get(position));
                        context.startActivity(playIntent);
                    }

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return audioInfoBeen.size();
    }


    public void showCheckBox(boolean isShow) {
        checkShow = isShow;
        notifyDataSetChanged();
    }


    public void clearCheckStatus() {
        for (int i = 0; i < audioInfoBeen.size(); i++) {
            selectMap.put(i, false);
        }
    }


    public interface OnAudioSelectedListener {
        void onAudioSelect(HashMap<Integer, Boolean> audioInfoBeanHashMap, List<AudioInfoBean> audioInfoBeanList);
    }
}
