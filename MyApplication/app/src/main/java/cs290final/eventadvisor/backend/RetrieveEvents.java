package cs290final.eventadvisor.backend;

import com.google.gson.Gson;

/**
 * Created by Jerry on 4/16/2017.
 */

public class RetrieveEvents {


    public void getEvents(){


        Gson g = new Gson();
        Event testEvent = new Event("TestEvent","4-31-2017","12:00","16:00","This is a test event",35.998456,-78.939116);
        System.out.println(g.toJson(testEvent));
    }
    public static void main(String args[]){
        RetrieveEvents test = new RetrieveEvents();
        test.getEvents();
    }
}
