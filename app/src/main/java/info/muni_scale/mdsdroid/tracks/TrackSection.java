package info.muni_scale.mdsdroid.tracks;

import java.util.ArrayList;
import java.util.List;

import info.muni_scale.mdsdroid.mscale.Mscale;

/**
 * Created by rocco on 8/2/15.
 */
public class TrackSection {

    private List<TrackPoint> points = new ArrayList<>();
    private float difficulty;
    private String comment;

    public List<TrackPoint> getPoints() {
        return points;
    }

    public void addPoint(TrackPoint point) {
        points.add(point);
    }

    public void setPoints(List<TrackPoint> points) {
        this.points = points;
    }

    public float getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Mscale difficulty) {
        this.difficulty = difficulty.getNumber();
    }

    public void setDifficulty(float number) {
        this.difficulty = number;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
