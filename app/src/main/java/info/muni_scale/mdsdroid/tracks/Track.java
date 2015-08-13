package info.muni_scale.mdsdroid.tracks;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rocco on 8/2/15.
 */
public class Track {

    private String name;
    private String description;
    private List<TrackSection> sections = new ArrayList<>();
    private TrackPoint lastFix;
    private static final String TAG = Track.class.getSimpleName();

    public Track() {
        startNewSection();
    }

    public void addPoint(TrackPoint point) {
        if(lastFix == null || !lastFix.equals(point)) {
            lastFix = point;
            sections.get(sections.size()-1).addPoint(point);
            Log.d(TAG, "Add point" + point);
        } else {
            Log.d(TAG, "Discard point");
        }
    }

    public TrackSection startNewSection() {
        TrackSection segment = new TrackSection();
        sections.add(segment);
        return segment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TrackSection> getSections() {
        return sections;
    }

    public void setSections(List<TrackSection> sections) {
        this.sections = sections;
    }

    @Override
    public String toString() {
        if(name == null) {
            return super.toString();
        } else {
            return "Track: " + name;
        }
    }
}
