package com.xiaoxie.ffmpeglib.mode;

/**
 * 视频偏好设置，参数主要配合视频类型和视觉优化的参数，或特别的情况。
 * 如果视频的内容符合其中一个可用的调整值又或者有其中需要，则可以使用此选项，否则建议不使用
 * Created by xcb on 2019/7/30.
 */
public class Tune {

    /**
     * 电影类型，对视频的质量非常严格时使用该选项
     */
    public static final String FILM = "film";

    /**
     * 动画片，压缩的视频是动画片时使用该选项
     */
    public static final String ANIMATION = "animation";

    /**
     * 颗粒物很重，该选项适用于颗粒感很重的视频
     */
    public static final String GRAIN = "grain";

    /**
     * 静态图像，该选项主要用于静止画面比较多的视频
     */
    public static final String STILLIMAGE = "stillimage";

    /**
     * 提高psnr，该选项编码出来的视频psnr比较高
     */
    public static final String PSNR = "psnr";

    /**
     * 提高ssim，该选项编码出来的视频ssim比较高
     */
    public static final String SSIM = "ssim";

    /**
     * 快速解码，该选项有利于快速解码
     */
    public static final String FASTDECODE = "fastdecode";

    /**
     * 零延迟，该选项主要用于视频直播
     */
    public static final String ZEROLATENCY = "zerolatency";

}
