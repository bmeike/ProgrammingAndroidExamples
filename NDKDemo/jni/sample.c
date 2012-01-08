#include "sample_lib.h"
#include <cpu-features.h>
#include <jni.h>
#include <android/log.h>

// usage of log
#define  LOGINFO(x...)  __android_log_print(ANDROID_LOG_INFO,"SampleJNI",x)


jdouble Java_com_oreilly_demo_android_pa_ndkdemo_SampleActivityWithNativeMethods_calculatePower( JNIEnv* env, jobject thisobject, jdouble x, jdouble y) {

    LOGINFO("Sample Debug Log Output");

    // we call sample-lib's calculate method
    return calculatePower(x, y);
}

jstring Java_com_oreilly_demo_android_pa_ndkdemo_SampleActivityWithNativeMethods_whatAmI(JNIEnv* env, jobject thisobject) {
	__android_log_print(ANDROID_LOG_WARN,"SampleJNI","Sample Warning Log Output");

	uint64_t cpu_features;

	if (android_getCpuFamily() != ANDROID_CPU_FAMILY_ARM) {
		return (*env)->NewStringUTF(env, "Not ARM");
	}

	cpu_features = android_getCpuFeatures();

	if ((cpu_features & ANDROID_CPU_ARM_FEATURE_ARMv7) != 0) {
		return (*env)->NewStringUTF(env, "ARMv7");
	} else if ((cpu_features & ANDROID_CPU_ARM_FEATURE_VFPv3) != 0) {
		return (*env)->NewStringUTF(env, "ARM w VFPv3 support");
	} else if ((cpu_features & ANDROID_CPU_ARM_FEATURE_NEON) != 0) {
		return (*env)->NewStringUTF(env, "ARM w NEON support");
	}
	
	return (*env)->NewStringUTF(env, "Unknown");
}
