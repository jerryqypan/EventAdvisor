package cs290final.eventadvisor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import cs290final.eventadvisor.adapters.EventItemAdapter;
import cs290final.eventadvisor.backend.Event;
import cs290final.eventadvisor.backend.JSONToEventGenerator;
import cs290final.eventadvisor.backend.RetrieveEvents;
import cs290final.eventadvisor.backend.SelectInterest;
import cs290final.eventadvisor.fragments.CustomBottomSheetDialogFragment;
import cs290final.eventadvisor.utils.CircleTransform;

/**
 * Activity that displays events on the map.
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    /**
     * Tag identifying this activity for debugging and logging.
     */
    private static final String TAG = "MAPS_ACTIVITY";

    /**
     * The GoogleMap instance for this application
     */
    private GoogleMap mMap;

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

    /**
     * The DrawerLayout of this application.
     */
    private DrawerLayout mDrawerLayout;

    /**
     * The NagivationView of this application.
     */
    private NavigationView mNavigationView;

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
    public FirebaseUser currentUser;

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

    @Override
    /**
     * Create the view hierarachy for this application.
     */
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, take them to the map activity directly
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_create_event);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createActivityAction(view);
            }
        });

        // Create Navigation drawer and inflate layout
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
                        // Set item in checked state
                        menuItem.setChecked(true);
                        // TODO: handle navigation
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


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

    }

    protected void onStart() {
        //mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
       // mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    public void onBackPressed() {
    }

    @Override
    /**
     * Centers the map when Location services is ready.
     */
    public void onConnected(@Nullable Bundle bundle) {
        centerOnLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setOnMyLocationButtonClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
//        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                List<Event> events = (List<Event>) marker.getTag();
                Log.d(TAG, "onMarkerClick: [Events: " + events.toString() + "]");
                mEventClickListener = new EventItemAdapter.EventItemListener() {
                    @Override
                    public void onItemClick(Event event) {
                        Log.d(TAG, "onItemClick: Clicked on event" + event.getTitle());
                    }
                };
                //createAndShowEventsPopupWindow(events);
                final BottomSheetDialogFragment bottomSheet = CustomBottomSheetDialogFragment.newInstance(5, events, mEventClickListener);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                return true;
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.d(TAG, "CAMERA IDLE");
//                clearEventsFromMap();
                CameraPosition place = mMap.getCameraPosition();
                float distanceInMeters = calculateMaxMapDistanceOnScreen();
                new RetrieveEvents(MapsActivity.this).execute(Double.toString(place.target.latitude), Double.toString(place.target.longitude),currentUser.getUid(),Float.toString(calculateMaxMapDistanceOnScreen()));
            }
        });
    }

    /**
     * Centers on the device's current location.
     * @param v View that was clicked.
     */
    public void centerOnDeviceLocationAction(View v) {
        centerOnLocation();
    }

    /**
     * Centers the map on the device's current location.
     */
    private void centerOnLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {  //default action does this
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15));
        }
    }

    /**
     * Calculates the maximum diameter of the visible portion of the map on screen.
     * @return  The diameter of the maximum distance of the visible portion of the map in meters
     */
    private float calculateMaxMapDistanceOnScreen() {
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        Location northEastCorner = new Location("");
        northEastCorner.setLatitude(visibleRegion.latLngBounds.northeast.latitude);
        northEastCorner.setLongitude(visibleRegion.latLngBounds.northeast.longitude);
        Location southWestCorner = new Location("");
        southWestCorner.setLatitude(visibleRegion.latLngBounds.southwest.latitude);
        southWestCorner.setLongitude(visibleRegion.latLngBounds.southwest.longitude);
        return southWestCorner.distanceTo(northEastCorner);     //distance in meters
    }

    /**
     * Removes all the markers from the map.
     */
    private void clearEventsFromMap() {
        mMap.clear();
    }

    /**
     * Parses a JSON String into a list of Event Objects.
     * @param json  JSON string containing information about the events in the area.
     */
    public void retrieveAndParseJSON(String json) {
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
        addEventsToGoogleMap();
    }

    /**
     * Adds all the events in the area to the map.
     */
    private void addEventsToGoogleMap() {
        for (String key : eventsMap.keySet()) {
            addEventToGoogleMap(key, eventsMap.get(key));
        }
        removeUnusedMarkersFromGoogleMap();
    }

    /**
     * Adds a marker representing the list of events at that location on the map.
     * @param key   Normalized location of the list of events.
     * @param events    The list of events at the location.
     */
    private void addEventToGoogleMap(String key, List<Event> events) {
        if (markersMap.containsKey(key)) {
            markersMap.get(key).setTag(events);
        } else {
            if (events.size() > 0) {
                MarkerOptions markerOptions = new MarkerOptions();
                String latitude = key.split(" ")[0];
                String longitude = key.split(" ")[1];
                markerOptions.position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
                Marker eventMarker = mMap.addMarker(markerOptions);
                eventMarker.setTag(events);
                markersMap.put(key, eventMarker);
            }
        }
    }

    /**
     * Removes event markers that are no longer valid from the map.
     */
    private void removeUnusedMarkersFromGoogleMap() {
        Iterator iterator = markersMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (!eventsMap.containsKey(key)) {
                markersMap.get(key).remove();
                iterator.remove();
            }
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {  //search bar place result
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                //Toast.makeText(this, place.getName(), Toast.LENGTH_SHORT).show();
                //don't need this as it will be taken care of in the camera idle listener
//                new RetrieveEvents(MapsActivity.this).execute(place.getLatLng().latitude,place.getLatLng().longitude);

            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            }
            searchBarMenuItem.collapseActionView(); //closes view so that things can be re-searched
            searchView.setIconified(true);
        }
        if (requestCode == CREATE_EVENTS && resultCode == RESULT_OK) {
            double lat = data.getDoubleExtra(CreateEventActivity.STATE_SELECTED_LATITUDE, -100000);
            double lon = data.getDoubleExtra(CreateEventActivity.STATE_SELECTED_LONGITUDE, -100000);
            if (!(lat == -100000 || lon == -100000)) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lon)));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Starts the Create Events Activity
     * @param v View that was clicked
     */
    public void createActivityAction(View v){
        Intent intent = new Intent(this,CreateEventActivity.class);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        intent.putExtra("latitude", Double.toString(mLastLocation.getLatitude()));
        intent.putExtra("longitude",Double.toString(mLastLocation.getLongitude()));
        intent.putExtra("uid",currentUser.getUid());
        startActivityForResult(intent, CREATE_EVENTS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
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
            this.searchBarMenuItem = item;
            try {       //opens google api search bar
                Intent intent = new PlaceAutocomplete.IntentBuilder
                        (PlaceAutocomplete.MODE_OVERLAY).build(MapsActivity.this);
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
        if (currentUser.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(currentUser.getPhotoUrl())
                    .fitCenter()
                    .transform(new CircleTransform(this))
                    .into(mUserProfilePicture);
        } else {
            mUserProfilePicture = (ImageView) findViewById(R.id.user_profile_picture);
        }

        mUserEmail.setText(
                TextUtils.isEmpty(currentUser.getEmail()) ? "No email" : currentUser.getEmail());
        mUserDisplayName.setText(
                TextUtils.isEmpty(currentUser.getDisplayName()) ? "No display name" : currentUser.getDisplayName());


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
                            startActivity(MainActivity.createIntent(MapsActivity.this));
                            finish();
                        } else {
                            showSnackbar(R.string.delete_account_failed);
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
                            startActivity(MainActivity.createIntent(MapsActivity.this));
                            finish();
                        } else {
                            Snackbar.make(mRootView, R.string.sign_out_failed, Snackbar.LENGTH_LONG);
                        }
                    }
                });
    }

    /**
     * Shows the Snackbar for this app
     * @param errorMessageRes
     */
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG)
                .show();
    }

    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent in = IdpResponse.getIntent(idpResponse);
        in.setClass(context, MapsActivity.class);
        return in;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        onMapReady(mMap);
        centerOnLocation();
//        mMap.setMyLocationEnabled(true);
    }
    
}

