package cs290final.eventadvisor.utils;

import android.location.Location;
import android.view.View;

import com.google.android.gms.location.places.Place;

/**
 * Created by Asim Hasan on 5/1/2017.
 */

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */

public interface IFragmentInteractionListener {
    // TODO: Update argument type and name
    void onLocationSelection(Place loc);

    void collapseSearch(boolean b);

    void onCreateActivityAction(View v, Location lastLocation);

    void onCameraUpdate(Place p);
}
