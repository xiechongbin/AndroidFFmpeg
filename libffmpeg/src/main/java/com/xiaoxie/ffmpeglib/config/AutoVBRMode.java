package com.xiaoxie.ffmpeglib.config;

import com.xiaoxie.ffmpeglib.mode.Mode;

/**
 * Created by xcb on 2019/7/30.
 */
public class AutoVBRMode extends VideoCompressConfig {

    public AutoVBRMode() {
        this.mode = Mode.AUTO_VBR;
    }

    /**
     * @param crfSize 压缩等级，0~51，值越大约模糊，视频越小，建议18~28.
     */
    public AutoVBRMode(int crfSize) {
        if (crfSize < 0 || crfSize > 51) {
            throw new IllegalArgumentException("crfSize 在0~51之间");
        }
        this.crfSize = crfSize;
        this.mode = Mode.AUTO_VBR;
    }
}
