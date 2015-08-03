package info.muni_scale.mdsdroid.tracks;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import info.muni_scale.mdsdroid.mscale.Mscale;
import info.muni_scale.mdsdroid.R;

public class TrackSectionAdapter extends BaseAdapter {


    private Track track;
    private LayoutInflater layoutInflator;
    private Activity parent;

    private static class ViewHolder {
        private Spinner difficultySpinner;
        private TextView nPoints;
        private TextView id;


    }

    public TrackSectionAdapter(Activity parent) {
        super();
        this.parent = parent;
        layoutInflator = parent.getLayoutInflater();
    }


    @Override
    public int getCount() {
        if(track != null) {
            return track.getSections().size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return track.getSections().get(position);
    }

    private TrackSection getTrackSection(int position) {
        return track.getSections().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewholder;
        final TrackSection section = getTrackSection(position);
        if (view == null) {
            view = layoutInflator.inflate(R.layout.tracksection_entry, null);
            viewholder = new ViewHolder();

            final Spinner s = (Spinner) view.findViewById(R.id.section_mscale_spinner);
            ArrayAdapter<Float> adapter = new ArrayAdapter<Float>(parent, R.layout.spinner_layout, Mscale.getAllowedOrdinals());
            s.setAdapter(adapter);
            s.setSelection(adapter.getPosition(section.getDifficulty()), true);
            s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    section.setDifficulty((Float) s.getSelectedItem());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    section.setDifficulty(0);
                }
            });
            viewholder.difficultySpinner = s;
            viewholder.nPoints = (TextView) view.findViewById(R.id.txt_section_points);
            viewholder.id = (TextView) view.findViewById(R.id.txt_section_id);
            view.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) view.getTag();
        }
        viewholder.nPoints.setText(String.valueOf(section.getPoints().size()));
        viewholder.id.setText("#" + (position + 1));
        return view;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }
}
