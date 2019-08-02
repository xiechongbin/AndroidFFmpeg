package com.xiaoxie.ffmpeglib;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoxie.ffmpeglib.config.BGMConfig;
import com.xiaoxie.ffmpeglib.config.VideoCompressConfig;
import com.xiaoxie.ffmpeglib.config.VideoMergeByTranscodeConfig;
import com.xiaoxie.ffmpeglib.config.VideoMergeConfig;
import com.xiaoxie.ffmpeglib.interfaces.OnCmdExecListener;
import com.xiaoxie.ffmpeglib.mode.Mode;
import com.xiaoxie.ffmpeglib.mode.Preset;
import com.xiaoxie.ffmpeglib.utils.VideoUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 视频处理类
 * Created by xcb on 2019/7/29.
 */
public class VideoHandleEditor {

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
            outputPath = VideoUtils.defaultPath + "/" + System.currentTimeMillis() + ".mp4";
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
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
        FFmpegJniBridge.invokeCommandSync(cmdList, VideoUtils.getVideoLength(inputPath), listener);
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
            outputPath = VideoUtils.defaultPath + "/" + System.currentTimeMillis() + ".mp4";
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
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
        FFmpegJniBridge.invokeCommandSync(cmdList, VideoUtils.getVideoLength(inputPath), listener);
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
            outputPath = VideoUtils.defaultPath + "/" + System.currentTimeMillis() + ".mp4";
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
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

        FFmpegJniBridge.invokeCommandSync(cmdList, VideoUtils.getVideoLength(inputPath), listener);
    }


    public static void compressVideo(String inputVideo, String outputVideo, String bitrate, int quality, String speed, OnCmdExecListener listener) {
        // String cmd = "ffmpeg -y -i /storage/emulated/0/in.mp4 -b 2097k -r 30 -vcodec libx264 -preset superfast /storage/emulated/0/a.mp4";
        if (TextUtils.isEmpty(inputVideo)) {
            throw new IllegalArgumentException("原始视频路径不能为空");
        }
        if (!new File(inputVideo).exists()) {
            throw new IllegalArgumentException("原始视频不存在");
        }
        if (TextUtils.isEmpty(outputVideo)) {
            outputVideo = VideoUtils.defaultPath + "/" + System.currentTimeMillis() + ".mp4";
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-i");
        cmdList.append(inputVideo);
        cmdList.append("-b");
        cmdList.append(bitrate);
        cmdList.append("-r");
        cmdList.append(quality);
        cmdList.append("-vcodec");
        cmdList.append("libx264");
        cmdList.append("-preset");
        cmdList.append(speed);
        cmdList.append(outputVideo);

        FFmpegJniBridge.invokeCommandSync(cmdList, VideoUtils.getVideoLength(inputVideo), listener);
    }

    /**
     * 视频压缩
     *
     * @param config   视频压缩配置
     * @param listener 回调监听器
     */
    public static void compressVideo(VideoCompressConfig config, OnCmdExecListener listener) {
        if (config == null) {
            throw new IllegalArgumentException("compressConfig 为空");
        }
        if (TextUtils.isEmpty(config.getInputVideo())) {
            throw new IllegalArgumentException("原始视频路径不能为空");
        }
        if (!new File(config.getInputVideo()).exists()) {
            throw new IllegalArgumentException("原始视频不存在");
        }
        if (TextUtils.isEmpty(config.getOutputVideo())) {
            config.setOutputVideo(VideoUtils.defaultPath + "/" + System.currentTimeMillis() + ".mp4");
        }

        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-threads");
        cmdList.append(config.getThread());
        cmdList.append("-i");
        cmdList.append(config.getInputVideo());
        cmdList.append("-c:v");
        cmdList.append("libx264");
        if (config.getMode() == Mode.VBR) {
            cmdList.append("-x264opts");
            cmdList.append("bitrate=" + config.getBitrate() + ":vbv-maxrate=" + config.getMaxBitrate());
        } else if (config.getMode() == Mode.CBR) {
            cmdList.append("-x264opts");
            cmdList.append("bitrate=" + config.getBitrate() + ":vbv-bufsize=" + config.getBufSize() + ":nal_hrd=cbr");
        }
        if (config.getMode() == Mode.AUTO_VBR && config.getCrfSize() > 0) {
            cmdList.append("-crf");
            cmdList.append(config.getCrfSize());
        } else {
            cmdList.append("-crf");
            cmdList.append(28);
        }
        if (!TextUtils.isEmpty(config.getPreset())) {
            cmdList.append("-preset");
            cmdList.append(config.getPreset());
        } else {
            cmdList.append("-preset");
            cmdList.append(Preset.ULTRAFAST);
        }
        cmdList.append("-c:a");
        cmdList.append("libfdk_aac");
        if (config.getMode() != Mode.CBR) {
            cmdList.append("-vbr");
            cmdList.append(4);
        }
        if (config.getFrameRate() > 0) {
            cmdList.append("-r");
            cmdList.append(config.getFrameRate());
        }
        String scaleWH = VideoUtils.getScaleWH(config.getInputVideo(), config.getScale());
        if (!TextUtils.isEmpty(scaleWH)) {
            cmdList.append("-s");
            cmdList.append(scaleWH);
        }

        cmdList.append(config.getOutputVideo());
        Log.d("ffmpeg_log", cmdList.toString());

        FFmpegJniBridge.invokeCommandSync(cmdList, VideoUtils.getVideoLength(config.getInputVideo()), listener);
    }

    /**
     * 无损合并视频 对需要合并的视频格式各项参数有严格要求，需要分辨率，帧率，码率都相同 否则合并失败
     *
     * @param config   视频合并配置{@link VideoMergeConfig}
     * @param listener 回调监听
     */
    public static void mergeVideosLossLess(VideoMergeConfig config, OnCmdExecListener listener) {
        if (config == null) {
            throw new IllegalArgumentException("config 为空");
        }
        if (config.getInputVideoList() == null || config.getInputVideoList().size() < 1) {
            throw new IllegalArgumentException("输入视频错误");
        }
        if (TextUtils.isEmpty(config.getOutputVideo())) {
            config.setOutputVideo(VideoUtils.defaultPath + "/" + System.currentTimeMillis() + ".mp4");
        }
        String filePath = VideoUtils.createMergeFile(config.getInputVideoList());
        if (!new File(filePath).exists()) {
            throw new IllegalArgumentException("视频合并txt文档不存在");
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-f");
        cmdList.append("concat");
        cmdList.append("-safe");
        cmdList.append("0");
        cmdList.append("-i");
        cmdList.append(filePath);
        cmdList.append("-c");
        cmdList.append("copy");
        cmdList.append(config.getOutputVideo());

        Log.d("ffmpeg_log", cmdList.toString());

        int videoLength = 0;
        for (String s : config.getInputVideoList()) {
            videoLength += VideoUtils.getVideoLength(s);
        }

        FFmpegJniBridge.invokeCommandSync(cmdList, videoLength, listener);
    }

    /**
     * 视频转码合并
     *
     * @param config   视频合并配置 {@link VideoMergeConfig}
     * @param listener 回调监听
     */
    public static void mergeVideoByTranscoding(VideoMergeByTranscodeConfig config, OnCmdExecListener listener) {
        if (config == null) {
            throw new IllegalArgumentException("config 为空");
        }
        if (config.getInputVideoList() == null || config.getInputVideoList().size() < 1) {
            throw new IllegalArgumentException("输入视频错误");
        }
        if (TextUtils.isEmpty(config.getOutputVideo())) {
            config.setOutputVideo(VideoUtils.defaultPath + "/" + System.currentTimeMillis() + ".mp4");
        }
        List<String> videos = config.getInputVideoList();

        //检测是否有无音轨视频
        boolean isNoAudioTrack = false;
        for (String video : videos) {
            MediaExtractor mediaExtractor = new MediaExtractor();
            try {
                mediaExtractor.setDataSource(video);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            int at = VideoUtils.selectAudioTrack(mediaExtractor);
            if (at == -1) {
                isNoAudioTrack = true;
                mediaExtractor.release();
                break;
            }
            mediaExtractor.release();
        }

        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        //添加输入视频路径
        for (String video : videos) {
            cmdList.append("-i");
            cmdList.append(video);
        }
        cmdList.append("-filter_complex");
        StringBuilder filter_complex = new StringBuilder();
        for (int i = 0; i < videos.size(); i++) {
            filter_complex.append("[")
                    .append(i)
                    .append(":v]")
                    .append("scale=")
                    .append(config.getWidth())
                    .append(":")
                    .append(config.getHeight())
                    .append(",")
                    .append("setdar=")
                    .append(config.getWidth())
                    .append("/")
                    .append(config.getHeight())
                    .append("[outv")
                    .append(i)
                    .append("]")
                    .append(";");
        }
        for (int i = 0; i < videos.size(); i++) {
            filter_complex.append("[outv").append(i).append("]");
        }
        filter_complex.append("concat=n=").append(videos.size()).append(":v=1:a=0[outv]");
        //是否添加音轨
        if (!isNoAudioTrack) {
            filter_complex.append(";");
            for (int i = 0; i < videos.size(); i++) {
                filter_complex.append("[").append(i).append(":a]");
            }
            filter_complex.append("concat=n=").append(videos.size()).append(":v=0:a=1[outa]");
        }
        cmdList.append(filter_complex.toString());
        cmdList.append("-map");
        cmdList.append("[outv]");
        if (!isNoAudioTrack) {
            cmdList.append("-map");
            cmdList.append("[outa]");
        }
        cmdList.append("-r");
        cmdList.append(config.getFrameRate() > 0 ? config.getFrameRate() : 30);
        cmdList.append("-b");
        cmdList.append(config.getBitRate() + "M");
        cmdList.append("-preset");
        cmdList.append(TextUtils.isEmpty(config.getPreset()) ? Preset.ULTRAFAST : config.getPreset());
        cmdList.append(config.getOutputVideo());

        int videoLength = 0;
        for (String s : config.getInputVideoList()) {
            videoLength += VideoUtils.getVideoLength(s);
        }
        Log.d("ffmpeg_log", cmdList.toString());
        FFmpegJniBridge.invokeCommandSync(cmdList, videoLength, listener);
    }

    /**
     * 给视频添加背景音乐
     *
     * @param config   {@link BGMConfig}
     * @param listener 回调监听
     */
    public static void addBackgroundMusic(BGMConfig config, OnCmdExecListener listener) {
        if (config == null) {
            throw new IllegalArgumentException("config 为空");
        }
        if (config.getInputVideo() == null) {
            throw new IllegalArgumentException("输入视频错误");
        }
        if (config.getAudioPath() == null) {
            throw new IllegalArgumentException("找不到音频文件");
        }
        if (TextUtils.isEmpty(config.getOutputVideo())) {
            config.setOutputVideo(VideoUtils.defaultPath + "/" + System.currentTimeMillis() + ".mp4");
        }
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(config.getInputVideo());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int at = VideoUtils.selectAudioTrack(mediaExtractor);

        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-i");
        cmdList.append(config.getInputVideo());
        if (at == -1) {
            int vt = VideoUtils.selectVideoTrack(mediaExtractor);
            float duration = (float) mediaExtractor.getTrackFormat(vt).getLong(MediaFormat.KEY_DURATION) / 1000 / 1000;
            cmdList.append("-ss");
            cmdList.append("0");
            cmdList.append("-t");
            cmdList.append(duration);
            cmdList.append("-i");
            cmdList.append(config.getAudioPath());
            cmdList.append("-acodec");
            cmdList.append("copy");
            cmdList.append("-vcodec");
            cmdList.append("copy");
        } else {
            cmdList.append("-i");
            cmdList.append(config.getAudioPath());
            cmdList.append("-filter_complex");
            cmdList.append("[0:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume="
                    + config.getOriginalVolume()
                    + "[a0];[1:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume="
                    + config.getNewAudioVolume() + "[a1];[a0][a1]amix=inputs=2:duration=first[aout]");
            cmdList.append("-map");
            cmdList.append("[aout]");
            cmdList.append("-ac");
            cmdList.append("2");
            cmdList.append("-c:v");
            cmdList.append("copy");
            cmdList.append("-map");
            cmdList.append("0:v:0");
        }
        cmdList.append(config.getOutputVideo());
        mediaExtractor.release();
        Log.d("ffmpeg_log", cmdList.toString());
        FFmpegJniBridge.invokeCommandSync(cmdList, VideoUtils.getVideoLength(config.getInputVideo()), listener);
    }
}
