/*
 *  UVCCamera
 *  library and sample to access to UVC web camera on non-rooted Android device
 *
 * Copyright (c) 2014-2017 saki t_saki@serenegiant.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *  All files in the folder are under this Apache License, Version 2.0.
 *  Files in the libjpeg-turbo, libusb, libuvc, rapidjson folder
 *  may have a different license, see the respective files.
 */

package com.serenegiant.usb.usbcameracommon;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.usb.UsbDevice;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.usb.encoder.MediaAudioEncoder;
import com.serenegiant.usb.encoder.MediaEncoder;
import com.serenegiant.usb.encoder.MediaMuxerWrapper;
import com.serenegiant.usb.encoder.MediaSurfaceEncoder;
import com.serenegiant.usb.encoder.MediaVideoBufferEncoder;
import com.serenegiant.usb.encoder.MediaVideoEncoder;
import com.serenegiant.usb.widget.CameraViewInterface;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

abstract class AbstractUVCCameraHandler extends Handler {
    private static final boolean DEBUG = true;
    private static final String TAG = "AbsUVCCameraHandler";
    private static final String KEY_NEED_AUDIO = "key_need_audio";
    private static final String KEY_PATH = "key_path";
    private static final String KEY_DIR = "dir";
    private static final String KEY_FILE_NAME = "fileName";

    public interface CameraCallback {
        void onOpen();

        void onClose();

        void onStartPreview();

        void onStopPreview();

        void onStartRecording();

        void onStopRecording();

        void onError(final Exception e);
    }

    private static final int MSG_OPEN = 0;
    private static final int MSG_CLOSE = 1;
    private static final int MSG_PREVIEW_START = 2;
    private static final int MSG_PREVIEW_STOP = 3;
    private static final int MSG_CAPTURE_STILL = 4;
    private static final int MSG_CAPTURE_START = 5;
    private static final int MSG_CAPTURE_STOP = 6;
    private static final int MSG_MEDIA_UPDATE = 7;
    private static final int MSG_RELEASE = 9;
    private static final int MSG_FRAME_DATA = 10;

    private final WeakReference<CameraThread> mWeakThread;
    private volatile boolean mReleased;

    protected AbstractUVCCameraHandler(final CameraThread thread) {
        mWeakThread = new WeakReference<CameraThread>(thread);
    }

    public void setFrameCallback(IFrameCallback callback) {
        sendMessage(obtainMessage(MSG_FRAME_DATA, callback));
    }

    public int getWidth() {
        final CameraThread thread = mWeakThread.get();
        return thread != null ? thread.getWidth() : 0;
    }

    public int getHeight() {
        final CameraThread thread = mWeakThread.get();
        return thread != null ? thread.getHeight() : 0;
    }

    public boolean isOpened() {
        final CameraThread thread = mWeakThread.get();
        return thread != null && thread.isCameraOpened();
    }

    public boolean isPreviewing() {
        final CameraThread thread = mWeakThread.get();
        return thread != null && thread.isPreviewing();
    }

    public boolean isRecording() {
        final CameraThread thread = mWeakThread.get();
        return thread != null && thread.isRecording();
    }

    public boolean isEqual(final UsbDevice device) {
        final CameraThread thread = mWeakThread.get();
        return (thread != null) && thread.isEqual(device);
    }

    public boolean isCameraThread() {
        final CameraThread thread = mWeakThread.get();
        return thread != null && (thread.getId() == Thread.currentThread().getId());
    }

    public boolean isReleased() {
        final CameraThread thread = mWeakThread.get();
        return mReleased || (thread == null);
    }

    public void checkReleased() {
        if (isReleased()) {
            throw new IllegalStateException("already released");
        }
    }

    public void open(final USBMonitor.UsbControlBlock ctrlBlock) {
        checkReleased();
        sendMessage(obtainMessage(MSG_OPEN, ctrlBlock));
    }

    public void close() {
        if (DEBUG) Log.v(TAG, "close:");
        if (isOpened()) {
            stopPreview();
            sendEmptyMessage(MSG_CLOSE);
        }
        if (DEBUG) Log.v(TAG, "close:finished");
    }

    public void resize(final int width, final int height) {
        checkReleased();
        throw new UnsupportedOperationException("does not support now");
    }

    public void startPreview(final Object surface) {
        checkReleased();
        if (!((surface instanceof SurfaceHolder) || (surface instanceof Surface) || (surface instanceof SurfaceTexture))) {
            throw new IllegalArgumentException("surface should be one of SurfaceHolder, Surface or SurfaceTexture");
        }
        sendMessage(obtainMessage(MSG_PREVIEW_START, surface));
    }

    public void stopPreview() {
        if (DEBUG) Log.v(TAG, "stopPreview:");
        removeMessages(MSG_PREVIEW_START);
        stopRecording();
        if (isPreviewing()) {
            final CameraThread thread = mWeakThread.get();
            if (thread == null) return;
            synchronized (thread.mSync) {
                sendEmptyMessage(MSG_PREVIEW_STOP);
                if (!isCameraThread()) {
                    // wait for actually preview stopped to avoid releasing Surface/SurfaceTexture
                    // while preview is still running.
                    // therefore this method will take a time to execute
                    try {
                        thread.mSync.wait();
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (DEBUG) Log.v(TAG, "stopPreview:finished");
    }

    public void captureStill() {
        checkReleased();
        sendMessage(obtainMessage(MSG_CAPTURE_STILL, 1, 0));
    }

    public void captureStill(String path) {
        checkReleased();
        sendMessage(obtainMessage(MSG_CAPTURE_STILL, 2, 0, path));
    }

    public void captureStill(String dir, String fileName) {
        checkReleased();
        Message message = new Message();
        message.what = MSG_CAPTURE_STILL;
        message.arg1 = 3;
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DIR, dir);
        bundle.putString(KEY_FILE_NAME, fileName);
        message.setData(bundle);
        sendMessage(message);
    }

    public void startRecording() {
        checkReleased();
        sendMessage(obtainMessage(MSG_CAPTURE_START, 1, 0));
    }

    public void startRecording(String path, boolean needAudio) {
        checkReleased();
        Message message = new Message();
        message.what = MSG_CAPTURE_START;
        message.arg1 = 2;
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_NEED_AUDIO, needAudio);
        bundle.putString(KEY_PATH, path);
        message.setData(bundle);
        sendMessage(message);
    }

    public void stopRecording() {
        sendEmptyMessage(MSG_CAPTURE_STOP);
    }

    public void release() {
        mReleased = true;
        close();
        sendEmptyMessage(MSG_RELEASE);
    }

    public void addCallback(final CameraCallback callback) {
        checkReleased();
        if (!mReleased && (callback != null)) {
            final CameraThread thread = mWeakThread.get();
            if (thread != null) {
                thread.mCallbacks.add(callback);
            }
        }
    }

    public void removeCallback(final CameraCallback callback) {
        if (callback != null) {
            final CameraThread thread = mWeakThread.get();
            if (thread != null) {
                thread.mCallbacks.remove(callback);
            }
        }
    }

    public void updateMedia(final String path) {
        sendMessage(obtainMessage(MSG_MEDIA_UPDATE, path));
    }

    public boolean checkSupportFlag(final long flag) {
        checkReleased();
        final CameraThread thread = mWeakThread.get();
        return thread != null && thread.mUVCCamera != null && thread.mUVCCamera.checkSupportFlag(flag);
    }

    public int getValue(final int flag) {
        checkReleased();
        final CameraThread thread = mWeakThread.get();
        final UVCCamera camera = thread != null ? thread.mUVCCamera : null;
        if (camera != null) {
            if (flag == UVCCamera.PU_BRIGHTNESS) {
                return camera.getBrightness();
            } else if (flag == UVCCamera.PU_CONTRAST) {
                return camera.getContrast();
            }
        }
        throw new IllegalStateException();
    }

    public int setValue(final int flag, final int value) {
        checkReleased();
        final CameraThread thread = mWeakThread.get();
        final UVCCamera camera = thread != null ? thread.mUVCCamera : null;
        if (camera != null) {
            if (flag == UVCCamera.PU_BRIGHTNESS) {
                camera.setBrightness(value);
                return camera.getBrightness();
            } else if (flag == UVCCamera.PU_CONTRAST) {
                camera.setContrast(value);
                return camera.getContrast();
            } else if (flag == UVCCamera.CTRL_ZOOM_ABS) {
                camera.setZoom(value);
                return camera.getZoom();
            }
        }
        throw new IllegalStateException();
    }

    public int resetValue(final int flag) {
        checkReleased();
        final CameraThread thread = mWeakThread.get();
        final UVCCamera camera = thread != null ? thread.mUVCCamera : null;
        if (camera != null) {
            if (flag == UVCCamera.PU_BRIGHTNESS) {
                camera.resetBrightness();
                return camera.getBrightness();
            } else if (flag == UVCCamera.PU_CONTRAST) {
                camera.resetContrast();
                return camera.getContrast();
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public void handleMessage(final Message msg) {
        final CameraThread thread = mWeakThread.get();
        if (thread == null) return;
        switch (msg.what) {
            case MSG_OPEN:
                thread.handleOpen((USBMonitor.UsbControlBlock) msg.obj);
                break;
            case MSG_CLOSE:
                thread.handleClose();
                break;
            case MSG_PREVIEW_START:
                thread.handleStartPreview(msg.obj);
                break;
            case MSG_PREVIEW_STOP:
                thread.handleStopPreview();
                break;
            case MSG_CAPTURE_STILL:
                if (msg.arg1 == 1) {
                    thread.handleCaptureStill(null);
                } else if (msg.arg1 == 2) {
                    thread.handleCaptureStill((String) msg.obj);
                } else if (msg.arg1 == 3) {
                    Bundle b = msg.getData();
                    thread.handleCaptureStill(b.getString(KEY_DIR), b.getString(KEY_FILE_NAME));
                }
                break;
            case MSG_CAPTURE_START:
                if (msg.arg1 == 1) {
                    thread.handleStartRecording();
                } else if (msg.arg1 == 2) {
                    Bundle b = msg.getData();
                    thread.handleStartRecording(b.getString(KEY_PATH), b.getBoolean(KEY_NEED_AUDIO, true));
                }
                break;
            case MSG_CAPTURE_STOP:
                thread.handleStopRecording();
                break;
            case MSG_MEDIA_UPDATE:
                thread.handleUpdateMedia((String) msg.obj);
                break;
            case MSG_RELEASE:
                thread.handleRelease();
                break;
            case MSG_FRAME_DATA:
                thread.handleSetFrameCallback((IFrameCallback) msg.obj);
                break;
            default:
                throw new RuntimeException("unsupported message:what=" + msg.what);
        }
    }

    static final class CameraThread extends Thread {
        private final Object mSync = new Object();
        private final Class<? extends AbstractUVCCameraHandler> mHandlerClass;
        private final WeakReference<Activity> mWeakParent;
        private final WeakReference<CameraViewInterface> mWeakCameraView;
        private final int mEncoderType;
        private final Set<CameraCallback> mCallbacks = new CopyOnWriteArraySet<CameraCallback>();
        private int mWidth, mHeight, mPreviewMode;
        private float mBandwidthFactor;
        private boolean mIsPreviewing;
        private boolean mIsRecording;
        /**
         * shutter sound
         */
        private SoundPool mSoundPool;
        private int mSoundId;
        private AbstractUVCCameraHandler mHandler;
        /**
         * for accessing UVC camera
         */
        private UVCCamera mUVCCamera;
        /**
         * muxer for audio/video recording
         */
        private MediaMuxerWrapper mMuxer;
        private MediaVideoBufferEncoder mVideoEncoder;
        private IFrameCallback frameCallback;
        private boolean isRecording = false;
        private byte[] bytes;

        /**
         * @param clazz           Class extends AbstractUVCCameraHandler
         * @param parent          parent Activity
         * @param cameraView      for still capturing
         * @param encoderType     0: use MediaSurfaceEncoder, 1: use MediaVideoEncoder, 2: use MediaVideoBufferEncoder
         * @param width           preview width
         * @param height          preview height
         * @param format          either FRAME_FORMAT_YUYV(0) or FRAME_FORMAT_MJPEG(1)
         * @param bandwidthFactor 带宽比例
         */
        CameraThread(final Class<? extends AbstractUVCCameraHandler> clazz,
                     final Activity parent, final CameraViewInterface cameraView,
                     final int encoderType, final int width, final int height, final int format,
                     final float bandwidthFactor) {
            super("CameraThread");
            mHandlerClass = clazz;
            mEncoderType = encoderType;
            mWidth = width;
            mHeight = height;
            mPreviewMode = format;
            mBandwidthFactor = bandwidthFactor;
            mWeakParent = new WeakReference<Activity>(parent);
            mWeakCameraView = new WeakReference<CameraViewInterface>(cameraView);
            loadShutterSound(parent);
        }

        @Override
        protected void finalize() throws Throwable {
            Log.i(TAG, "CameraThread#finalize");
            super.finalize();
        }

        public AbstractUVCCameraHandler getHandler() {
            if (DEBUG) Log.v(TAG, "getHandler:");
            synchronized (mSync) {
                if (mHandler == null)
                    try {
                        mSync.wait();
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
            }
            return mHandler;
        }

        public int getWidth() {
            synchronized (mSync) {
                return mWidth;
            }
        }

        public int getHeight() {
            synchronized (mSync) {
                return mHeight;
            }
        }

        public void handleSetFrameCallback(IFrameCallback callback) {
            this.frameCallback = callback;
        }

        public boolean isCameraOpened() {
            synchronized (mSync) {
                return mUVCCamera != null;
            }
        }

        public boolean isPreviewing() {
            synchronized (mSync) {
                return mUVCCamera != null && mIsPreviewing;
            }
        }

        public boolean isRecording() {
            synchronized (mSync) {
                return (mUVCCamera != null) && (mMuxer != null);
            }
        }

        public boolean isEqual(final UsbDevice device) {
            return (mUVCCamera != null) && (mUVCCamera.getDevice() != null) && mUVCCamera.getDevice().equals(device);
        }

        public void handleOpen(final USBMonitor.UsbControlBlock ctrlBlock) {
            if (DEBUG) Log.v(TAG, "handleOpen:");
            handleClose();
            try {
                final UVCCamera camera = new UVCCamera();
                camera.open(ctrlBlock);
                synchronized (mSync) {
                    mUVCCamera = camera;
                }
                callOnOpen();
            } catch (final Exception e) {
                callOnError(e);
            }
            if (DEBUG)
                Log.i(TAG, "supportedSize:" + (mUVCCamera != null ? mUVCCamera.getSupportedSize() : null));
        }

        public void handleClose() {
            if (DEBUG) Log.v(TAG, "handleClose:");
            handleStopRecording();
            final UVCCamera camera;
            synchronized (mSync) {
                camera = mUVCCamera;
                mUVCCamera = null;
            }
            if (camera != null) {
                camera.stopPreview();
                camera.destroy();
                callOnClose();
            }
        }

        public void handleStartPreview(final Object surface) {
            if (DEBUG) Log.v(TAG, "handleStartPreview:");
            if ((mUVCCamera == null) || mIsPreviewing) return;
            try {
                mUVCCamera.setPreviewSize(mWidth, mHeight, 1, 31, mPreviewMode, mBandwidthFactor);
            } catch (final IllegalArgumentException e) {
                try {
                    // fallback to YUV mode
                    mUVCCamera.setPreviewSize(mWidth, mHeight, 1, 31, UVCCamera.DEFAULT_PREVIEW_MODE, mBandwidthFactor);
                } catch (final IllegalArgumentException e1) {
                    callOnError(e1);
                    return;
                }
            }
            if (surface instanceof SurfaceHolder) {
                mUVCCamera.setPreviewDisplay((SurfaceHolder) surface);
            }
            if (surface instanceof Surface) {
                mUVCCamera.setPreviewDisplay((Surface) surface);
            } else {
                mUVCCamera.setPreviewTexture((SurfaceTexture) surface);
            }
            mUVCCamera.startPreview();
            mUVCCamera.updateCameraParams();
            mUVCCamera.setFrameCallback(mIFrameCallback, UVCCamera.PIXEL_FORMAT_YUV420SP);
            synchronized (mSync) {
                mIsPreviewing = true;
            }
            callOnStartPreview();
        }

        public void handleStopPreview() {
            if (DEBUG) Log.v(TAG, "handleStopPreview:");
            if (mIsPreviewing) {
                if (mUVCCamera != null) {
                    mUVCCamera.stopPreview();
                }
                synchronized (mSync) {
                    mIsPreviewing = false;
                    mSync.notifyAll();
                }
                callOnStopPreview();
            }
            if (DEBUG) Log.v(TAG, "handleStopPreview:finished");
        }

        public void handleCaptureStill(final String path) {
            if (DEBUG) Log.v(TAG, "handleCaptureStill:");
            final Activity parent = mWeakParent.get();
            if (parent == null) return;
            mSoundPool.play(mSoundId, 0.2f, 0.2f, 0, 0, 1.0f);    // play shutter sound
            try {
                final Bitmap bitmap = mWeakCameraView.get().captureStillImage();
                // get buffered output stream for saving a captured still image as a file on external storage.
                // the file name is came from current time.
                // You should use extension name as same as CompressFormat when calling Bitmap#compress.
                final File outputFile = TextUtils.isEmpty(path)
                        ? MediaMuxerWrapper.getCaptureFile(Environment.DIRECTORY_DCIM, ".png")
                        : new File(path);
                final BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile));
                try {
                    try {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                        os.flush();
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_MEDIA_UPDATE, outputFile.getPath()));
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                } finally {
                    os.close();
                }
            } catch (final Exception e) {
                callOnError(e);
            }
        }

        public void handleCaptureStill(final String path, final String fileName) {
            if (DEBUG) Log.v(TAG, "handleCaptureStill:");
            try {
                if (bytes == null) {
                    Log.v(TAG, "bytes is null:");
                    return;
                }
                byte[] b = bytes;
                final Bitmap bitmap = getColorOrgBitmap(b, mWidth, mHeight);
                // get buffered output stream for saving a captured still image as a file on external storage.
                // the file name is came from current time.
                // You should use extension name as same as CompressFormat when calling Bitmap#compress.
                if (bitmap == null) {
                    if (DEBUG) Log.v(TAG, "bitmap is null:");
                    return;
                }
                saveBitmap(path, bitmap, fileName);
            } catch (final Exception e) {
                Log.v(TAG, "error:" + e.getMessage());
                callOnError(e);
            }
        }

        public static Bitmap getColorOrgBitmap(byte[] data, int width, int height) {
            Bitmap bmp = null;
            try {
                YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
                Rect rc = new Rect(0, 0, width, height);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(rc, 95, stream);
                bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                stream.close();
            } catch (Exception ex) {
                Log.v(TAG, "error:" + ex.getMessage());
            }
            return bmp;
        }

        public static String saveBitmap(String dir, Bitmap bmImage, String fileName) {
            File file = new File(dir);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    Log.d("App", "failed to create directory");
                    return "";
                }
            }
            File file1 = new File(file.getAbsolutePath(), fileName);
            try {
                FileOutputStream fOut = new FileOutputStream(file1);
                bmImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                return "";
            }
            return file.getAbsolutePath();
        }

        public void handleStartRecording() {
            if (DEBUG) Log.v(TAG, "handleStartRecording:");
            try {
                if ((mUVCCamera == null) || (mMuxer != null)) return;
                final MediaMuxerWrapper muxer = new MediaMuxerWrapper(".mp4");    // if you record audio only, ".m4a" is also OK.
                MediaVideoBufferEncoder videoEncoder = null;
                switch (mEncoderType) {
                    case 1:    // for video capturing using MediaVideoEncoder
                        new MediaVideoEncoder(muxer, getWidth(), getHeight(), mMediaEncoderListener);
                        break;
                    case 2:    // for video capturing using MediaVideoBufferEncoder
                        videoEncoder = new MediaVideoBufferEncoder(muxer, getWidth(), getHeight(), mMediaEncoderListener);
                        break;
                    // case 0:	// for video capturing using MediaSurfaceEncoder
                    default:
                        new MediaSurfaceEncoder(muxer, getWidth(), getHeight(), mMediaEncoderListener);
                        break;
                }
                if (true) {
                    // for audio capturing
                    new MediaAudioEncoder(muxer, mMediaEncoderListener);
                }
                muxer.prepare();
                muxer.startRecording();
                if (videoEncoder != null) {
                    isRecording = true;
                }
                synchronized (mSync) {
                    mMuxer = muxer;
                    mVideoEncoder = videoEncoder;
                }
                callOnStartRecording();
            } catch (final IOException e) {
                callOnError(e);
                Log.e(TAG, "startCapture:", e);
            }
        }

        public void handleStartRecording(String path, boolean needAudio) {
            if (DEBUG) {
                Log.v(TAG, "handleStartRecording:");
            }
            try {
                if ((mUVCCamera == null) || (mMuxer != null)) return;
                final MediaMuxerWrapper muxer = new MediaMuxerWrapper(path, ".mp4");    // if you record audio only, ".m4a" is also OK.
                MediaVideoBufferEncoder videoEncoder = null;
                switch (mEncoderType) {
                    case 1:    // for video capturing using MediaVideoEncoder
                        new MediaVideoEncoder(muxer, getWidth(), getHeight(), mMediaEncoderListener);
                        break;
                    case 2:    // for video capturing using MediaVideoBufferEncoder
                        videoEncoder = new MediaVideoBufferEncoder(muxer, getWidth(), getHeight(), mMediaEncoderListener);
                        break;
                    // case 0:	// for video capturing using MediaSurfaceEncoder
                    default:
                        new MediaSurfaceEncoder(muxer, getWidth(), getHeight(), mMediaEncoderListener);
                        break;
                }
                if (needAudio) {
                    //for audio capturing
                    new MediaAudioEncoder(muxer, mMediaEncoderListener);
                }
                muxer.prepare();
                muxer.startRecording();
                if (videoEncoder != null) {
                    isRecording = true;
                }
                synchronized (mSync) {
                    mMuxer = muxer;
                    mVideoEncoder = videoEncoder;
                }
                callOnStartRecording();
            } catch (final IOException e) {
                callOnError(e);
                Log.e(TAG, "startCapture:", e);
            }
        }

        public void handleStopRecording() {
            if (DEBUG) Log.v(TAG, "handleStopRecording:mMuxer=" + mMuxer);
            final MediaMuxerWrapper muxer;
            synchronized (mSync) {
                muxer = mMuxer;
                mMuxer = null;
                mVideoEncoder = null;
                if (mUVCCamera != null) {
                    mUVCCamera.stopCapture();
                }
            }
            try {
                mWeakCameraView.get().setVideoEncoder(null);
            } catch (final Exception e) {
                e.printStackTrace();
            }
            if (muxer != null) {
                muxer.stopRecording();
                isRecording = false;
                // you should not wait here
                callOnStopRecording();
            }
        }

        private final IFrameCallback mIFrameCallback = new IFrameCallback() {
            @Override
            public void onFrame(final ByteBuffer frame) {
                if (frameCallback != null) {
                    frameCallback.onFrame(frame);
                }
                int length = frame.limit();
                byte[] data = new byte[length];
                frame.get(data, 0, length);
                frame.flip();
                bytes = data;

                if (isRecording) {
                    final MediaVideoBufferEncoder videoEncoder;
                    synchronized (mSync) {
                        videoEncoder = mVideoEncoder;
                    }
                    byte[] dest = new byte[length];
                    dest = NV21ToI420p(mWidth, mHeight, data, dest);
                    ByteBuffer newBuffer = ByteBuffer.wrap(dest);
                    if (videoEncoder != null) {
                        videoEncoder.frameAvailableSoon();
                        videoEncoder.encode(newBuffer);
                    }
                }
            }
        };

        private byte[] NV21ToI420p(int width, int height, byte[] src, byte[] dst) {
            int size = width * height;
            System.arraycopy(src, 0, dst, 0, size);
            int var8 = 0;
            for (int i = 0; i < size / 2; i += 2) {
                dst[size + var8] = src[size + i + 1];
                dst[size + size / 4 + var8] = src[size + i];
                ++var8;
            }
            return dst;
        }

        public void handleUpdateMedia(final String path) {
            if (DEBUG) Log.v(TAG, "handleUpdateMedia:path=" + path);
            final Activity parent = mWeakParent.get();
            final boolean released = (mHandler == null) || mHandler.mReleased;
            if (parent != null && parent.getApplicationContext() != null) {
                try {
                    if (DEBUG) Log.i(TAG, "MediaScannerConnection#scanFile");
                    MediaScannerConnection.scanFile(parent.getApplicationContext(), new String[]{path}, null, null);
                } catch (final Exception e) {
                    Log.e(TAG, "handleUpdateMedia:", e);
                }
                if (released || parent.isDestroyed())
                    handleRelease();
            } else {
                Log.w(TAG, "MainActivity already destroyed");
                // give up to add this movie to MediaStore now.
                // Seeing this movie on Gallery app etc. will take a lot of time.
                handleRelease();
            }
        }

        public void handleRelease() {
            if (DEBUG) Log.v(TAG, "handleRelease:mIsRecording=" + mIsRecording);
            handleClose();
            mCallbacks.clear();
            if (!mIsRecording) {
                mHandler.mReleased = true;
                Looper.myLooper().quit();
            }
            if (DEBUG) Log.v(TAG, "handleRelease:finished");
        }

        private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
            @Override
            public void onPrepared(final MediaEncoder encoder) {
                if (DEBUG) Log.v(TAG, "onPrepared:encoder=" + encoder);
                mIsRecording = true;
                if (encoder instanceof MediaVideoEncoder)
                    try {
                        mWeakCameraView.get().setVideoEncoder((MediaVideoEncoder) encoder);
                    } catch (final Exception e) {
                        Log.e(TAG, "onPrepared:", e);
                    }
                if (encoder instanceof MediaSurfaceEncoder)
                    try {
                        mWeakCameraView.get().setVideoEncoder((MediaSurfaceEncoder) encoder);
                        mUVCCamera.startCapture(((MediaSurfaceEncoder) encoder).getInputSurface());
                    } catch (final Exception e) {
                        Log.e(TAG, "onPrepared:", e);
                    }
            }

            @Override
            public void onStopped(final MediaEncoder encoder) {
                if (DEBUG) Log.v(TAG, "onStopped:encoder=" + encoder);
                if ((encoder instanceof MediaVideoEncoder)
                        || (encoder instanceof MediaSurfaceEncoder))
                    try {
                        mIsRecording = false;
                        final Activity parent = mWeakParent.get();
                        mWeakCameraView.get().setVideoEncoder(null);
                        synchronized (mSync) {
                            if (mUVCCamera != null) {
                                mUVCCamera.stopCapture();
                            }
                        }
                        final String path = encoder.getOutputPath();
                        if (!TextUtils.isEmpty(path)) {
                            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_MEDIA_UPDATE, path), 1000);
                        } else {
                            final boolean released = (mHandler == null) || mHandler.mReleased;
                            if (released || parent == null || parent.isDestroyed()) {
                                handleRelease();
                            }
                        }
                    } catch (final Exception e) {
                        Log.e(TAG, "onPrepared:", e);
                    }
            }
        };

        /**
         * prepare and load shutter sound for still image capturing
         */
        private void loadShutterSound(final Context context) {
            // get system stream type using reflection
            int streamType;
            try {
                final Class<?> audioSystemClass = Class.forName("android.media.AudioSystem");
                final Field sseField = audioSystemClass.getDeclaredField("STREAM_SYSTEM_ENFORCED");
                streamType = sseField.getInt(null);
            } catch (final Exception e) {
                streamType = AudioManager.STREAM_SYSTEM;    // set appropriate according to your app policy
            }
            if (mSoundPool != null) {
                try {
                    mSoundPool.release();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                mSoundPool = null;
            }
            // load shutter sound from resource
            mSoundPool = new SoundPool(2, streamType, 0);
            // mSoundId = mSoundPool.load(context, R.raw.camera_click, 1);
        }

        @Override
        public void run() {
            Looper.prepare();
            AbstractUVCCameraHandler handler = null;
            try {
                final Constructor<? extends AbstractUVCCameraHandler> constructor = mHandlerClass.getDeclaredConstructor(CameraThread.class);
                handler = constructor.newInstance(this);
            } catch (final NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                Log.w(TAG, e);
            }
            if (handler != null) {
                synchronized (mSync) {
                    mHandler = handler;
                    mSync.notifyAll();
                }
                Looper.loop();
                if (mSoundPool != null) {
                    mSoundPool.release();
                    mSoundPool = null;
                }
                if (mHandler != null) {
                    mHandler.mReleased = true;
                }
            }
            mCallbacks.clear();
            synchronized (mSync) {
                mHandler = null;
                mSync.notifyAll();
            }
        }

        private void callOnOpen() {
            for (final CameraCallback callback : mCallbacks) {
                try {
                    callback.onOpen();
                } catch (final Exception e) {
                    mCallbacks.remove(callback);
                    Log.w(TAG, e);
                }
            }
        }

        private void callOnClose() {
            for (final CameraCallback callback : mCallbacks) {
                try {
                    callback.onClose();
                } catch (final Exception e) {
                    mCallbacks.remove(callback);
                    Log.w(TAG, e);
                }
            }
        }

        private void callOnStartPreview() {
            for (final CameraCallback callback : mCallbacks) {
                try {
                    callback.onStartPreview();
                } catch (final Exception e) {
                    mCallbacks.remove(callback);
                    Log.w(TAG, e);
                }
            }
        }

        private void callOnStopPreview() {
            for (final CameraCallback callback : mCallbacks) {
                try {
                    callback.onStopPreview();
                } catch (final Exception e) {
                    mCallbacks.remove(callback);
                    Log.w(TAG, e);
                }
            }
        }

        private void callOnStartRecording() {
            for (final CameraCallback callback : mCallbacks) {
                try {
                    callback.onStartRecording();
                } catch (final Exception e) {
                    mCallbacks.remove(callback);
                    Log.w(TAG, e);
                }
            }
        }

        private void callOnStopRecording() {
            for (final CameraCallback callback : mCallbacks) {
                try {
                    callback.onStopRecording();
                } catch (final Exception e) {
                    mCallbacks.remove(callback);
                    Log.w(TAG, e);
                }
            }
        }

        private void callOnError(final Exception e) {
            for (final CameraCallback callback : mCallbacks) {
                try {
                    callback.onError(e);
                } catch (final Exception e1) {
                    mCallbacks.remove(callback);
                    Log.w(TAG, e);
                }
            }
        }
    }
}
