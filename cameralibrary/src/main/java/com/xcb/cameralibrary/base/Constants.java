package com.xcb.cameralibrary.base;

/**
 * 摄像头常量相关
 * Created by xcb on 2019-09-12.
 */
public interface Constants {
    AspectRatio DEFAULT_ASPECT_RATIO = AspectRatio.of(4, 3);

    /**
     * 后置摄像头
     */
    int FACING_BACK = 0;

    /**
     * 前置摄像头
     */
    int FACING_FRONT = 1;

    /**
     * 闪光灯关闭
     */
    int FLASH_OFF = 0;

    /**
     * 闪光灯打开
     */
    int FLASH_ON = 1;

    /**
     * torch
     */
    int FLASH_TORCH = 2;

    /**
     * 自动
     */
    int FLASH_AUTO = 3;

    /**
     * 红眼模式
     */
    int FLASH_RED_EYE = 4;

    /**
     * 横屏90度
     */
    int LANDSCAPE_90 = 90;
    /**
     * 横屏270度
     */
    int LANDSCAPE_270 = 270;
}
