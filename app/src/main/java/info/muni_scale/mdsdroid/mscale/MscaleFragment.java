package info.muni_scale.mdsdroid.mscale;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.muni_scale.mdsdroid.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MscaleFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String ARG_MSCALE = "ARG_MSCALE";
    private Mscale mscale;
    private int mPage;
    private Typeface iconFont;

    public static MscaleFragment newInstance(int page, Mscale mscale) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putSerializable(ARG_MSCALE, mscale);
        MscaleFragment fragment = new MscaleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        mscale = (Mscale) getArguments().getSerializable(ARG_MSCALE);
        iconFont = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mscale, container, false);
        TextView header = (TextView) view.findViewById(R.id.mscale_header);
        header.setText(mscale.toString());

        TextView i1 = (TextView) view.findViewById(R.id.icon1);
        TextView i2 = (TextView) view.findViewById(R.id.icon2);
        TextView i3 = (TextView) view.findViewById(R.id.icon3);
        TextView i4 = (TextView) view.findViewById(R.id.icon4);
        i1.setTypeface(iconFont);
        i2.setTypeface(iconFont);
        i3.setTypeface(iconFont);
        i4.setTypeface(iconFont);
        TextView characteristics = (TextView) view.findViewById(R.id.mscale_characteristics_txt);
        characteristics.setText(TextUtils.join(" | ", mscale.getCharacteristics()));

        TextView obstacles = (TextView) view.findViewById(R.id.mscale_obstacles_txt);
        obstacles.setText(TextUtils.join(" | ", mscale.getObstacles()));

        TextView underground = (TextView) view.findViewById(R.id.mscale_underground_txt);
        underground.setText(TextUtils.join(" | ", mscale.getUnderground()));

        TextView slope = (TextView) view.findViewById(R.id.mscale_slope_txt);
        slope.setText(mscale.getSlopePercent() + " %");
        return view;
    }
}
