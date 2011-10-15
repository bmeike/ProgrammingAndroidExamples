/* $Id: $
 */
package com.oreilly.demo.android.ch06;

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

    public class AsyncDBReq
        extends AsyncTask<PreparedStatement, Void, ResultSet>
    {
        @Override
        protected ResultSet doInBackground(PreparedStatement... q) {
            // implementation...
            return null;
        }

        @Override
        protected void onPostExecute(ResultSet result) {
            // implementation...
        }
     }

    public class AsyncContentReq
        extends AsyncTask<URI, Void, HttpResponse>
    {
        @Override
        protected HttpResponse doInBackground(URI... req) {
            // implementation...
            return null;
        }


        @Override
        protected void onPostExecute(HttpResponse result) {
            // implementation...
        }
     }

    int mCount;

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