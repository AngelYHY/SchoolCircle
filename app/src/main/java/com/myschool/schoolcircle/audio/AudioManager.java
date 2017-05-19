package com.myschool.schoolcircle.audio;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 音频管理类
 * Created by Mr.R on 2016/6/20.
 */
public class AudioManager {
    private MediaRecorder mediaRecorder;
    private String dir;//文件夹名称
    private String filePath;//文件夹路径
    private static AudioManager instance;//存放本类实例
    private boolean isPrepared = false;//标识是否准备完毕

    //单例化
    private AudioManager(String dir) {
        this.dir = dir;
    }

    //录音状态监听接口
    public interface AudioStateListener{
        //准备完毕
        void donePrepared();
    }

    public AudioStateListener stateListener;

    //录音状态监听器
    public void setOnAudioStateListener(AudioStateListener listener) {
        this.stateListener = listener;
    }

    //获取实例的方法
    public static AudioManager getInstance(String dir) {
        if (instance == null) {
            synchronized (AudioManager.class) {
                instance = new AudioManager(dir);
            }
        }
        return instance;
    }

    public String getFilePath() {
        return filePath;
    }

    //准备
    public void prepareAudio() {
        //准备开始前
        isPrepared = false;
        File file = new File(dir);
        //若文件不存在则创建文件
        if (!file.exists()) {
            file.mkdirs();
        }

        String fileName = generateFileName();
        File newFile = new File(dir,fileName);
        //设置路径
        filePath = newFile.getAbsolutePath();

        mediaRecorder = new MediaRecorder();
        //设置输出文件
        mediaRecorder.setOutputFile(filePath);
        //设置MediaRecorder的音频源为麦克风
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置音频的格式
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        //设置音频的编码
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            //准备完毕之后才能开始
            mediaRecorder.prepare();
            //开始
            mediaRecorder.start();
            //准备结束
            isPrepared = true;

            if (stateListener != null) {
                stateListener.donePrepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //随机生成音频文件名
    private String generateFileName() {
        return UUID.randomUUID().toString()+".amr";
    }

    //获取音量等级
    public int getVoiceLevel(int maxLv) {
        if (isPrepared) {
            try {
                //mediaRecorder.getMaxAmplitude()获取的音量振幅在1-32767之间
                return maxLv * mediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {
                //获取音量失败
                e.printStackTrace();
            }
        }

        return 1;
    }

    //释放资源
    public void release() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        isPrepared = false;
    }

    //取消录音
    public void cancel() {
        release();
        if (filePath != null) {
            File file = new File(filePath);
            file.delete();
        }
        filePath = null;
    }
}
