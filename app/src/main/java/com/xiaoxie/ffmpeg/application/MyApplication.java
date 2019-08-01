package com.xiaoxie.ffmpeg.application;

import android.app.Application;

import com.xiaoxie.ffmpeglib.utils.VideoUtils;

/**
 * Created by xcb on 2019/8/1.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        VideoUtils.initDefaultPath();
    }
}
