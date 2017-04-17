package cs290final.eventadvisor.backend;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Titan on 4/16/2017.
 */

public class JSONToEventGenerator {

    private static ObjectMapper mapper = new ObjectMapper();

    private JSONToEventGenerator() {
    }

    public static List<Event> unmarshallJSONString(String jsonInput) {
        List<Event> events = new ArrayList<Event>();
        try {
            Event.EventsWrapper eventsWrapper = mapper.readValue(jsonInput, Event.EventsWrapper.class);
            events = eventsWrapper.getEvents();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return events;
    }
}
