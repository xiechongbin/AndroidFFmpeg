package com.xcb.cameralibrary.camera.api14;

import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.xcb.cameralibrary.R;
import com.xcb.cameralibrary.base.PreviewImpl;

import androidx.core.view.ViewCompat;

/**
 * Created by xcb on 2019-09-12.
 */
public class SurfaceViewPreview extends PreviewImpl {
    private SurfaceView mSurfaceView;

    public SurfaceViewPreview(Context context, ViewGroup parent) {
        final View view = View.inflate(context, R.layout.surface_view, parent);
        mSurfaceView = view.findViewById(R.id.surface_view);
        SurfaceHolder holder = mSurfaceView.getHolder();
        //noinspection deprecation
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder h) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder h, int format, int width, int height) {
                setSize(width, height);
                if (!ViewCompat.isInLayout(mSurfaceView)) {
                    dispatchSurfaceChanged();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder h) {
                setSize(0, 0);
            }
        });
    }

    @Override
    public Surface getSurface() {
        return getSurfaceHolder().getSurface();
    }

    @Override
    public View getView() {
        return mSurfaceView;
    }

    @Override
    public Class getOutputClass() {
        return SurfaceHolder.class;
    }

    @Override
    public void setDisplayOrientation(int displayOrientation) {

    }

    @Override
    public boolean isReady() {
        return getWidth() != 0 && getHeight() != 0;
    }

    @Override
    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceView.getHolder();
    }
}
