package com.xiaoxie.ffmpeglib.videoplay;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.xiaoxie.ffmpeglib.FFmpegJniBridge;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;

/**
 * ffmpeg 播放视频自定义控件
 * Created by xcb on 2019-08-30.
 */
public class FFmpegVideoView extends SurfaceView implements IRenderView {
    private static final String TAG = "FFmpegVideoView";
    private SurfaceHolder holder;
    private MeasureHelper mMeasureHelper;
    private SurfaceCallback mSurfaceCallback;

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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
    }


    private void initView() {
        holder = getHolder();
        mMeasureHelper = new MeasureHelper(this);
        mSurfaceCallback = new SurfaceCallback(this);
        if (holder != null) {
            holder.addCallback(mSurfaceCallback);
            //noinspection deprecation
            getHolder().setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
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

    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean shouldWaitForResize() {
        return true;
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            getHolder().setFixedSize(videoWidth, videoHeight);
            requestLayout();
        }
    }

    @Override
    public void setVideoRotation(int degree) {
        Log.d(TAG, "degree =" + degree);
    }

    @Override
    public void setAspectRatio(int aspectRatio) {
        mMeasureHelper.setAspectRatio(aspectRatio);
        requestLayout();
    }


    private static final class SurfaceCallback implements SurfaceHolder.Callback {

        public SurfaceHolder mSurfaceHolder;
        public WeakReference<FFmpegVideoView> videoViewWeakReference;

        private boolean mIsFormatChanged;
        private int mFormat;
        private int mWidth;
        private int mHeight;

        public SurfaceCallback(@NonNull FFmpegVideoView fFmpegVideoView) {
            videoViewWeakReference = new WeakReference<>(fFmpegVideoView);
        }


        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            mIsFormatChanged = false;
            mFormat = 0;
            mWidth = 0;
            mHeight = 0;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mSurfaceHolder = holder;
            mIsFormatChanged = true;
            mFormat = format;
            mWidth = width;
            mHeight = height;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mSurfaceHolder = null;
            mIsFormatChanged = false;
            mFormat = 0;
            mWidth = 0;
            mHeight = 0;

        }
    }
}
