package com.xiaoxie.ffmpeg;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.xiaoxie.ffmpeglib.FFmpegJniBridge;
import com.xiaoxie.ffmpeglib.VideoHandleEditor;
import com.xiaoxie.ffmpeglib.config.AutoVBRMode;
import com.xiaoxie.ffmpeglib.config.BGMConfig;
import com.xiaoxie.ffmpeglib.config.BaseConfig;
import com.xiaoxie.ffmpeglib.config.CBRMode;
import com.xiaoxie.ffmpeglib.config.ChangePTSConfig;
import com.xiaoxie.ffmpeglib.config.Image2VideoConfig;
import com.xiaoxie.ffmpeglib.config.ReverseConfig;
import com.xiaoxie.ffmpeglib.config.VBRMode;
import com.xiaoxie.ffmpeglib.config.Video2ImageConfig;
import com.xiaoxie.ffmpeglib.config.VideoMergeByTranscodeConfig;
import com.xiaoxie.ffmpeglib.config.VideoMergeConfig;
import com.xiaoxie.ffmpeglib.config.waterMark.AddImageWaterMakerConfig;
import com.xiaoxie.ffmpeglib.config.waterMark.AddTextWatermarkConfig;
import com.xiaoxie.ffmpeglib.config.waterMark.Locations;
import com.xiaoxie.ffmpeglib.interfaces.OnCmdExecListener;
import com.xiaoxie.ffmpeglib.mode.Format;
import com.xiaoxie.ffmpeglib.mode.Mode;
import com.xiaoxie.ffmpeglib.mode.PTS;
import com.xiaoxie.ffmpeglib.mode.Preset;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnCmdExecListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String inputPath = "/storage/emulated/0/in.mp4";
    private static final String inputPath1 = "/storage/emulated/0/in1.mp4";
    private static final String inputPath2 = "/storage/emulated/0/in2.mp4";
    private static final String outputPath = "/storage/emulated/0/out.mp4";
    private static final String logoPath = "/storage/emulated/0/logo.gif";
    private EditText ed_command;
    private Button btn_invoke;
    private ProgressDialog dialog;
    private LinearLayout ll_do_cut_video;
    private LinearLayout ll_do_compress_video;
    private LinearLayout ll_do_compress_auto_vbr;
    private LinearLayout ll_do_compress_cbr;
    private LinearLayout ll_do_compress_vbr;
    private LinearLayout ll_do_merge_undamage;
    private LinearLayout ll_do_merge_transcoding;
    private LinearLayout ll_do_add_bgm_music;
    private LinearLayout ll_do_separate_video;
    private LinearLayout ll_do_separate_audio;
    private LinearLayout ll_do_change_pts;
    private LinearLayout ll_do_reverse;
    private LinearLayout ll_video_2_image;
    private LinearLayout ll_image_2_video;
    private LinearLayout ll_video_add_text;
    private LinearLayout ll_video_add_image;
    private LinearLayout ll_do_play_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        ed_command = findViewById(R.id.ed_command);
        btn_invoke = findViewById(R.id.btn_invoke);
        ll_do_cut_video = findViewById(R.id.ll_do_cut_video);
        ll_do_compress_video = findViewById(R.id.ll_do_compress_video);
        ll_do_compress_auto_vbr = findViewById(R.id.ll_do_compress_auto_vbr);
        ll_do_compress_cbr = findViewById(R.id.ll_do_compress_cbr);
        ll_do_compress_vbr = findViewById(R.id.ll_do_compress_vbr);
        ll_do_merge_undamage = findViewById(R.id.ll_do_merge_undamage);
        ll_do_merge_transcoding = findViewById(R.id.ll_do_merge_transcoding);
        ll_do_add_bgm_music = findViewById(R.id.ll_do_add_bgm_music);
        ll_do_separate_video = findViewById(R.id.ll_do_separate_video);
        ll_do_separate_audio = findViewById(R.id.ll_do_separate_audio);
        ll_do_change_pts = findViewById(R.id.ll_do_change_pts);
        ll_do_reverse = findViewById(R.id.ll_do_reverse);
        ll_video_2_image = findViewById(R.id.ll_video_2_image);
        ll_image_2_video = findViewById(R.id.ll_image_2_video);
        ll_video_add_text = findViewById(R.id.ll_video_add_text);
        ll_video_add_image = findViewById(R.id.ll_video_add_image);
        ll_do_play_video = findViewById(R.id.ll_do_play_video);
        setListener();
        MainActivityPermissionsDispatcher.onClickWithPermissionCheck(this, 0);
    }

    private void setListener() {
        btn_invoke.setOnClickListener(this);
        ll_do_cut_video.setOnClickListener(this);
        ll_do_compress_video.setOnClickListener(this);
        ll_do_compress_auto_vbr.setOnClickListener(this);
        ll_do_compress_cbr.setOnClickListener(this);
        ll_do_compress_vbr.setOnClickListener(this);
        ll_do_merge_undamage.setOnClickListener(this);
        ll_do_merge_transcoding.setOnClickListener(this);
        ll_do_add_bgm_music.setOnClickListener(this);
        ll_do_separate_video.setOnClickListener(this);
        ll_do_separate_audio.setOnClickListener(this);
        ll_do_change_pts.setOnClickListener(this);
        ll_do_reverse.setOnClickListener(this);
        ll_video_2_image.setOnClickListener(this);
        ll_image_2_video.setOnClickListener(this);
        ll_video_add_text.setOnClickListener(this);
        ll_video_add_image.setOnClickListener(this);
        ll_do_play_video.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onClick(View v) {
        onClick(v.getId());
    }


    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onClick(int id) {
        switch (id) {
            case R.id.btn_invoke:
                // String cmd = "ffmpeg -y -i /storage/emulated/0/in.mp4 -f image2 -r 1 -q:v 10 -preset superfast /storage/emulated/0/2/%3d.jpg";
                // String cmd = "ffmpeg -y -i /storage/emulated/0/in1.mp4 -f image2 -r 1 -q:v 10 -preset superfast /storage/emulated/0/2/%3d.jpg";
                //String cmd = "ffmpeg -y -i /storage/emulated/0/in.mp4 -filter_complex drawtext=fontfile=/storage/emulated/0/hua_kang.ttf:fontsize=35.0:fontcolor=#EE00EE:x=10:y=10:text='华康少女字体' -preset superfast /storage/emulated/0/add_text_.mp4";
                String cmd = "ffmpeg -i /storage/emulated/0/in.mp4 -i /storage/emulated/0/logo.png -filter_complex 'overlay=10:main_h-overlay_h-10' /storage/emulated/0/ou.mp4";
                FFmpegJniBridge.invokeCommandSync(cmd.split(" "), 1000, this);
                break;
            case R.id.ll_do_cut_video:
                showDialog();
                VideoHandleEditor.doCutVideoWithEndTime(inputPath, outputPath, 3, 10, true, true, this);
                break;
            case R.id.ll_do_compress_video:
                showDialog();
                VideoHandleEditor.compressVideo(inputPath, outputPath, "2000k", 30, "fast", this);
                break;
            case R.id.ll_do_compress_auto_vbr:
                showDialog();
                AutoVBRMode auto_vbr_config = new AutoVBRMode();
                auto_vbr_config.setInputPath(inputPath);
                auto_vbr_config.setOutputPath("/storage/emulated/0/auto_vbr.mp4");
                auto_vbr_config.setMode(Mode.AUTO_VBR);
                auto_vbr_config.setCrfSize(21);
                auto_vbr_config.setScale(1.2f);
                auto_vbr_config.setThread(16);
                auto_vbr_config.setFrameRate(24);
                VideoHandleEditor.compressVideo(auto_vbr_config, this);
                break;
            case R.id.ll_do_compress_cbr:
                showDialog();
                CBRMode cbrModeConfig = new CBRMode(166, 2097);
                cbrModeConfig.setInputPath(inputPath);
                cbrModeConfig.setOutputPath("/storage/emulated/0/cbr.mp4");
                cbrModeConfig.setScale(1.2f);
                cbrModeConfig.setThread(16);
                cbrModeConfig.setPreset(Preset.ULTRAFAST);
                cbrModeConfig.setFrameRate(24);
                VideoHandleEditor.compressVideo(cbrModeConfig, this);
                break;

            case R.id.ll_do_compress_vbr:
                showDialog();
                VBRMode vbrModeConfig = new VBRMode(4000, 2097);
                vbrModeConfig.setInputPath(inputPath);
                vbrModeConfig.setOutputPath("/storage/emulated/0/vbr.mp4");
                vbrModeConfig.setScale(1.2f);
                vbrModeConfig.setThread(16);
                vbrModeConfig.setFrameRate(24);
                vbrModeConfig.setPreset(Preset.ULTRAFAST);
                VideoHandleEditor.compressVideo(vbrModeConfig, this);
                break;

            case R.id.ll_do_merge_undamage:
                showDialog();
                VideoMergeConfig config = new VideoMergeConfig();
                List<String> inputList = new ArrayList<>();
                inputList.add(inputPath2);
                inputList.add(inputPath);
                config.setInputPathList(inputList);
                config.setOutputPath("/storage/emulated/0/merge_lossless.mp4");
                VideoHandleEditor.mergeVideosLossLess(config, this);
                break;
            case R.id.ll_do_merge_transcoding:
                showDialog();
                VideoMergeByTranscodeConfig videoMergeByTranscodeConfig = new VideoMergeByTranscodeConfig();
                List<String> list = new ArrayList<>();
                list.add(inputPath2);
                list.add(inputPath);
                videoMergeByTranscodeConfig.setInputPathList(list);
                videoMergeByTranscodeConfig.setWidth(720);
                videoMergeByTranscodeConfig.setHeight(1280);
                videoMergeByTranscodeConfig.setBitRate(10);
                videoMergeByTranscodeConfig.setFrameRate(24);
                videoMergeByTranscodeConfig.setOutputPath("/storage/emulated/0/merge_transcoding.mp4");
                VideoHandleEditor.mergeVideoByTranscoding(videoMergeByTranscodeConfig, this);
                break;
            case R.id.ll_do_add_bgm_music:
                showDialog();
                BGMConfig bgmConfig = new BGMConfig();
                bgmConfig.setInputPath(inputPath2);
                bgmConfig.setAudioPath("/storage/emulated/0/north.mp3");
                bgmConfig.setOutputPath("/storage/emulated/0/add_bgm.mp4");
                bgmConfig.setOriginalVolume(0.2f);
                bgmConfig.setNewAudioVolume(0.9f);
                VideoHandleEditor.addBackgroundMusic(bgmConfig, this);
                break;
            case R.id.ll_do_separate_video:
                showDialog();
                BaseConfig baseConfig = new BaseConfig();
                baseConfig.setInputPath(inputPath2);
                baseConfig.setOutputPath("/storage/emulated/0/separate_.mp4");
                VideoHandleEditor.separateVideo(baseConfig, this);
                break;
            case R.id.ll_do_separate_audio:
                showDialog();
                BaseConfig baseConfig1 = new BaseConfig();
                baseConfig1.setInputPath(inputPath2);
                baseConfig1.setOutputPath("/storage/emulated/0/separate_");
                VideoHandleEditor.separateAudio(baseConfig1, Format.AAC, this);
                break;
            case R.id.ll_do_change_pts:
                showDialog();
                ChangePTSConfig changePTSConfig = new ChangePTSConfig();
                changePTSConfig.setInputPath(inputPath2);
                changePTSConfig.setPtsType(PTS.ALL);
                changePTSConfig.setTimes(0.5f);
                changePTSConfig.setOutputPath("/storage/emulated/0/视频变速.mp4");
                VideoHandleEditor.changeVideoPTS(changePTSConfig, this);
                break;
            case R.id.ll_do_reverse:
                showDialog();
                ReverseConfig reverseConfig = new ReverseConfig();
                reverseConfig.setInputPath(inputPath);
                reverseConfig.setVideoReverse(true);
                reverseConfig.setAudioReverse(true);
                reverseConfig.setOutputPath("/storage/emulated/0/视频倒序.mp4");
                VideoHandleEditor.reverse(reverseConfig, this);
                break;
            case R.id.ll_video_2_image:
                showDialog();
                Video2ImageConfig viConfig = new Video2ImageConfig();
                viConfig.setInputPath(inputPath);
                viConfig.setRate(1);
                viConfig.setImageQuality(2);
                viConfig.setWidth(640);
                viConfig.setHeight(480);
                viConfig.setOutputPath("/storage/emulated/0/z_video_2_image");
                viConfig.setOutputNameFormat("%3d");
                viConfig.setImageSuffix(Format.JPG);
                VideoHandleEditor.video2Image(viConfig, this);
                break;
            case R.id.ll_image_2_video:
                showDialog();
                Image2VideoConfig ivConfig = new Image2VideoConfig();
                ivConfig.setWidth(1080);
                ivConfig.setHeight(1920);
                ivConfig.setRate(10);
                ivConfig.setDuration(10);
                ivConfig.setLoop(1);
                ivConfig.setAudioPath("/storage/emulated/0/separate_.mp3");
                ivConfig.setInputPath("/storage/emulated/0/ztest/%3d.jpg");
                ivConfig.setOutputPath("/storage/emulated/0/图片转视频.mp4");
                VideoHandleEditor.image2Video(ivConfig, this);
                break;
            case R.id.ll_video_add_text:
                showDialog();
                AddTextWatermarkConfig textWatermarkConfig = new AddTextWatermarkConfig();
                textWatermarkConfig.setText(AddTextWatermarkConfig.REAL_TIME);
                textWatermarkConfig.setLocation(new Locations("30", "30"));
                textWatermarkConfig.setLine_spacing(7);
                textWatermarkConfig.setFontColor("red");
                textWatermarkConfig.setFontSize(90);
                textWatermarkConfig.setBox(1);
                textWatermarkConfig.setBoxColor("yellow");
                textWatermarkConfig.setTtf("/storage/emulated/0/hua_kang.ttf");
                textWatermarkConfig.setInputPath(inputPath);
                textWatermarkConfig.setOutputPath("/storage/emulated/0/文字水印.mp4");
                VideoHandleEditor.addWaterMaker(textWatermarkConfig, this);
                break;

            case R.id.ll_video_add_image:
                showDialog();
                AddImageWaterMakerConfig addImageWaterMakerConfig = new AddImageWaterMakerConfig();
                addImageWaterMakerConfig.setWaterMakerPath(logoPath);
                addImageWaterMakerConfig.setInputPath(inputPath);
                addImageWaterMakerConfig.setLocations(new Locations("30", "30"));
                addImageWaterMakerConfig.setOutputPath("/storage/emulated/0/图片水印.mp4");
                VideoHandleEditor.addWaterImageMaker(addImageWaterMakerConfig, this);
                break;
            //视频播放
            case R.id.ll_do_play_video:
                Intent intent = new Intent(this, VideoPlayActivity.class);
                this.startActivity(intent);
                break;
        }
    }

    private void showDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("开始执行");
        dialog.setCancelable(true);
        dialog.setMax(100);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();
    }

    @Override
    public void onSuccess(String result) {
        Log.d(TAG, "视频处理成功");
    }

    @Override
    public void onFailure() {
        Log.d(TAG, "视频处理失败");
    }

    @Override
    public void onProgress(float progress) {
        if (dialog != null && dialog.isShowing()) {
            dialog.setProgress((int) (progress * 100));
        }
    }
}
