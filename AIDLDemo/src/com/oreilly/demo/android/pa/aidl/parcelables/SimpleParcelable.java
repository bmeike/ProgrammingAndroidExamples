/* $Id: $
 */
package com.oreilly.demo.android.pa.aidl.parcelables;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:bmeike@callmeike.net">Blake Meike</a>
 */
public class SimpleParcelable implements Parcelable {
    public enum State { BEGIN, MIDDLE, END; }
    
    private static final Map<State, String> marshalState;
    static {
        Map<State, String> m = new HashMap<State, String>();
        m.put(State.BEGIN, "begin");
        m.put(State.MIDDLE, "middle");
        m.put(State.END, "end");
        marshalState = Collections.unmodifiableMap(m);
    }
    private static final Map<String, State> unmarshalState;
    static {
        Map<String, State> m = new HashMap<String, State>();
        m.put("begin", State.BEGIN);
        m.put("middle", State.MIDDLE);
        m.put("end", State.END);
        unmarshalState = Collections.unmodifiableMap(m);
    }

    public static final Parcelable.Creator<SimpleParcelable> CREATOR
        = new Parcelable.Creator<SimpleParcelable>() {
            public SimpleParcelable createFromParcel(Parcel src) {
                return new SimpleParcelable(
                    src.readLong(),
                    src.readString());
            }
    
            public SimpleParcelable[] newArray(int size) {
                return new SimpleParcelable[size];
            }
        };

    private State state;
    private Date date;

    public SimpleParcelable(long date, String state) {
        if (0 <= date) { this.date = new Date(date); }
        if ((null != state) && (0 < state.length())) {
            this.state = unmarshalState.get(state);
        }
    }

    public State getState() { return state; }
    public void setState(State state) { this.state = state; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // translate the Date to a long
        dest.writeLong(
            (null == date)
            ? -1
            : date.getTime());
        
        dest.writeString(
            (null == state)
            ? ""
            : marshalState.get(state));
    }

    @Override
    public String toString() {
        return "SimpleParcelable{" + date + "," + state + "}";
    }
}
