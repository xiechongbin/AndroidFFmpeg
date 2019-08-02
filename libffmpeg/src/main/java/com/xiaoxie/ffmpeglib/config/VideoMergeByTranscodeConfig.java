package com.xiaoxie.ffmpeglib.config;

/**
 * 视频转码合并config
 * Created by xcb on 2019/8/2.
 */
public class VideoMergeByTranscodeConfig extends VideoMergeConfig {

    /**
     * 输出视频宽度
     */
    private int width;
    /**
     * 输出视频高度
     */
    private int height;
    /**
     * 输出视频码率
     */
    private int bitRate;
    /**
     * 输出视频帧率
     */
    private int frameRate;

    /**
     * 视频转码速度
     */
    private String preset;


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

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public String getPreset() {
        return preset == null ? "" : preset;
    }

    public void setPreset(String preset) {
        this.preset = preset;
    }
}
