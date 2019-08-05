package com.xiaoxie.ffmpeglib.config;

/**
 * 音视频倒序
 * Created by xcb on 2019/8/5.
 */
public class ReverseConfig extends BaseConfig {
    /**
     * 视频是否倒序
     */
    private boolean isVideoReverse;
    /**
     * 音频是否倒序
     */
    private boolean isAudioReverse;

    public boolean isVideoReverse() {
        return isVideoReverse;
    }

    public void setVideoReverse(boolean videoReverse) {
        isVideoReverse = videoReverse;
    }

    public boolean isAudioReverse() {
        return isAudioReverse;
    }

    public void setAudioReverse(boolean audioReverse) {
        isAudioReverse = audioReverse;
    }
}
