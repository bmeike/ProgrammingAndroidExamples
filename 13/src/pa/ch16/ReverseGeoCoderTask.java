package pa.ch16;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;


/**
 * Reverse geo-coding yields a street address for a geo-point.  Reverse
 * geo-coding accesses the network so it should never run on the UI thread.
 *
 * TODO: useful as a caching content provider?  would yield quick geo-lookup...
 */
public class ReverseGeoCoderTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "ReverseGeocoder";

    public static interface Callback {
        public void onComplete(String location);
    }

    private Geocoder mGeocoder;
    private float mLat;
    private float mLng;
    private Callback mCompletionCallback;

    public ReverseGeoCoderTask(Geocoder geocoder, float[] latlng,
            Callback callback) {
        mGeocoder = geocoder;
        mLat = latlng[0];
        mLng = latlng[1];
        mCompletionCallback = callback;
    }

    @Override
    protected String doInBackground(Void... params) {
        String value = "";
        try {
            List<Address> address =
                    mGeocoder.getFromLocation(mLat, mLng, 1);
            StringBuilder sb = new StringBuilder();
            for (Address addr : address) {
                int index = addr.getMaxAddressLineIndex();
                sb.append(addr.getAddressLine(index));
            }
            value = sb.toString();
        } catch (IOException ex) {
            Log.e(TAG, "Geocoder exception: ", ex);
        } catch (RuntimeException ex) {
            Log.e(TAG, "Geocoder exception: ", ex);
        }
        return value;
    }

    @Override
    protected void onPostExecute(String location) {
        mCompletionCallback.onComplete(location);
    }
}

