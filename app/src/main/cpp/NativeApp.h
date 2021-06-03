//
// Created by lizongtai on 2021/6/3.
//

#ifndef CARDBOARD_IMU_TRACKING_NATIVEAPP_H
#define CARDBOARD_IMU_TRACKING_NATIVEAPP_H

#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <assert.h>
#include <jni.h>
#include <pthread.h>
#include <android/native_window.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <GLES3/gl3.h>
#include <GLES3/gl3ext.h>

#include "IMU_CardBoard/head_tracker.h"

class NativeApp {
public:
    NativeApp(jobject jAct);

    ~NativeApp();
    // variables used by OC messageQ
    bool init;
    bool SurfaceReady;
    pthread_t NativeThread;
    bool ReadyToExit;

    cardboard::HeadTracker *head_tracker_ = NULL;

    JNIEnv *Jni;
    jobject jActivity;    //Global reference for Activity object
    jclass javaClass;

    //Methods used by OC messageQ

    static void *ThreadStarter(void *param);

    void ThreadFunction();

    void StartNativeThread(void *param);

    void StopNativeThread();

    void Native_SensorPause();

    void Native_SensorResume();

    std::vector<float> getOutput();
};


#endif //CARDBOARD_IMU_TRACKING_NATIVEAPP_H
