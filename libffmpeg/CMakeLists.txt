cmake_minimum_required(VERSION 3.4.1)

add_library(
        ffmpeg_jni
        SHARED
        src/main/cpp/include/cmdutils.c
        src/main/cpp/include/ffmpeg.c
        src/main/cpp/include/ffmpeg_hw.c
        src/main/cpp/include/ffmpeg_opt.c
        src/main/cpp/include/ffmpeg_filter.c
        src/main/cpp/xx_ffmpeg.c
        src/main/cpp/xx_ffmpeg_play_video.c
        src/main/cpp/xx_ffmpeg_play_audio.c
)

find_library(
        android-lib
        android
)

add_library(
        ffmpeg_core
        SHARED
        IMPORTED
)

if (${ANDROID_ABI} STREQUAL "armeabi")
    set_target_properties(
            ffmpeg_core
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libffmpeg_core.so
    )
endif (${ANDROID_ABI} STREQUAL "armeabi")


if (${ANDROID_ABI} STREQUAL "armeabi-v7a")

    set_target_properties(
            ffmpeg_core
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libffmpeg_core.so
    )

endif (${ANDROID_ABI} STREQUAL "armeabi-v7a")

if (${ANDROID_ABI} STREQUAL "arm64-v8a")
    set_target_properties(
            ffmpeg_core
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a/libffmpeg_core.so
    )

endif (${ANDROID_ABI} STREQUAL "arm64-v8a")

if (${ANDROID_ABI} STREQUAL "x86")
    set_target_properties(
            ffmpeg_core
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86/libffmpeg_core.so
    )

endif (${ANDROID_ABI} STREQUAL "x86")

if (${ANDROID_ABI} STREQUAL "x86_64")
    set_target_properties(
            ffmpeg_core
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86_64/libffmpeg_core.so
    )
endif (${ANDROID_ABI} STREQUAL "x86_64")

include_directories(
        ../ffmpeg-4.1.3/
)

find_library(
        log-lib
        log)

target_link_libraries(
        ffmpeg_jni
        ffmpeg_core
        OpenSLES
        ${log-lib}
        ${android-lib})