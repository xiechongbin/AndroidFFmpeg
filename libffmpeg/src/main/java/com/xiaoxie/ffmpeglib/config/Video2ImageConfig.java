package com.xiaoxie.ffmpeglib.config;

/**
 * 视频 图片相互转换
 * Created by xcb on 2019/8/5.
 */
public class Video2ImageConfig extends BaseConfig {

    /**
     * 图片输出宽度
     */
    private int width;

    /**
     * 图片输出高度
     */
    private int height;

    /**
     * 每秒输出的图片张数
     */
    private int rate;

    /**
     * 输出图片路径（2-10)数字越大，质量月低
     */
    private int imageQuality;

    /**
     * 图片输出格式化
     */
    private String outputNameFormat;

    /**
     * 图片格式 png 或者jpg png输出文件会大很多
     */
    private String imageSuffix;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getImageQuality() {
        return imageQuality;
    }

    public void setImageQuality(int imageQuality) {
        this.imageQuality = imageQuality;
    }

    public String getImageSuffix() {
        return imageSuffix == null ? "" : imageSuffix;
    }

    public void setImageSuffix(String imageSuffix) {
        this.imageSuffix = imageSuffix;
    }

    public String getOutputNameFormat() {
        return outputNameFormat == null ? "" : outputNameFormat;
    }

    public void setOutputNameFormat(String outputNameFormat) {
        this.outputNameFormat = outputNameFormat;
    }
}
