package com.mabeijianxi.smallvideorecord2;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;

import com.mabeijianxi.smallvideorecord2.jniinterface.FFmpegBridge;
import com.mabeijianxi.smallvideorecord2.model.MediaObject;
import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.usb.usbcameracommon.UVCCameraHandlerMultiSurface;
import com.serenegiant.usb.widget.CameraViewInterface;
import com.serenegiant.usb.widget.UVCCameraTextureView;

import java.nio.ByteBuffer;


/**
 * 视频录制：边录制边底层处理视频（旋转和裁剪）
 */
public class UVCMediaRecorderNative extends MediaRecorderBase implements MediaRecorder.OnErrorListener, FFmpegBridge.FFmpegStateListener, IFrameCallback, USBMonitor.OnDeviceConnectListener {

    /**
     * 视频后缀
     */
    private static final String VIDEO_SUFFIX = ".ts";

    /**
     * uvc摄像头相关
     */
    private final Object mSync = new Object();
    private USBMonitor mUSBMonitor;
    private UVCCameraHandlerMultiSurface mCameraHandler;
    private UVCCameraTextureView mUVCCameraView;
    private Activity activity;


    private final CameraViewInterface.Callback mCallback = new CameraViewInterface.Callback() {
        @Override
        public void onSurfaceCreated(final CameraViewInterface view, final Surface surface) {
            if (mCameraHandler != null) {
                mCameraHandler.addSurface(surface.hashCode(), surface, false);
            }
        }

        @Override
        public void onSurfaceChanged(final CameraViewInterface view, final Surface surface, final int width, final int height) {

        }

        @Override
        public void onSurfaceDestroy(final CameraViewInterface view, final Surface surface) {
            synchronized (mSync) {
                if (mCameraHandler != null) {
                    mCameraHandler.removeSurface(surface.hashCode());
                }
            }
        }
    };

    public UVCMediaRecorderNative(Activity activity, UVCCameraTextureView uvcCameraTextureView) {
        this.activity = activity;
        this.mUVCCameraView = uvcCameraTextureView;
        FFmpegBridge.registFFmpegStateListener(this);
        initUsbCamera();
    }


    private void initUsbCamera() {
        if (mUSBMonitor == null) {
            mUSBMonitor = new USBMonitor(activity, this);
        }
        registerUsbMonitor();
        mUVCCameraView.setCallback(mCallback);
        synchronized (mSync) {
            mCameraHandler = UVCCameraHandlerMultiSurface.createHandler(activity, mUVCCameraView, 2, UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, 1);
        }
        mCameraHandler.setFrameCallback(this);
    }

    /**
     * 开始录制
     */
    @Override
    public MediaObject.MediaPart startRecord() {
        int vCustomFormat;
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            vCustomFormat = FFmpegBridge.ROTATE_90_CROP_LT;
        } else {
            vCustomFormat = FFmpegBridge.ROTATE_270_CROP_LT_MIRROR_LR;
        }

        FFmpegBridge.prepareJXFFmpegEncoder(mMediaObject.getOutputDirectory(), mMediaObject.getBaseName(), vCustomFormat, mSupportedPreviewWidth, SMALL_VIDEO_HEIGHT, SMALL_VIDEO_WIDTH, SMALL_VIDEO_HEIGHT, mFrameRate, mVideoBitrate);

        MediaObject.MediaPart result = null;

        if (mMediaObject != null) {

            result = mMediaObject.buildMediaPart(mCameraId, VIDEO_SUFFIX);
            String cmd = String.format("filename = \"%s\"; ", result.mediaPath);
            //如果需要定制非480x480的视频，可以启用以下代码，其他vf参数参考ffmpeg的文档：

            if (mAudioRecorder == null && result != null) {
                mAudioRecorder = new AudioRecorder(this);
                mAudioRecorder.start();
            }
            mRecording = true;

        }
        return result;
    }

    /**
     * 停止录制
     */
    @Override
    public void stopRecord() {
        super.stopRecord();
        if (mOnEncodeListener != null) {
            mOnEncodeListener.onEncodeStart();
        }
        FFmpegBridge.recordEnd();
    }

    /**
     * 数据回调(内置摄像头)
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        super.onPreviewFrame(data, camera);
    }


    /**
     * usb摄像头数据回调
     */
    @Override
    public void onFrame(ByteBuffer frame) {
        if (mRecording) {
            int length = frame.limit();
            byte[] data = new byte[length];
            frame.get(data, 0, length);
            frame.flip();
            FFmpegBridge.encodeFrame2H264(data);
            mPreviewFrameCallCount++;
        }
    }

    /**
     * 预览成功，设置视频输入输出参数
     */
    @Override
    protected void onStartPreviewSuccess() {
//        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
//            UtilityAdapter.RenderInputSettings(mSupportedPreviewWidth, SMALL_VIDEO_WIDTH, 0, UtilityAdapter.FLIPTYPE_NORMAL);
//        } else {
//            UtilityAdapter.RenderInputSettings(mSupportedPreviewWidth, SMALL_VIDEO_WIDTH, 180, UtilityAdapter.FLIPTYPE_HORIZONTAL);
//        }
//        UtilityAdapter.RenderOutputSettings(SMALL_VIDEO_WIDTH, SMALL_VIDEO_HEIGHT, mFrameRate, UtilityAdapter.OUTPUTFORMAT_YUV | UtilityAdapter.OUTPUTFORMAT_MASK_MP4/*| UtilityAdapter.OUTPUTFORMAT_MASK_HARDWARE_ACC*/);
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            Log.e("stopRecord", e);
        } catch (Exception e) {
            Log.e("stopRecord", e);
        }
        if (mOnErrorListener != null)
            mOnErrorListener.onVideoError(what, extra);
    }

    /**
     * 接收音频数据，传递到底层
     */
    @Override
    public void receiveAudioData(byte[] sampleBuffer, int len) {
        if (mRecording && len > 0) {
            FFmpegBridge.encodeFrame2AAC(sampleBuffer);
        }
    }

    @Override
    public void allRecordEnd() {

        final boolean captureFlag = FFMpegUtils.captureThumbnails(mMediaObject.getOutputTempTranscodingVideoPath(), mMediaObject.getOutputVideoThumbPath(), String.valueOf(CAPTURE_THUMBNAILS_TIME));

        if (mOnEncodeListener != null) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (captureFlag) {
                        mOnEncodeListener.onEncodeComplete();
                    } else {
                        mOnEncodeListener.onEncodeError();
                    }
                }
            }, 0);

        }

    }

    public void activityStop() {
        FFmpegBridge.unRegistFFmpegStateListener(this);
    }

    /**
     * usb设备关联上
     */
    @Override
    public void onAttach(UsbDevice device) {
        Log.d("usb camera attach");
        if (mUSBMonitor != null) {
            boolean result = mUSBMonitor.requestPermission(device);
            Log.d("usb mUSBMonitor result =" + result);
        }
    }

    /**
     * usb设备失去关联
     */
    @Override
    public void onDetach(UsbDevice device) {
        Log.d("usb camera detach");
    }

    /**
     * usb设备连接上
     */
    @Override
    public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
        Log.d("usb camera onConnect");
        synchronized (mSync) {
            if (mCameraHandler == null) {
                mCameraHandler = UVCCameraHandlerMultiSurface.createHandler(activity, mUVCCameraView, 2, UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, 1);
                mCameraHandler.open(ctrlBlock);
                mCameraHandler.startPreview();
            } else {
                if (!mCameraHandler.isOpened()) {
                    mCameraHandler.open(ctrlBlock);
                    mCameraHandler.startPreview();
                }
            }
        }
    }

    /**
     * usb设备断开连接
     */
    @Override
    public void onDisconnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {
        Log.d("usb camera disConnect");
        synchronized (mSync) {
            if (mCameraHandler != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mCameraHandler.close();
                    }
                }).start();
            }
        }
    }

    /**
     * usb设备取消连接
     */
    @Override
    public void onCancel(UsbDevice device) {
        Log.d("usb camera cancel");
    }

    /**
     * 注册usbMonitor
     */
    private void registerUsbMonitor() {
        if (mUSBMonitor != null && !mUSBMonitor.isRegistered()) {
            mUSBMonitor.register();
        }
    }

    /**
     * 反注册usbMonitor
     */
    private void unRegisterUsbMonitor() {
        if (mUSBMonitor != null && mUSBMonitor.isRegistered()) {
            mUSBMonitor.unregister();
            mUSBMonitor = null;
        }
    }

}
