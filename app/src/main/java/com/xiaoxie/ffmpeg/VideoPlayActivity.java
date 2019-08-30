package com.xiaoxie.ffmpeg;

import android.os.Bundle;
import android.view.View;

import com.xiaoxie.ffmpeglib.videoplay.FFmpegVideoView;

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
        setContentView(R.layout.activity_videoplay);
        videoView = findViewById(R.id.video_view);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.startPlayVideo("/storage/emulated/0/in.mp4");
            }
        });
    }
}
