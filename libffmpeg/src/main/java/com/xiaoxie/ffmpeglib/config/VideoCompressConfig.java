package com.xiaoxie.ffmpeglib.config;

import com.xiaoxie.ffmpeglib.mode.Mode;
import com.xiaoxie.ffmpeglib.mode.Tune;

/**
 * 视频压缩配置类
 * Created by xcb on 2019/7/30.
 */
public class VideoCompressConfig extends BaseConfig {
    /**
     * 码率模式{@link Mode}
     */
    protected int mode = -1;

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

    /**
     * 缓冲区大小
     */
    protected int bufSize = -1;
    /**
     * 码率等级0~51，越大
     */
    protected int crfSize = -1;

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
