/**
 * Created by xiaoxie on 2019/7/25.
 */

#include <string.h>
#include "include/ffmpeg.h"
#include <libavcodec/avcodec.h>
#include <assert.h>
#include <unistd.h>
#include "xx_ffmpeg.h"
#include "android_log.h"

pthread_t tid;
int count = 0;
char **argvs = NULL;

JNIEXPORT jstring JNICALL
Java_com_xiaoxie_ffmpeglib_FFmpegJniBridge_getVersionInfo(JNIEnv *env, jclass type) {
    char info[1024] = {0};
    sprintf(info, "%s\n", av_version_info());
    return (*env)->NewStringUTF(env, info);
}

JNIEXPORT jstring JNICALL
Java_com_xiaoxie_ffmpeglib_FFmpegJniBridge_getConfigInfo(JNIEnv *env, jclass type) {
    char info[10000] = {0};
    sprintf(info, "%s\n", avcodec_configuration());
    return (*env)->NewStringUTF(env, info);
}

JNIEXPORT jint JNICALL
Java_com_xiaoxie_ffmpeglib_FFmpegJniBridge_invokeCommands(JNIEnv *env, jclass type,
                                                          jobjectArray commands) {
    int argc = (*env)->GetArrayLength(env, commands);
    char *argv[argc];
    int i;
    for (i = 0; i < argc; i++) {
        jstring js = (jstring) ((*env)->GetObjectArrayElement(env, commands, i));
        argv[i] = (char *) (*env)->GetStringUTFChars(env, js, 0);
    }
    jint result = ffmpeg_exec(argc, argv);
    return result;
}

JNIEXPORT jint JNICALL
Java_com_xiaoxie_ffmpeglib_FFmpegJniBridge_invokeCommandSync(JNIEnv *env, jclass type,
                                                             jobjectArray commands) {
    h_clazz = (*env)->NewGlobalRef(env, type);
    int argc = (*env)->GetArrayLength(env, commands);
    char **argv = NULL;
    int i = 0;
    jstring *str = NULL;
    if (commands != NULL) {
        argv = (char **) malloc(sizeof(char *) * argc);
        str = (jstring *) malloc(sizeof(jstring) * argc);
        for (i = 0; i < argc; i++) {
            str[i] = (jstring) ((*env)->GetObjectArrayElement(env, commands, i));
            argv[i] = (char *) (*env)->GetStringUTFChars(env, str[i], 0);
        }
    }
    //建线程 执行ffmpeg 命令
    ffmpeg_thread_run_cmd(argc, argv);

    free(str);
    return 0;
}

void *thread(void *arg) {
    int res = ffmpeg_exec(count, argvs);
    ffmpeg_thread_exit(res);
    return ((void *) 0);
}

/**
 * 创建一个子线程来执行命令
 */
int ffmpeg_thread_run_cmd(int argc, char **argv) {
    count = argc;
    argvs = argv;
    int res = pthread_create(&tid, NULL, thread, NULL);
    if (res != 0) {
        LOGE("can't create thread: %s ", strerror(res));
        return -1;
    }
    return 0;
}

/**
 * 退出线程
 */
void ffmpeg_thread_exit(int ret) {
    ffmpeg_exec_finish_callback(ret);
    pthread_exit("ffmpeg_thread_exit");
}

/**
 * 取消线程
 */
void ffmpeg_thread_cancel() {
    void *ret = NULL;
    pthread_join(tid, &ret);
}

/**
* 回调执行java方法
*/
void callJavaMethodOnExecuted(JNIEnv *env, jclass clazz, int ret) {
    if (clazz == NULL) {
        return;
    }
    jmethodID methodID = (*env)->GetStaticMethodID(env, clazz, "onExecuted", "(I)V");
    if (methodID == NULL) {
        return;
    }
    (*env)->CallStaticVoidMethod(env, clazz, methodID, ret);
}

void callJavaMethodProgress(JNIEnv *env, jclass clazz, float ret) {
    if (clazz == NULL) {
        return;
    }
    jmethodID methodID = (*env)->GetStaticMethodID(env, clazz, "onProgress", "(F)V");
    if (methodID == NULL) {
        return;
    }
    //调用该java方法
    (*env)->CallStaticVoidMethod(env, clazz, methodID, ret);
}

/**
 * 线程回调
 */
void ffmpeg_exec_finish_callback(int ret) {
    JNIEnv *env;
    //附加到当前线程从JVM中取出JNIEnv, C/C++从子线程中直接回到Java里的方法时  必须经过这个步骤
    (*jvm)->AttachCurrentThread(jvm, (void **) &env, NULL);
    callJavaMethodOnExecuted(env, h_clazz, ret);

    //完毕-脱离当前线程
    (*jvm)->DetachCurrentThread(jvm);
}

void ffmpeg_exec_progress_callback(float progress) {
    JNIEnv *env;
    (*jvm)->AttachCurrentThread(jvm, (void **) &env, NULL);
    callJavaMethodProgress(env, h_clazz, progress);
    (*jvm)->DetachCurrentThread(jvm);
}


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = -1;
    LOGE("JNI_OnLoad");
    //判断jni版本相关
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("ERROR: JNI_OnLoad GetEnv failed");
        return -1;
    }
    assert(env != NULL);
    jvm = vm;
    result = JNI_VERSION_1_4;
    initClassHelper(env, &gInterfaceObject);
    return result;
}

void initClassHelper(JNIEnv *env, jobject *objptr) {
    j_clazz = (*env)->FindClass(env, "com/xiaoxie/ffmpeglib/FFmpegJniBridge");
    if (j_clazz == NULL) {
        return;
    }
    //默认的构造方法 不传递参数
    jmethodID construct_methodID = (*env)->GetMethodID(env, j_clazz, "<init>", "()V");
    if (construct_methodID == NULL) {
        return;
    }
    //查找实例方法的ID
    jmethodID methodID = (*env)->GetMethodID(env, j_clazz, "test", "(I)V");
    //创建类的实力
    jobject obj = (*env)->NewObject(env, j_clazz, construct_methodID);
    if (obj == NULL) {
        return;
    }
    (*env)->CallVoidMethod(env, obj, methodID, 4);
}








