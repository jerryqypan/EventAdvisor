package cs290final.eventadvisor.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs290final.eventadvisor.R;
import cs290final.eventadvisor.backend.Event;
import cs290final.eventadvisor.backend.RetrieveEvents;
import cs290final.eventadvisor.utils.IFragmentInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    private static final String TAG = "MapFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CURRENT_USER = "CURRENT_USER";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mCurrentUserId;
    private String mParam2;

    /**
     * Map containing a location to a list of events near that location.
     */
    private Map<String, List<Event>> eventsMap = new HashMap<String,List<Event>>();

    /**
     * Map containing a location to the visible map marker representing that location.
     */
    private Map<String, Marker> markersMap = new HashMap<String, Marker>();

    private SupportMapFragment mGoogleMapFragment;

    private View mRootview;

    private Location mLastLocation;

    private GoogleMap mGoogleMap;

    private IFragmentInteractionListener mListener;

    /**
     * The GoogleMapAPI connection client.
     * Used for location services.
     */
    private GoogleApiClient mGoogleApiClient;

    private Marker mCurrLocMarker;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currUserId Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String currUserId, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(CURRENT_USER, currUserId);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentUserId = getArguments().getString(CURRENT_USER);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootview = inflater.inflate(R.layout.fragment_map, container, false);

        try {
            FragmentManager fragmentManager = getChildFragmentManager();
            mGoogleMapFragment = ((SupportMapFragment) fragmentManager.findFragmentById(R.id.map_view));
            mGoogleMapFragment.getMapAsync(this);

            // Check and see if the map is created successfully
            if (mGoogleMapFragment == null) {
                mGoogleMapFragment = SupportMapFragment.newInstance();
                fragmentManager.beginTransaction().replace(R.id.map, mGoogleMapFragment).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FloatingActionButton create_event_fab = (FloatingActionButton) mRootview.findViewById(R.id.fab_create_event);
        FloatingActionButton center_on_loc_fab = (FloatingActionButton) mRootview.findViewById(R.id.fab_center_on_location);

        create_event_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                mListener.onCreateActivityAction(v, mLastLocation);
            }
        });

        center_on_loc_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {  //default action does this
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15));
                }
            }
        });

        return mRootview;
    }

    public void onLocationSearch(Place location) {
        Log.d(TAG, "onLocationSearch: New Search Location: " + location);
        if (mListener != null) {
            mListener.collapseSearch(true);
            this.updateLocation(location);
        }
    }

    public void updateLocation(Place location) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(location.getLatLng()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IFragmentInteractionListener) {
            mListener = (IFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    protected synchronized void initializeApiConnection() {
        Log.d(TAG, "initializeApiConnection: INITIALIZING API CONNECTION");
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    /**
     * Calculates the maximum diameter of the visible portion of the map on screen.
     * @return  The diameter of the maximum distance of the visible portion of the map in meters
     */
    private float calculateMaxMapDistanceOnScreen() {
        VisibleRegion visibleRegion = mGoogleMap.getProjection().getVisibleRegion();
        Location northEastCorner = new Location("");
        northEastCorner.setLatitude(visibleRegion.latLngBounds.northeast.latitude);
        northEastCorner.setLongitude(visibleRegion.latLngBounds.northeast.longitude);
        Location southWestCorner = new Location("");
        southWestCorner.setLatitude(visibleRegion.latLngBounds.southwest.latitude);
        southWestCorner.setLongitude(visibleRegion.latLngBounds.southwest.longitude);
        return southWestCorner.distanceTo(northEastCorner);     //distance in meters
    }



    /**
     * Centers the map on the device's current location.
     */
    private void centerOnLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            mGoogleMap.setMyLocationEnabled(true);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {  //default action does this
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                initializeApiConnection();
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            initializeApiConnection();
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                List<Event> events = (List<Event>) marker.getTag();
                Log.d(TAG, "onMarkerClick: [Events: " + events.toString() + "]");
                //createAndShowEventsPopupWindow(events);

                final BottomSheetDialogFragment bottomSheet = CustomBottomSheetDialogFragment.newInstance(5, events, null);
                bottomSheet.show(getActivity().getSupportFragmentManager(), bottomSheet.getTag());
                return true;
            }
        });

        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.d(TAG, "CAMERA IDLE");
                CameraPosition place = mGoogleMap.getCameraPosition();
                new RetrieveEvents(getContext()).execute(Double.toString(place.target.latitude), Double.toString(place.target.longitude),mCurrentUserId,Float.toString(calculateMaxMapDistanceOnScreen()));
            }
        });
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        centerOnLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: INSIDE ON PAUSE");
        super.onPause();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocMarker != null) {
            mCurrLocMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            initializeApiConnection();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Adds a marker representing the list of events at that location on the map.
     * @param key   Normalized location of the list of events.
     * @param events    The list of events at the location.
     */
    public void populateEventsInMap(String key, List<Event> events) {
        if (markersMap.containsKey(key)) {
            markersMap.get(key).setTag(events);
        } else {
            if (events.size() > 0) {
                MarkerOptions markerOptions = new MarkerOptions();
                String latitude = key.split(" ")[0];
                String longitude = key.split(" ")[1];
                markerOptions.position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
                Marker eventMarker = mGoogleMap.addMarker(markerOptions);
                eventMarker.setTag(events);
                markersMap.put(key, eventMarker);
            }
        }
    }

}
