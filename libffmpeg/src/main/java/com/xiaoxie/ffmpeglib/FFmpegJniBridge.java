package com.xiaoxie.ffmpeglib;

/**
 * Created by xcb on 2019/7/25.
 */
public class FFmpegJniBridge {
    static {
        System.loadLibrary("ffmpeg_jni");
        System.loadLibrary("avcodec");
        System.loadLibrary("avdevice");
        System.loadLibrary("avfilter");
        System.loadLibrary("avformat");
        System.loadLibrary("avutil");
        System.loadLibrary("postproc");
        System.loadLibrary("avcodec");
        System.loadLibrary("swresample");
        System.loadLibrary("swscale");
        System.loadLibrary("x264");
    }

    private static OnCmdExecListner listener;
    private static long sDuration;

    /**
     * 获取ffmpeg版本
     */
    public static native String getVersionInfo();

    /**
     * 获取ffmpeg编译相关信息
     */
    public static native String getConfigInfo();

    /**
     * 执行ffmpeg命令
     */
    public static native int invokeCommands(String[] commands);

    /**
     * 异步的方式执行ffmpeg 命令
     */
    public static native int invokeCommandSync(String[] commands);


    public static void invokeCommandSync(String[] commands, long duration, OnCmdExecListner onCmdExecListner) {
        listener = onCmdExecListner;
        sDuration = duration;
        invokeCommandSync(commands);
    }

    /**
     * 这个方法将在c语言中被调用
     */
    public static void onExecuted(int ret) {
        if (listener != null) {
            if (ret == 0) {
                listener.onProgress(sDuration);
                listener.onSuccess();
            } else {
                listener.onFailure();
            }
        }
    }

    /**
     * FFmpeg执行进度回调，由C代码调用
     */
    public static void onProgress(float progress) {
        if (listener != null) {
            if (sDuration != 0) {
                listener.onProgress(progress / (sDuration / 1000) * 0.95f);
            }
        }
    }

    public void test(int i) {
        System.out.println("i________________ = " + i);
    }
}
