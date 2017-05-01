package cs290final.eventadvisor.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cs290final.eventadvisor.MapsActivity;
import cs290final.eventadvisor.R;
import cs290final.eventadvisor.adapters.EventItemAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MySpotsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MySpotsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MySpotsFragment extends Fragment {
    private static final String dTAG = "MySpotsFragment";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    public MySpotsFragment() {

    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_my_spots, container, false);

        // Initialize Recycler View For Local Quizzes
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.event_list);
        mRecyclerView.setHasFixedSize(true);
        // Set up Layout Manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        Log.d("dTAG", "Inside .onCreateView()");
        // Get Fav events for current user
        EventItemAdapter mAdapter = new EventItemAdapter(MapsActivity.mUserEvents, null, rootView.getContext());

        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

}