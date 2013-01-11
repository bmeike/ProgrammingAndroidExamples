/* $Id: $
 */
package com.oreilly.demo.android.pa.simplefragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * DateTime
 */
public class DateTime extends Fragment {
    /** Bundle tag for Date/Time */
    public static final String TAG_DATE_TIME = "DateTime";


    /**
     * @param time the initial time for the instance
     * @return a new DateTime object
     */
    public static DateTime newInstance(Date time) {
        Bundle init = new Bundle();
        init.putString(
            DateTime.TAG_DATE_TIME,
            getDateTimeString(time));

        DateTime frag = new DateTime();
        frag.setArguments(init);
        return frag;
    }

    private static String getDateTimeString(Date time) {
        return new SimpleDateFormat("d MMM yyyy HH:mm:ss")
            .format(time);
    }

    private String time;;

    /** @see android.app.Fragment#onCreate(android.os.Bundle) */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        if (null == state) { state = getArguments(); }

        time = (null != state)
            ? state.getString(TAG_DATE_TIME)
            : getDateTimeString(new Date());
    }

    /** @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle) */
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle b)
    {
        View view = inflater.inflate(
            R.layout.date_time,
            container,
            false);  //!!! this is important

        ((TextView) view.findViewById(R.id.last_view_time))
            .setText(time);

        return view;
    }

    /** @see android.app.Fragment#onSaveInstanceState(android.os.Bundle) */
    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(TAG_DATE_TIME, time);
    }

    /** @see android.app.Fragment#onPause() */
    @Override
    public void onPause() {
        super.onPause();
        // stop anything that's running...
    }
}
