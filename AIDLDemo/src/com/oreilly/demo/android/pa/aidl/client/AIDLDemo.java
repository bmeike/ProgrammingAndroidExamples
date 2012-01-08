package com.oreilly.demo.android.pa.aidl.client;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.oreilly.demo.android.pa.aidl.R;
import com.oreilly.demo.android.pa.aidl.service.PathService;
import com.oreilly.demo.android.pa.aidl.parcelables.ParcelableList;


/**
 * AIDLDemo
 */
public class AIDLDemo extends Activity {
    Button button;
    PathService service;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(
            ComponentName conn,
            IBinder binder)
        {
            service = PathService.Stub.asInterface(binder);
            Log.d("AIDLDemo", "Connected: " + service);
            try {
                button.setText(service.getPoint("blake"));
            }
            catch (Exception e) { e.printStackTrace(); }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("AIDLDemo", "Disconnected: " + service);
            service = null;
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        button = ((Button) findViewById(R.id.button));
        button.setOnClickListener(
            new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    testParcelable();
                    Log.d("AIDLDemo", "Binding: "
                        + bindService(
                            new Intent(PathService.class.getName()),
                            connection,
                            Context.BIND_AUTO_CREATE));
                    Log.d("AIDLDemo", "Service: " + service);
                }
            });

        Log.d("AIDLDemo", "Created");
    }

    void testParcelable() {
        Parcel p1 = Parcel.obtain();
        Parcel p2 = Parcel.obtain();

        List<List<String>> list = new ArrayList<List<String>>();
        List<String> l = new ArrayList<String>();
        l.add("foo");
        list.add(l);
        l = new ArrayList<String>();
        l.add("bar");
        list.add(l);
        l = new ArrayList<String>();
        l.add("baz");
        list.add(l);
        ParcelableList orig = new ParcelableList(list);

        try {
            p1.writeParcelable(orig, 0);
            byte[] bytes = p1.marshall();

            p2.unmarshall(bytes, 0, bytes.length);
            p2.setDataPosition(0);
            ParcelableList result = p2.readParcelable(ParcelableList.class.getClassLoader());
            Log.d("AIDLDemo", result.toString());
        }
        catch (Exception e) { e.printStackTrace(); }
        finally {
            p1.recycle();
            p2.recycle();
        }

    }
}