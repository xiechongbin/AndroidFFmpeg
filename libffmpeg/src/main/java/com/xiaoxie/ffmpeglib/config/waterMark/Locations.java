package com.xiaoxie.ffmpeglib.config.waterMark;

import android.view.Gravity;

/**
 * 水印在视频中的位置
 * Created by xcb on 2019-08-29.
 */
public class Locations {
    public String x;
    public String y;
    /**
     * 静态
     */
    private static final int MODE_STATIC = 1;
    /**
     * 动态
     */
    private static final int MODE_DYNAMIC = 2;
    /**
     * 随机
     */
    private static final int MODE_RANDOM = 3;

    /**
     * x坐标居中
     */
    public static final String CENTER_X = "(w-text_w)/2";
    /**
     * y坐标居中
     */
    public static final String CENTER_Y = "(h-text_h)/2";

    /**
     * 随机位置出现 %s占位符代表每隔多少秒
     */
    public static final String MODE_RANDOM_X = "if(eq(mod(t\\,%s)\\,0)\\,rand(0\\,(w-text_w))\\,x)";
    public static final String MODE_RANDOM_Y = "if(eq(mod(t\\,%s)\\,0)\\,rand(0\\,(h-text_h))\\,y)";


    public Locations(String x, String y) {
        this.x = x;
        this.y = y;
    }


    public Locations(int gravity) {
        if (gravity == Gravity.CENTER) {
            this.x = CENTER_X;
            this.y = CENTER_Y;
        } else if (gravity == Gravity.CENTER_HORIZONTAL) {
            this.x = CENTER_X;
            this.y = "0";
        } else if (gravity == Gravity.CENTER_VERTICAL) {
            this.x = "0";
            this.y = CENTER_Y;
        }
    }
}
