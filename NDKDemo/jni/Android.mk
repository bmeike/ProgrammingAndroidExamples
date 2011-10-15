LOCAL_PATH := $(call my-dir)

# this is our sample library
include $(CLEAR_VARS)

LOCAL_MODULE    := sample_lib
LOCAL_SRC_FILES := samplelib/sample_lib.c
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/samplelib

include $(BUILD_STATIC_LIBRARY)

# sample uses the sample lib we created as well as the cpufeatures lib
include $(CLEAR_VARS)

LOCAL_MODULE    := sample
LOCAL_SRC_FILES := sample.c
LOCAL_LDLIBS    := -llog
LOCAL_STATIC_LIBRARIES := sample_lib cpufeatures

include $(BUILD_SHARED_LIBRARY)

# this is our sample native activity
include $(CLEAR_VARS)

LOCAL_MODULE    := sample_native_activity
LOCAL_SRC_FILES := sample_nativeactivity.c
LOCAL_LDLIBS    := -llog -landroid
LOCAL_STATIC_LIBRARIES := android_native_app_glue

include $(BUILD_SHARED_LIBRARY)

# this is our sample native activity with opengl
include $(CLEAR_VARS)

LOCAL_MODULE    := sample_native_activity_opengl
LOCAL_SRC_FILES := sample_nativeactivity_opengl.c
LOCAL_LDLIBS    := -llog -landroid -lEGL -lGLESv1_CM
LOCAL_STATIC_LIBRARIES := android_native_app_glue

include $(BUILD_SHARED_LIBRARY)

$(call import-module,android/native_app_glue)
$(call import-module,cpufeatures)
