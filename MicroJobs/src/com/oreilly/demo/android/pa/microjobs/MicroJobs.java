package com.oreilly.demo.android.pa.microjobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;


/**
 * MicroJobs
 */
public class MicroJobs extends MapActivity {
    /**
     * Application-wide log tag
     */
    static final String LOG_TAG = "MicroJobs";

    /**
     * Database cursor to access user information
     */
    private MicroJobsDatabase.WorkerCursor worker;

    /**
     * MJJobsOverlay
     */
    private class MJJobsOverlay extends ItemizedOverlay<OverlayItem> {

        /**
         * @param marker the push-pin
         */
        public MJJobsOverlay(Drawable marker) {
            super(marker);
            populate();
        }

        /**
         * @see com.google.android.maps.ItemizedOverlay#size()
         */
        @Override
        public int size() {
            int size = db.getJobsCount();
            return size;
        }

        /**
         * @see com.google.android.maps.ItemizedOverlay#createItem(int)
         */
        @Override
        protected OverlayItem createItem(int i) {
            MicroJobsDatabase.JobDetailCursor c = db.getJobDetails(i+1);
            startManagingCursor(c);
            String contactName = c.getColContactName();
            String description = c.getColDescription();
            int lat = (int) c.getColLatitude();
            int lon = (int) c.getColLongitude();
            return new OverlayItem(new GeoPoint(lat, lon), contactName, description);
        }

        /**
         * React to tap events on Map by showing an appropriate detail activity
         *
         * @see com.google.android.maps.ItemizedOverlay#onTap(com.google.android.maps.GeoPoint, com.google.android.maps.MapView)
         */
        @Override
        public boolean onTap(GeoPoint p, MapView mvMap1) {
            long lat = p.getLatitudeE6();
            long lon = p.getLongitudeE6();

            long rowid = -1;
            MicroJobsDatabase.JobsCursor c = db.getJobs(MicroJobsDatabase.JobsCursor.SortBy.title);
            startManagingCursor(c);
            startManagingCursor(c);
            for( int i=0; i<c.getCount(); i++){
                if ((Math.abs(c.getColLatitude()-lat)<1000) && (Math.abs(c.getColLongitude()-lon)<1000)){
                    rowid = c.getColJobsId();
                    break;
                }

                c.moveToNext();
            }

            if (0 > rowid) { return false; }

            Bundle b = new Bundle();
            b.putLong("_id", rowid);
            Intent i = new Intent(MicroJobs.this, MicroJobsDetail.class);
            i.putExtras(b);
            startActivity(i);

            return true;
        }
    }


    MapView mvMap;
    MicroJobsDatabase db;
    MyLocationOverlay mMyLocationOverlay;
    int latitude, longitude;
    double[] curlocation = new double[2];

    /**
     * Called when the activity is first created.
     *
     * @see com.google.android.maps.MapActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        db = new MicroJobsDatabase(this);

        getCurrentLocation((LocationManager) getSystemService(Context.LOCATION_SERVICE));

        Spinner spnLocations = (Spinner) findViewById(R.id.spnLocations);
        mvMap = (MapView) findViewById(R.id.mapmain);

        // get the map controller
        final MapController mc = mvMap.getController();

        mMyLocationOverlay = new MyLocationOverlay(this, mvMap);
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.runOnFirstFix(
            new Runnable() {
                @Override
                public void run() {
                    mc.animateTo(mMyLocationOverlay.getMyLocation());
                    mc.setZoom(16);
                    updateCurLocation(mMyLocationOverlay.getMyLocation());
                }
            });

        Drawable marker = getResources().getDrawable(R.drawable.android_tiny_image);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        mvMap.getOverlays().add(new MJJobsOverlay(marker));

        mvMap.setClickable(true);
        mvMap.setEnabled(true);
        mvMap.setSatellite(false);
        mvMap.setTraffic(false);

        // start out with a general zoom
        mc.setZoom(16);
        mvMap.invalidate();

        // Create a button click listener for the List Jobs button.
        Button btnList = (Button) findViewById(R.id.btnShowList);
        btnList.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MicroJobs.this.getApplication(), MicroJobsList.class);
                startActivity(intent);
            }
        });

        // Load a HashMap with locations and positions
        List<String> lsLocations = new ArrayList<String>();
        final HashMap<String, GeoPoint> hmLocations = new HashMap<String, GeoPoint>();
        hmLocations.put("Current Location", new GeoPoint(latitude, longitude));
        lsLocations.add("Current Location");

        // Add favorite locations from this user's record in workers table
        worker = db.getWorker();
        hmLocations.put(worker.getColLoc1Name(), new GeoPoint((int)worker.getColLoc1Lat(), (int)worker.getColLoc1Long()));
        lsLocations.add(worker.getColLoc1Name());
        hmLocations.put(worker.getColLoc2Name(), new GeoPoint((int)worker.getColLoc2Lat(), (int)worker.getColLoc2Long()));
        lsLocations.add(worker.getColLoc2Name());
        hmLocations.put(worker.getColLoc3Name(), new GeoPoint((int)worker.getColLoc3Lat(), (int)worker.getColLoc3Long()));
        lsLocations.add(worker.getColLoc3Name());

        ArrayAdapter<String> aspnLocations
        = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lsLocations);
        aspnLocations.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLocations.setAdapter(aspnLocations);

        // Setup a callback for the spinner
        spnLocations.setOnItemSelectedListener(
            new OnItemSelectedListener() {
                @Override
                public void onNothingSelected(AdapterView<?> arg0) { }

                @Override
                public void onItemSelected(AdapterView<?> parent, View v, int position, long id)  {
                    TextView vt = (TextView) v;
                    if ("Current Location".equals(vt.getText())) {
                        mMyLocationOverlay.enableMyLocation();
                        try {
                            GeoPoint myLocation =
                                mMyLocationOverlay.getMyLocation();
                            if (myLocation != null) {
                                mc.animateTo(myLocation);
                                updateCurLocation(myLocation);
                            }
                        }
                        catch (Exception e) {
                            Log.i("MicroJobs", "Unable to animate map", e);
                        }
                        mvMap.invalidate();
                    } else {
                        mMyLocationOverlay.disableMyLocation();
                        mc.animateTo(hmLocations.get(vt.getText()));
                        updateCurLocation(hmLocations.get(vt.getText()));
                    }
                    mvMap.invalidate();
                }
            });
    }

    protected GeoPoint setCurrentGeoPoint(){
        return mMyLocationOverlay.getMyLocation();
    }

    /**
     * @see com.google.android.maps.MapActivity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        mMyLocationOverlay.disableMyLocation();
    }

    /**
     * @see com.google.android.maps.MapActivity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        mMyLocationOverlay.enableMyLocation();
    }

    /**
     * Setup menus for this page
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean supRetVal = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.map_menu_zoom_in));
        menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.map_menu_zoom_out));
        menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.map_menu_set_satellite));
        menu.add(Menu.NONE, 3, Menu.NONE, getString(R.string.map_menu_streetview));
        menu.add(Menu.NONE, 4, Menu.NONE, getString(R.string.map_menu_set_traffic));
        menu.add(Menu.NONE, 5, Menu.NONE, getString(R.string.map_menu_show_list));
        return supRetVal;
    }

    /**
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                // Zoom in
                zoomIn();
                return true;
            case 1:
                // Zoom out
                zoomOut();
                return true;
            case 2:
                // Toggle satellite views
                mvMap.setSatellite(!mvMap.isSatellite());
                return true;
            case 3:
                // Launch StreetView with lat/lon of center of current map
            	String uri = "google.streetview:cbll="+curlocation[0]+","+curlocation[1]+"&cbp=1,0,,0,1.0&mz="+mvMap.getZoomLevel();
            	Intent streetView = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            	startActivity(streetView);
                return true;
            case 4:
                // Toggle traffic views
                mvMap.setTraffic(!mvMap.isTraffic());
                return true;
            case 5:
                // Show the job list activity
                startActivity(new Intent(MicroJobs.this, MicroJobsList.class));
                return true;
        }
        return false;
    }

    /**
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP: // zoom in
                zoomIn();
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN: // zoom out
                zoomOut();
                return true;
            case KeyEvent.KEYCODE_BACK: // go back (meaning exit the app)
                finish();
                return true;
            default:
                return false;
        }
    }

    /**
     * Required method to indicate whether we display routes
     */
    @Override
    protected boolean isRouteDisplayed() { return false; }

    /**
     * Zoom in on the map
     */
    private void zoomIn() {
        mvMap.getController().setZoom(mvMap.getZoomLevel() + 1);
    }

    /**
     * Zoom out on the map, but not past level 10
     */
    private void zoomOut() {
        int zoom = mvMap.getZoomLevel() - 1;
        if (zoom < 5) { zoom = 5; }
        mvMap.getController().setZoom(zoom);
    }

    /**
     * @return the current location
     */
    private Location getCurrentLocation(LocationManager lm) {
        Location l = lm.getLastKnownLocation("gps");
        if (null != l) { updateCurLocation(l); return l; }

        // getLastKnownLocation returns null if loc provider is not enabled
        l = new Location("gps");
        l.setLatitude(42.352299);
        l.setLatitude(-71.063979);
        
        updateCurLocation(l);
        return l;
    }
    
    /**
     * updates curlocation for streetview use
     */
    private void updateCurLocation(GeoPoint point) {
    	curlocation[0] = ((double) point.getLatitudeE6()) / ((double) 1E6);
    	curlocation[1] = ((double) point.getLongitudeE6()) / ((double) 1E6);
    }
    
    /**
     * updates curlocation for streetview use
     */
    private void updateCurLocation(Location loc) {
    	curlocation[0] = loc.getLatitude();
    	curlocation[1] = loc.getLongitude();
    }

}
