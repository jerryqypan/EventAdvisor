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

import java.util.List;

import cs290final.eventadvisor.MapsActivity;
import cs290final.eventadvisor.R;
import cs290final.eventadvisor.TabbedActivity;
import cs290final.eventadvisor.adapters.EventItemAdapter;
import cs290final.eventadvisor.backend.Event;
import cs290final.eventadvisor.backend.RetrieveUserEvents;
import cs290final.eventadvisor.backend.ShowInterest;

public class MySpotsFragment extends Fragment {
    private static final String TAG = "MySpotsFragment";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private EventItemAdapter mItemAdapter;

    public MySpotsFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: Context: " + context);
        new RetrieveUserEvents(context).execute(TabbedActivity.mCurrentUser.getUid());
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
        Log.d("TAG", "Inside .onCreateView()");
        // Get Fav events for current user
        mItemAdapter = new EventItemAdapter(rootView.getContext());

        mRecyclerView.setAdapter(mItemAdapter);
        return rootView;
    }

    public void setData(List<Event> events) {
        mItemAdapter.addEvents(events);
        this.updateAdapter();
    }

    public void updateAdapter() {
        mItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: paused!");
    }

}