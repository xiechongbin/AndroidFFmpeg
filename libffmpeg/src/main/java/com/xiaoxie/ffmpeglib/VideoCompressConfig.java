package com.xiaoxie.ffmpeglib;

import com.xiaoxie.ffmpeglib.mode.Mode;
import com.xiaoxie.ffmpeglib.mode.Preset;
import com.xiaoxie.ffmpeglib.mode.Tune;

/**
 * 视频压缩配置类
 * Created by xcb on 2019/7/30.
 */
public class VideoCompressConfig {
    /**
     * 码率模式{@link Mode}
     */
    protected int mode = -1;

    /**
     * 编码速度参数{@link Preset}
     */
    private String preset = null;

    /**
     * 视频偏好设置{@link Tune}
     */
    private String tune = null;

    /**
     * 固定码率值
     */
    protected int bitrate = -1;
    /**
     * 最大码率值
     */
    protected int maxBitrate = -1;

    protected int bufSize = -1;
    /**
     * 码率等级0~51，越大
     */
    protected int crfSize = -1;

    /**
     * 视频输入路径
     */
    private String inputVideo;

    /**
     * 视频输出路径
     */
    private String outputVideo;

    /**
     * 线程数
     */
    private int thread;

    /**
     * 视频缩放比例 大于1有效
     */
    private float scale;

    /**
     * 帧率 如果没有写入帧率，就使用原视频的帧率
     */
    private int frameRate;


    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getPreset() {
        return preset == null ? "" : preset;
    }

    public void setPreset(String preset) {
        this.preset = preset;
    }

    public String getTune() {
        return tune == null ? "" : tune;
    }

    public void setTune(String tune) {
        this.tune = tune;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getMaxBitrate() {
        return maxBitrate;
    }

    public void setMaxBitrate(int maxBitrate) {
        this.maxBitrate = maxBitrate;
    }

    public int getBufSize() {
        return bufSize;
    }

    public void setBufSize(int bufSize) {
        this.bufSize = bufSize;
    }

    public int getCrfSize() {
        return crfSize;
    }

    public void setCrfSize(int crfSize) {
        this.crfSize = crfSize;
    }

    public String getInputVideo() {
        return inputVideo == null ? "" : inputVideo;
    }

    public void setInputVideo(String inputVideo) {
        this.inputVideo = inputVideo;
    }

    public String getOutputVideo() {
        return outputVideo == null ? "" : outputVideo;
    }

    public void setOutputVideo(String outputVideo) {
        this.outputVideo = outputVideo;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }
}
