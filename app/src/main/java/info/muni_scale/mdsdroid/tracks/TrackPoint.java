package info.muni_scale.mdsdroid.tracks;

import android.location.Location;

/**
 * Created by rocco on 8/2/15.
 */
public class TrackPoint {

    private Location location;

    public TrackPoint(Location location) {
        this.location = location;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLat() {
        return String.valueOf(location.getLatitude());
    }

    public String getLon() {
        return String.valueOf(location.getLongitude());
    }

    public String getAltitude() {
        return String.valueOf(location.getAltitude());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackPoint that = (TrackPoint) o;
        return getAltitude().equals(that.getAltitude()) && getLon().equals(that.getLon()) && getLat().equals(that.getLat());
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }
}
