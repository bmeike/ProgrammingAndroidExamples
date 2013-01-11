/* $Id: $
 */
package com.oreilly.demo.android.pa.serialize;

import java.io.Serializable;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;

import com.oreilly.android.demo.pa.serialize.R;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:bmeike@callmeike.net">Blake Meike</a>
 */
public class JSerialize extends Activity {
    /** Bundle tag for app state */
    public static final String APP_STATE
        = "com.oreilly.android.app.state";

    static class AppState implements Serializable {
        // definitions, getters and setters
        // for application state parameters here.
        // ...
    }

    private AppState applicationState;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedAppState) {
        super.onCreate(savedAppState);

        applicationState = (null == savedAppState)
            ? new AppState(/* ... */)
            : (AppState) savedAppState.getSerializable(APP_STATE);

        setContentView(R.layout.main);

      Bundle bundle = new Bundle();
      bundle.putFloat(APP_STATE, 3.14159F);

      Parcel p = Parcel.obtain();
      p.writeBundle(bundle);

      p.setDataPosition(0);
      bundle = p.readBundle();
      bundle.getInt(APP_STATE);
    }

    /**
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(APP_STATE, applicationState);
    }
}
