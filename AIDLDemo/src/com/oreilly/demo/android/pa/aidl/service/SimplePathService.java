/* $Id: $
 */
package com.oreilly.demo.android.pa.aidl.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.oreilly.demo.android.pa.aidl.service.PathService;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:bmeike@callmeike.net">Blake Meike</a>
 */
public class SimplePathService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("PathService", "onCreate()");
    }

    @Override
    public void onDestroy() {
        super.onCreate();
        Log.d("PathService", "onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("PathService", "onBind()");

        return new PathService.Stub() {

            @Override
            public void deletePoint(String name)
            throws RemoteException
            {
                SimplePathService.this.deletePoint(name);
            }

            @Override
            public String getPoint(String name)
            throws RemoteException
            {
                return SimplePathService.this.getPoint(name);
            }

            @Override
            public void setPoint(String name, String path)
            throws RemoteException
            {
                SimplePathService.this.setPoint(name, path);
            }
        };
    }

    void deletePoint(String name) {
        Log.d("PathService", "deletePoint(): " + name);
    }

    String getPoint(String name) {
        Log.d("PathService", "getPoint(): " + name);
        return "Service says: 'Hi!'"; // verify connectivity
    }

    void setPoint(String name, String path) {
        Log.d("PathService", "setPoint(): " + name + ", " + path);
    }
}
