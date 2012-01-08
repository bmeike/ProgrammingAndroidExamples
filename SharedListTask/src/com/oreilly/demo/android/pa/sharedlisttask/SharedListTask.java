/* $Id: $
 */
package com.oreilly.demo.android.pa.sharedlisttask;

import java.util.Vector;


/**
 * Demonstrate that atomic methods may not be sufficient
 */
public class SharedListTask implements Runnable {
    private final Vector<String> list;

    /** @param l the list to share */
    public SharedListTask(Vector<String> l) {
        this.list = l;
    }

    /** @see java.lang.Runnable#run() */
    @Override
    public void run() {
        // the size of the list is obtained early
        int s = list.size();

        while (true) {
            for (int i = 0; i < s; i++ ) {
                // throws IndexOutOfBoundsException!!
                // when the list is size 3, and s is 4.
                System.out.println(list.get(i));
            }
        }
    }

    /**
     * @param args ignored
     */
    public static void main(String[] args) {
        Vector<String> list = new Vector<String>();
        list.add("one");
        list.add("two");
        list.add("three");
        list.add("four");

        new Thread(new SharedListTask(list)).start();

        try { Thread.sleep(2000); }
        catch (InterruptedException e) { /* ignore */}

        // the data structure is fully synchronized,
        // but that only protects the individual methods!
        list.remove("three");
    }
}
