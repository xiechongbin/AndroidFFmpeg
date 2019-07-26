package com.xiaoxie.ffmpeglib;

/**
 * 命令执行时的回调
 * Created by xcb on 2019/7/25.
 */
public interface OnCmdExecListner {
    void onSuccess();

    void onFailure();

    void onProgress(float progress);
}
