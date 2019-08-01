package com.xiaoxie.ffmpeglib;

import java.util.List;

/**
 * 视频合并配置类
 * Created by xcb on 2019/8/1.
 */
public class VideoMergeConfig {
    /**
     * 需要合并的的视频集合
     */
    private List<String> inputVideoList;

    /**
     * 输出视频
     */
    private String outputVideo;


    public List<String> getInputVideoList() {
        return inputVideoList;
    }

    public void setInputVideoList(List<String> inputVideoList) {
        this.inputVideoList = inputVideoList;
    }

    public String getOutputVideo() {
        return outputVideo == null ? "" : outputVideo;
    }

    public void setOutputVideo(String outputVideo) {
        this.outputVideo = outputVideo;
    }
}
