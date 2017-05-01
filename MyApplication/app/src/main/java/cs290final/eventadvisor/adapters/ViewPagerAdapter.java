package cs290final.eventadvisor.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cs290final.eventadvisor.fragments.FavoritesFragment;
import cs290final.eventadvisor.fragments.MySpotsFragment;

/**
 * Created by Asim Hasan on 4/30/2017.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FavoritesFragment();
        } else {
            return new MySpotsFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
