package com.xiaoxie.ffmpeglib.config;

/**
 * 文字水印配置 详细文档请打开官网链接查看
 *
 * @see <a href="http://ffmpeg.org/ffmpeg-filters.html#drawtext-1"></a>
 * Created by xcb on 2019/8/7.
 */
public class AddTextWatermarkConfig extends BaseConfig {
    /**
     * 添加的文字
     */
    private String text;
    /**
     * 文字颜色（颜色名称例如red,white,或者#EE00EE格式)
     * 官方解释:color for drawing fonts,color name or 0xRRGGBB[AA]format,default is black
     */
    private String textColor;
    /**
     * 文字在视频中的坐标x
     */
    private int locationX;
    /**
     * 文字在视频中的坐标Y
     */
    private int locationY;
    /**
     * 字体大小 默认16px
     * 官方解释：font size of the text to draw ,default value is 16
     */
    private float textSize;
    /**
     * 字体文件,是一个强制参数，必不可少
     * 官方解释:font file to be used for drawing text with proper path mandatory parameter
     */
    private String ttf;

    public String getText() {
        return text == null ? "" : text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextColor() {
        return textColor == null ? "" : textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public int getLocationX() {
        return locationX;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public String getTtf() {
        return ttf == null ? "" : ttf;
    }

    public void setTtf(String ttf) {
        this.ttf = ttf;
    }
}
