package cs290final.eventadvisor.backend;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;


/**
 * Created by Jerry on 4/13/2017.
 */
@IgnoreExtraProperties
public class Event {
    private String title;
    private String date;
    private String startTime;
    private String endTime;
    private String description;
    private double latitude;
    private double longitude;


    private String uid;

    public int getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }

    private int idEvent;
    private String uid;

    public Event() {
    }

    public Event(String title, String date, String startTime, String endTime, String description, double longitude, double latitude, String uid){
        this.title=title;
        this.date=date;
        this.startTime=startTime;
        this.endTime=endTime;
        this.description=description;
        this.latitude=latitude;
        this.longitude=longitude;
        this.uid = uid;
    }


    public int getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
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

    @Override
    public String toString() {
        return this.title;
    }

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