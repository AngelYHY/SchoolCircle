package com.myschool.schoolcircle.config;

/**
 * Created by Mr.R on 2016/8/27.
 */
public class SettingConfig {
    //-1关闭，0打开
    private int notification;//通知
    private int voice;//声音
    private int vibrator;//震动
    private int messageNotify;//消息免打扰
    private int shack;//自动刷新

    public SettingConfig () {

    }

    public SettingConfig(int notification, int voice, int vibrator,
                         int messageNotify, int shack) {
        this.notification = notification;
        this.voice = voice;
        this.vibrator = vibrator;
        this.messageNotify = messageNotify;
        this.shack = shack;
    }

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }

    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }

    public int getVibrator() {
        return vibrator;
    }

    public void setVibrator(int vibrator) {
        this.vibrator = vibrator;
    }

    public int getMessageNotify() {
        return messageNotify;
    }

    public void setMessageNotify(int messageNotify) {
        this.messageNotify = messageNotify;
    }

    public int getShack() {
        return shack;
    }

    public void setShack(int shack) {
        this.shack = shack;
    }
}
