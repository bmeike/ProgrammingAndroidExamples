package com.finchframework.finch;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

/**
 * Provides the framework's Application subclass. This illustrates what
 * you may need to do in an Application subclass.
 * <p/>
 * To get this class instantiated, you must refer to it in the
 * application tag of the manifest.
 */
public class FinchApplication extends Application {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate() {
        // First, call the parent class
        super.onCreate();

        // This is a place to put code that must manage storage across
        // multiple activities, but it's better to keep most things in a
        // database, rather than in memory
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onTerminate() {
        Log.i(TAG, "onTerminate");

    }

    @Override
    public void onLowMemory() {
        // In-memory caches should be thrown overboard here
        Log.i(TAG, "onLowMemory");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG, "onConifgurationChanged");
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, newConfig.toString());
        }
    }
}
