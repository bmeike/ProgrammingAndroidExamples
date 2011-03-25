#include <jni.h>
#include <android/log.h>
#include <android_native_app_glue.h>

// usage of log
#define  LOGINFO(x...)  __android_log_print(ANDROID_LOG_INFO,"SampleNativeActivity",x)

// handle commands
static void custom_handle_cmd(struct android_app* app, int32_t cmd) {
	switch(cmd) {
	   case APP_CMD_INIT_WINDOW:
		LOGINFO("App Init Window");
		break;
	}
}

// handle input
static int32_t custom_handle_input(struct android_app* app, AInputEvent* event) {
    if (AInputEvent_getType(event) == AINPUT_EVENT_TYPE_MOTION) {  // we see a motion event and we log it
	LOGINFO("Motion Event: x %f / y %f", AMotionEvent_getX(event, 0), AMotionEvent_getY(event, 0));
	return 1;
    } 
    return 0;  
}

// This is the function that application code must implement, representing the main entry to the app.
void android_main(struct android_app* state) {
    // Make sure glue isn't stripped.
    app_dummy();

    int events;
    state->onAppCmd = custom_handle_cmd;  // set up so when commands happen we call our custom handler
    state->onInputEvent = custom_handle_input; // set up so when input happen we call our custom handler

    while (1) {
        struct android_poll_source* source;

        // we block for events
        while (ALooper_pollAll(-1, NULL, &events, (void**)&source) >= 0) {

            // Process this event.
            if (source != NULL) {
                source->process(state, source);
            }

            // Check if we are exiting.
            if (state->destroyRequested != 0) {
                LOGINFO("We are exiting");
                return;
            }
        }
    }
}
