package com.example.lost.audiodemo.holder;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.lost.audiodemo.R;


/**
 * 录音列表Holder
 * Created by wuchanghe on 2017/3/27 9:21.
 */

public class AudioListHolder extends RecyclerView.ViewHolder {

    public TextView tv_audio_length;
    public TextView tv_name;
    public AppCompatCheckBox checkBox;
    public TextView tv_record_time;



    public AudioListHolder(View itemView) {
        super(itemView);
        tv_audio_length = (TextView) itemView.findViewById(R.id.tv_audio_length);
        tv_name = (TextView) itemView.findViewById(R.id.tv_name);
        checkBox = (AppCompatCheckBox) itemView.findViewById(R.id.checkbox);
        tv_record_time = (TextView) itemView.findViewById(R.id.tv_record_time);
    }
}
