package cs290final.eventadvisor.backend;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;


/**
 * Created by Jerry on 4/13/2017.
 * A class to hold the details of a specific event.
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


    /**
     *
     * @return The User ID of who created the event
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the user id of who created this event
     * @param uid The user id
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     *
     * @return The title of this event
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the event.
     * @param title     The title of the event
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return  The start date of the event.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of this event.
     * @param date  The date to set.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return The start time of this event.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of this event
     * @param startTime The start time.
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     *
     * @return The end time of this event.
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time of this event
     * @param endTime   The end time
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     *
     * @return  The description of this event.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this event.
     * @param description   The description of this event
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return  The latitude of the location of this event.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the location of this event.
     * @param latitude  The latitude of the location.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     *
     * @return  The longitude of the location of this event.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the location of this event.
     * @param longitude The longitude of the location of this event.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlace(){return place;}

    public void setPlace(String place){this.place=place;}

    /**
     *
     * @return  Returns true is the user is interested in this event.
     */
    public boolean getisInterested(){return isInterested;}

    public void setisInterested(boolean isInterested){this.isInterested=isInterested;}

    /**
     *
     * @return The event id.
     */
    public int getIdEvent() {
        return idEvent;
    }

    /**
     * Sets the event id
     * @param idEvent   The event id
     */
    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }

    /**
     *
     * @return  Gets the URL of an image of this event.
     */
    public String getUrl(){return url;}

    /**
     * Sets the image location url for this event
     * @param url   The url of the image.
     */
    public void setUrl(String url){this.url=url;}

    @Override
    public String toString() {
        return this.title;
    }

    @Override
    public boolean equals(Object o) {
        if (!(this instanceof Event)) {
            return false;
        }
        Event b = (Event) o;
        return this.idEvent == b.idEvent;
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

    /**
     * A wrapper class, designed to parse a list of JSON events.
     */
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