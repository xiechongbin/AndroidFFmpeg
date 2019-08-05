package com.xiaoxie.ffmpeglib;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.text.TextUtils;

import com.xiaoxie.ffmpeglib.config.BGMConfig;
import com.xiaoxie.ffmpeglib.config.BaseConfig;
import com.xiaoxie.ffmpeglib.config.ChangePTSConfig;
import com.xiaoxie.ffmpeglib.config.Image2VideoConfig;
import com.xiaoxie.ffmpeglib.config.ReverseConfig;
import com.xiaoxie.ffmpeglib.config.Video2ImageConfig;
import com.xiaoxie.ffmpeglib.config.VideoCompressConfig;
import com.xiaoxie.ffmpeglib.config.VideoMergeByTranscodeConfig;
import com.xiaoxie.ffmpeglib.config.VideoMergeConfig;
import com.xiaoxie.ffmpeglib.interfaces.OnCmdExecListener;
import com.xiaoxie.ffmpeglib.mode.Format;
import com.xiaoxie.ffmpeglib.mode.Mode;
import com.xiaoxie.ffmpeglib.mode.PTS;
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
            throw new IllegalArgumentException("视频输出路径不能为空");
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-threads");
        cmdList.append(BaseConfig.DEFAULT_THREAD);
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
            throw new IllegalArgumentException("视频输出路径不能为空");
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-threads");
        cmdList.append(BaseConfig.DEFAULT_THREAD);
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
            throw new IllegalArgumentException("视频输出路径不能为空");
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-threads");
        cmdList.append(BaseConfig.DEFAULT_THREAD);
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
            throw new IllegalArgumentException("视频输出路径不能为空");
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-threads");
        cmdList.append(BaseConfig.DEFAULT_THREAD);
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
        if (TextUtils.isEmpty(config.getInputPath())) {
            throw new IllegalArgumentException("原始视频路径不能为空");
        }
        if (!new File(config.getInputPath()).exists()) {
            throw new IllegalArgumentException("原始视频不存在");
        }
        if (TextUtils.isEmpty(config.getOutputPath())) {
            throw new IllegalArgumentException("视频输出路径不能为空");
        }

        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-threads");
        cmdList.append(config.getThread());
        cmdList.append("-i");
        cmdList.append(config.getInputPath());
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
        String scaleWH = VideoUtils.getScaleWH(config.getInputPath(), config.getScale());
        if (!TextUtils.isEmpty(scaleWH)) {
            cmdList.append("-s");
            cmdList.append(scaleWH);
        }
        cmdList.append(config.getOutputPath());

        FFmpegJniBridge.invokeCommandSync(cmdList, VideoUtils.getVideoLength(config.getInputPath()), listener);
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
        if (config.getInputPathList() == null || config.getInputPathList().size() < 1) {
            throw new IllegalArgumentException("输入视频错误");
        }
        if (TextUtils.isEmpty(config.getOutputPath())) {
            throw new IllegalArgumentException("视频输出路径不能为空");
        }
        String filePath = VideoUtils.createMergeFile(config.getInputPathList());
        if (!new File(filePath).exists()) {
            throw new IllegalArgumentException("视频合并txt文档不存在");
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-threads");
        cmdList.append(config.getThread());
        cmdList.append("-f");
        cmdList.append("concat");
        cmdList.append("-safe");
        cmdList.append("0");
        cmdList.append("-i");
        cmdList.append(filePath);
        cmdList.append("-c");
        cmdList.append("copy");
        cmdList.append(config.getOutputPath());

        int videoLength = 0;
        for (String s : config.getInputPathList()) {
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
        if (config.getInputPathList() == null || config.getInputPathList().size() < 1) {
            throw new IllegalArgumentException("输入视频错误");
        }
        if (TextUtils.isEmpty(config.getOutputPath())) {
            throw new IllegalArgumentException("视频输出路径不能为空");
        }
        List<String> videos = config.getInputPathList();

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
        cmdList.append("-threads");
        cmdList.append(config.getThread());
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
        cmdList.append(config.getOutputPath());

        int videoLength = 0;
        for (String s : config.getInputPathList()) {
            videoLength += VideoUtils.getVideoLength(s);
        }

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
        if (config.getInputPath() == null) {
            throw new IllegalArgumentException("输入视频错误");
        }
        if (config.getAudioPath() == null) {
            throw new IllegalArgumentException("找不到音频文件");
        }
        if (TextUtils.isEmpty(config.getOutputPath())) {
            throw new IllegalArgumentException("视频输出路径不能为空");
        }
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(config.getInputPath());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int at = VideoUtils.selectAudioTrack(mediaExtractor);

        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-threads");
        cmdList.append(config.getThread());
        cmdList.append("-i");
        cmdList.append(config.getInputPath());
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
        cmdList.append(config.getOutputPath());
        mediaExtractor.release();

        FFmpegJniBridge.invokeCommandSync(cmdList, VideoUtils.getVideoLength(config.getInputPath()), listener);
    }

    /**
     * 提取视频
     *
     * @param config   {@link BaseConfig}
     * @param listener 回调监听
     */
    public static void separateVideo(BaseConfig config, OnCmdExecListener listener) {

        if (config == null) {
            throw new IllegalArgumentException("config 为空");
        }
        if (config.getInputPath() == null) {
            throw new IllegalArgumentException("输入视频错误");
        }
        if (TextUtils.isEmpty(config.getOutputPath())) {
            throw new IllegalArgumentException("视频输出路径不能为空");
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-threads");
        cmdList.append(config.getThread());
        cmdList.append("-i");
        cmdList.append(config.getInputPath());
        cmdList.append("-vcodec");
        cmdList.append("copy");
        cmdList.append("-an");

        cmdList.append(config.getOutputPath());
        FFmpegJniBridge.invokeCommandSync(cmdList, VideoUtils.getVideoLength(config.getInputPath()), listener);
    }

    /**
     * 提取音频
     *
     * @param config   {@link BaseConfig}
     * @param format   输出音频封装格式
     * @param listener 回调监听
     */
    public static void separateAudio(BaseConfig config, String format, OnCmdExecListener listener) {
        if (config == null) {
            throw new IllegalArgumentException("config 为空");
        }
        if (config.getInputPath() == null) {
            throw new IllegalArgumentException("输入视频错误");
        }
        if (TextUtils.isEmpty(config.getOutputPath())) {
            throw new IllegalArgumentException("视频输出路径不能为空");
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-threads");
        cmdList.append(config.getThread());
        cmdList.append("-i");
        cmdList.append(config.getInputPath());

        if (TextUtils.isEmpty(format)) {
            //默认用mp3封装格式
            format = Format.MP3;
        }
        if (format.equals(Format.MP3)) {
            cmdList.append("-vn");
            cmdList.append("-acodec");
            cmdList.append("libmp3lame");

        } else if (format.equals(Format.AAC)) {
            cmdList.append("-vn");
            cmdList.append("-acodec");
            cmdList.append("libfdk_aac");
        } else if (format.equals(Format.M4A)) {
            cmdList.append("-acodec");
            cmdList.append("copy");
            cmdList.append("-vn");
        }
        cmdList.append(config.getOutputPath() + "." + format);
        FFmpegJniBridge.invokeCommandSync(cmdList, VideoUtils.getVideoLength(config.getInputPath()), listener);
    }

    /**
     * 视频变速
     *
     * @param config   {@link ChangePTSConfig}
     * @param listener 回调监听
     */
    public static void changeVideoPTS(ChangePTSConfig config, OnCmdExecListener listener) {
        if (config == null) {
            throw new IllegalArgumentException("config 为空");
        }
        if (config.getInputPath() == null) {
            throw new IllegalArgumentException("输入视频错误");
        }
        if (TextUtils.isEmpty(config.getOutputPath())) {
            throw new IllegalArgumentException("视频输出路径不能为空");
        }
        float times = config.getTimes();
        if (times < 0.25f || times > 4.0f) {
            throw new IllegalArgumentException("视频倍率只能在0.25-4的范围之间");
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-threads");
        cmdList.append(config.getThread());
        cmdList.append("-i");
        cmdList.append(config.getInputPath());

        String t = "atempo=" + times;

        if (times < 0.5f) {
            t = "atempo=0.5,atempo=" + (times / 0.5f);
        } else if (times > 2.0f) {
            t = "atempo=2.0,atempo=" + (times / 2.0f);
        }
        PTS ptsType = config.getPtsType();
        switch (ptsType) {
            case VIDEO:
                cmdList.append("-filter_complex");
                cmdList.append("[0:v]setpts=" + (1 / times) + "*PTS").append("-an");
                break;
            case AUDIO:
                cmdList.append("-filter:a");
                cmdList.append(t);
                break;
            case ALL:
                cmdList.append("-filter_complex");
                cmdList.append("[0:v]setpts=" + (1 / times) + "*PTS[v];[0:a]" + t + "[a]");
                cmdList.append("-map");
                cmdList.append("[v]");
                cmdList.append("-map");
                cmdList.append("[a]");
                break;
        }
        cmdList.append("-preset");
        cmdList.append(config.getPreset() == null ? "superfast" : config.getPreset());
        cmdList.append(config.getOutputPath());
        FFmpegJniBridge.invokeCommandSync(cmdList, VideoUtils.getVideoLength(config.getInputPath()), listener);
    }

    /**
     * 音视频倒序
     *
     * @param config   {@link ReverseConfig}
     * @param listener 回调监听
     */
    public static void reverse(ReverseConfig config, OnCmdExecListener listener) {
        if (config == null) {
            throw new IllegalArgumentException("config 为空");
        }
        if (config.getInputPath() == null) {
            throw new IllegalArgumentException("输入视频错误");
        }
        if (TextUtils.isEmpty(config.getOutputPath())) {
            throw new IllegalArgumentException("视频输出路径不能为空");
        }
        if (!config.isAudioReverse() && !config.isVideoReverse()) {
            if (listener != null) {
                listener.onFailure();
                return;
            }
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-threads");
        cmdList.append(config.getThread());
        cmdList.append("-i");
        cmdList.append(config.getInputPath());
        cmdList.append("-filter_complex");
        String filter = "";
        if (config.isVideoReverse()) {
            filter += "[0:v]reverse[v];";
        }
        if (config.isAudioReverse()) {
            filter += "[0:a]areverse[a];";
        }
        cmdList.append(filter.substring(0, filter.length() - 1));
        if (config.isVideoReverse()) {
            cmdList.append("-map");
            cmdList.append("[v]");
        }
        if (config.isAudioReverse()) {
            cmdList.append("-map");
            cmdList.append("[a]");
        }
        if (config.isAudioReverse() && !config.isVideoReverse()) {
            cmdList.append("-acodec");
            cmdList.append("libmp3lame");
        }
        cmdList.append("-preset");
        cmdList.append(config.getPreset() == null ? "superfast" : config.getPreset());
        cmdList.append(config.getOutputPath());
        FFmpegJniBridge.invokeCommandSync(cmdList, VideoUtils.getVideoLength(config.getInputPath()), listener);
    }

    /**
     * 视频转图片
     *
     * @param config   {@link Video2ImageConfig}
     * @param listener 回调监听
     */
    public static void video2Image(Video2ImageConfig config, OnCmdExecListener listener) {
        if (config == null) {
            throw new IllegalArgumentException("config 为空");
        }
        if (config.getInputPath() == null) {
            throw new IllegalArgumentException("输入视频错误");
        }
        if (TextUtils.isEmpty(config.getOutputPath())) {
            throw new IllegalArgumentException("图片输出路径不能为空");
        }
        if (TextUtils.isEmpty(config.getOutputNameFormat())) {
            throw new IllegalArgumentException("图片输出格式化的名称不能为空");
        }
        if ((!Format.JPG.equals(config.getImageSuffix()) && !Format.PNG.equals(config.getImageSuffix()))) {
            throw new IllegalArgumentException("图片输出格式不合法");
        }
        int rate = config.getRate();
        if (rate <= 0) {
            throw new IllegalArgumentException("参数不合法");
        }
        String outputPath = config.getOutputPath();
        File file = new File(outputPath);
        if (!file.exists()) {
            file.mkdir();
        }

        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-threads");
        cmdList.append(config.getThread());
        cmdList.append("-i");
        cmdList.append(config.getInputPath());
        cmdList.append("-f");
        cmdList.append("image2");
        cmdList.append("-r");
        cmdList.append(rate);
        if (config.getWidth() > 0 && config.getHeight() > 0) {
            cmdList.append("-s");
            cmdList.append(config.getWidth() + "x" + config.getHeight());
        }
        cmdList.append("-q:v");
        int imageQuality = config.getImageQuality();
        if (imageQuality < 2) {
            imageQuality = 2;
        } else if (imageQuality > 10) {
            imageQuality = 10;
        }
        cmdList.append(imageQuality);
        cmdList.append("-preset");
        cmdList.append(TextUtils.isEmpty(config.getPreset()) ? "superfast" : config.getPreset());
        cmdList.append(outputPath + "/" + config.getOutputNameFormat() + "." + config.getImageSuffix());
        FFmpegJniBridge.invokeCommandSync(cmdList, VideoUtils.getVideoLength(config.getInputPath()), listener);
    }

    /**
     * 图片转视频
     * -t duration
     * 用做输入选项（在-i之前），是限制读取输入文件的的时长；
     * 用做输出选项，超过这个时间停止写输出文件；
     * 比如：循环读取一个输入文件时（-loop 1），当到时间就会停止输出，生成一个duration时长的视频。
     * 但是如果没有循环选项，而且输入文件短于这个时长时，就会随着输入文件结束就结束，生成视频，视频时长小于duration。
     * 所以 -t 并不仅仅是输出文件时长。
     * <p>
     * -r fps
     * 帧率，可以指定两个帧率，输入帧率，输出帧率；
     * 输入帧率：-i之前，设定读入帧率，比如 -r 0.5 ,也就是说1秒要播0.5个图片，那么一个图也就是要播2s；
     * 输出频率：-i之后，真正的输出视频播放帧率，不写话，是默认和输入频率一样。比如设 -r 30 ,对应上面的设定，一个图播2
     * s，那么输出文件播放时，这2s内，都是这张图，但是播放了60帧
     *
     * @param config   {@link Video2ImageConfig}
     * @param listener 回调监听
     */
    public static void image2Video(Image2VideoConfig config, OnCmdExecListener listener) {
        if (config == null) {
            throw new IllegalArgumentException("config 为空");
        }
        if (config.getInputPath() == null) {
            throw new IllegalArgumentException("输入视频错误");
        }
        if (TextUtils.isEmpty(config.getOutputPath())) {
            throw new IllegalArgumentException("视频输出路径不能为空");
        }
        int width = config.getWidth();
        int height = config.getHeight();
        int rate = config.getRate();
        if (width <= 0 || height <= 0 || rate <= 0) {
            throw new IllegalArgumentException("参数不合法");
        }
        CmdList cmdList = new CmdList();
        cmdList.append("ffmpeg");
        cmdList.append("-y");
        cmdList.append("-threads");
        cmdList.append(config.getThread());
        cmdList.append("-loop");
        cmdList.append(config.getLoop());
        cmdList.append("-f");
        cmdList.append("image2");
        cmdList.append("-i");
        cmdList.append(config.getInputPath());
        if (!TextUtils.isEmpty(config.getAudioPath())) {
            cmdList.append("-i");
            cmdList.append(config.getAudioPath());
            cmdList.append("-absf");
            cmdList.append("aac_adtstoasc");
        }

        cmdList.append("-vcodec");
        cmdList.append("libx264");
        cmdList.append("-r");
        cmdList.append(rate);
        if (config.getDuration() > 1) {
            cmdList.append("-t");
            cmdList.append(config.getDuration());
        }
        cmdList.append("-s");
        cmdList.append(width + "x" + height);
        cmdList.append(config.getOutputPath());
        FFmpegJniBridge.invokeCommandSync(cmdList, VideoUtils.getVideoLength(config.getInputPath()), listener);
    }
}
