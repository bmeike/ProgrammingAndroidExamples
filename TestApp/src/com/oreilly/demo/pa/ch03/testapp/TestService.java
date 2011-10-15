package com.oreilly.demo.pa.ch03.testapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TestService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
