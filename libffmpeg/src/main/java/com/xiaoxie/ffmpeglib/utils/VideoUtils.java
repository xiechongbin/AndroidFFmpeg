package com.xiaoxie.ffmpeglib.utils;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Locale;

/**
 * Created by xcb on 2019/7/31.
 */
public class VideoUtils {
    public static final String TAG = "xxffmpeg";
    public static String defaultPath;

    public static void initDefaultPath() {
        defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/xxffmpeg/";
        File file = new File(defaultPath);
        try {
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param videoPath 视频路径
     * @param scale     缩放比例 大于1有效
     * @return 格式化命令
     */
    public static String getScaleWH(String videoPath, float scale) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(videoPath);
        String s = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        String videoW = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String videoH = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        int srcW = Integer.valueOf(videoW);
        int srcH = Integer.valueOf(videoH);
        int newSrcWidth = (int) (srcW / scale);
        int newSrcHeight = (int) (srcH / scale);
        if (newSrcHeight % 2 != 0) {
            newSrcHeight += 1;
        }
        if (newSrcWidth % 2 != 0) {
            newSrcWidth += 1;
        }
        switch (s) {
            case "90":
            case "270":
                return String.format(Locale.ENGLISH, "%dx%d", newSrcHeight, newSrcWidth);

            case "0":
            case "180":
            case "360":
                return String.format(Locale.ENGLISH, "%dx%d", newSrcWidth, newSrcHeight);
            default:
                return "";
        }
    }

    /**
     * @param video 视频输入路径
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

    /**
     * 创建视频合并txt文档
     *
     * @param videos 视频集合
     * @return 文件路径
     */
    public static String createMergeFile(List<String> videos) {
        String fileName = "ffmpeg_concat.txt";
        //先创建文件夹
        if (TextUtils.isEmpty(defaultPath) || !new File(defaultPath).exists()) {
            initDefaultPath();
        }
        try {
            String filePath = defaultPath + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                if (file.delete()) {
                    Log.d(TAG, "清除旧文件成功");
                }
            }
            File newFile = new File(filePath);
            if (newFile.createNewFile()) {
                Log.d(TAG, "创建新文件成功");
            }
            StringBuilder builder = new StringBuilder();
            for (String video : videos) {
                builder.append("file ").append(video).append("\r\n");
            }

            RandomAccessFile accessFile = new RandomAccessFile(newFile, "rw");
            accessFile.seek(file.length());
            accessFile.write(builder.toString().getBytes());
            accessFile.close();
            return filePath;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
