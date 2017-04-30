package cs290final.eventadvisor.backend;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;


/**
 * Created by Jerry on 4/13/2017.
 */
@IgnoreExtraProperties
public class Event implements Parcelable {
    private String title;
    private String date;
    private String startTime;
    private String endTime;
    private String description;
    private double latitude;
    private double longitude;
    private int idEvent;
    private String place;
    private String uid;
    private String url;
    private boolean isInterested;


    public Event() {
    }

    public Event(String title, String date, String startTime, String endTime, String description, double longitude, double latitude,String place, String uid, String url, boolean isInterested){
        this.title=title;
        this.date=date;
        this.startTime=startTime;
        this.endTime=endTime;
        this.description=description;
        this.latitude=latitude;
        this.longitude=longitude;
        this.place = place;
        this.uid = uid;
        this.url = url;
        this.isInterested=isInterested;
    }



    public String getUid() {
        return uid;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlace(){return place;}

    public void setPlace(String place){this.place=place;}

    public boolean getisInterested(){return isInterested;}
    public void setisInterested(boolean isInterested){this.isInterested=isInterested;}
    public int getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }
    public String getUrl(){return url;}
    public void setUrl(String url){this.url=url;}

    @Override
    public String toString() {
        return this.title;
    }

    protected Event(Parcel in) {
        title = in.readString();
        date = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        description = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        place = in.readString();
        idEvent = in.readInt();
        uid = in.readString();
        isInterested = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(description);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(place);
        dest.writeInt(idEvent);
        dest.writeString(uid);
        dest.writeByte((byte) (isInterested ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
    public static class EventsWrapper {
        private List<Event> events;

        public List<Event> getEvents() {
            return events;
        }

        public void setEvents(List<Event> events) {
            this.events = events;
        }
    }
}