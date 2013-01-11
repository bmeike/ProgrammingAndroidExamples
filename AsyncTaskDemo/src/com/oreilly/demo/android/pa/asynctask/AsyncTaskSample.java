/* $Id: $
 */
package com.oreilly.demo.android.pa.asynctask;

import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;

import org.apache.http.HttpResponse;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:bmeike@callmeike.net">Blake Meike</a>
 */
public class AsyncTaskSample {

    /**
     * AsyncDBReq
     */
    public class AsyncDBReq
        extends AsyncTask<PreparedStatement, Void, ResultSet>
    {
        /**
         * @see android.os.AsyncTask#doInBackground(PreparedStatement[])
         */
        @Override
        protected ResultSet doInBackground(PreparedStatement... q) {
            // implementation...
            return null;
        }

        /**
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(ResultSet result) {
            // implementation...
        }
     }

    /**
     * AsyncContentReq
     */
    public class AsyncContentReq
        extends AsyncTask<URI, Void, HttpResponse>
    {
        /**
         * @see android.os.AsyncTask#doInBackground(PreparedStatement[])
         */
        @Override
        protected HttpResponse doInBackground(URI... req) {
            // implementation...
            return null;
        }


        /**
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(HttpResponse result) {
            // implementation...
        }
     }

    int mCount;

    /**
     * @param button
     */
    public void initButton1( Button button) {
        mCount = 0;
        button.setOnClickListener(
            new View.OnClickListener() {
                @Override public void onClick(View v) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... args) {
                            mCount++; // !!! NOT THREAD SAFE!
                            return null;
                        }
                    }.execute();
                } });
    }

    /**
     * @param button
     * @param vals
     */
    public void initButton2(
        Button button,
        final Map<String, String> vals)
    {
        button.setOnClickListener(
            new View.OnClickListener() {
                @SuppressWarnings("unchecked")
                @Override public void onClick(View v) {
                    new AsyncTask<Map<String, String>, Void, Void>() {
                        @Override
                        protected Void doInBackground(
                            Map<String, String>... params)
                        {
                            // examine the map
                            return null;
                        }
                    }.execute(vals);
                    vals.clear();  // !!! NOT THREAD SAFE!
                } });
    }
}