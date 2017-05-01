package cs290final.eventadvisor.backend;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Titan on 4/16/2017.
 * Contains methods to parse JSON strings into custom objects
 */

public class JSONToEventGenerator {

    private static ObjectMapper mapper = new ObjectMapper();

    private JSONToEventGenerator() {
    }

    /**
     *
     * @param jsonInput The JSON String to parse
     * @return  A List of Event objects, parsed from the input string
     */
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