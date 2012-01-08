package com.oreilly.demo.android.pa.clientserver.client.sync.authsync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service {
    private static final Object lock = new Object();
    private static SyncAdapter adapter = null;

    @Override
    public void onCreate() {
        synchronized (lock) {
            if (adapter == null) {
            	adapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }
    
    @Override
    public void onDestroy() {
    	adapter = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return adapter.getSyncAdapterBinder();
    }
}
