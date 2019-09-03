package com.xiaoxie.ffmpeg;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.xiaoxie.ffmpeglib.FFmpegJniBridge;
import com.xiaoxie.ffmpeglib.mode.Size;
import com.xiaoxie.ffmpeglib.utils.VideoUtils;
import com.xiaoxie.ffmpeglib.videoplay.FFmpegVideoView;
import com.xiaoxie.ffmpeglib.videoplay.IRenderView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 视频播放
 * Created by xcb on 2019-08-30.
 */
public class VideoPlayActivity extends AppCompatActivity {
    private FFmpegVideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_videoplay);
        videoView = findViewById(R.id.video_view);
        final String videoPath = "/storage/emulated/0/in.mp4";
        Size size = VideoUtils.getVideoSize(videoPath);
        videoView.setVideoSize(size.getWidth(), size.getHeight());
        videoView.setAspectRatio(IRenderView.AR_4_3_FIT_PARENT);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FFmpegJniBridge.playSound(videoPath);
                    }
                }).start();

            }
        });
    }
}
