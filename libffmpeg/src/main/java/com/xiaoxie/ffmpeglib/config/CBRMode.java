package com.xiaoxie.ffmpeglib.config;

import com.xiaoxie.ffmpeglib.mode.Mode;

/**
 * Created by xcb on 2019/7/30.
 */
public class CBRMode extends VideoCompressConfig {
    /**
     * @param bufSize 缓冲区大小
     * @param bitrate 固定码率值
     */
    public CBRMode(int bufSize, int bitrate) {
        if (bufSize <= 0 || bitrate <= 0) {
            throw new IllegalArgumentException("bufSize or bitrate value error!");
        }
        this.bufSize = bufSize;
        this.bitrate = bitrate;
        this.mode = Mode.CBR;
    }
}
