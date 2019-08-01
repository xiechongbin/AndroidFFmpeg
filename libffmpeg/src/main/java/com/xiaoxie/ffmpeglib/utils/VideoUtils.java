package com.xiaoxie.ffmpeglib.utils;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by xcb on 2019/7/31.
 */
public class VideoUtils {
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
}
