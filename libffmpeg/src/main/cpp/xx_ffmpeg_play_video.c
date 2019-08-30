//
//ffmpeg 播放视频
// Created by cxy on 2019-08-30.
//

#include <jni.h>
#include <string.h>
#include "android_log.h"

//编码
#include "libavcodec/avcodec.h"
//封装格式处理
#include "libavformat/avformat.h"
//像素处理
#include "libswscale/swscale.h"
#include <android/native_window_jni.h>
#include <unistd.h>
#include <stdbool.h>

JNIEXPORT jint JNICALL
Java_com_xiaoxie_ffmpeglib_FFmpegJniBridge_render(JNIEnv *env, jclass type, jstring inputVideo,
                                                  jobject surface) {
    //获取到视频路径
    const char *input = (*env)->GetStringUTFChars(env, inputVideo, false);
    //注册各大组件
    av_register_all();
    //获取到上下文
    AVFormatContext *avFormatContext = avformat_alloc_context();

    //打开视频地址并获取里面的内容(解封装)
    int error;
    char buff[] = "";
    error = avformat_open_input(&avFormatContext, input, NULL, NULL);
    if (error < 0) {
        av_strerror(error, buff, 1024);
        LOGE("打开视频失败 errorCode= %d errorInfo = %s", error, buff);
        return error;
    }

    error = avformat_find_stream_info(avFormatContext, NULL);
    if (error < 0) {
        LOGE("获取视频内容失败 errorCode= %d", error);
        return error;
    }
    //获取到整个内容过后找到里面的视频流
    int video_index = -1;
    for (int i = 0; i < avFormatContext->nb_streams; ++i) {
        if (avFormatContext->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
            video_index = i;
        }
    }
    //对视频流进行解码
    //获取解码器上下文
    AVCodecContext *avCodecContext = avFormatContext->streams[video_index]->codec;
    //获取解码器
    AVCodec *avCodec = avcodec_find_decoder(avCodecContext->codec_id);
    //打开解码器
    error = avcodec_open2(avCodecContext, avCodec, NULL);
    if (error < 0) {
        LOGE("打开解码器失败 errorCode= %d", error);
        return error;
    }
    //申请分配AVPacket
    AVPacket *avPacket = (AVPacket *) av_malloc(sizeof(AVPacket));
    av_init_packet(avPacket);

    //申请分配一个AVFrame结构体,AVFrame结构体一般用于存储原始数据，指向解码后的原始帧
    AVFrame *avFrame = av_frame_alloc();
    AVFrame *rgb_frame = av_frame_alloc();//分配一个AVFrame结构体，指向存放转换成rgb后的帧

    //缓冲器
    uint8_t *output_buffer = (uint8_t *) av_malloc(
            avpicture_get_size(AV_PIX_FMT_RGBA, avCodecContext->width, avCodecContext->height));

    //与缓存区相关联，设置rgb_frame缓存区
    avpicture_fill((AVPicture *) rgb_frame, output_buffer, AV_PIX_FMT_RGBA, avCodecContext->width,
                   avCodecContext->height);

    struct SwsContext *swsContext = sws_getContext(avCodecContext->width, avCodecContext->height,
                                                   avCodecContext->pix_fmt,
                                                   avCodecContext->width, avCodecContext->height,
                                                   AV_PIX_FMT_RGBA,
                                                   SWS_BICUBIC, NULL, NULL, NULL);

    //取到nativewindow
    ANativeWindow *aNativeWindow = ANativeWindow_fromSurface(env, surface);
    if (aNativeWindow == 0) {
        LOGE("nativewindow取到失败");
        return -1;
    }
    ANativeWindow_Buffer nativeWindowBuffer;
    int frameCount;
    while (av_read_frame(avFormatContext, avPacket) >= 0) {
        LOGE("解码 %d", avPacket->stream_index);
        LOGE("VINDEX %d", video_index);
        if (avPacket->stream_index == video_index) {
            avcodec_decode_video2(avCodecContext, avFrame, &frameCount, avPacket);
            LOGE("解码中....  %d", frameCount);
            if (frameCount) {
                LOGE("转换并绘制");
                //绘制之前配置nativewindow
                LOGE("width = %d  height = %d", avCodecContext->width, avCodecContext->height);
                ANativeWindow_setBuffersGeometry(aNativeWindow, avCodecContext->width,
                                                 avCodecContext->height, WINDOW_FORMAT_RGBA_8888);
                ANativeWindow_lock(aNativeWindow, &nativeWindowBuffer, NULL);

                //转换为rgb格式
                sws_scale(swsContext, (const uint8_t *const *) avFrame->data, avFrame->linesize, 0,
                          avFrame->height, rgb_frame->data,
                          rgb_frame->linesize);
                uint8_t *dst = (uint8_t *) nativeWindowBuffer.bits;
                // 拿到一行有多少个字节 RGBA
                int destStride = nativeWindowBuffer.stride * 4;
                //像素数据的首地址
                uint8_t *src = rgb_frame->data[0];
                // 实际内存一行数量
                int srcStride = rgb_frame->linesize[0];
                for (int i = 0; i < avCodecContext->height; ++i) {
                    //将rgb_frame中每一行的数据复制给nativewindow
                    memcpy(dst + i * destStride, src + i * srcStride, srcStride);
                }
                ANativeWindow_unlockAndPost(aNativeWindow);
            }
        }
    }
    av_free_packet(avPacket);
    //释放
    ANativeWindow_release(aNativeWindow);
    av_frame_free(&avFrame);
    av_frame_free(&rgb_frame);
    avcodec_close(avCodecContext);
    avformat_free_context(avFormatContext);
    (*env)->ReleaseStringUTFChars(env, inputVideo, input);
}
