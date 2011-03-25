package pa.ch16;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.Observer;

public class GeoCodeTask extends AsyncTask {
    private static final int MAX_GEOCODE_ATTEMPT = 2;

    private Address a;

    private Observer mPostObserver;

    public GeoCodeTask(Observer postObserver) {
        mPostObserver = postObserver;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        Context context = (Context) objects[0];
        String addressText = (String)objects[1];

        /**
         * Use google to get a geocoded address for the given readable string address.
         */
        int attempts = 0;
        do {
            try {
                Geocoder geocoder = new Geocoder(context);
                List<Address> addresses = geocoder.getFromLocationName(addressText, 1);

                if (addresses.size() > 0) {
                    return addresses.get(0);
                } else {
                    attempts++;
                    if (attempts == MAX_GEOCODE_ATTEMPT) {
                    return null;

                    } else {
                        continue;
                    }
                }
            } catch (IOException e) {
                attempts++;
                if (attempts == MAX_GEOCODE_ATTEMPT) {
                    return null;
                }
            }
        } while (true);
    }

    @Override
    protected void onPostExecute(Object o) {
        mPostObserver.update(null, o);
    }
}
