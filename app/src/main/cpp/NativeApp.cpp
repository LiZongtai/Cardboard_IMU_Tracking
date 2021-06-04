//
// Created by lizongtai on 2021/6/3.
//

#include <unistd.h>            // for usleep
#include <android/log.h>
#include <stdlib.h>
#include <stdio.h>
#include "NativeApp.h"

std::array<float, 3> temp_out_position = {0.0f, 0.0f, 0.0f};      //x,y,z
std::array<float, 4> temp_out_orientation = {0.0f, 0.0f, 0.0f, 0.0f};  //x,y,z,w

JavaVM *VrLibJavaVM;

bool Native_OnLoad(JavaVM *JavaVm_) {
    __android_log_print(ANDROID_LOG_DEBUG, "EKF", "Native_OnLoad()");

    if (JavaVm_ == NULL) {
        __android_log_print(ANDROID_LOG_DEBUG, "EKF", "JavaVm == NULL");
    }
    if (VrLibJavaVM != NULL) {
        // Should we silently return instead?
        __android_log_print(ANDROID_LOG_DEBUG, "EKF", "Native_OnLoad() called , return false");
        return false;
    }

    VrLibJavaVM = JavaVm_;
    JNIEnv *jni;
    if (JNI_OK != VrLibJavaVM->GetEnv(reinterpret_cast<void **>(&jni), JNI_VERSION_1_6)) {
        __android_log_print(ANDROID_LOG_DEBUG, "EKF", "Creating temporary JNIEnv");
        const jint rtn = VrLibJavaVM->AttachCurrentThread(&jni, 0);
        if (rtn != JNI_OK) {
            __android_log_print(ANDROID_LOG_DEBUG, "EKF", "AttachCurrentThread returned %i", rtn);
        }
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, "EKF", "Using caller's JNIEnv");
    }
    return true;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {

    return JNI_VERSION_1_6;
}

NativeApp::NativeApp(jobject jAct) : init(false) {
    if (VrLibJavaVM != NULL) {
        VrLibJavaVM->AttachCurrentThread(&Jni, 0);
    }
    __android_log_print(ANDROID_LOG_DEBUG, "native", "createNativeApp");
    head_tracker_ = new cardboard::HeadTracker();
    ReadyToExit = false;
}

NativeApp::~NativeApp() {
    delete head_tracker_;
    head_tracker_ = NULL;
}

static constexpr uint64_t kNanosInSeconds = 1000000000;
constexpr uint64_t kPredictionTimeWithoutVsyncNanos = 50000000;

long GetMonotonicTimeNano() {
    struct timespec res;
    clock_gettime(CLOCK_MONOTONIC, &res);
    return (res.tv_sec * kNanosInSeconds) + res.tv_nsec;
}

void NativeApp::StartNativeThread(void *param) {
    __android_log_print(ANDROID_LOG_DEBUG, "EKF", "StartNativeAppThread");

    const int createErr = pthread_create(&NativeThread,
                                         NULL /* default attributes */, &ThreadStarter, param);
    if (createErr != 0) {
        __android_log_print(ANDROID_LOG_DEBUG, "EKF", "Pthread_create returned %i", createErr);
    }
}

void *NativeApp::ThreadStarter(void *param) {
    ((NativeApp *) param)->ThreadFunction();
    return NULL;
}

void NativeApp::ThreadFunction() {
    if (VrLibJavaVM != NULL) {
        VrLibJavaVM->AttachCurrentThread(&Jni, 0);
        __android_log_print(ANDROID_LOG_DEBUG, "EKF", "NativeAppThread AttachCurrentThread");
    }
    pthread_setname_np(pthread_self(), "NativeAppThread");
    while (!ReadyToExit) {
        // Process incoming messages until queue is empty
        std::array<float, 3> out_position;      //x,y,z
        std::array<float, 4> out_orientation;   //x,y,z,w
        long monotonic_time_nano = GetMonotonicTimeNano();
        monotonic_time_nano += kPredictionTimeWithoutVsyncNanos;

        head_tracker_->GetPose(monotonic_time_nano, temp_out_position, temp_out_orientation);

//        __android_log_print(ANDROID_LOG_DEBUG, "EKF", "GetPose Position:%f,%f,%f--Rotation:%f,%f,%f,%f", out_position[0], out_position[1],
//            out_position[2],
//            out_orientation[0], out_orientation[1], out_orientation[2], out_orientation[3]);
//        temp_out_position = out_position;
//        temp_out_orientation = out_orientation;

        //TODO: Update frame rate control
        timespec t, rem;
        t.tv_sec = 0;
        t.tv_nsec = 12 * 1e6;
        nanosleep(&t, &rem);

    }
    if (VrLibJavaVM != NULL) {
        VrLibJavaVM->DetachCurrentThread();
    }

}

void NativeApp::Native_SensorPause() {
    head_tracker_->Pause();
}

void NativeApp::Native_SensorResume() {
    head_tracker_->Resume();
}

void NativeApp::StopNativeThread() {
    //NativeMsQueue.SendPrintf( MsType_Destroy,"quit " );
    const int ret = pthread_join(NativeThread, NULL);
    if (ret != 0) {
        __android_log_print(ANDROID_LOG_DEBUG, "EKF", "Failed to join NativeThread (%i)", ret);
    }
}

std::vector<float> NativeApp::getOutput() {
    std::vector<float> res;
    res.clear();
    res.push_back(temp_out_position[0]);
    res.push_back(temp_out_position[1]);
    res.push_back(temp_out_position[2]);
    res.push_back(temp_out_orientation[0]);
    res.push_back(temp_out_orientation[1]);
    res.push_back(temp_out_orientation[2]);
    res.push_back(temp_out_orientation[3]);
    return res;
}

std::array<float,4> NativeApp::getRvec() {
    return temp_out_orientation;
}

std::array<float,3> NativeApp::getTvec() {
    return temp_out_position;
}
