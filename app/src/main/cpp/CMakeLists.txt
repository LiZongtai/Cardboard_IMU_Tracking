# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("cardboard_imu_tracking")
include_directories(.)
include_directories(IMU_CardBoard)
include_directories(Eigen)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp
        NativeApp.cpp
        IMU_CardBoard/head_tracker.cpp
        IMU_CardBoard/sensors/gyroscope_bias_estimator.cpp
        IMU_CardBoard/sensors/lowpass_filter.cpp
        IMU_CardBoard/sensors/mean_filter.cpp
        IMU_CardBoard/sensors/median_filter.cpp
        IMU_CardBoard/sensors/neck_model.cpp
        IMU_CardBoard/sensors/pose_prediction.cpp
        IMU_CardBoard/sensors/sensor_fusion_ekf.cpp
        IMU_CardBoard/sensors/android/device_accelerometer_sensor.cpp
        IMU_CardBoard/sensors/android/device_gyroscope_sensor.cpp
        IMU_CardBoard/sensors/android/sensor_event_producer.cpp
        IMU_CardBoard/util/matrix_3x3.cpp
        IMU_CardBoard/util/matrix_4x4.cpp
        IMU_CardBoard/util/matrixutils.cpp
        IMU_CardBoard/util/rotation.cpp
        IMU_CardBoard/util/vectorutils.cpp)


#设置OpenCV-android-sdk路径
set(pathtoOpenCV /home/lizongtai/Android/opencv-4.5.0-android-sdk/OpenCV-android-sdk/sdk)
#包含OpenCV头文件
include_directories(opencv-libs PRIVATE ${pathtoOpenCV}/native/jni/include)

#动态方式加载
add_library(lib_opencv STATIC IMPORTED)
#引入libopencv_java3.so文件
set_target_properties(lib_opencv
        PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libopencv_java4.so)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        native-lib
        lib_opencv #链接OpenCV库
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        android)