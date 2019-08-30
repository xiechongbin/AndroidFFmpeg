package com.xiaoxie.ffmpeglib.videoplay;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xiaoxie.ffmpeglib.FFmpegJniBridge;

/**
 * ffmpeg 播放视频自定义控件
 * Created by xcb on 2019-08-30.
 */
public class FFmpegVideoView extends SurfaceView {
    private SurfaceHolder holder;

    public FFmpegVideoView(Context context) {
        super(context);
        initView();
    }

    public FFmpegVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FFmpegVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public FFmpegVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    private void initView() {
        holder = getHolder();
        if (holder != null) {
            holder.setFormat(PixelFormat.RGB_888);
        }
    }

    /**
     * 开始播放视频
     */
    public void startPlayVideo(final String videoPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FFmpegJniBridge.render(videoPath, holder.getSurface());
            }
        }).start();
    }
}
