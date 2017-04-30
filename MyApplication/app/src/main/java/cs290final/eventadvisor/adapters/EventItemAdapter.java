package cs290final.eventadvisor.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import cs290final.eventadvisor.R;
import cs290final.eventadvisor.backend.Event;

/**
 * Created by Asim Hasan on 4/30/2017.
 */

public class EventItemAdapter extends RecyclerView.Adapter<EventItemAdapter.ViewHolder> {

    private final List<Event> mEvents;
    private Context mContext;
    private EventItemListener mEventItemListener;

    private static final String TAG = "EventItemAdapter";


    public EventItemAdapter(List<Event> events, EventItemListener listener, Context context) {
        mEvents = events;
        mContext = context;
        mEventItemListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event e = mEvents.get(position % mEvents.size());
        holder.setData(e);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView eventAvatar;
        public TextView eventTitle;
        public TextView eventDate;
        public TextView eventLocation;
        public TextView eventTime;
        public TextView eventDescription;

        public Event mEvent;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_card_view, parent, false));
            eventAvatar = (ImageView) itemView.findViewById(R.id.card_avatar);
            eventTitle = (TextView) itemView.findViewById(R.id.card_title);
            eventDescription = (TextView) itemView.findViewById(R.id.card_text);
            eventDate = (TextView) itemView.findViewById(R.id.card_date);
            eventLocation = (TextView) itemView.findViewById(R.id.card_location);
            eventTime = (TextView) itemView.findViewById(R.id.card_time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEventClick(v);
                }
            });

            ImageButton favoriteImageButton =
                    (ImageButton) itemView.findViewById(R.id.favorite_button);
            favoriteImageButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Added to Favorite",
                            Snackbar.LENGTH_LONG).show();
                }
            });

            ImageButton shareImageButton = (ImageButton) itemView.findViewById(R.id.share_button);
            shareImageButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Share article",
                            Snackbar.LENGTH_LONG).show();
                }
            });
        }

        public void setData(Event event) {
            this.mEvent = event;
            eventTitle.setText(event.getTitle());
            eventDescription.setText(event.getDescription());
            eventAvatar.setImageResource(R.drawable.anon_user_48dp);
            eventDate.setText(event.getDate());
            eventLocation.setText("Durham, NC");
            eventTime.setText(event.getStartTime());
        }

        public void onEventClick(View v) {
            if (mEventItemListener != null) {
                Log.d(TAG, "onClick: Inside");
                mEventItemListener.onItemClick(mEvent);
            }
        }
    }

    public interface EventItemListener {
        void onItemClick(Event event);
    }
}