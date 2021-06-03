#include <jni.h>
#include <string>
#include <android/log.h>
#include "NativeApp.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_tongji_lzt_cardboard_1imu_1tracking_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jlong JNICALL
Java_tongji_lzt_cardboard_1imu_1tracking_MainActivity_createNativeApp(JNIEnv *env, jobject jAct) {
    NativeApp *app = new NativeApp(jAct);
//    __android_log_print(ANDROID_LOG_DEBUG, "native", "createNativeApp");
    app->StartNativeThread(app);
    return (jlong) app;
}
extern "C"
JNIEXPORT void JNICALL
Java_tongji_lzt_cardboard_1imu_1tracking_MainActivity_nativePause(
        JNIEnv *env,
        jobject /* this */,
        jlong appPtr) {
    ((NativeApp *) appPtr)->Native_SensorPause();
}
extern "C"
JNIEXPORT void JNICALL
Java_tongji_lzt_cardboard_1imu_1tracking_MainActivity_nativeResume(
        JNIEnv *env,
        jobject /* this */,
        jlong appPtr) {
    ((NativeApp *) appPtr)->Native_SensorResume();
}
extern "C"
JNIEXPORT void JNICALL
Java_tongji_lzt_cardboard_1imu_1tracking_MainActivity_nativeStop(JNIEnv *env, jobject thiz,
                                                                 jlong appPtr) {
    ((NativeApp *) appPtr)->StopNativeThread();
}
extern "C"
JNIEXPORT jfloatArray JNICALL
Java_tongji_lzt_cardboard_1imu_1tracking_MainActivity_getValue(JNIEnv *env, jobject thiz,
                                                               jlong appPtr) {
    std::vector<float> res = ((NativeApp *) appPtr)->getOutput();
//    __android_log_print(ANDROID_LOG_DEBUG, "EKF", "GetPose Position:%f,%f,%f--Rotation:%f,%f,%f,%f",
//                        res[0], res[1],res[2],
//                        res[3], res[4], res[5], res[6]);
    jfloatArray resArray = env->NewFloatArray(res.size());

    env->SetFloatArrayRegion(resArray, 0, res.size(), res.data());
    return resArray;
}
extern "C"
JNIEXPORT jfloatArray JNICALL
Java_tongji_lzt_cardboard_1imu_1tracking_MainActivity_getTransformationMatrix(JNIEnv *env,
                                                                              jobject thiz,
                                                                              jlong appPtr) {
    std::vector<float> res = ((NativeApp *) appPtr)->getOutput();
    jfloatArray resArray = env->NewFloatArray(res.size());

    env->SetFloatArrayRegion(resArray, 0, res.size(), res.data());
    return resArray;
}