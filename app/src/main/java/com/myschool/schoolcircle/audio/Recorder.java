package com.myschool.schoolcircle.audio;

/**
 * Created by Mr.R on 2016/6/22.
 */
public class Recorder {
    private float time;
    private String filePath;

    public Recorder(float time, String filePath) {
        this.time = time;
        this.filePath = filePath;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public float getTime() {
        return time;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
