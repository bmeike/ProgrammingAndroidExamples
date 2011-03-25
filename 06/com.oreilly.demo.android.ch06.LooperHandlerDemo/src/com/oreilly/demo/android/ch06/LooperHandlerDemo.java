/* $Id: $
 */
package com.oreilly.demo.android.ch06;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.oreilly.android.demo.R;
import com.oreilly.android.demo.R.id;
import com.oreilly.android.demo.R.layout;
import com.oreilly.demo.android.ch06.game.Car;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:bmeike@callmeike.net">Blake Meike</a>
 */
public class LooperHandlerDemo extends Activity {


    /** @see android.app.Activity#onCreate(android.os.Bundle) */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        
        final Car car = new Car();
         
        car.start();

        setContentView(R.layout.looperhandlerdemo);

        ((Button) findViewById(R.id.fast)).setOnClickListener(
            new View.OnClickListener() {
                @Override public void onClick(View v) {
                    car.stop();
                 } });
    }
}
