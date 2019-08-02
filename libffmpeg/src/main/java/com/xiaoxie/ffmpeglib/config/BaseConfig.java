package com.xiaoxie.ffmpeglib.config;

import com.xiaoxie.ffmpeglib.mode.Preset;

import java.util.List;

/**
 * 视频处理基类
 * Created by xcb on 2019/8/2.
 */
public class BaseConfig {
    /**
     * 视频输入路径
     */
    private List<String> inputVideoList;

    /**
     * 视频输入路径
     */
    private String inputVideo;


    /**
     * 视频输出路径
     */
    private String outputPath;

    /**
     * 线程数
     */
    private int thread;

    /**
     * 编码速度参数{@link Preset}
     */
    private String preset = null;


    public List<String> getInputVideoList() {
        return inputVideoList;
    }

    public void setInputVideoList(List<String> inputVideoList) {
        this.inputVideoList = inputVideoList;
    }

    public String getInputVideo() {
        return inputVideo == null ? "" : inputVideo;
    }

    public String getOutputPath() {
        return outputPath == null ? "" : outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public void setInputVideo(String inputVideo) {
        this.inputVideo = inputVideo;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public String getPreset() {
        return preset == null ? "" : preset;
    }

    public void setPreset(String preset) {
        this.preset = preset;
    }
}
