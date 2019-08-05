package com.xiaoxie.ffmpeglib.config;

/**
 * 视频 图片相互转换
 * Created by xcb on 2019/8/5.
 */
public class Image2VideoConfig extends BaseConfig {

    /**
     * 视频的输出宽度
     */
    private int width;

    /**
     * 视频的输出高度
     */
    private int height;

    /**
     * 视频的帧率
     */
    private int rate;

    /**
     * 音频路径 为空则不带音频
     */
    private String audioPath;

    /**
     * 1 为循环读取输入 0 为一次读取输入
     */
    private int loop;

    /**
     * 指定视频时长
     */
    private int duration;


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getAudioPath() {
        return audioPath == null ? "" : audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public int getLoop() {
        return loop;
    }

    public void setLoop(int loop) {
        this.loop = loop;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
