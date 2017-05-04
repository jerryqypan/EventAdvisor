package cs290final.eventadvisor;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.SupportMapFragment;

import cs290final.eventadvisor.R;
import cs290final.eventadvisor.adapters.RegisteredFragmentStatePagerAdapter;
import cs290final.eventadvisor.adapters.ViewPagerAdapter;
import cs290final.eventadvisor.fragments.FavoritesFragment;
import cs290final.eventadvisor.fragments.MySpotsFragment;

public class FavoriteEventsActivity extends AppCompatActivity {

    private static final String TAG = "FavEventsActivity";

    private RegisteredFragmentStatePagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private String[] mPageTitles = {"Favorites", "My Spots"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_events);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null) {
            supportActionBar.setTitle("Important Spots");
        }

        // Instantiate a PagerAdapter to Display the fragments in each individual tab
        mPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the tabs using the above adapter
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mPagerAdapter);
        // This is required to avoid a black flash when the map is loaded.  The flash is due
        // to the use of a SurfaceView as the underlying view of the map.
        mViewPager.requestTransparentRegion(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.onDestroy();
    }

    /**
     * A customized adapter to display fragments. Currently
     * this class adds a Card View for each individual quiz item
     * defined in the resources if the user is in "Local Quizzes Tab"
     * and fetches and displays the Quizzes from Firebase if the user
     * is in "Web Quizzes" tab. It implements the RegisteredFragmentStateAdapter,
     * which keeps track of the state of the adapter, i.e, how many fragments have
     * been instantiated in memory.
     */
    public static class TabViewPagerAdapter extends RegisteredFragmentStatePagerAdapter {

        public TabViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        private final String[] mFragmentTitles = new String[] {
                "Favorites",
                "My Spots"
        };
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FavoritesFragment();
                case 1:
                    return new MySpotsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mFragmentTitles.length;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles[position];
        }
    }



}
