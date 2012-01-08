/* $Id: $
 */
package com.oreilly.demo.android.pa.filllisttask;

import java.util.ArrayList;
import java.util.List;


/**
 * Demonstrate cooperative concurrent programming
 */
public class FillListTask implements Runnable {
    private final int size;

    private List<String> strings;

    /** @param size the size of the desired list */
    public FillListTask(int size) {
        this.size = size;
    }

    /** @return true if the list has been filled */
    public synchronized boolean isFinished() {
        return null != strings;
    }

    /** @return null or the filled list */
    public synchronized List<String> getList() {
        return strings;
    }

    /** @see java.lang.Runnable#run() */
    @Override
    public void run() {
        List<String> strs = new ArrayList<String>(size);
        try {
            for (int i = 0; i < size; i++ ) {
                Thread.sleep(2000);
                strs.add("element " + String.valueOf(i));
            }

            synchronized (this) {
                strings = strs;
                this.notifyAll();
            }
        }
        catch (InterruptedException e) {
             // catch interrupted exception outside loop,
             // since interrupted exception is a sign that
             // the thread should quit.
        }
    }


    /**
     * Waits for the fill array task to complete
     *
     * @param args unused
     * @throws InterruptedException if slumber interrupted
     */
    public static void main(String[] args)
        throws InterruptedException
    {
        FillListTask task = new FillListTask(7);

        new Thread(task).start();

        synchronized (task) {
            while ( !task.isFinished()) {
                task.wait();
            }
        }

        System.out.println("Array full: " + task.getList());
    }
}
