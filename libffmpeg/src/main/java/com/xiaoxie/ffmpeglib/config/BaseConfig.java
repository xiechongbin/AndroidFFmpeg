package com.xiaoxie.ffmpeglib.config;

import com.xiaoxie.ffmpeglib.mode.Preset;

import java.util.List;

/**
 * 视频处理基类
 * Created by xcb on 2019/8/2.
 */
public class BaseConfig {

    public static final int DEFAULT_THREAD = 8;
    /**
     * 视频输入路径
     */
    private List<String> inputPathList;

    /**
     * 输入路径
     */
    private String inputPath;


    /**
     * 输出路径
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


    public String getOutputPath() {
        return outputPath == null ? "" : outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    /**
     * @return 默认返回16线程
     */
    public int getThread() {
        return thread <= 0 ? DEFAULT_THREAD : thread;
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

    public List<String> getInputPathList() {
        return inputPathList;
    }

    public void setInputPathList(List<String> inputPathList) {
        this.inputPathList = inputPathList;
    }

    public String getInputPath() {
        return inputPath == null ? "" : inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }
}
