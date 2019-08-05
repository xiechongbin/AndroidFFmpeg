package com.xiaoxie.ffmpeglib.config;

import com.xiaoxie.ffmpeglib.mode.PTS;

/**
 * Created by xcb on 2019/8/5.
 */
public class ChangePTSConfig extends BaseConfig {
    /**
     * 倍速比例（0.25-4）
     */
    private float times;

    /**
     * 变速类型 {@link PTS}
     */
    private PTS ptsType;

    public float getTimes() {
        return times;
    }

    public void setTimes(float times) {
        this.times = times;
    }

    public PTS getPtsType() {
        return ptsType;
    }

    public void setPtsType(PTS ptsType) {
        this.ptsType = ptsType;
    }
}
