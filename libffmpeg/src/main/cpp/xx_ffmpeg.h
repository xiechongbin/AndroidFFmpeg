//
// Created by cxy on 2019/7/25.
//
#include <jni.h>

//java虚拟机
static JavaVM *jvm = NULL;
static jclass j_clazz = NULL;
static jclass gInterfaceObject = NULL;
static jclass h_clazz = NULL;//全局对象

void callJavaMethodOnExecuted(JNIEnv *env, jclass clazz, int ret);

void callJavaMethodProgress(JNIEnv *env, jclass clazz, float ret);

void ffmpeg_exec_progress_callback(float progress);

int ffmpeg_thread_run_cmd(int cmdLength, char **argv);

void ffmpeg_thread_exit(int ret);

void ffmpeg_exec_finish_callback(int ret);

void ffmpeg_thread_cancel();

void initClassHelper(JNIEnv *env, jobject *objptr);

