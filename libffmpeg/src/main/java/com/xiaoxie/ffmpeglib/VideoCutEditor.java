package com.xiaoxie.ffmpeglib;

import android.media.MediaPlayer;
import android.os.Environment;
import android.text.TextUtils;

import com.xiaoxie.ffmpeglib.interfaces.OnCmdExecListener;

import java.io.File;
import java.io.IOException;

/**
 * 视频处理类
 * Created by xcb on 2019/7/29.
 */
public class VideoCutEditor {
    private static String defaultPath;

    public VideoCutEditor() {
        defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/xxffmpeg/";
    }

    public String getDefaultPath() {
        return defaultPath;
    }

    /**
     * 视频剪切 从第start秒开始 剪切到第end秒位置
     *
     * @param inputPath       原视频路径
     * @param outputPath      视频输出路径
     * @param start           开始剪切时间
     * @param end             结束剪切时间
     * @param useKeyframeTech 是否是使用关键帧技术
     * @param needSaveTime    是否是保留时间戳
     * @param listener        回调方法
     */
    public static void doCutVideoWithEndTime(String inputPath, String outputPath, int start, int end,
                                             boolean useKeyframeTech, boolean needSaveTime, OnCmdExecListener listener) {
        //"ffmpeg -ss 10 -t0 15 -accurate_seek -i test.mp4 -codec copy -avoid_negative_ts 1 cut.mp4";
        if (TextUtils.isEmpty(inputPath)) {
            throw new IllegalArgumentException("原始视频路径不能为空");
        }
        if (!new File(inputPath).exists()) {
            throw new IllegalArgumentException("原始视频不存在");
        }
        if (TextUtils.isEmpty(outputPath)) {
            outputPath = defaultPath;
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        //关键帧技术，ffmpeg为了加速，会使用关键帧技术，所以有时剪切出来的结果在起止时间上未必准确。
        // 通常来说，把 -ss 选项放在 -i 之前，会使用关键帧技术； 把 -ss 选项放在 -i 之后，则不使用关键帧技术。
        // 如果要使用关键帧技术又要保留时间戳，可以加上 -copyts 选项：
        if (useKeyframeTech) {
            cmdList.append("-ss");
            cmdList.append(start);
            cmdList.append("-to");
            cmdList.append(end);
            cmdList.append("-accurate_seek");
            cmdList.append("-i");
            cmdList.append(inputPath);
            cmdList.append("-c");
            cmdList.append("copy");
            if (needSaveTime) {
                cmdList.append("-copyts");
            }
            cmdList.append("-avoid_negative_ts");
            cmdList.append(1);
            cmdList.append(outputPath);
        } else {
            cmdList.append("-i");
            cmdList.append(inputPath);
            cmdList.append("-ss");
            cmdList.append(start);
            cmdList.append("-c");
            cmdList.append("copy");
            if (needSaveTime) {
                cmdList.append("-copyts");
            }
            cmdList.append("-to");
            cmdList.append(end);
            cmdList.append(outputPath);
        }
        FFmpegJniBridge.invokeCommandSync(cmdList, getVideoLength(inputPath), listener);
    }

    /**
     * 视频剪切 从第start秒开始 向后剪切length秒长度的视频
     *
     * @param inputPath       原视频路径
     * @param outputPath      视频输出路径
     * @param start           开始剪切时间
     * @param length          视频剪切的长度
     * @param useKeyframeTech 是否是使用关键帧技术
     * @param needSaveTime    是否是保留时间戳
     * @param listener        回调方法
     */
    public static void doCutVideoWithLength(String inputPath, String outputPath, int start, int length,
                                            boolean useKeyframeTech, boolean needSaveTime, OnCmdExecListener listener) {
        //"ffmpeg -i input.wmv -ss 30 -c copy -t 10 output.wmv";
        if (TextUtils.isEmpty(inputPath)) {
            throw new IllegalArgumentException("原始视频路径不能为空");
        }
        if (!new File(inputPath).exists()) {
            throw new IllegalArgumentException("原始视频不存在");
        }
        if (TextUtils.isEmpty(outputPath)) {
            outputPath = defaultPath;
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");

        if (useKeyframeTech) {
            cmdList.append("-ss");
            cmdList.append(start);
            cmdList.append("-i");
            cmdList.append(inputPath);
            cmdList.append("-t");
            cmdList.append(length);
            cmdList.append("-c");
            cmdList.append("copy");
            if (needSaveTime) {
                cmdList.append("-copyts");
            }
            cmdList.append(outputPath);
        } else {
            cmdList.append("-i");
            cmdList.append(inputPath);
            cmdList.append("-ss");
            cmdList.append(start);
            cmdList.append("-c");
            cmdList.append("copy");
            if (needSaveTime) {
                cmdList.append("-copyts");
            }
            cmdList.append("-t");
            cmdList.append(length);
            cmdList.append(outputPath);
        }
        FFmpegJniBridge.invokeCommandSync(cmdList, getVideoLength(inputPath), listener);
    }

    /**
     * 视频剪切 从视频start位置开始，向后剪切length长度
     *
     * @param inputPath       原视频路径
     * @param outputPath      视频输出路径
     * @param start           开始剪切时间点
     * @param length          结束剪切时间点
     * @param useKeyframeTech 是否是使用关键帧技术
     * @param needSaveTime    是否是保留时间戳
     * @param listener        回调方法
     */
    public static void doCutVideo(String inputPath, String outputPath, String start, String length,
                                  boolean useKeyframeTech, boolean needSaveTime, OnCmdExecListener listener) {
        //"ffmpeg -i input.wmv -ss 00:00:30.0 -c copy -t 00:00:10.0 output.wmv";
        if (TextUtils.isEmpty(inputPath)) {
            throw new IllegalArgumentException("原始视频路径不能为空");
        }
        if (!new File(inputPath).exists()) {
            throw new IllegalArgumentException("原始视频不存在");
        }
        if (TextUtils.isEmpty(outputPath)) {
            outputPath = defaultPath;
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        if (useKeyframeTech) {
            cmdList.append("-ss");
            cmdList.append(start);
            cmdList.append("-i");
            cmdList.append(inputPath);
            cmdList.append("-t");
            cmdList.append(length);
            cmdList.append("-c");
            cmdList.append("copy");
            if (needSaveTime) {
                cmdList.append("-copyts");
            }
            cmdList.append(outputPath);
        } else {
            cmdList.append("-i");
            cmdList.append(inputPath);
            cmdList.append("-ss");
            cmdList.append(start);
            cmdList.append("-c");
            cmdList.append("copy");
            if (needSaveTime) {
                cmdList.append("-copyts");
            }
            cmdList.append("-t");
            cmdList.append(length);
            cmdList.append(outputPath);
        }

        FFmpegJniBridge.invokeCommandSync(cmdList, getVideoLength(inputPath), listener);
    }

    /**
     * 重新编码的方式视频剪切 从视频start位置开始，向前剪切length长度
     *
     * @param inputPath       原视频路径
     * @param outputPath      视频输出路径
     * @param start           开始剪切时间点
     * @param length          结束剪切时间点
     * @param useKeyframeTech 是否是使用关键帧技术
     * @param needSaveTime    是否是保留时间戳
     * @param listener        回调方法
     */
    public static void doCutVideoWithRecodec(String inputPath, String outputPath, String start, String length,
                                             boolean useKeyframeTech, boolean needSaveTime, OnCmdExecListener listener) {
        //"ffmpeg -i input.wmv -ss 00:00:30.0 -c copy -t 00:00:10.0 output.wmv";
        String cmd = "-ss 0 -t %s -accurate_seek -i %s -acodec copy -vcodec copy -avoid_negative_ts 1 %s";

        if (TextUtils.isEmpty(inputPath)) {
            throw new IllegalArgumentException("原始视频路径不能为空");
        }
        if (!new File(inputPath).exists()) {
            throw new IllegalArgumentException("原始视频不存在");
        }
        if (TextUtils.isEmpty(outputPath)) {
            outputPath = defaultPath;
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        if (useKeyframeTech) {
            cmdList.append("-ss");
            cmdList.append(start);
            cmdList.append("-i");
            cmdList.append(inputPath);
            cmdList.append("-t");
            cmdList.append(length);
            cmdList.append("-c");
            cmdList.append("copy");
            if (needSaveTime) {
                cmdList.append("-copyts");
            }
            cmdList.append(outputPath);
        } else {
            cmdList.append("-i");
            cmdList.append(inputPath);
            cmdList.append("-ss");
            cmdList.append(start);
            cmdList.append("-c");
            cmdList.append("copy");
            if (needSaveTime) {
                cmdList.append("-copyts");
            }
            cmdList.append("-t");
            cmdList.append(length);
            cmdList.append(outputPath);
        }

        FFmpegJniBridge.invokeCommandSync(cmdList, getVideoLength(inputPath), listener);
    }


    /**
     * 获取视频的时长
     *
     * @return 视频时长
     */
    public static long getVideoLength(String video) {
        int length = 0;
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(video);
            mediaPlayer.prepare();
            length = mediaPlayer.getDuration();
            mediaPlayer.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return length;
    }
}
