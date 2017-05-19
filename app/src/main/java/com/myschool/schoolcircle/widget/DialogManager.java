package com.myschool.schoolcircle.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myschool.schoolcircle.main.R;

/**
 * Created by Mr.R on 2016/6/20.
 */
public class DialogManager {
    private Dialog mDialog;
    private ImageView iv_icon;
    private ImageView iv_voice;
    private TextView tv_recorder;

    private Context context;

    public DialogManager(Context context) {
        this.context = context;
    }

    //显示对话框
    public void showRecordingDialog() {
        //设置对话框的布局界面
        mDialog = new Dialog(context, R.style.Theme_AudioDialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_recorder,null);
        mDialog.setContentView(view);

        iv_icon = (ImageView) mDialog.findViewById(R.id.iv_dialog_icon);
        iv_voice = (ImageView) mDialog.findViewById(R.id.iv_dialog_voice);
        tv_recorder = (TextView) mDialog.findViewById(R.id.tv_recorder);

        mDialog.show();
    }

    //正在录音
    public void showRecording() {

        if (mDialog != null && mDialog.isShowing()) {
            iv_icon.setVisibility(View.VISIBLE);
            iv_voice.setVisibility(View.VISIBLE);
            tv_recorder.setVisibility(View.VISIBLE);

            iv_icon.setImageResource(R.mipmap.recorder);
            tv_recorder.setText("手指上滑，取消发送");
        }

    }

    //取消
    public void showCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            iv_icon.setVisibility(View.VISIBLE);
            iv_voice.setVisibility(View.GONE);
            tv_recorder.setVisibility(View.VISIBLE);

            iv_icon.setImageResource(R.mipmap.cancel);
            tv_recorder.setText("松开手指，取消发送");
        }
    }

    //录音时间过短
    public void showTooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            iv_icon.setVisibility(View.VISIBLE);
            iv_voice.setVisibility(View.GONE);
            tv_recorder.setVisibility(View.VISIBLE);

            iv_icon.setImageResource(R.mipmap.voice_to_short);
            tv_recorder.setText("录音时间过短！");
        }
    }

    //隐藏对话框
    public void dimissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    //更新音量等级显示
    public void updateVoiceLevel(int level) {
        if (mDialog != null && mDialog.isShowing()) {
//            iv_icon.setVisibility(View.VISIBLE);
//            iv_voice.setVisibility(View.VISIBLE);
//            tv_recorder.setVisibility(View.VISIBLE);

            int resId = context.getResources().getIdentifier("v"+level,
                    "mipmap",context.getPackageName());
            iv_voice.setImageResource(resId);
        }
    }

}
