/* $Id: $
 */
package com.oreilly.demo.android.ch06.game;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:bmeike@callmeike.net">Blake Meike</a>
 */
public class Car {
    public static final long REPORTING_INTERVAL_MS = 100;

    private Handler mHandler;
    private Thread mThread;
    
    
    private double mLocationX = 0.0;
    private double mLocationY = 0.0;

    private double mHeading = 0;
    private double mVelocity = 1.0;
    
    private double mTurn = 10;

    private static final class Motor implements Runnable {
        private final Car mCar;
        
        public Motor(Car car) { mCar = car; }

        @Override
        public void run() {
            Looper.prepare();
            mCar.init();
            Looper.loop();
            mCar.cleanup();
        }
    }
    

    /* External thread */
    public synchronized void start() {
        if (null == mThread) {
            mThread = new Thread(new Motor(this));
            mThread.start();
        }
    }

    /* External thread */
    public synchronized void stop() {
        if (null != mHandler) { mHandler.getLooper().quit(); }
    }

    /* Looper thread */
    void init() {
        synchronized (this) { mHandler = new Handler(); }
        move();
    }
    
    /* Looper thread */
    synchronized void cleanup() {
        mThread = null;
        mHandler = null;
    }
    
    /* Looper thread */
    void move() {
        mLocationX += mVelocity * Math.cos(mHeading);
        mLocationY += mVelocity * Math.sin(mHeading);
        
        mHeading += (Math.PI * mTurn) / 100.0D;
        if (0 > mHeading) {
            mHeading += Math.PI * 2;
        }
        else if ((Math.PI * 2) < mHeading) {
            mHeading -= Math.PI * 2;
        }
        
        System.out.println(
            "@" + System.currentTimeMillis()
            + ": " + mLocationX + ", " + mLocationY
            + " (" + mVelocity + ", " + mHeading + ")");
        
        mHandler.postDelayed(
            new Runnable() { public void run() { move(); } },
            REPORTING_INTERVAL_MS);
    }
}
