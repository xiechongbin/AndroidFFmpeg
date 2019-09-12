package com.xiaoxie.ffmpeglib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * 摄像头预览的自定义View
 * Created by xcb on 2019-09-12.
 */
public class CameraView extends SurfaceView {
    private Context mContext;

    public CameraView(Context context) {
        super(context);
        initView(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化
     */
    private void initView(Context context) {
        this.mContext = context;
    }

}
