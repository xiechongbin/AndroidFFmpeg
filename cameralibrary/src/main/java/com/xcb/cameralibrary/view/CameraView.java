package com.xcb.cameralibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.xcb.cameralibrary.R;
import com.xcb.cameralibrary.base.AspectRatio;
import com.xcb.cameralibrary.base.CameraViewImpl;
import com.xcb.cameralibrary.base.Constants;
import com.xcb.cameralibrary.base.OrientationDetector;
import com.xcb.cameralibrary.base.PreviewImpl;
import com.xcb.cameralibrary.camera.api14.Camera1;
import com.xcb.cameralibrary.camera.api14.SurfaceViewPreview;
import com.xcb.cameralibrary.camera.api14.TextureViewPreview;
import com.xcb.cameralibrary.camera.api21.Camera2;
import com.xcb.cameralibrary.camera.api23.Camera2Api23;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

/**
 * 摄像头的自定义view
 * Created by xcb on 2019-09-12.
 */
public class CameraView extends FrameLayout {

    private CallbackBridge mCallbacks;
    private OrientationDetector detector;
    private PreviewImpl mPreviewImpl;
    private CameraViewImpl mImpl;

    private boolean mAdjustViewBounds;
    private String aspectRatio;
    private boolean autoFocus;
    private int flash;
    /**
     * 前置或者后置摄像头
     */
    private int faceID;

    public CameraView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs, 0);
        initView(context);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context) {
        //如果在自定义控件的构造函数或者其他绘制相关地方使用系统依赖的代码，会导致可视化编辑器无法报错并提示：
        // Use View.isInEditMode() in your custom views to skip code when shown in Eclipse
        if (isInEditMode()) {
            mCallbacks = null;
            detector = null;
            return;
        }
        mPreviewImpl = createPreviewImpl(context);
        mCallbacks = new CallbackBridge();
        if (Build.VERSION.SDK_INT < 21) {
            mImpl = new Camera1(mCallbacks, mPreviewImpl);
        } else if (Build.VERSION.SDK_INT < 23) {
            mImpl = new Camera2(mCallbacks, mPreviewImpl, context);
        } else {
            mImpl = new Camera2Api23(mCallbacks, mPreviewImpl, context);
        }
        initCameraParameters(context);
    }

    @NonNull
    private PreviewImpl createPreviewImpl(Context context) {
        PreviewImpl preview;
        if (Build.VERSION.SDK_INT >= 23) {
            preview = new SurfaceViewPreview(context, this);
        } else {
            preview = new TextureViewPreview(context, this);
        }
        return preview;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            if (detector != null) {
                detector.enable(ViewCompat.getDisplay(this));
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!isInEditMode()) {
            if (detector != null) {
                detector.disable();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isInEditMode()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            if (mAdjustViewBounds) {
                if (!isCameraOpened()) {
                    mCallbacks.reserveRequestLayoutOnOpen();
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                } else {
                    final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
                    final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
                    if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
                        final AspectRatio ratio = getAspectRatio();
                        if (ratio != null) {
                            int height = (int) (MeasureSpec.getSize(widthMeasureSpec) * ratio.toFloat());
                            if (heightMode == MeasureSpec.AT_MOST) {
                                height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
                            }
                            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                        } else {
                            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        }
                    } else if (widthMode != MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
                        final AspectRatio ratio = getAspectRatio();
                        if (ratio != null) {
                            int width = (int) (MeasureSpec.getSize(heightMeasureSpec) * ratio.toFloat());
                            if (widthMode == MeasureSpec.AT_MOST) {
                                width = Math.min(width, MeasureSpec.getSize(widthMeasureSpec));
                            }
                            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), heightMeasureSpec);
                        } else {
                            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        }
                    } else {
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    }
                }
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            //开始测量TextureView
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            AspectRatio ratio = getAspectRatio();
            if (detector.getLastKnownDisplayOrientation() % 180 == 0) {
                ratio = ratio.inverse();
            }
            assert ratio != null;
            if (height < width * ratio.getY() / ratio.getX()) {
                mImpl.getView().measure(
                        MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(width * ratio.getY() / ratio.getX(),
                                MeasureSpec.EXACTLY));
            } else {
                mImpl.getView().measure(
                        MeasureSpec.makeMeasureSpec(height * ratio.getX() / ratio.getY(),
                                MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
            }

        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());
        state.facing = getFacing();
        state.ratio = getAspectRatio();
        state.autoFocus = getAutoFocus();
        state.flash = getFlash();
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setFacing(ss.facing);
        setAspectRatio(ss.ratio);
        setAutoFocus(ss.autoFocus);
        setFlash(ss.flash);
    }


    private void initAttributeSet(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CameraView, defStyleAttr, R.style.Widget_CameraView);
        if (typedArray != null) {
            mAdjustViewBounds = typedArray.getBoolean(R.styleable.CameraView_android_adjustViewBounds, false);
            faceID = typedArray.getInt(R.styleable.CameraView_facing, Constants.FACING_BACK);
            aspectRatio = typedArray.getString(R.styleable.CameraView_aspectRatio);
            autoFocus = typedArray.getBoolean(R.styleable.CameraView_autoFocus, true);
            flash = typedArray.getInt(R.styleable.CameraView_flash, Constants.FLASH_AUTO);
            typedArray.recycle();
        }

    }

    /**
     * 初始化camera相关参数
     */
    private void initCameraParameters(Context context) {
        setAutoFocus(autoFocus);
        setFlash(flash);
        if (aspectRatio != null) {
            setAspectRatio(AspectRatio.parse(aspectRatio));
        } else {
            setAspectRatio(Constants.DEFAULT_ASPECT_RATIO);
        }
        setFacing(faceID);
        detector = new OrientationDetector(context) {
            @Override
            public void onDisplayOrientationChanged(int displayOrientation) {
                if (mImpl != null) {
                    mImpl.setDisplayOrientation(displayOrientation);
                }
            }
        };
    }

    /**
     * 开始预览
     */
    public void start() {
        if (mImpl != null) {
            if (!mImpl.start()) {
                Parcelable state = onSaveInstanceState();
                // Camera2 uses legacy hardware layer; fall back to Camera1
                mImpl = new Camera1(mCallbacks, createPreviewImpl(getContext()));
                onRestoreInstanceState(state);
                mImpl.start();
            }
        }
    }

    /**
     * 停止预览
     */
    public void stop() {
        if (mImpl != null) {
            mImpl.stop();
        }
    }

    /**
     * 摄像头是否打开
     */
    public boolean isCameraOpened() {
        return mImpl != null && mImpl.isCameraOpened();
    }

    /**
     * 设置是前置还是后置摄像头
     */
    public void setFacing(int id) {
        if (mImpl != null) {
            mImpl.setFacing(id);
        }
    }

    public int getFacing() {
        return mImpl == null ? -1 : mImpl.getFacing();
    }

    public boolean getAutoFocus() {
        return mImpl != null && mImpl.getAutoFocus();
    }

    /**
     * 设置自动聚焦
     */
    public void setAutoFocus(boolean autoFocus) {
        if (mImpl != null) {
            mImpl.setAutoFocus(autoFocus);
        }

    }

    public int getFlash() {
        return mImpl == null ? -1 : mImpl.getFlash();
    }

    /**
     * 设置闪光灯
     */
    public void setFlash(int flash) {
        if (mImpl != null) {
            mImpl.setFlash(flash);
        }

    }

    /**
     * 设置画面比例
     */
    public void setAspectRatio(AspectRatio aspectRatio) {
        if (mImpl != null) {
            mImpl.setAspectRatio(aspectRatio);
        }

    }

    /**
     * 获取比例
     */
    public AspectRatio getAspectRatio() {
        if (mImpl != null) {
            return mImpl.getAspectRatio();
        }
        return null;
    }

    public Set<AspectRatio> getSupportedAspectRatios() {
        return mImpl.getSupportedAspectRatios();
    }

    public void addCallbacks(Callback callback) {
        if (mCallbacks != null) {
            mCallbacks.add(callback);
        }
    }

    public void removeCallbacks(Callback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }

    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (mAdjustViewBounds != adjustViewBounds) {
            mAdjustViewBounds = adjustViewBounds;
            requestLayout();
        }
    }

    public boolean getAdjustViewBounds() {
        return mAdjustViewBounds;
    }

    public void takePicture() {
        mImpl.takePicture();
    }

    private static class SavedState extends BaseSavedState {
        private int facing;
        private AspectRatio ratio;
        private boolean autoFocus;
        private int flash;

        public SavedState(Parcel source, ClassLoader loader) {
            super(source);
            facing = source.readInt();
            ratio = source.readParcelable(loader);
            autoFocus = source.readByte() != 0;
            flash = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(facing);
            out.writeParcelable(ratio, 0);
            out.writeByte((byte) (autoFocus ? 1 : 0));
            out.writeInt(flash);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return createFromParcel(in, null);
            }

            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

        };

    }

    /**
     * Callback for monitoring events about {@link CameraView}
     */
    private class CallbackBridge implements CameraViewImpl.Callback {
        private List<Callback> callbackList = new ArrayList<>();
        private boolean mRequestLayoutOnOpen;

        @Override
        public void onCameraOpened() {
            if (mRequestLayoutOnOpen) {
                mRequestLayoutOnOpen = false;
                requestLayout();
            }
            for (Callback callback : callbackList) {
                callback.onCameraOpened(CameraView.this);
            }
        }

        @Override
        public void onCameraClosed() {
            for (Callback callback : callbackList) {
                callback.onCameraClosed(CameraView.this);
            }
        }

        @Override
        public void onPictureTaken(byte[] data) {
            for (Callback callback : callbackList) {
                callback.onPictureTaken(CameraView.this, data);
            }
        }

        public void add(Callback callback) {
            this.callbackList.add(callback);
        }

        public void remove(Callback callback) {
            this.callbackList.remove(callback);
        }

        public void reserveRequestLayoutOnOpen() {
            mRequestLayoutOnOpen = true;
        }
    }

    public static abstract class Callback {
        public void onCameraOpened(CameraView cameraView) {
        }

        public void onCameraClosed(CameraView cameraView) {
        }

        public void onPictureTaken(CameraView cameraView, byte[] data) {
        }
    }
}
