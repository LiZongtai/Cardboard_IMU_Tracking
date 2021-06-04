#include <jni.h>
#include <string>
#include <android/log.h>
#include "NativeApp.h"
#include <Eigen/Core>
#include <Eigen/Dense>
#include <opencv2/core/eigen.hpp>
#include <opencv2/opencv.hpp>
#include "Eigen/Eigen/Core"

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
Java_tongji_lzt_cardboard_1imu_1tracking_MainActivity_getTransMatrix(JNIEnv *env,
                                                                     jclass thiz,
                                                                     jlong appPtr) {
    std::array<float,4> rvec = ((NativeApp *) appPtr)->getRvec();
    std::array<float,3> tvec = ((NativeApp *) appPtr)->getTvec();
    //T
    cv::Mat T=(cv::Mat_<float>(4,4)<<1.0f,0.0f,0.0f,tvec[0],0.0f,1.0f,0.0f,tvec[1],0.0f,0.0f,1.0f,tvec[2],0.0f,0.0f,0.0f,1.0f);
    //R
    Eigen::Quaternion<float> quat(rvec[0],rvec[1],rvec[2],rvec[3]);
    Eigen::Matrix<float, 3, 3> rotM=quat.toRotationMatrix();
    float* rot = rotM.data();
    cv::Mat R=(cv::Mat_<float>(4,4)<< rot[0],rot[1],rot[2],0.0f,rot[3],rot[4],rot[5],0.0f,rot[6],rot[7],rot[8],0.0f,0.0f,0.0f,0.0f,1.0f);
//    cv::Mat AugmentedPart=(cv::Mat_<float>(1,4)<< 0.0f, 0.0f, 0.0f, 1.0f);

    cv::Mat trans=T*R;

    float *viewData = (float *) trans.data;
    jfloatArray result;
    result = env->NewFloatArray(16);
    env->SetFloatArrayRegion( result, 0, 16, viewData);
    return result;
}