package com.xiaoxie.ffmpeglib.mode;

import com.xiaoxie.ffmpeglib.VideoCompressConfig;

/**
 * Created by xcb on 2019/7/30.
 */
public class VBRMode extends VideoCompressConfig {
    
    /**
     * @param maxBitrate 最大码率
     * @param bitrate    额定码率
     */
    public VBRMode(int maxBitrate, int bitrate) {
        if (maxBitrate <= 0 || bitrate <= 0) {
            throw new IllegalArgumentException("maxBitrate or bitrate value error!");
        }
        this.maxBitrate = maxBitrate;
        this.bitrate = bitrate;
        this.mode = Mode.VBR;
    }
}
