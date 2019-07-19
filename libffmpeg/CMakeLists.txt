cmake_minimum_required(VERSION 3.4.1)

add_library(
        ffmpeg_jni
        SHARED
        src/main/cpp/include/cmdutils.c
        src/main/cpp/include/ffmpeg.c
        src/main/cpp/include/ffmpeg_hw.c
        src/main/cpp/include/ffmpeg_opt.c
        src/main/cpp/include/ffmpeg_filter.c
)

add_library(
        avcodec
        SHARED
        IMPORTED
)

add_library(
        avdevice
        SHARED
        IMPORTED
)
add_library(
        avfilter
        SHARED
        IMPORTED
)


add_library(
        avformat
        SHARED
        IMPORTED
)


add_library(
        avutil
        SHARED
        IMPORTED
)

add_library(
        postproc
        SHARED
        IMPORTED
)

add_library(
        swresample
        SHARED
        IMPORTED
)

add_library(
        swscale
        SHARED
        IMPORTED
)


if (${ANDROID_ABI} STREQUAL "armeabi")
    set_target_properties(
            avcodec
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavcodec.so
    )
    set_target_properties(
            avdevice
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavdevice.so
    )

    set_target_properties(
            avfilter
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavfilter.so
    )

    set_target_properties(
            avformat
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavformat.so
    )

    set_target_properties(
            avutil
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavutil.so
    )
    set_target_properties(
            postproc
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libpostproc.so
    )

    set_target_properties(
            swresample
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libswresample.so
    )

    set_target_properties(
            swscale
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libswscale.so
    )
endif (${ANDROID_ABI} STREQUAL "armeabi")


if (${ANDROID_ABI} STREQUAL "armeabi-v7a")

    set_target_properties(
            avcodec
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libavcodec.so
    )

    set_target_properties(
            avdevice
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libavdevice.so
    )

    set_target_properties(
            avfilter
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libavfilter.so
    )

    set_target_properties(
            avformat
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libavformat.so
    )

    set_target_properties(
            avutil
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libavutil.so
    )

    set_target_properties(
            postproc
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libpostproc.so
    )

    set_target_properties(
            swresample
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libswresample.so
    )

    set_target_properties(
            swscale
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libswscale.so
    )

endif (${ANDROID_ABI} STREQUAL "armeabi-v7a")

if (${ANDROID_ABI} STREQUAL "arm64-v8a")
    set_target_properties(
            avcodec
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a/libavcodec.so
    )

    set_target_properties(
            avdevice
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a/libavdevice.so
    )

    set_target_properties(
            avfilter
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a/libavfilter.so
    )

    set_target_properties(
            avformat
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a/libavformat.so
    )

    set_target_properties(
            avutil
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a/libavutil.so
    )

    set_target_properties(
            postproc
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a/libpostproc.so
    )

    set_target_properties(
            swresample
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a/libswresample.so
    )

    set_target_properties(
            swscale
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a/libswscale.so
    )

endif (${ANDROID_ABI} STREQUAL "arm64-v8a")

if (${ANDROID_ABI} STREQUAL "x86")
    set_target_properties(
            avcodec
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86/libavcodec.so
    )

    set_target_properties(
            avdevice
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86/libavdevice.so
    )

    set_target_properties(
            avfilter
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86/libavfilter.so
    )

    set_target_properties(
            avformat
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86/libavformat.so
    )

    set_target_properties(
            avutil
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86/libavutil.so
    )

    set_target_properties(
            postproc
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86/libpostproc.so
    )

    set_target_properties(
            swresample
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86/libswresample.so
    )

    set_target_properties(
            swscale
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86/libswscale.so
    )
endif (${ANDROID_ABI} STREQUAL "x86")

if (${ANDROID_ABI} STREQUAL "x86_64")
    set_target_properties(
            avcodec
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86_64/libavcodec.so
    )

    set_target_properties(
            avdevice
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86_64/libavdevice.so
    )

    set_target_properties(
            avfilter
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86_64/libavfilter.so
    )

    set_target_properties(
            avformat
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86_64/libavformat.so
    )

    set_target_properties(
            avutil
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86_64/libavutil.so
    )

    set_target_properties(
            postproc
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86_64/libpostproc.so
    )

    set_target_properties(
            swresample
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86_64/libswresample.so
    )

    set_target_properties(
            swscale
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86_64/libswscale.so
    )

endif (${ANDROID_ABI} STREQUAL "x86_64")

include_directories(
        $../{CMAKE_SOURCE_DIR}/ffmpeg-4.1.3/

)

find_library(
        log-lib
        log)

target_link_libraries(
        ffmpeg_jni
        avcodec
        avdevice
        avfilter
        avformat
        avutil
        postproc
        swresample
        swscale
        ${log-lib})