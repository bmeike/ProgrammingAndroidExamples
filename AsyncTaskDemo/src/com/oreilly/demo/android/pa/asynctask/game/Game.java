/* $Id: $
 */
package com.oreilly.demo.android.pa.asynctask.game;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:bmeike@callmeike.net">Blake Meike</a>
 */
public abstract class Game {

    /** ProgressListener */
    public static interface InitProgressListener { 
        /** @param percentComplete */
        void onInitProgress(int percentComplete);
    }
    
    /**
     * Game factory.
     * 
     * @return the game object
     */
    public static Game newGame() { return new MockGame(); }


    private String level;
    private InitProgressListener progressListener;
    

    /** @return the size of the content, in bytes */
    protected abstract String init();
  
    /** @return return the game level */
    public final String getLevel() { return level; }
    
    /** @return return the progress listener */
    public final InitProgressListener getProgressListener() {
        return progressListener;
    }

    /**
     * Initialize the game
     *
     * @param lvl the game level
     * @return a welcome message
     */
    public final String initialize(String lvl) {
        return initialize(lvl, null);
    }

    /**
     * Initialize the game
     *
     * @param lvl the game level
     * @param lstnr the progress listener
     * 
     * @return a welcome message
     */
    public final String initialize(
        String lvl,
        InitProgressListener lstnr)
    {
        level = lvl;
        progressListener = lstnr;
        return init();
    }
}
