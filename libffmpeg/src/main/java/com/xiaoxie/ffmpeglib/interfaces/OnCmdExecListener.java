package com.xiaoxie.ffmpeglib.interfaces;

/**
 * 命令执行时的回调
 * Created by xcb on 2019/7/25.
 */
public interface OnCmdExecListener {
    void onSuccess(String result);

    void onFailure();

    void onProgress(float progress);
}
