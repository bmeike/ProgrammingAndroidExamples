#include <jni.h>
#include <android/log.h>
#include <android_native_app_glue.h>

#include <EGL/egl.h>
#include <GLES/gl.h>

// usage of log
#define  LOGINFO(x...)  __android_log_print(ANDROID_LOG_INFO,"SampleNativeActivityWOpenGL",x)

struct eglengine {
    EGLDisplay display;
    EGLSurface surface;
    EGLContext context;
};

// initialize the egl engine
static int engine_init_display(struct android_app* app, struct eglengine* engine) {
    const EGLint attribs[] = {
            EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
            EGL_BLUE_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_RED_SIZE, 8,
            EGL_NONE
    };
    EGLint w, h, dummy, format;
    EGLint numConfigs;
    EGLConfig config;
    EGLSurface surface;
    EGLContext context;

    EGLDisplay display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    eglInitialize(display, 0, 0);
    eglChooseConfig(display, attribs, &config, 1, &numConfigs);
    eglGetConfigAttrib(display, config, EGL_NATIVE_VISUAL_ID, &format);

    ANativeWindow_setBuffersGeometry(app->window, 0, 0, format);

    surface = eglCreateWindowSurface(display, config, app->window, NULL);
    context = eglCreateContext(display, config, NULL, NULL);

    if (eglMakeCurrent(display, surface, surface, context) == EGL_FALSE) {
        LOGINFO("eglMakeCurrent FAIL");
        return -1;
    }

    eglQuerySurface(display, surface, EGL_WIDTH, &w);
    eglQuerySurface(display, surface, EGL_HEIGHT, &h);

    engine->display = display;
    engine->context = context;
    engine->surface = surface;

    glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);
    glEnable(GL_CULL_FACE);
    glShadeModel(GL_SMOOTH);
    glDisable(GL_DEPTH_TEST);

    return 0;
}

// draw to the screen
static void engine_color_screen(struct eglengine* engine) {
    if (engine->display == NULL) {
        return;
    }

    glClearColor(255, 0, 0, 1);  // lets make the screen all red
    glClear(GL_COLOR_BUFFER_BIT);

    eglSwapBuffers(engine->display, engine->surface);
}

// when things need to be terminated
static void engine_terminate(struct eglengine* engine) {
    if (engine->display != EGL_NO_DISPLAY) {
        eglMakeCurrent(engine->display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
        if (engine->context != EGL_NO_CONTEXT) {
            eglDestroyContext(engine->display, engine->context);
        }
        if (engine->surface != EGL_NO_SURFACE) {
            eglDestroySurface(engine->display, engine->surface);
        }
        eglTerminate(engine->display);
    }
    engine->display = EGL_NO_DISPLAY;
    engine->context = EGL_NO_CONTEXT;
    engine->surface = EGL_NO_SURFACE;
}

// handle commands
static void custom_handle_cmd(struct android_app* app, int32_t cmd) {
	struct eglengine* engine = (struct eglengine*)app->userData;
	switch(cmd) {
	   case APP_CMD_INIT_WINDOW:  // things are starting up... lets initialize the engine and color the screen
	        if (app->window != NULL) {
	            engine_init_display(app, engine);
		    engine_color_screen(engine);
	        }
            break;
	    case APP_CMD_TERM_WINDOW:  // things are ending lets clean up the engine
            	engine_terminate(engine);
            break;
	}
}

// handle input
static int32_t custom_handle_input(struct android_app* app, AInputEvent* event) {
    if (AInputEvent_getType(event) == AINPUT_EVENT_TYPE_MOTION) { // we see a motion event and we log it
	LOGINFO("Motion Event: x %f / y %f", AMotionEvent_getX(event, 0), AMotionEvent_getY(event, 0));
	return 1;
    } 
    return 0;  
}

// This is the function that application code must implement, representing the main entry to the app.
void android_main(struct android_app* state) {
    // Make sure glue isn't stripped.
    app_dummy();

    // here we add the eglengine to the app
    struct eglengine engine;
    memset(&engine, 0, sizeof(engine));    
    state->userData = &engine;   // set engine as userdata so we can reference

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
