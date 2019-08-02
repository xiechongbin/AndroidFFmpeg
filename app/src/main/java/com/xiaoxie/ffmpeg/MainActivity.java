package com.xiaoxie.ffmpeg;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.xiaoxie.ffmpeglib.VideoHandleEditor;
import com.xiaoxie.ffmpeglib.config.AutoVBRMode;
import com.xiaoxie.ffmpeglib.config.BGMConfig;
import com.xiaoxie.ffmpeglib.config.BaseConfig;
import com.xiaoxie.ffmpeglib.config.CBRMode;
import com.xiaoxie.ffmpeglib.config.VBRMode;
import com.xiaoxie.ffmpeglib.config.VideoMergeByTranscodeConfig;
import com.xiaoxie.ffmpeglib.config.VideoMergeConfig;
import com.xiaoxie.ffmpeglib.interfaces.OnCmdExecListener;
import com.xiaoxie.ffmpeglib.mode.Format;
import com.xiaoxie.ffmpeglib.mode.Mode;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                break;
            case R.id.ll_do_cut_video:
                if (dialog == null) {
                    dialog = getDialog();
                }
                dialog.setProgress(0);
                dialog.show();
                VideoHandleEditor.doCutVideoWithEndTime(inputPath, outputPath, 3, 10, true, true, this);
                break;
            case R.id.ll_do_compress_video:
                if (dialog == null) {
                    dialog = getDialog();
                }
                dialog.setProgress(0);
                dialog.show();
                VideoHandleEditor.compressVideo(inputPath, outputPath, "2000k", 30, "fast", this);
                break;
            case R.id.ll_do_compress_auto_vbr:
                AutoVBRMode auto_vbr_config = new AutoVBRMode();
                auto_vbr_config.setInputVideo(inputPath);
                auto_vbr_config.setOutputPath("/storage/emulated/0/auto_vbr.mp4");
                auto_vbr_config.setMode(Mode.AUTO_VBR);
                auto_vbr_config.setCrfSize(21);
                auto_vbr_config.setScale(1.2f);
                auto_vbr_config.setThread(16);
                auto_vbr_config.setFrameRate(24);
                if (dialog == null) {
                    dialog = getDialog();
                }
                dialog.show();
                VideoHandleEditor.compressVideo(auto_vbr_config, this);
                break;

            case R.id.ll_do_compress_cbr:
                CBRMode cbrModeConfig = new CBRMode(166, 2097);
                cbrModeConfig.setInputVideo(inputPath);
                cbrModeConfig.setOutputPath("/storage/emulated/0/cbr.mp4");
                cbrModeConfig.setScale(1.2f);
                cbrModeConfig.setThread(16);
                cbrModeConfig.setPreset(Preset.ULTRAFAST);
                cbrModeConfig.setFrameRate(24);
                if (dialog == null) {
                    dialog = getDialog();
                }
                dialog.show();
                VideoHandleEditor.compressVideo(cbrModeConfig, this);
                break;

            case R.id.ll_do_compress_vbr:
                VBRMode vbrModeConfig = new VBRMode(4000, 2097);
                vbrModeConfig.setInputVideo(inputPath);
                vbrModeConfig.setOutputPath("/storage/emulated/0/vbr.mp4");
                vbrModeConfig.setScale(1.2f);
                vbrModeConfig.setThread(16);
                vbrModeConfig.setFrameRate(24);
                vbrModeConfig.setPreset(Preset.ULTRAFAST);
                if (dialog == null) {
                    dialog = getDialog();
                }
                dialog.show();
                VideoHandleEditor.compressVideo(vbrModeConfig, this);
                break;

            case R.id.ll_do_merge_undamage:
                if (dialog == null) {
                    dialog = getDialog();
                }
                dialog.show();
                VideoMergeConfig config = new VideoMergeConfig();
                List<String> inputList = new ArrayList<>();
                inputList.add(inputPath2);
                inputList.add(inputPath);
                config.setInputVideoList(inputList);
                config.setOutputPath("/storage/emulated/0/merge_lossless.mp4");
                VideoHandleEditor.mergeVideosLossLess(config, this);
                break;
            case R.id.ll_do_merge_transcoding:
                if (dialog == null) {
                    dialog = getDialog();
                }
                dialog.show();
                VideoMergeByTranscodeConfig videoMergeByTranscodeConfig = new VideoMergeByTranscodeConfig();
                List<String> list = new ArrayList<>();
                list.add(inputPath2);
                list.add(inputPath);
                videoMergeByTranscodeConfig.setInputVideoList(list);
                videoMergeByTranscodeConfig.setWidth(720);
                videoMergeByTranscodeConfig.setHeight(1280);
                videoMergeByTranscodeConfig.setBitRate(10);
                videoMergeByTranscodeConfig.setFrameRate(24);
                videoMergeByTranscodeConfig.setOutputPath("/storage/emulated/0/merge_transcoding.mp4");
                VideoHandleEditor.mergeVideoByTranscoding(videoMergeByTranscodeConfig, this);
                break;
            case R.id.ll_do_add_bgm_music:
                if (dialog == null) {
                    dialog = getDialog();
                }
                dialog.show();
                BGMConfig bgmConfig = new BGMConfig();
                bgmConfig.setInputVideo(inputPath2);
                bgmConfig.setAudioPath("/storage/emulated/0/north.mp3");
                bgmConfig.setOutputPath("/storage/emulated/0/add_bgm.mp4");
                bgmConfig.setOriginalVolume(0.2f);
                bgmConfig.setNewAudioVolume(0.9f);
                VideoHandleEditor.addBackgroundMusic(bgmConfig, this);
                break;
            case R.id.ll_do_separate_video:
                if (dialog == null) {
                    dialog = getDialog();
                }
                dialog.show();
                BaseConfig baseConfig = new BaseConfig();
                baseConfig.setInputVideo(inputPath2);
                baseConfig.setOutputPath("/storage/emulated/0/separate_.mp4");
                VideoHandleEditor.separateVideo(baseConfig, this);
                break;
            case R.id.ll_do_separate_audio:
                if (dialog == null) {
                    dialog = getDialog();
                }
                dialog.show();
                BaseConfig baseConfig1 = new BaseConfig();
                baseConfig1.setInputVideo(inputPath2);
                baseConfig1.setOutputPath("/storage/emulated/0/separate_");
                VideoHandleEditor.separateAudio(baseConfig1, Format.AAC, this);
                break;
        }
    }

    private ProgressDialog getDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("开始执行");
        progressDialog.setCancelable(true);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        return progressDialog;
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
