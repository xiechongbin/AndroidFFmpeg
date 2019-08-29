package com.xiaoxie.ffmpeglib.config.waterMark;

import com.xiaoxie.ffmpeglib.config.BaseConfig;

/**
 * 文字水印配置 详细文档请打开官网链接查看
 *
 * @see <a href="http://ffmpeg.org/ffmpeg-filters.html#drawtext-1"></a>
 * Created by xcb on 2019/8/7.
 */
public class AddTextWatermarkConfig extends BaseConfig {

    public static final String WATERMARK_TIME_LTE = "lte(t\\,%s)";
    public static final String WATERMARK_TIME_GTE = "gte(t\\,%s)";
    public static final String WATERMARK_TIME_BETWEEN = "between(t\\,%s\\,%s)";
    public static final String WATERMARK_TIME_INTERVAL = "lte(mod(t\\,%s)\\,%s)";
    /**
     * 真实时间
     */
    public static final String REAL_TIME = "\'%{localtime}\'";
    /**
     * 添加的文字
     */
    private String text;
    /**
     * 文字颜色（颜色名称例如red,white,或者#EE00EE格式)
     * 官方解释:color for drawing fonts,color name or 0xRRGGBB[AA]format,default is black
     */
    private String fontColor;

    /**
     * 设置文字的透明度（0-1）
     */
    private float fontAlpha;
    /**
     * 文字在视频中的坐标
     */
    private Locations location;

    /**
     * 字体大小 默认16px
     * 官方解释：font size of the text to draw ,default value is 16
     */
    private float fontSize;


    /**
     * 文字间隔 默认为0
     */
    private float line_spacing;
    /**
     * 字体文件,是一个强制参数，必不可少
     * 官方解释:font file to be used for drawing text with proper path mandatory parameter
     */
    private String ttf;


    /**
     * 设置文字区域 0 表示默认不带背景区域 1表示设置背景区域
     */
    private int box;

    /**
     * 设置背景颜色
     */
    private String boxColor;


    /**
     * 设置背景颜色的透明度（0-1）
     */
    private float boxAlpha;

    /**
     * 添加水印时间
     * lte(t\,5) 前五秒
     * gte(t\,5) 从五秒之后开始添加
     * between(t\,10\,20) 第10秒到第20秒
     * lte(mod(t\,10)\,5) 每间隔5秒显示10秒
     */
    private String watermark_time;

    public String getText() {
        return text == null ? "" : text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFontColor() {
        return fontColor == null ? "" : fontColor;
    }

    public void setFontColor(String textColor) {
        this.fontColor = textColor;
    }

    public Locations getLocation() {
        return location == null ? new Locations("30", "30") : location;
    }

    public void setLocation(Locations location) {
        this.location = location;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public String getTtf() {
        return ttf == null ? "" : ttf;
    }

    public void setTtf(String ttf) {
        this.ttf = ttf;
    }

    public int getBox() {
        return box;
    }

    public void setBox(int box) {
        this.box = box;
    }

    public String getBoxColor() {
        return boxColor == null ? "" : boxColor;
    }

    public void setBoxColor(String boxColor) {
        this.boxColor = boxColor;
    }

    public float getFontAlpha() {
        return fontAlpha;
    }

    public void setFontAlpha(float fontAlpha) {
        this.fontAlpha = fontAlpha;
    }

    public float getBoxAlpha() {
        return boxAlpha;
    }

    public void setBoxAlpha(float boxAlpha) {
        this.boxAlpha = boxAlpha;
    }

    public float getLine_spacing() {
        return line_spacing;
    }

    public void setLine_spacing(float line_spacing) {
        this.line_spacing = line_spacing;
    }

    public String getWatermark_time() {
        return watermark_time == null ? "" : watermark_time;
    }

    public void setWatermark_time(String watermark_time) {
        this.watermark_time = watermark_time;
    }
}
