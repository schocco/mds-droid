package info.muni_scale.mdsdroid.gpx;

import info.muni_scale.mdsdroid.tracks.Track;

/**
 * Created by rocco on 8/2/15.
 */
public interface GpxLogger {

    boolean isRecording();
    Track getTrack();
    Track recordTrack();
    void stopRecording();

    void addServiceListener(GpxServiceListener listener);
    void removeServiceListener(GpxServiceListener listener);
}
