package pa.ch16;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import pa.ch16.kml.KmlController;
import pa.ch16.kml.LayoutListener;
import pa.ch16.kml.KmlParser;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Displays a kml based geographic site, such as a location for bicycling, or
 * hiking.
 */
public class SiteViewerActivity extends MapActivity implements LayoutListener {
    private LayoutControlMapView kmlRenderer;
    private boolean laidOut = false;

    public SiteViewerActivity() {
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_siteviewer);

        kmlRenderer = (LayoutControlMapView) findViewById(R.id.site_viewer);
        kmlRenderer.setListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected int onGetMapDataSource() {
        return super.onGetMapDataSource();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    public void initialLayout() {
        laidOut = true;

        Intent i = getIntent();

        String siteUrl = i.getExtras().getString(MapSelectActivity.SITE_URL);

        if (siteUrl != null) {
            viewSite(siteUrl);
        }
    }

    private void viewSite(final String site) {
        if (!laidOut) {
            return;
        }

        try {
            Uri siteUri = Uri.parse(site);
            InputStream siteStream =
                    getContentResolver().openInputStream(siteUri);

            KmlController kmlController = newController(siteStream);
            if (kmlController != null) {
                kmlController.renderOverlay();
                kmlController.spanWayPoints();
            }

        } catch (FileNotFoundException e) {
            Log.d(Ch16.LOG_TAG, "could not open provider uri: " + site);
        }
    }

    private KmlController newController(InputStream kmlInput) {
        KmlParser kmlParser = new KmlParser(kmlInput);
        kmlParser.parse();

        if (kmlParser.isEmpty()) {
            return null;
        }

        MapView siteViewer = (MapView) findViewById(R.id.site_viewer);
        KmlController kmlController =
                new KmlController(this, siteViewer, kmlParser);
        return kmlController;
    }
}
