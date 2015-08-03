package info.muni_scale.mdsdroid.tracks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rocco on 8/2/15.
 */
public class Track {

    private String name;
    private String description;
    private List<TrackSection> sections = new ArrayList<>();

    public Track() {
        startNewSection();
    }

    public void addPoint(TrackPoint point) {
        sections.get(sections.size()-1).addPoint(point);
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
        StringBuffer sb = new StringBuffer();
        for(TrackSection seg : sections) {
            sb.append("<section>\n");
            for(TrackPoint pt : seg.getPoints()) {
                sb.append("<Point lat=");
                sb.append(pt.getLocation().getLatitude());
                sb.append(" lon=");
                sb.append(pt.getLocation().getLongitude());
                sb.append("/>\n");
            }
            sb.append("</section>");
        }
        return sb.toString();
    }
}
