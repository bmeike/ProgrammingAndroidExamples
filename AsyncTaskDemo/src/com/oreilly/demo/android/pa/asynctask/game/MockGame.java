/* $Id: $
 */
package com.oreilly.demo.android.pa.asynctask.game;


/**
 *
 * A mock service
 */
public class MockGame extends Game {
    /** Modeled network latency */
    public static final int LATENCY = 10;

    private static long serial = 1;

    private static synchronized long incSerial() { return serial++; }

    /**
     * Fake a game initialization, modeling latency
     * 
     * @return an initialization message
     */
    @Override public String init() {
        InitProgressListener lstnr = getProgressListener();

        for (int i = 0; i < LATENCY; i++) {
            try { Thread.sleep(1000); }
            catch (InterruptedException e) { }
            
            if (null != lstnr) {
                lstnr.onInitProgress(((i + 1) * 100) / LATENCY);
            }
        }
        
        return "Game " + incSerial()
            + ", level " + getLevel() + ", ready!";
    }

}
