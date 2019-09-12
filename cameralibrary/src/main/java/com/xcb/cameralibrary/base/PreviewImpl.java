package com.xcb.cameralibrary.base;

import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

/**
 * 摄像头预览抽象类
 * Created by xcb on 2019-09-12.
 */
public abstract class PreviewImpl {

    private Callback mCallback;

    private int mWidth;

    private int mHeight;

    public abstract Surface getSurface();

    public abstract View getView();

    public abstract Class getOutputClass();

    public abstract void setDisplayOrientation(int displayOrientation);

    public abstract boolean isReady();

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    protected void dispatchSurfaceChanged() {
        mCallback.onSurfaceChanged();
    }

    public SurfaceHolder getSurfaceHolder() {
        return null;
    }

    public Object getSurfaceTexture() {
        return null;
    }

    public void setBufferSize(int width, int height) {
    }

    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public interface Callback {
        void onSurfaceChanged();
    }
}
