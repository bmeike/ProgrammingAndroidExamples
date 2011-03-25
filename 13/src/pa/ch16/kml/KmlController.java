package pa.ch16.kml;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import pa.ch16.geo.GeoCalc;
import pa.ch16.R;

import java.util.ArrayList;
import java.util.List;

public class KmlController {
    private KmlParser kmlParser;
    private MapView mSiteViewer;
    private WayPoint[] wayPoints;
    private GeoPoint[] wayGeoPoints;
    private Bitmap mDotBitmap;
    private MapController mMapController;

    public KmlController(Context context, MapView siteViewer,
                         KmlParser kmlParser)
    {
        this.mSiteViewer = siteViewer;
        mMapController = siteViewer.getController();
        this.kmlParser = kmlParser;
        this.mDotBitmap =
                BitmapFactory.decodeResource(context.getResources(),
                R.drawable.blue_dot);
    }

    public void renderOverlay() {
        wayPoints = kmlParser.getWayPoints();

        List geoPointList = new ArrayList();
        if (wayPoints != null) {
            for (int i = 0; i < wayPoints.length ; i++) {
                WayPoint wp = wayPoints[i];
                int wpLat = GeoCalc.microDegrees(wp.getLatitude());
                int wpLon = GeoCalc.microDegrees(wp.getLongitude());
                if ((wpLat != 0) && (wpLon != 0)) {
                    geoPointList.add(new GeoPoint(wpLat, wpLon));
                }
            }

            if (geoPointList.size() > 0) {
                wayGeoPoints = (GeoPoint[])
                        geoPointList.toArray(new GeoPoint[]{});
            }

            if (wayGeoPoints != null) {
                WayPointsOverlay wpo = new WayPointsOverlay(wayGeoPoints);
                List<Overlay> overlays = mSiteViewer.getOverlays();
                overlays.clear();
                overlays.add(wpo);
            }
        }
    }

    public GeoPoint[] getWayGeoPoints() {
        return wayGeoPoints;
    }

    public WayPoint[] getWayPoints() {
        return wayPoints;
    }

    public void spanWayPoints() {
        GeoPoint[] wayGeoPoints = getWayGeoPoints();
        GeoCalc.zoomToPointsSpan(mSiteViewer, wayGeoPoints);
    }

    private class WayPointsOverlay extends Overlay {
        private GeoPoint[] wayGeoPoints;

        public WayPointsOverlay(GeoPoint[] wayGeoPoints) {
            this.wayGeoPoints = wayGeoPoints;
        }

        @Override
        public void draw(Canvas canvas, MapView mapView, boolean b) {
            for (GeoPoint geoPoint: wayGeoPoints) {
                Point mapxy = new Point();
                mSiteViewer.getProjection().toPixels(geoPoint, mapxy);
                Paint aap = new Paint(Paint.ANTI_ALIAS_FLAG);
                canvas.drawBitmap(mDotBitmap,
                        mapxy.x - (mDotBitmap.getWidth() / 2),
                        mapxy.y - mDotBitmap.getHeight(), aap);
            }

            super.draw(canvas, mapView, b);
        }
    }
}
