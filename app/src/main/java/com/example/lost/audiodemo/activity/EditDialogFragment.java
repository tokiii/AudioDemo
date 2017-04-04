package com.example.lost.audiodemo.activity;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lost.audiodemo.R;


/**
 * 可输入对话框
 * Created by wuchanghe on 2017/3/24 13:34.
 */

public class EditDialogFragment extends DialogFragment {


    private TextView tv_title;
    private EditText et_record_name;
    private Button btn_delete;
    private Button btn_save;
    private OnSaveClickListener saveClickListener;

    // End Of Content View Elements

    private void bindViews(View view) {
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        et_record_name = (EditText) view.findViewById(R.id.et_record_name);
        btn_delete = (Button) view.findViewById(R.id.btn_delete);
        btn_save = (Button) view.findViewById(R.id.btn_save);
    }


    public static EditDialogFragment getInstance(Bundle bundle) {
        EditDialogFragment editDialogFragment = new EditDialogFragment();
        editDialogFragment.setArguments(bundle);
        return editDialogFragment;
    }


    public void setOnSaveClickListener(OnSaveClickListener onSaveClickListener) {
        this.saveClickListener = onSaveClickListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_dialog, container, false);
        bindViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        String name = bundle.getString("recordName");
        String title = bundle.getString("title");
        String leftText = bundle.getString("leftText");
        String rightText = bundle.getString("rightText");
        et_record_name.setText(name);
        btn_save.setText(rightText);
        btn_delete.setText(leftText);
        tv_title.setText(title);
        et_record_name.setSelection(name.length());
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveClickListener != null) {
                    saveClickListener.delete(et_record_name.getText().toString());
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveClickListener != null) {
                    if (!TextUtils.isEmpty(et_record_name.getText())) {
                        String saveName = et_record_name.getText().toString();
                        saveName.replace("/", "");// 去除掉反斜杠，保证文件名称的正确性，带斜杠代表文件分隔符
                        saveClickListener.saveClick(saveName);
                    } else {
                        Toast.makeText(getActivity(), "文件名不能为空！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public interface OnSaveClickListener {
        void saveClick(String recordName);
        void delete(String recordName);
    }
}
