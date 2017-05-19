package com.myschool.schoolcircle.widget;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.myschool.schoolcircle.audio.AudioManager;
import com.myschool.schoolcircle.main.R;

/**
 * Created by Mr.R on 2016/6/20.
 */
public class AudioRecorderButton extends Button implements AudioManager.AudioStateListener {
    private static final int DISTANCE_Y = 50;
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_CANCEL = 3;

    private static final int MSG_AUDIO_PREPARED = 101;
    private static final int MSG_AUDIO_CHANGE = 102;
    private static final int MSG_AUDIO_DIMISS = 103;

    private float time;
    private boolean ready;

    private AudioManager audioManager;
    private DialogManager dialogManager;

    //记录当前按钮状态
    private int state = STATE_NORMAL;
    //是否正在录音的标识
    private boolean isRecording = false;

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        dialogManager = new DialogManager(getContext());
        //初始化音频存放的路径
        String dir = Environment.getExternalStorageDirectory()+"/recorder_audios";
        audioManager = AudioManager.getInstance(dir);
        audioManager.setOnAudioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ready = true;
                audioManager.prepareAudio();
                return false;
            }
        });
    }

    //录音完成后的回调
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }

    private AudioFinishRecorderListener listener;

    public void setOnAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        this.listener = listener;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    dialogManager.showRecordingDialog();
                    isRecording = true;

                    new Thread(GetVoiceLevelRunnable).start();
                    break;

                case MSG_AUDIO_CHANGE:
                    dialogManager.updateVoiceLevel(audioManager.getVoiceLevel(7));
                    break;

                case MSG_AUDIO_DIMISS:
                    dialogManager.dimissDialog();
                    break;
            }
        }
    };

    @Override
    public void donePrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int)event.getX();
        int y = (int)event.getY();

        switch (action) {
            case MotionEvent.ACTION_UP:
                if (!ready) {
                    reset();
                    return super.onTouchEvent(event);
                }

                if (!isRecording || time < 0.6f) {

                    dialogManager.showTooShort();
                    audioManager.cancel();
                    //1.3秒后关闭对话框
                    mHandler.sendEmptyMessageDelayed(MSG_AUDIO_DIMISS, 1300);

                } else if (state == STATE_RECORDING) {//正常录制结束

                    dialogManager.dimissDialog();
                    audioManager.release();

                    if (listener != null) {
                        listener.onFinish(time, audioManager.getFilePath());
                    }

                } else if (state == STATE_CANCEL) {

                    dialogManager.dimissDialog();
                    audioManager.cancel();

                }
                reset();
                break;

            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;

            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    //根据x,y的坐标判断是否想要取消
                    if (wantToCancel(x,y)) {
                        changeState(STATE_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }

                break;

            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    //重置状态及标志位
    private void reset() {
        isRecording = false;
        ready = false;
        time = 0;
        changeState(STATE_NORMAL);
    }

    //判断是否要取消
    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }

        if (y < -DISTANCE_Y || y > getHeight()+DISTANCE_Y) {
            return true;
        }
        return false;
    }

    //更改按钮显示状态
    private void changeState(int state) {
        if (this.state != state) {
            this.state = state;

            switch (state) {
                case STATE_NORMAL:
                    setText(R.string.str_recorder_normal);
                    break;

                case STATE_RECORDING:
                    setText(R.string.str_recorder_recording);
                    if (isRecording) {
                        dialogManager.showRecording();
                    }
                    break;

                case STATE_CANCEL:
                    setText(R.string.str_recorder_cancel);
                    dialogManager.showCancel();
                    break;

                default:
                    break;
            }
        }
    }

    //获取音量大小
//    private class GetVoiceLevelRunnable implements Runnable {
//        @Override
//        public void run() {
//            while (isRecording) {
//                try {
//                    Thread.sleep(100);
//                    time += 0.1f;
//                    mHandler.sendEmptyMessage(MSG_AUDIO_CHANGE);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private Runnable GetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    time += 0.1f;
                    mHandler.sendEmptyMessage(MSG_AUDIO_CHANGE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
