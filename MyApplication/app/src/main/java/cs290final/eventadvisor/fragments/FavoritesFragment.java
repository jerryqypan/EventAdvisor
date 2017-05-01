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

import java.util.Collection;
import java.util.List;

import cs290final.eventadvisor.MapsActivity;
import cs290final.eventadvisor.R;
import cs290final.eventadvisor.TabbedActivity;
import cs290final.eventadvisor.adapters.EventItemAdapter;
import cs290final.eventadvisor.backend.Event;
import cs290final.eventadvisor.backend.ShowInterest;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {

    private static final String TAG = "FavoritesFragment";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private EventItemAdapter mEventItemAdapter;

    public FavoritesFragment() {

    }

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: Context: " + context);
        new ShowInterest(context).execute(TabbedActivity.mCurrentUser.getUid());
    }
    


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        // Initialize Recycler View For Local Quizzes
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.event_list);
        mRecyclerView.setHasFixedSize(true);
        // Set up Layout Manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        Log.d(TAG, "Inside .onCreateView()");
        // Get Fav events for current user
        mEventItemAdapter = new EventItemAdapter(rootView.getContext());

        mRecyclerView.setAdapter(mEventItemAdapter);
        return rootView;
    }

    public void updateAdapter() {
        Log.d(TAG, "updateAdapter: UPDATEEEEEEEEEEEEEEE");
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }


    public void setData(List<Event> values) {
        mEventItemAdapter.addEvents(values);
        this.updateAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.getAdapter().notifyDataSetChanged();
        Log.d(TAG, "onResume: RESUMED!");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: paused!");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
