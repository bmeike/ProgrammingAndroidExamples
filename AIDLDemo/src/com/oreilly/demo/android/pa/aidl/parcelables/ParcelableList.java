/* $Id: $
 */
package com.oreilly.demo.android.pa.aidl.parcelables;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:bmeike@callmeike.net">Blake Meike</a>
 */
public class ParcelableList implements Parcelable {
    public static final Parcelable.Creator<ParcelableList> CREATOR
    = new Parcelable.Creator<ParcelableList>() {
        public ParcelableList createFromParcel(Parcel src) {
            List<List<String>> l = new ArrayList<List<String>>();
            src.readList(l, null);
            return new ParcelableList(l);
        }

        public ParcelableList[] newArray(int size) {
            return new ParcelableList[size];
        }
    };


    private List<List<String>> list;
    
    public ParcelableList(List<List<String>> list) {
        this.list = list;
    }
    
    public List<List<String>> getList() { return list; }
    public void setList(List<List<String>> list) { this.list = list; }
    
    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(list);
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("ParcelableList{");
        for (List<String> l: list) {
            s.append("[");
            for (String i: l) { s.append(i).append(","); }            
            s.append("]");
        }
        return s.append("}").toString();
    }
}