package com.xiaoxie.ffmpeglib.config;

/**
 * 音乐相关处理
 * Created by xcb on 2019/8/2.
 */
public class BGMConfig extends BaseConfig {
    /**
     * 音频路径
     */
    private String audioPath;
    /**
     * 原始视频音量大小（0-1）
     */
    private float originalVolume;
    /**
     * 新添加的音频音量大小
     */
    private float newAudioVolume;

    public String getAudioPath() {
        return audioPath == null ? "" : audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public float getOriginalVolume() {
        return originalVolume;
    }

    public void setOriginalVolume(float originalVolume) {
        this.originalVolume = originalVolume;
    }

    public float getNewAudioVolume() {
        return newAudioVolume;
    }

    public void setNewAudioVolume(float newAudioVolume) {
        this.newAudioVolume = newAudioVolume;
    }
}
