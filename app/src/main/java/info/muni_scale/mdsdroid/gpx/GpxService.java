package info.muni_scale.mdsdroid.gpx;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import info.muni_scale.mdsdroid.tracks.Track;
import info.muni_scale.mdsdroid.tracks.TrackPoint;

public class GpxService extends Service implements GpxLogger {

    private static final long MIN_TIME = 3;
    private static final long MIN_DISTANCE = 1;
    private static final String TAG = "GpxService";
    private long time;
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private Track track;
    private Set<GpxServiceListener> serviceListeners = new HashSet<>();

    private long interval = 600;
    private float minDistance = 5;
    private final IBinder binder = new GpsBinder();

    public class GpsBinder extends Binder {
        public GpxService getService() {
            return GpxService.this;
        }
    }

    public GpxService() {

    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                updateLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);

        return START_STICKY;
    }

    private void updateLocation(Location location) {
        if(track != null) {
            track.addPoint(new TrackPoint(location));
            Log.d(TAG, track.toString());
        }
        for(GpxServiceListener listener : serviceListeners) {
            listener.updateLocation(location);
        }
    }

    /**
     * The service discards all location updates, until this is called by another activity.
     * @return
     */
    @Override
    public Track recordTrack() {
        //TODO: register listener, start receiving updates
        track = new Track();
        return track;
    }

    @Override
    public Track getTrack() {
        return track;
    }

    @Override
    public void stopRecording() {
        //TODO: unregister listener
        this.track = null;
    }

    @Override
    public void addServiceListener(GpxServiceListener listener) {
        serviceListeners.add(listener);
    }

    @Override
    public void removeServiceListener(GpxServiceListener listener) {
        serviceListeners.remove(listener);
    }

    @Override
    public boolean isRecording() {
        return track != null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
