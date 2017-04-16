package cs290final.eventadvisor.backend;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Jerry on 4/13/2017.
 */
@IgnoreExtraProperties
public class Event {
    public String title;
    public String date;
    public String startTime;
    public String endTime;
    public String description;
    //public double[] coordinates;
    public double longitude;
    public double latitude;

    public Event(){

    }
    public Event(String title, String date, String startTime, String endTime, String description, double longitude, double latitude){
        this.title=title;
        this.date=date;
        this.startTime=startTime;
        this.endTime=endTime;
        this.description=description;
        this.latitude=latitude;
        this.longitude=longitude;
    }
}
