package com.example.lost.audiodemo.activity;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.lost.audiodemo.R;


/**
 * 删除对话框
 * Created by wuchanghe on 2017/3/24 14:57.
 */

public class DeleteDialogFragment extends DialogFragment {

    private TextView tv_title;
    private TextView tv_delete_tip;
    private Button btn_cancel;
    private Button btn_delete;
    private DeleteDialogClickListener deleteDialogClickListener;

    // End Of Content View Elements

    public static DeleteDialogFragment getInstance(Bundle bundle) {
        DeleteDialogFragment deleteDialogFragment = new DeleteDialogFragment();
        if (bundle != null)
            deleteDialogFragment.setArguments(bundle);
        return deleteDialogFragment;
    }


    public void setDeleteDialogClickListener(DeleteDialogClickListener deleteDialogClickListener) {
        this.deleteDialogClickListener = deleteDialogClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_dialog, container, false);
        bindViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && !bundle.isEmpty()) {
            String tip = bundle.getString("deleteTip");
            tv_delete_tip.setText(tip);
        }
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteDialogClickListener != null) {
                    deleteDialogClickListener.delete();
                }
            }
        });
    }

    private void bindViews(View view) {
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_delete_tip = (TextView) view.findViewById(R.id.tv_delete_tip);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_delete = (Button) view.findViewById(R.id.btn_delete);
    }


    public interface DeleteDialogClickListener {
        void delete();
    }
}
