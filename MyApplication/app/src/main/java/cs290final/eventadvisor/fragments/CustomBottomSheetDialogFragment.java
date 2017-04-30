package cs290final.eventadvisor.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cs290final.eventadvisor.R;
import cs290final.eventadvisor.adapters.EventItemAdapter;
import cs290final.eventadvisor.backend.Event;

/**
 * Created by Asim Hasan on 4/26/2017.
 */
public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private List<Event> mEvents;
    private static final String TAG = "CBSDFrag";
    private BottomSheetBehavior mBehavior;
    private static EventItemAdapter.EventItemListener mListener;

    public static CustomBottomSheetDialogFragment newInstance(int arg, List<Event> events, EventItemAdapter.EventItemListener clickListener) {
        CustomBottomSheetDialogFragment f = new CustomBottomSheetDialogFragment();
        mListener = clickListener;
        Bundle args = new Bundle();
        args.putParcelableArrayList("events", (ArrayList<Event>) events);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvents = getArguments().getParcelableArrayList("events");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_events_list, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.event_list);
        EventItemAdapter adapter = new EventItemAdapter(mEvents, mListener, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return v;
    }
}