package cs290final.eventadvisor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cs290final.eventadvisor.backend.Event;
import cs290final.eventadvisor.backend.JSONToEventGenerator;
import cs290final.eventadvisor.backend.RetrieveEvents;

// API Key: AIzaSyCJm1es7DqRc1zqyW7AKQFQpeXcD1kNFm0
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;
//    private List<Event> eventsList;
    private Map<String, List<Event>> eventsMap = new HashMap<String,List<Event>>();
    private Map<String, Marker> markersMap = new HashMap<String, Marker>();
    private String eventsJSON;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    private static final int REQUEST_SELECT_PLACE = 1234;
    private SearchView searchView;
    private MenuItem searchBarMenuItem;

    View mRootView;         //can these be private? -Chirag
    ImageView mUserProfilePicture;
    TextView mUserEmail;
    TextView mUserDisplayName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, take them to the map activity directly
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }

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
//        setupSearchBar();
        String testEvents = "{ \"events\":[{\"title\":\"Test1Chapel\",\"date\":\"4-31-2017\",\"startTime\":\"12:00\",\"endTime\":\"16:00\",\"description\":\"This is a test event\",\"longitude\":-78.940278,\"latitude\":36.001901},{\"title\":\"Test2WU\",\"date\":\"4-31-2017\",\"startTime\":\"12:00\",\"endTime\":\"16:00\",\"description\":\"This is a test event\",\"longitude\":-78.939011,\"latitude\":36.000798}]}";
//        eventsList = JSONToEventGenerator.unmarshallJSONString(testEvents);
        System.out.println("is ui" + Thread.currentThread());
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
        mMap.setOnMyLocationButtonClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                List<Event> events = (List<Event>) marker.getTag();
//                Event event = (Event) marker.getTag();
                if (events == null || events.size() == 0) {
                    return true;
                }
                if (events.size() == 1) {
                    marker.setTitle(events.get(0).getTitle());
                    marker.setSnippet(events.get(0).getDescription());
                    marker.showInfoWindow();
                    return true;
                }
//                marker.showInfoWindow();
//                Toast.makeText(MapsActivity.this, event.getTitle(), Toast.LENGTH_SHORT).show();

//                Fragment newFragment = new EventsFragment();
//                getFragmentManager().beginTransaction().add(R.id.map, newFragment).commit();
//                PopupWindow popupWindow = new PopupWindow(MapsActivity.this);
//                popupWindow.showAtLocation(findViewById(R.id.map), Gravity.BOTTOM, 10, 10);
                AlertDialog.Builder popup = new AlertDialog.Builder(MapsActivity.this);
//                popup.setIcon(R.drawable.com_facebook_button_icon);
                popup.setTitle("Events at this location:");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.select_dialog_item);
                final EventsAdapter eventsAdapter = new EventsAdapter(events, MapsActivity.this);
//                for (Event event : events) {
//                    arrayAdapter.add(event.getTitle() + " " + event.getDate());
//                }
//                arrayAdapter.add("Hardik");
//                arrayAdapter.add("Archit");
//                arrayAdapter.add("Jignesh");
//                arrayAdapter.add("Umang");
//                arrayAdapter.add("Gatti");


                popup.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                popup.setAdapter(eventsAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Event event = (Event) eventsAdapter.getItem(which);
                        marker.setTitle(event.getTitle() + " " + which);
                        marker.setSnippet(event.getDescription());
                        marker.showInfoWindow();
                        String strName = event.getTitle() + " " + event.getDescription();
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(MapsActivity.this);
                        builderInner.setMessage(strName);
                        builderInner.setTitle(event.getTitle() + " " + event.getStartTime()+ " - " + event.getEndTime());
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                dialog.dismiss();
                            }
                        });
                        builderInner.show();
                    }
                });
                popup.show();
                return true;
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                System.out.println("camera idle");
                Toast.makeText(MapsActivity.this, "Camera Idle", Toast.LENGTH_SHORT).show();
//                clearEventsFromMap();
                CameraPosition place = mMap.getCameraPosition();
                float distanceInMeters = calculateMaxMapDistanceOnScreen();
                System.out.println("Distance: " + distanceInMeters);
                new RetrieveEvents(MapsActivity.this).execute(place.target.latitude, place.target.longitude);
            }
        });
//        addEventsToGoogleMap();

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        centerOnLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        centerOnLocation();
        return false;
    }

    private void centerOnLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Toast.makeText(this, "Centered LOCATION", Toast.LENGTH_LONG).show();
        if (mLastLocation != null) {  //default action does this
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    public void createActivityAction(View v){
        Intent intent = new Intent(this,CreateEventActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                System.out.println("Searched: " + query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {  //search bar place result
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                Toast.makeText(this, place.getName(), Toast.LENGTH_SHORT).show();
//                new RetrieveEvents(MapsActivity.this).execute(place.getLatLng().latitude,place.getLatLng().longitude);

            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            }
            searchBarMenuItem.collapseActionView(); //closes view so that things can be re-searched
            searchView.setIconified(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

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

    private void populateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .fitCenter()
                    .into(mUserProfilePicture);
        } else {
            mUserProfilePicture = (ImageView) findViewById(R.id.user_profile_picture);
        }

        mUserEmail.setText(
                TextUtils.isEmpty(user.getEmail()) ? "No email" : user.getEmail());
        mUserDisplayName.setText(
                TextUtils.isEmpty(user.getDisplayName()) ? "No display name" : user.getDisplayName());


    }

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

    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG)
                .show();
    }

    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent in = IdpResponse.getIntent(idpResponse);
        in.setClass(context, MapsActivity.class);
        return in;
    }

    private void addEventsToGoogleMap() {
        for (String key : eventsMap.keySet()) {
            addEventToGoogleMap(key, eventsMap.get(key));
        }
        removeUnusedMarkersFromGoogleMap();
    }

    private void addEventToGoogleMap(String key, List<Event> events) {
        if (markersMap.containsKey(key)) {
            markersMap.get(key).setTag(events);
        } else {
            if (events.size() > 0) {
                MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.title(event.getTitle());
                String latitude = key.split(" ")[0];
                String longitude = key.split(" ")[1];
                markerOptions.position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
//            markerOptions.snippet(event.getDescription());
                Marker eventMarker = mMap.addMarker(markerOptions);
                eventMarker.setTag(events);
                markersMap.put(key, eventMarker);
            }
        }
    }

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


//    private void setupSearchBar() {
//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                System.out.println("place selected");
//                Marker searchedPlace = mMap.addMarker(new MarkerOptions().title((String) place.getName()).position(place.getLatLng()));
//                System.out.println(place.getLatLng().longitude);
//                System.out.println(place.getLatLng().latitude);
//                new RetrieveEvents(MapsActivity.this).execute(place.getLatLng().latitude,place.getLatLng().longitude);
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 13));
//                searchedPlace.showInfoWindow();
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                System.out.println("searchbar error");
//            }
//        });
//    }

    public void retrieveAndParseJSON(String json) {
        System.out.println("maps" + Thread.currentThread());
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

    private String normalizeKeyForMap(Event event) {
        DecimalFormat decimalFormat = new DecimalFormat("##.####");	//four decimal places corresponds to 11.1 meters
        decimalFormat.setRoundingMode(RoundingMode.UP);
        return decimalFormat.format(event.getLatitude()) + " " + decimalFormat.format(event.getLongitude());
    }

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

    private void clearEventsFromMap() {
        mMap.clear();
    }

//    public static class EventsFragment extends DialogFragment {
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the current date as the default date in the picker
//
//            return new DatePickerDialog(getActivity(), this, year, month, day);
//        }
//    }

    public static class EventsAdapter extends BaseAdapter {
        private List<Event> eventsList;
        private Context context;

        public EventsAdapter(List<Event> eventsList, Context context) {
            this.eventsList = eventsList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return eventsList.size();
        }

        @Override
        public Object getItem(int position) {
            return eventsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return eventsList.get(position).getIdEvent();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            TextView textView = new TextView(context);
            Event event = eventsList.get(position);
//            textView.setText(event.getTitle() + " " + event.getDate());
            View v = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
            ((TextView) v).setText(event.getTitle() + " " + event.getDate());
            return v;
        }
    }
}

