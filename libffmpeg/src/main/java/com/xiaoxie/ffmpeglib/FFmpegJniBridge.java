package com.xiaoxie.ffmpeglib;

import android.util.Log;
import android.view.Surface;

import com.xiaoxie.ffmpeglib.interfaces.OnCmdExecListener;

/**
 * Created by xcb on 2019/7/25.
 */
public class FFmpegJniBridge {
    private static final String TAG = FFmpegJniBridge.class.getSimpleName();

    static {
        System.loadLibrary("ffmpeg_jni");
        System.loadLibrary("ffmpeg_core");
    }

    private static OnCmdExecListener listener;
    private static long sDuration;
    private static String outputPath;

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

    /**
     * ffmpeg 播放视频
     */
    public static native int render(String videoPath, Surface surface);

    public static native int playSound(String path);


    public static void invokeCommandSync(CmdList cmdList, long duration, OnCmdExecListener onCmdExecListener) {
        Log.d("ffmpeg_log", cmdList.toString());
        listener = onCmdExecListener;
        sDuration = duration;
        String[] commands = cmdList.toArray(new String[cmdList.size()]);
        invokeCommandSync(commands);
    }

    public static void invokeCommandSync(String[] commands, long duration, OnCmdExecListener onCmdExecListener) {
        listener = onCmdExecListener;
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
                listener.onSuccess(outputPath);
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
        System.out.println("c_to_java test i = " + i);
    }
}
