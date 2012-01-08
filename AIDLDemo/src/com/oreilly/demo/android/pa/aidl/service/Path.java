package com.oreilly.demo.android.pa.aidl.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;


/** A list of points. */
public class Path implements Parcelable {
    public static final Parcelable.Creator<Path> CREATOR
        = new Parcelable.Creator<Path>() {

            @Override
            public Path createFromParcel(Parcel source) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Path[] newArray(int size) {
                // TODO Auto-generated method stub
                return null;
            }
    };

    /** PathChangeListener. */
    public interface PathChangeListener {
        /** @param points the points that changed. */
        void onPathChange(Path points);
    }
    
    private final LinkedList<Point> points = new LinkedList<Point>();
    private final List<Point> safePoints
        = Collections.unmodifiableList(points);
    
    private PathChangeListener pathChangeListener;
    
    /** @param l set the change listener. */
    public void setPathChangeListener(PathChangeListener l) {
        pathChangeListener = l;
    }

    /** @return the most recently added point. */
    public Point getLastPoint() {
        return (points.size() <= 0) ? null : points.getLast();
    }
    
    /** @return immutable list of points. */
    public List<Point> getPoints() { return safePoints; }

    /**
     * @param x point horizontal coordinate.
     * @param y point vertical coordinate.
     * @param color point color.
     * @param diameter point size.
      */
    public void addPoint(float x, float y, int color, int diameter) {
        points.add(new Point(x, y, color, diameter));
        notifyListener();
    }

    /** Remove all points. */
    public void clear() {
        points.clear();
        notifyListener();
    }

    private void notifyListener() {
        if (null != pathChangeListener) {
            pathChangeListener.onPathChange(this); 
        }
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        
    }
}
