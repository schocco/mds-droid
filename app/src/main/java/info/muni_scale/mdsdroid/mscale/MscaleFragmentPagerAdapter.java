package info.muni_scale.mdsdroid.mscale;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MscaleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Mscale[] mscales = Mscale.getCollection();
    final int PAGE_COUNT = mscales.length;
    private Context context;

    public MscaleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return MscaleFragment.newInstance(position + 1, mscales[position]);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return mscales[position].toString();
    }
}
