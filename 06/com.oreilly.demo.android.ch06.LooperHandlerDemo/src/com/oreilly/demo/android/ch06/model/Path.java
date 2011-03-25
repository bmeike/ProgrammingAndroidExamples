package com.oreilly.demo.android.ch06.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.oreilly.demo.android.ch06.model.Point;


/** A list of points. */
public class Path {
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
}
