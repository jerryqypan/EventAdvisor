package cs290final.eventadvisor.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Asim Hasan on 4/30/2017.
 */

public abstract class RegisteredFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public RegisteredFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    // Instantiate and register the fragment
    @Override
    public Object instantiateItem(ViewGroup container, int pos) {
        Fragment f = (Fragment) super.instantiateItem(container, pos);
        registeredFragments.put(pos, f);
        return f;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object obj) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, obj);
    }

    public Fragment getFragment(int position) {
        return registeredFragments.get(position);
    }
}
