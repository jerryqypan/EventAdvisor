package cs290final.eventadvisor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs290final.eventadvisor.adapters.EventItemAdapter;
import cs290final.eventadvisor.adapters.RegisteredFragmentStatePagerAdapter;
import cs290final.eventadvisor.backend.Event;
import cs290final.eventadvisor.backend.JSONToEventGenerator;
import cs290final.eventadvisor.fragments.FavoritesFragment;
import cs290final.eventadvisor.fragments.MapFragment;
import cs290final.eventadvisor.fragments.MySpotsFragment;
import cs290final.eventadvisor.utils.CircleTransform;
import cs290final.eventadvisor.utils.IFragmentInteractionListener;

/**
 * Created by Asim Hasan on 5/1/2017.
 */

public class TabbedActivity extends AppCompatActivity implements
        IFragmentInteractionListener,
        PlaceSelectionListener {

    private static final String TAG = "TabbedActivity";

    /**
     * The GoogleMapAPI connection client.
     * Used for location services.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * The device's last known location.
     */
    private Location mLastLocation;


    /**
     * Map containing a location to a list of events near that location.
     */
    private Map<String, List<Event>> eventsMap = new HashMap<String,List<Event>>();

    /**
     * Map containing a location to the visible map marker representing that location.
     */
    private Map<String, Marker> markersMap = new HashMap<String, Marker>();

    /**
     * The JSON String containing information about the events in the surrounding area.
     */
    private String eventsJSON;


    private List<Event> mUserFavs;
    private  List<Event> mUserEvents;


    /**
     * The DrawerLayout of this application.
     */

    private DrawerLayout mDrawerLayout;

    /**
     * The NagivationView of this application.
     */
    private NavigationView mNavigationView;
    private RegisteredFragmentStatePagerAdapter mPagerAdapter;
    private ViewPager mViewPager;


    /**
     * The Request code to obtain results from the google maps searchbar
     * Used to center the map location on the searched location.
     */
    private static final int REQUEST_SELECT_PLACE = 1234;

    /**
     * The Request Code to obtain results from the Create Events Activity.
     * Used to center the map location to the location of the new user created event.
     */
    private static final int CREATE_EVENTS = 12345;

    /**
     * The search bar icon. Clicking this will open the searchbar.
     */
    private SearchView searchView;

    /**
     * The default searchbar.
     */
    private MenuItem searchBarMenuItem;

    /**
     * The current user of the application.
     */
    public static FirebaseUser mCurrentUser;

    /**
     * The root view of the application
     */
    private View mRootView;

    /**
     * The image view displaying the user's profile picture
     */
    private ImageView mUserProfilePicture;

    /**
     * The text view displaying the user's email
     */
    private TextView mUserEmail;

    /**
     * The text view displaying the user's name.
     */
    private TextView mUserDisplayName;

    /**
     * The listener associated with listening to user clicks of an event.
     */
    private EventItemAdapter.EventItemListener mEventClickListener;

    private PlaceAutocompleteFragment mAutoCompleteFragment;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        mContext = getBaseContext();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mCurrentUser != null) {
            // User is already signed in, take them to the map activity directly
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = mNavigationView.inflateHeaderView(R.layout.nav_header);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Get the id of the current selected MenuItem
                        int id = menuItem.getGroupId();
                        if (id == 0) {
                            Log.d(TAG, "onNavigationItemSelected: Clicked on MapView");
                        } else if (id == 1){
                            Log.d(TAG, "onNavigationItemSelected: Clicked on Favs");
                        } else {
                            Log.d(TAG, "onNavigationItemSelected: Clicked on My Spots");
                        }
                        Log.d(TAG, "onNavigationItemSelected: Item " + menuItem.getItemId());

                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        mRootView = (View) findViewById(android.R.id.content);
        mUserProfilePicture = (ImageView) headerLayout.findViewById(R.id.user_profile_picture);
        mUserEmail = (TextView) headerLayout.findViewById(R.id.user_email);
        mUserDisplayName = (TextView) headerLayout.findViewById(R.id.user_display_name);
        populateProfile();

        // Instantiate a PagerAdapter which will display the fragments in each individual tab
        mPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the tabs using the adapter
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    /**
     * Deletes an user account
     * @param v The view that was clicked
     */
    public void onDeleteAccountClick(View v) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete this account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("No", null)
                .create();
        dialog.show();
    }

    /**
     * Updates the profile information for the current user on the display
     */
    private void populateProfile() {
        if (mCurrentUser.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(mCurrentUser.getPhotoUrl())
                    .fitCenter()
                    .transform(new CircleTransform(this))
                    .into(mUserProfilePicture);
        } else {
            mUserProfilePicture = (ImageView) findViewById(R.id.user_profile_picture);
        }

        mUserEmail.setText(
                TextUtils.isEmpty(mCurrentUser.getEmail()) ? "No email" : mCurrentUser.getEmail());
        mUserDisplayName.setText(
                TextUtils.isEmpty(mCurrentUser.getDisplayName()) ? "No display name" : mCurrentUser.getDisplayName());


    }

    /**
     * Deletes the user account and relaunches the main activity.
     */
    private void deleteAccount() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(MainActivity.createIntent(TabbedActivity.this));
                            finish();
                        } else {
                            Snackbar.make(mRootView, R.string.delete_account_failed, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Signs out of the current user account.
     * @param v The view that was clicked
     */
    public void onSignOutClick(View v) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(MainActivity.createIntent(TabbedActivity.this));
                            finish();
                        } else {
                            Snackbar.make(mRootView, R.string.sign_out_failed, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            searchBarMenuItem = item;
            try {       //opens google api search bar
                Intent intent = new PlaceAutocomplete.IntentBuilder
                        (PlaceAutocomplete.MODE_FULLSCREEN).build(TabbedActivity.this);
                startActivityForResult(intent, REQUEST_SELECT_PLACE);

            } catch (GooglePlayServicesRepairableException |
                    GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationSelection(Place location) {
        MapFragment frag = (MapFragment) mPagerAdapter.getFragment(0);
        Log.d(TAG, "onLocationSelection: Got Fragment " + frag);
        frag.onLocationSearch(location);
    }

    @Override
    public void collapseSearch(boolean b) {
        searchBarMenuItem.collapseActionView(); //closes view so that things can be re-searched
    }

    /**
     * Starts the Create Events Activity
     * @param v View that was clicked
     */
    @Override
    public void onCreateActivityAction(View v, Location lastLocation) {
        Intent intent = new Intent(this, CreateEventActivity.class);
        intent.putExtra("latitude", Double.toString(lastLocation.getLatitude()));
        intent.putExtra("longitude",Double.toString(lastLocation.getLongitude()));
        intent.putExtra("uid", mCurrentUser.getUid());
        startActivityForResult(intent, CREATE_EVENTS);
    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.i(TAG, "Place Selected: " + place.getName());
        this.onLocationSelection(place);
    }

    @Override
    public void onError(Status status) {
        Log.e(TAG, "onError: Status = " + status.toString());
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    public void onCameraUpdate(Place loc) {
        MapFragment frag = (MapFragment) mPagerAdapter.getFragment(0);
        Log.d(TAG, "onCameraUpdate: Got Fragment " + frag);
        Location newLoc = new Location("dunmmy");
        newLoc.setLatitude(loc.getLatLng().latitude);
        newLoc.setLongitude(loc.getLatLng().longitude);
        frag.onLocationChanged(newLoc);
    }

    public void onCameraUpdate(double lat, double lon) {
        MapFragment frag = (MapFragment) mPagerAdapter.getFragment(0);
        Log.d(TAG, "onCameraUpdate: Got Fragment " + frag);
        Location newLoc = new Location("dummmy");
        newLoc.setLatitude(lat);
        newLoc.setLongitude(lon);
        frag.onLocationChanged(newLoc);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                this.onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                this.onError(status);
            }
        }
        if (requestCode == CREATE_EVENTS && resultCode == RESULT_OK) {
            double lat = data.getDoubleExtra(CreateEventActivity.STATE_SELECTED_LATITUDE, -100000);
            double lon = data.getDoubleExtra(CreateEventActivity.STATE_SELECTED_LONGITUDE, -100000);
            if (!(lat == -100000 || lon == -100000)) {
                this.onCameraUpdate(lat,lon);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Parses a JSON String into a list of Event Objects.
     * @param json  JSON string containing information about the events in the area.
     */

    public void retrieveAndParseJSON(String json, int flag) {
        Log.d(TAG, "retrieveAndParseJSON: Getting json\n" + json);
        eventsJSON = json;
        eventsMap = new HashMap<>();
        List<Event> events = JSONToEventGenerator.unmarshallJSONString(eventsJSON);
        for (Event event : events) {
            String mapKey = normalizeKeyForMap(event);
            if (!eventsMap.containsKey(mapKey)) {
                eventsMap.put(mapKey, new ArrayList<Event>());
            }
            eventsMap.get(mapKey).add(event);
        }
        Log.d(TAG, "retrieveAndParseJSON: FLAG " + flag);
        switch (flag) {
            case 0:
                Log.d(TAG, "retrieveAndParseJSON: Retreiving Search Map Events");
                MapFragment mapFrag = (MapFragment) mPagerAdapter.getFragment(flag);
                for (String key : eventsMap.keySet()) {
                    mapFrag.populateEventsInMap(key, eventsMap.get(key));
                }
                break;
            case 1:
                Log.d(TAG, "retrieveAndParseJSON: RETRIEVING LIKED EVENTS");
                FavoritesFragment favFrag = (FavoritesFragment) mPagerAdapter.getFragment(flag);
                favFrag.setData(events);
                break;
            case 2:
                Log.d(TAG, "retrieveAndParseJSON: RETRIEVING MY SPOTS");
                MySpotsFragment spotFrag = (MySpotsFragment) mPagerAdapter.getFragment(flag);
                spotFrag.setData(events);
                break;
            default:

        }
//        MapFragment frag = (MapFragment) mPagerAdapter.getFragment(flag);
//        for (String key : eventsMap.keySet()) {
//            frag.populateEventsInMap(key, eventsMap.get(key));
//        }
    }

    /**
     * Normalizes the location of the event to 11.1 meters.
     * Effectively rounds latitude and longitude values to the fourth decimal place to group events at similar locations.
     * @param event
     * @return
     */
    private String normalizeKeyForMap(Event event) {
        DecimalFormat decimalFormat = new DecimalFormat("##.####");	//four decimal places corresponds to 11.1 meters
        decimalFormat.setRoundingMode(RoundingMode.UP);
        return decimalFormat.format(event.getLatitude()) + " " + decimalFormat.format(event.getLongitude());
    }

    public static Context mContext;

    static class TabViewPagerAdapter extends RegisteredFragmentStatePagerAdapter {

        public TabViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        private final String[] mFragmentTitles = new String[] {
                "Search",
                "Favorites",
                "My Spots"
        };
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MapFragment().newInstance(mCurrentUser.getUid(), "");
                case 1:
                    return new FavoritesFragment().newInstance();
                case 2:
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: getCurrentItem() = " +  mPagerAdapter.getFragment(mViewPager.getCurrentItem()));
        if ( mPagerAdapter.getFragment(mViewPager.getCurrentItem()) instanceof FavoritesFragment){
            ((FavoritesFragment)  mPagerAdapter.getFragment(mViewPager.getCurrentItem())).updateAdapter();
        }

        if ( mPagerAdapter.getFragment(mViewPager.getCurrentItem()) instanceof MySpotsFragment){
            ((MySpotsFragment)  mPagerAdapter.getFragment(mViewPager.getCurrentItem())).updateAdapter();
        }

    }

}
