//
//ffmpeg 单独播放视频
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
#include "include/libswresample/swresample.h"
#include <android/native_window_jni.h>
#include <unistd.h>
#include <stdbool.h>

JNIEXPORT jint JNICALL
Java_com_xiaoxie_ffmpeglib_FFmpegJniBridge_playSound(JNIEnv *env, jclass type,
                                                     jstring videoPath) {
    //获取到文件路径
    const char *input = (*env)->GetStringUTFChars(env, videoPath, false);
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
    //获取到整个内容过后找到里面的音频流
    int audio_index = -1;
    for (int i = 0; i < avFormatContext->nb_streams; ++i) {
        if (avFormatContext->streams[i]->codec->codec_type == AVMEDIA_TYPE_AUDIO) {
            audio_index = i;
        }
    }
    //对视频流进行解码
    //获取解码器上下文
    AVCodecContext *avCodecContext = avFormatContext->streams[audio_index]->codec;
    //获取解码器
    AVCodec *avCodec = avcodec_find_decoder(avCodecContext->codec_id);
    //打开解码器
    error = avcodec_open2(avCodecContext, avCodec, NULL);
    if (error < 0) {
        LOGE("打开解码器失败 errorCode= %d", error);
        return error;
    }
    //申请分配AVPacket
    AVPacket *packet = (AVPacket *) av_malloc(sizeof(AVPacket));

    //申请分配一个AVFrame结构体,AVFrame结构体一般用于存储原始数据，指向解码后的原始帧
    AVFrame *avFrame = av_frame_alloc();
    //缓冲器
    uint8_t *output_buffer = (uint8_t *) av_malloc(44100 * 2);

    struct SwrContext *swrContext = swr_alloc();
    //输出的声道布局（立体声）
    uint64_t out_ch_layout = AV_CH_LAYOUT_STEREO;
    //输出采样位数  16位
    enum AVSampleFormat out_formate = AV_SAMPLE_FMT_S16;

    //输出的采样率必须与输入相同
    int out_sample_rate = avCodecContext->sample_rate;

    //swr_alloc_set_opts将PCM源文件的采样格式转换为自己希望的采样格式
    swr_alloc_set_opts(swrContext, out_ch_layout, out_formate, out_sample_rate,
                       avCodecContext->channel_layout, avCodecContext->sample_fmt,
                       avCodecContext->sample_rate, 0,
                       NULL);

    swr_init(swrContext);

    //    获取通道数  2
    int out_channel_nb = av_get_channel_layout_nb_channels(AV_CH_LAYOUT_STEREO);

    //    反射得到Class类型
    jclass david_player = (*env)->FindClass(env, "com/xiaoxie/ffmpeglib/videoplay/MusicPlayWithAudioTrack");

    jmethodID construct_id = (*env)->GetMethodID(env, david_player, "<init>", "()V");

    //    反射得到createAudio方法
    jmethodID createAudio = (*env)->GetMethodID(env, david_player, "createTrack", "(II)V");
    //创建类的实力
    jobject obj = (*env)->NewObject(env, david_player, construct_id);
//    反射调用createAudio
    (*env)->CallVoidMethod(env, obj, createAudio, 44100, out_channel_nb);


    jmethodID audio_write = (*env)->GetMethodID(env, david_player, "playTrack", "([BI)V");


    int got_frame;

    while (av_read_frame(avFormatContext, packet) >= 0) {
        if (packet->stream_index == audio_index) {
            //            解码  mp3   编码格式frame----pcm   frame
            avcodec_decode_audio4(avCodecContext, avFrame, &got_frame, packet);
            if (got_frame) {
                swr_convert(swrContext, &output_buffer, 44100 * 2, (const uint8_t **) avFrame->data,
                            avFrame->nb_samples);
//                缓冲区的大小
                int size = av_samples_get_buffer_size(NULL, out_channel_nb, avFrame->nb_samples,
                                                      AV_SAMPLE_FMT_S16, 1);
                jbyteArray audio_sample_array = (*env)->NewByteArray(env, size);
                (*env)->SetByteArrayRegion(env, audio_sample_array, 0, size,
                                           (const jbyte *) output_buffer);

                (*env)->CallVoidMethod(env, obj, audio_write, audio_sample_array, size);
                (*env)->DeleteLocalRef(env, audio_sample_array);
            }
        }
    }
    av_frame_free(&avFrame);
    swr_free(&swrContext);
    avcodec_close(avCodecContext);
    avformat_close_input(&avFormatContext);
    (*env)->ReleaseStringUTFChars(env, videoPath, input);
}
